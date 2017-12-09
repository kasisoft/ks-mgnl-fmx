package com.kasisoft.mgnl.fmx.internal;

import com.kasisoft.mgnl.fmx.freemarker.*;
import com.kasisoft.mgnl.fmx.tasks.*;
import com.kasisoft.mgnl.versionhandler.*;
import com.kasisoft.mgnl.versionhandler.tasks.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FmxVersionHandler extends KsModuleVersionHandler {

  public FmxVersionHandler() {
    register( 1, new FreemarkerExtension() );
    register( 2, new KsSetPropertyTask( "/modules/rendering/renderers/freemarker/contextAttributes/fmx@componentClass", FmxMgnlDirectives.class.getName() ) );
    register( 3, new KsSetPropertyTask( "/modules/rendering/renderers/freemarker/contextAttributes/fmx@name", "fmx" ) );
  }

} /* ENDCLASS */
