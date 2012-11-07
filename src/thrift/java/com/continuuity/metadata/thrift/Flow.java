/**
 * Autogenerated by Thrift Compiler (0.8.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.continuuity.metadata.thrift;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines a Flow
 */
public class Flow implements org.apache.thrift.TBase<Flow, Flow._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Flow");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField APPLICATION_FIELD_DESC = new org.apache.thrift.protocol.TField("application", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField STREAMS_FIELD_DESC = new org.apache.thrift.protocol.TField("streams", org.apache.thrift.protocol.TType.LIST, (short)4);
  private static final org.apache.thrift.protocol.TField DATASETS_FIELD_DESC = new org.apache.thrift.protocol.TField("datasets", org.apache.thrift.protocol.TType.LIST, (short)5);
  private static final org.apache.thrift.protocol.TField EXISTS_FIELD_DESC = new org.apache.thrift.protocol.TField("exists", org.apache.thrift.protocol.TType.BOOL, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new FlowStandardSchemeFactory());
    schemes.put(TupleScheme.class, new FlowTupleSchemeFactory());
  }

  private String id; // required
  private String application; // required
  private String name; // optional
  private List<String> streams; // optional
  private List<String> datasets; // optional
  private boolean exists; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "id"),
    APPLICATION((short)2, "application"),
    NAME((short)3, "name"),
    STREAMS((short)4, "streams"),
    DATASETS((short)5, "datasets"),
    EXISTS((short)6, "exists");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ID
          return ID;
        case 2: // APPLICATION
          return APPLICATION;
        case 3: // NAME
          return NAME;
        case 4: // STREAMS
          return STREAMS;
        case 5: // DATASETS
          return DATASETS;
        case 6: // EXISTS
          return EXISTS;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __EXISTS_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);
  private _Fields optionals[] = {_Fields.NAME,_Fields.STREAMS,_Fields.DATASETS,_Fields.EXISTS};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.APPLICATION, new org.apache.thrift.meta_data.FieldMetaData("application", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.STREAMS, new org.apache.thrift.meta_data.FieldMetaData("streams", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.DATASETS, new org.apache.thrift.meta_data.FieldMetaData("datasets", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.EXISTS, new org.apache.thrift.meta_data.FieldMetaData("exists", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Flow.class, metaDataMap);
  }

  public Flow() {
    this.exists = true;

  }

  public Flow(
    String id,
    String application)
  {
    this();
    this.id = id;
    this.application = application;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Flow(Flow other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetId()) {
      this.id = other.id;
    }
    if (other.isSetApplication()) {
      this.application = other.application;
    }
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetStreams()) {
      List<String> __this__streams = new ArrayList<String>();
      for (String other_element : other.streams) {
        __this__streams.add(other_element);
      }
      this.streams = __this__streams;
    }
    if (other.isSetDatasets()) {
      List<String> __this__datasets = new ArrayList<String>();
      for (String other_element : other.datasets) {
        __this__datasets.add(other_element);
      }
      this.datasets = __this__datasets;
    }
    this.exists = other.exists;
  }

  public Flow deepCopy() {
    return new Flow(this);
  }

  @Override
  public void clear() {
    this.id = null;
    this.application = null;
    this.name = null;
    this.streams = null;
    this.datasets = null;
    this.exists = true;

  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void unsetId() {
    this.id = null;
  }

  /** Returns true if field id is set (has been assigned a value) and false otherwise */
  public boolean isSetId() {
    return this.id != null;
  }

  public void setIdIsSet(boolean value) {
    if (!value) {
      this.id = null;
    }
  }

  public String getApplication() {
    return this.application;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public void unsetApplication() {
    this.application = null;
  }

  /** Returns true if field application is set (has been assigned a value) and false otherwise */
  public boolean isSetApplication() {
    return this.application != null;
  }

  public void setApplicationIsSet(boolean value) {
    if (!value) {
      this.application = null;
    }
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public int getStreamsSize() {
    return (this.streams == null) ? 0 : this.streams.size();
  }

  public java.util.Iterator<String> getStreamsIterator() {
    return (this.streams == null) ? null : this.streams.iterator();
  }

  public void addToStreams(String elem) {
    if (this.streams == null) {
      this.streams = new ArrayList<String>();
    }
    this.streams.add(elem);
  }

  public List<String> getStreams() {
    return this.streams;
  }

  public void setStreams(List<String> streams) {
    this.streams = streams;
  }

  public void unsetStreams() {
    this.streams = null;
  }

  /** Returns true if field streams is set (has been assigned a value) and false otherwise */
  public boolean isSetStreams() {
    return this.streams != null;
  }

  public void setStreamsIsSet(boolean value) {
    if (!value) {
      this.streams = null;
    }
  }

  public int getDatasetsSize() {
    return (this.datasets == null) ? 0 : this.datasets.size();
  }

  public java.util.Iterator<String> getDatasetsIterator() {
    return (this.datasets == null) ? null : this.datasets.iterator();
  }

  public void addToDatasets(String elem) {
    if (this.datasets == null) {
      this.datasets = new ArrayList<String>();
    }
    this.datasets.add(elem);
  }

  public List<String> getDatasets() {
    return this.datasets;
  }

  public void setDatasets(List<String> datasets) {
    this.datasets = datasets;
  }

  public void unsetDatasets() {
    this.datasets = null;
  }

  /** Returns true if field datasets is set (has been assigned a value) and false otherwise */
  public boolean isSetDatasets() {
    return this.datasets != null;
  }

  public void setDatasetsIsSet(boolean value) {
    if (!value) {
      this.datasets = null;
    }
  }

  public boolean isExists() {
    return this.exists;
  }

  public void setExists(boolean exists) {
    this.exists = exists;
    setExistsIsSet(true);
  }

  public void unsetExists() {
    __isset_bit_vector.clear(__EXISTS_ISSET_ID);
  }

  /** Returns true if field exists is set (has been assigned a value) and false otherwise */
  public boolean isSetExists() {
    return __isset_bit_vector.get(__EXISTS_ISSET_ID);
  }

  public void setExistsIsSet(boolean value) {
    __isset_bit_vector.set(__EXISTS_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ID:
      if (value == null) {
        unsetId();
      } else {
        setId((String)value);
      }
      break;

    case APPLICATION:
      if (value == null) {
        unsetApplication();
      } else {
        setApplication((String)value);
      }
      break;

    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case STREAMS:
      if (value == null) {
        unsetStreams();
      } else {
        setStreams((List<String>)value);
      }
      break;

    case DATASETS:
      if (value == null) {
        unsetDatasets();
      } else {
        setDatasets((List<String>)value);
      }
      break;

    case EXISTS:
      if (value == null) {
        unsetExists();
      } else {
        setExists((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return getId();

    case APPLICATION:
      return getApplication();

    case NAME:
      return getName();

    case STREAMS:
      return getStreams();

    case DATASETS:
      return getDatasets();

    case EXISTS:
      return Boolean.valueOf(isExists());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ID:
      return isSetId();
    case APPLICATION:
      return isSetApplication();
    case NAME:
      return isSetName();
    case STREAMS:
      return isSetStreams();
    case DATASETS:
      return isSetDatasets();
    case EXISTS:
      return isSetExists();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Flow)
      return this.equals((Flow)that);
    return false;
  }

  public boolean equals(Flow that) {
    if (that == null)
      return false;

    boolean this_present_id = true && this.isSetId();
    boolean that_present_id = true && that.isSetId();
    if (this_present_id || that_present_id) {
      if (!(this_present_id && that_present_id))
        return false;
      if (!this.id.equals(that.id))
        return false;
    }

    boolean this_present_application = true && this.isSetApplication();
    boolean that_present_application = true && that.isSetApplication();
    if (this_present_application || that_present_application) {
      if (!(this_present_application && that_present_application))
        return false;
      if (!this.application.equals(that.application))
        return false;
    }

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_streams = true && this.isSetStreams();
    boolean that_present_streams = true && that.isSetStreams();
    if (this_present_streams || that_present_streams) {
      if (!(this_present_streams && that_present_streams))
        return false;
      if (!this.streams.equals(that.streams))
        return false;
    }

    boolean this_present_datasets = true && this.isSetDatasets();
    boolean that_present_datasets = true && that.isSetDatasets();
    if (this_present_datasets || that_present_datasets) {
      if (!(this_present_datasets && that_present_datasets))
        return false;
      if (!this.datasets.equals(that.datasets))
        return false;
    }

    boolean this_present_exists = true && this.isSetExists();
    boolean that_present_exists = true && that.isSetExists();
    if (this_present_exists || that_present_exists) {
      if (!(this_present_exists && that_present_exists))
        return false;
      if (this.exists != that.exists)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_id = true && (isSetId());
    builder.append(present_id);
    if (present_id)
      builder.append(id);

    boolean present_application = true && (isSetApplication());
    builder.append(present_application);
    if (present_application)
      builder.append(application);

    boolean present_name = true && (isSetName());
    builder.append(present_name);
    if (present_name)
      builder.append(name);

    boolean present_streams = true && (isSetStreams());
    builder.append(present_streams);
    if (present_streams)
      builder.append(streams);

    boolean present_datasets = true && (isSetDatasets());
    builder.append(present_datasets);
    if (present_datasets)
      builder.append(datasets);

    boolean present_exists = true && (isSetExists());
    builder.append(present_exists);
    if (present_exists)
      builder.append(exists);

    return builder.toHashCode();
  }

  public int compareTo(Flow other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    Flow typedOther = (Flow)other;

    lastComparison = Boolean.valueOf(isSetId()).compareTo(typedOther.isSetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id, typedOther.id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetApplication()).compareTo(typedOther.isSetApplication());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetApplication()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.application, typedOther.application);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetName()).compareTo(typedOther.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, typedOther.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStreams()).compareTo(typedOther.isSetStreams());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStreams()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.streams, typedOther.streams);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDatasets()).compareTo(typedOther.isSetDatasets());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDatasets()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.datasets, typedOther.datasets);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetExists()).compareTo(typedOther.isSetExists());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetExists()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.exists, typedOther.exists);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Flow(");
    boolean first = true;

    sb.append("id:");
    if (this.id == null) {
      sb.append("null");
    } else {
      sb.append(this.id);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("application:");
    if (this.application == null) {
      sb.append("null");
    } else {
      sb.append(this.application);
    }
    first = false;
    if (isSetName()) {
      if (!first) sb.append(", ");
      sb.append("name:");
      if (this.name == null) {
        sb.append("null");
      } else {
        sb.append(this.name);
      }
      first = false;
    }
    if (isSetStreams()) {
      if (!first) sb.append(", ");
      sb.append("streams:");
      if (this.streams == null) {
        sb.append("null");
      } else {
        sb.append(this.streams);
      }
      first = false;
    }
    if (isSetDatasets()) {
      if (!first) sb.append(", ");
      sb.append("datasets:");
      if (this.datasets == null) {
        sb.append("null");
      } else {
        sb.append(this.datasets);
      }
      first = false;
    }
    if (isSetExists()) {
      if (!first) sb.append(", ");
      sb.append("exists:");
      sb.append(this.exists);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'id' is unset! Struct:" + toString());
    }

    if (!isSetApplication()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'application' is unset! Struct:" + toString());
    }

  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bit_vector = new BitSet(1);
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class FlowStandardSchemeFactory implements SchemeFactory {
    public FlowStandardScheme getScheme() {
      return new FlowStandardScheme();
    }
  }

  private static class FlowStandardScheme extends StandardScheme<Flow> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Flow struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.id = iprot.readString();
              struct.setIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // APPLICATION
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.application = iprot.readString();
              struct.setApplicationIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // STREAMS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.streams = new ArrayList<String>(_list0.size);
                for (int _i1 = 0; _i1 < _list0.size; ++_i1)
                {
                  String _elem2; // required
                  _elem2 = iprot.readString();
                  struct.streams.add(_elem2);
                }
                iprot.readListEnd();
              }
              struct.setStreamsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // DATASETS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list3 = iprot.readListBegin();
                struct.datasets = new ArrayList<String>(_list3.size);
                for (int _i4 = 0; _i4 < _list3.size; ++_i4)
                {
                  String _elem5; // required
                  _elem5 = iprot.readString();
                  struct.datasets.add(_elem5);
                }
                iprot.readListEnd();
              }
              struct.setDatasetsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // EXISTS
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.exists = iprot.readBool();
              struct.setExistsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Flow struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.id != null) {
        oprot.writeFieldBegin(ID_FIELD_DESC);
        oprot.writeString(struct.id);
        oprot.writeFieldEnd();
      }
      if (struct.application != null) {
        oprot.writeFieldBegin(APPLICATION_FIELD_DESC);
        oprot.writeString(struct.application);
        oprot.writeFieldEnd();
      }
      if (struct.name != null) {
        if (struct.isSetName()) {
          oprot.writeFieldBegin(NAME_FIELD_DESC);
          oprot.writeString(struct.name);
          oprot.writeFieldEnd();
        }
      }
      if (struct.streams != null) {
        if (struct.isSetStreams()) {
          oprot.writeFieldBegin(STREAMS_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.streams.size()));
            for (String _iter6 : struct.streams)
            {
              oprot.writeString(_iter6);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.datasets != null) {
        if (struct.isSetDatasets()) {
          oprot.writeFieldBegin(DATASETS_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.datasets.size()));
            for (String _iter7 : struct.datasets)
            {
              oprot.writeString(_iter7);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetExists()) {
        oprot.writeFieldBegin(EXISTS_FIELD_DESC);
        oprot.writeBool(struct.exists);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class FlowTupleSchemeFactory implements SchemeFactory {
    public FlowTupleScheme getScheme() {
      return new FlowTupleScheme();
    }
  }

  private static class FlowTupleScheme extends TupleScheme<Flow> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Flow struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.id);
      oprot.writeString(struct.application);
      BitSet optionals = new BitSet();
      if (struct.isSetName()) {
        optionals.set(0);
      }
      if (struct.isSetStreams()) {
        optionals.set(1);
      }
      if (struct.isSetDatasets()) {
        optionals.set(2);
      }
      if (struct.isSetExists()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetName()) {
        oprot.writeString(struct.name);
      }
      if (struct.isSetStreams()) {
        {
          oprot.writeI32(struct.streams.size());
          for (String _iter8 : struct.streams)
          {
            oprot.writeString(_iter8);
          }
        }
      }
      if (struct.isSetDatasets()) {
        {
          oprot.writeI32(struct.datasets.size());
          for (String _iter9 : struct.datasets)
          {
            oprot.writeString(_iter9);
          }
        }
      }
      if (struct.isSetExists()) {
        oprot.writeBool(struct.exists);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Flow struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.id = iprot.readString();
      struct.setIdIsSet(true);
      struct.application = iprot.readString();
      struct.setApplicationIsSet(true);
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.name = iprot.readString();
        struct.setNameIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list10 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.streams = new ArrayList<String>(_list10.size);
          for (int _i11 = 0; _i11 < _list10.size; ++_i11)
          {
            String _elem12; // required
            _elem12 = iprot.readString();
            struct.streams.add(_elem12);
          }
        }
        struct.setStreamsIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list13 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.datasets = new ArrayList<String>(_list13.size);
          for (int _i14 = 0; _i14 < _list13.size; ++_i14)
          {
            String _elem15; // required
            _elem15 = iprot.readString();
            struct.datasets.add(_elem15);
          }
        }
        struct.setDatasetsIsSet(true);
      }
      if (incoming.get(3)) {
        struct.exists = iprot.readBool();
        struct.setExistsIsSet(true);
      }
    }
  }

}

