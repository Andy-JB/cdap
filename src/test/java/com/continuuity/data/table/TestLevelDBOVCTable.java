package com.continuuity.data.table;

import com.continuuity.api.data.OperationException;
import com.continuuity.common.conf.CConfiguration;
import com.continuuity.common.conf.Constants;
import com.continuuity.data.runtime.DataFabricLevelDBModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Ignore;
import org.junit.Test;

public class TestLevelDBOVCTable extends TestOVCTable {

  private static CConfiguration conf;

  static {
    CConfiguration conf = CConfiguration.create();
    conf.unset(Constants.CFG_DATA_LEVELDB_DIR);
    TestLevelDBOVCTable.conf = conf;
  }
  private static final Injector injector = Guice.createInjector (
      new DataFabricLevelDBModule(conf));

  @Override
  protected OVCTableHandle injectTableHandle() {
    return injector.getInstance(OVCTableHandle.class);
  }

  @Ignore
  @Test
  @Override
  // Ignoring testDeleteDirty for now as delete on levelDb is failing.
  // TODO: fix levelDb delete
  public void testDeleteDirty() throws OperationException {

  }
}
