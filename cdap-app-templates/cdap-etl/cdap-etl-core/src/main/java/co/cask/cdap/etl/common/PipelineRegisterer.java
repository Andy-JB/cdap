/*
 * Copyright © 2015 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.cdap.etl.common;

import co.cask.cdap.api.dataset.DatasetProperties;
import co.cask.cdap.api.plugin.PluginConfigurer;
import co.cask.cdap.api.plugin.PluginProperties;
import co.cask.cdap.etl.api.PipelineConfigurable;
import co.cask.cdap.etl.api.PipelineConfigurer;
import co.cask.cdap.etl.api.Transform;
import co.cask.cdap.etl.api.Transformation;
import co.cask.cdap.etl.api.realtime.RealtimeSink;
import co.cask.cdap.etl.api.realtime.RealtimeSource;
import co.cask.cdap.etl.common.guice.TypeResolver;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registers plugins needed by an ETL pipeline.
 */
public class PipelineRegisterer {

  private final PluginConfigurer configurer;
  private final String sourcePluginType;
  private final String sinkPluginType;


  public PipelineRegisterer(PluginConfigurer configurer, String programType) {
    this.configurer = configurer;
    this.sourcePluginType = programType + "source";
    this.sinkPluginType = programType + "sink";
  }

  /**
   * Registers the plugins that will be used in the pipeline
   *
   * @param config the config containing pipeline information
   * @param errorDatasetType error dataset type class
   * @param errorDatasetProperties properties of the error dataset
   * @param sinkWithErrorDataset boolean flag to indicate if the sinks uses error dataset
   * @return the ids of each plugin used in the pipeline
   */
  public Pipeline registerPlugins(ETLConfig config, Class errorDatasetType, DatasetProperties errorDatasetProperties,
                                  boolean sinkWithErrorDataset) {
    config = config.getCompatibleConfig();
    ETLStage sourceConfig = config.getSource();
    List<ETLStage> transformConfigs = config.getTransforms();
    List<ETLStage> sinkConfigs = config.getSinks();
    if (sinkConfigs == null || sinkConfigs.isEmpty()) {
      throw new IllegalArgumentException("At least one sink must be specified.");
    }
    if (sourceConfig == null) {
      throw new IllegalArgumentException("A source must be specified.");
    }

    // validate that the stage names are unique
    validateStageNames(sourceConfig, config.getTransforms(), config.getSinks());

    // validate connections, there are no-cycles, all sinks are reachable, etc.
    Map<String, List<String>> connectionsMap = validateConnections(config);
    String sourcePluginId = sourceConfig.getName();

    // instantiate source
    PipelineConfigurable source = configurer.usePlugin(sourcePluginType,
                                                       sourceConfig.getPlugin().getName(),
                                                       sourcePluginId, getPluginProperties(sourceConfig));
    if (source == null) {
      throw new IllegalArgumentException(String.format("No Plugin of type '%s' named '%s' was found.",
                                                       Constants.Source.PLUGINTYPE,
                                                       sourceConfig.getPlugin().getName()));
    }
    // configure source, allowing it to add datasets, streams, etc
    PipelineConfigurer sourceConfigurer = new DefaultPipelineConfigurer(configurer, sourcePluginId);
    source.configurePipeline(sourceConfigurer);

    // transform id list will eventually be serialized and passed to the driver program
    List<TransformInfo> transformInfos = new ArrayList<>(transformConfigs.size());
    List<Transformation> transforms = new ArrayList<>(transformConfigs.size());
    for (ETLStage transformConfig : transformConfigs) {
      String transformId = transformConfig.getName();

      PluginProperties transformProperties = getPluginProperties(transformConfig);
      Transform transformObj = configurer.usePlugin(Constants.Transform.PLUGINTYPE,
                                                    transformConfig.getPlugin().getName(),
                                                    transformId, transformProperties);
      if (transformObj == null) {
        throw new IllegalArgumentException(String.format("No Plugin of type '%s' named '%s' was found",
                                                         Constants.Transform.PLUGINTYPE,
                                                         transformConfig.getPlugin().getName()));
      }
      // if the transformation is configured to write filtered records to error dataset, we create that dataset.
      if (transformConfig.getErrorDatasetName() != null) {
        configurer.createDataset(transformConfig.getErrorDatasetName(), errorDatasetType, errorDatasetProperties);
      }

      PipelineConfigurer transformConfigurer = new DefaultPipelineConfigurer(configurer, transformId);
      transformObj.configurePipeline(transformConfigurer);
      transformInfos.add(new TransformInfo(transformId, transformConfig.getErrorDatasetName()));
      transforms.add(transformObj);
    }

    List<SinkInfo> sinksInfo = new ArrayList<>();
    List<PipelineConfigurable> sinks = new ArrayList<>();
    for (ETLStage sinkConfig : sinkConfigs) {
      String sinkPluginId = sinkConfig.getName();

      // create error dataset for sink - if the sink supports it and error dataset is configured for it.
      if (sinkWithErrorDataset && sinkConfig.getErrorDatasetName() != null) {
        configurer.createDataset(sinkConfig.getErrorDatasetName(), errorDatasetType, errorDatasetProperties);
      }

      sinksInfo.add(new SinkInfo(sinkPluginId, sinkConfig.getErrorDatasetName()));

      // try to instantiate the sink
      PipelineConfigurable sink = configurer.usePlugin(sinkPluginType, sinkConfig.getPlugin().getName(),
                                                       sinkPluginId, getPluginProperties(sinkConfig));
      if (sink == null) {
        throw new IllegalArgumentException(
          String.format(
            "No Plugin of type '%s' named '%s' was found" +
              "Please check that an artifact containing the plugin exists, " +
              "and that it extends the etl application.",
            Constants.Sink.PLUGINTYPE, sinkConfig.getPlugin().getName()));
      }
      // run configure pipeline on sink to let it add datasets, etc.
      PipelineConfigurer sinkConfigurer = new DefaultPipelineConfigurer(configurer, sinkPluginId);
      sink.configurePipeline(sinkConfigurer);
      sinks.add(sink);
    }

    // Validate Source -> Transform -> Sink hookup
    try {
      validateStages(source, sinks, transforms);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return new Pipeline(sourcePluginId, sinksInfo, transformInfos, connectionsMap);
  }

  @VisibleForTesting
  static Map<String, List<String>> validateConnections(ETLConfig config) {
    Map<String, List<String>> stageConnections = new HashMap<>();

    // 1) basic validation, # of connections >= (source + #transform + sink - 1)
    if (config.getConnections().size() < (config.getTransforms().size() + config.getSinks().size())) {
      throw new IllegalArgumentException(
        "Number of edges connecting the pipeline is less than the number of vertices, please check the connections");
    }
    // 2) connections end up in a sink and all sinks are covered also check there are no cycles.

    // set containing names of stages.
    Set<String> stageNamesFromConfig = new HashSet<>();

    stageNamesFromConfig.add(config.getSource().getName());
    for (ETLStage stage : config.getTransforms()) {
      stageNamesFromConfig.add(stage.getName());
    }
    for (ETLStage stage : config.getSinks()) {
      stageNamesFromConfig.add(stage.getName());
    }

    for (Connection connection : config.getConnections()) {
      // check if this connections's from and to belong to actual stage
      Preconditions.checkState(
        stageNamesFromConfig.contains(connection.getFrom()),
        String.format("Connection from : %s does not belong to an actual stage name, please check the config",
                      connection.getFrom()));
      Preconditions.checkState(
        stageNamesFromConfig.contains(connection.getTo()),
        String.format("Connection to : %s does not belong to an actual stage name, please check the config",
                      connection.getFrom()));
      Preconditions.checkState(stageNamesFromConfig.contains(connection.getTo()));
      if (stageConnections.containsKey(connection.getFrom())) {
        stageConnections.get(connection.getFrom()).add(connection.getTo());
      } else {
        List<String> destinations = new ArrayList<>();
        destinations.add(connection.getTo());
        stageConnections.put(connection.getFrom(), destinations);
      }
    }

    Set<String> visited = new HashSet<>();
    visited.add(config.getSource().getName());
    Set<String> sinksFromConfig = new HashSet<>();
    for (ETLStage sink : config.getSinks()) {
      sinksFromConfig.add(sink.getName());
    }
    connectionsReachabilityValidation(stageConnections, config,
                                      config.getSource().getName(), visited, new HashSet<String>(), sinksFromConfig);
    return stageConnections;
  }

  private static void connectionsReachabilityValidation(
    Map<String, List<String>> mapStageToConnections, ETLConfig config,
    String stageName, Set<String> visited, Set<String> sinksVisited, Set<String> sinksFromConfig) {

    if (mapStageToConnections.get(stageName) == null) {
      // check if this stage is a sink, if its not a sink, throw an exception.
      if (!sinksFromConfig.contains(stageName)) {
        throw new IllegalArgumentException(
          String.format(
            "Stage : %s is not connected to any transform or sink, please check the config", stageName));
      }
      // this is a sink, add it to sinks visited set
      sinksVisited.add(stageName);
      return;
    }

    for (String nextConnection : mapStageToConnections.get(stageName)) {
      if (visited.contains(nextConnection)) {
        // already been visited, cycle exists, throw exception
        throw new IllegalArgumentException(
          String.format(
            "Connection %s --> %s causes a cycle, Graph has to be a DAG, " +
              "please check the graph connections", stageName, nextConnection));
      }
      HashSet<String> nextVisited = new HashSet<>(visited);
      nextVisited.add(nextConnection);
      connectionsReachabilityValidation(mapStageToConnections, config, nextConnection, nextVisited,
                                        sinksVisited, sinksFromConfig);
    }
    // check if we have visited all the sinks, check this only when we are at source iteration level
    if (config.getSource().getName().equals(stageName)) {
      for (ETLStage sink : config.getSinks()) {
        if (!sinksVisited.contains(sink.getName())) {
          // if the sink hasn't been visited, throw exception
          throw new IllegalArgumentException(
            String.format("Sink %s is not connected, please check the connections", sink.getName()));
        }
      }
    }
  }

  @VisibleForTesting
  static void validateStageNames(ETLStage sourceConfig, List<ETLStage> transforms, List<ETLStage> sinks) {
    Set<String> uniqueStageNames = new HashSet<>();
    uniqueStageNames.add(sourceConfig.getName());
    validateUniqueStageName(uniqueStageNames, transforms);
    validateUniqueStageName(uniqueStageNames, sinks);
  }


  private static void validateUniqueStageName(Set<String> uniqueStageNames, List<ETLStage> stages) {
    for (ETLStage stage : stages) {
      boolean isUniqueStageName = uniqueStageNames.add(stage.getName());
      if (!isUniqueStageName) {
        throw new IllegalArgumentException(
          String.format("Stage name : %s is not unique, its used for more than one stage in the pipeline, " +
                          "check the pipeline config",
                        stage.getName()));
      }
    }
  }

  private PluginProperties getPluginProperties(ETLStage config) {
    PluginProperties.Builder builder = PluginProperties.builder();
    if (config.getPlugin().getProperties() != null) {
      builder.addAll(config.getPlugin().getProperties());
    }
    return builder.build();
  }

  public static void validateStages(PipelineConfigurable source, List<PipelineConfigurable> sinks,
                                    List<Transformation> transforms) throws Exception {
    ArrayList<Type> unresTypeList = Lists.newArrayListWithCapacity(transforms.size() + 2);
    Type inType = Transformation.class.getTypeParameters()[0];
    Type outType = Transformation.class.getTypeParameters()[1];

    // Load the classes using the class names provided
    Class<?> sourceClass = source.getClass();
    TypeToken sourceToken = TypeToken.of(sourceClass);

    // Extract the source's output type
    if (RealtimeSource.class.isAssignableFrom(sourceClass)) {
      Type type = RealtimeSource.class.getTypeParameters()[0];
      unresTypeList.add(sourceToken.resolveType(type).getType());
    } else {
      unresTypeList.add(sourceToken.resolveType(outType).getType());
    }

    // Extract the transforms' input and output type
    for (Transformation transform : transforms) {
      Class<?> klass = transform.getClass();
      TypeToken transformToken = TypeToken.of(klass);
      unresTypeList.add(transformToken.resolveType(inType).getType());
      unresTypeList.add(transformToken.resolveType(outType).getType());
    }

    // Extract the sink's input type
    for (PipelineConfigurable sink : sinks) {
      Class<?> sinkClass = sink.getClass();
      TypeToken sinkToken = TypeToken.of(sinkClass);
      // some inefficiency if there are multiple sinks since the source and transform types will be re-validated
      // each time. this only happens when the app is created though, and logic is easier to follow if its
      // always one stage followed by another, rather than having a fork at the very end.
      List<Type> pipelineTypes = Lists.newArrayList(unresTypeList);
      if (RealtimeSink.class.isAssignableFrom(sinkClass)) {
        Type type = RealtimeSink.class.getTypeParameters()[0];
        pipelineTypes.add(sinkToken.resolveType(type).getType());
      } else {
        pipelineTypes.add(sinkToken.resolveType(inType).getType());
      }
      // Invoke validation method with list of unresolved types
      validateTypes(pipelineTypes);
    }

  }

  /**
   * Takes in an unresolved type list and resolves the types and verifies if the types are assignable.
   * Ex: An unresolved type could be : String, T, List<T>, List<String>
   *     The above will resolve to   : String, String, List<String>, List<String>
   *     And the assignability will be checked : String --> String && List<String> --> List<String>
   *     which is true in the case above.
   */
  @VisibleForTesting
  static void validateTypes(List<Type> unresTypeList) {
    Preconditions.checkArgument(unresTypeList.size() % 2 == 0, "ETL Stages validation expects even number of types");
    List<Type> resTypeList = Lists.newArrayListWithCapacity(unresTypeList.size());

    // Add the source output to resolved type list as the first resolved type.
    resTypeList.add(unresTypeList.get(0));
    try {
      // Resolve the second type using just the first resolved type.
      Type nType = (new TypeResolver()).where(unresTypeList.get(1), resTypeList.get(0)).resolveType(
        unresTypeList.get(1));
      resTypeList.add(nType);
    } catch (IllegalArgumentException e) {
      // If unable to resolve type, add the second type as is, to the resolved list.
      resTypeList.add(unresTypeList.get(1));
    }

    for (int i = 2; i < unresTypeList.size(); i++) {
      // ActualType is previous resolved type; FormalType is previous unresolved type;
      // ToResolveType is current unresolved type;
      // Ex: Actual = String; Formal = T; ToResolve = List<T>;  ==> newType = List<String>
      Type actualType = resTypeList.get(i - 1);
      Type formalType = unresTypeList.get(i - 1);
      Type toResolveType = unresTypeList.get(i);
      try {
        Type newType;
        // If the toResolveType is a TypeVariable or a Generic Array, then try to resolve
        // using just the previous resolved type.
        // Ex: Actual = List<String> ; Formal = List<T> ; ToResolve = T ==> newType = String which is not correct;
        // newType should be List<String>. Hence resolve only from the previous resolved type (Actual)
        if ((toResolveType instanceof TypeVariable) || (toResolveType instanceof GenericArrayType)) {
          newType = (new TypeResolver()).where(toResolveType, actualType).resolveType(toResolveType);
        } else {
          newType = (new TypeResolver()).where(formalType, actualType).resolveType(toResolveType);
        }
        resTypeList.add(newType);
      } catch (IllegalArgumentException e) {
        // If resolution failed, add the type as is to the resolved list.
        resTypeList.add(toResolveType);
      }
    }

    // Check isAssignable on the resolved list for every paired elements. 0 --> 1 | 2 --> 3 | 4 --> 5 where | is a
    // transform (which takes in type on its left and emits the type on its right).
    for (int i = 0; i < resTypeList.size(); i += 2) {
      Type firstType = resTypeList.get(i);
      Type secondType = resTypeList.get(i + 1);
      // Check if secondType can accept firstType
      Preconditions.checkArgument(TypeToken.of(secondType).isAssignableFrom(firstType),
                                  "Types between stages didn't match. Mismatch between {} -> {}",
                                  firstType, secondType);
    }
  }
}
