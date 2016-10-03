package com.kasisoft.mgnl.fmx;

import com.kasisoft.mgnl.fmx.tasks.*;
import com.kasisoft.mgnl.versionhandler.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FmxVersionHandler extends KsModuleVersionHandler {

  public FmxVersionHandler() {
    register( 1, new FreemarkerExtensionTask() );
  }
  
} /* ENDCLASS */
