package com.kasisoft.mgnl.fmx.tasks;

import com.kasisoft.mgnl.fmx.freemarker.*;
import com.kasisoft.mgnl.versionhandler.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FreemarkerExtensionTask extends JcrConfigurationTask {

  public FreemarkerExtensionTask() {
    super( "Freemarker extension", "Installing freemarker extension" );
    register( extendedConfig () );
    register( ftxLoader      () );
  }
  
  private TreeBuilder extendedConfig() {
    return new TreeBuilder()
      .sNode( "server/rendering/freemarker" )
        .clazz( ExtendedFreemarkerConfig.class )
      .sEnd()
      ;
  }
  
  private TreeBuilder ftxLoader() {
    return new TreeBuilder()
      .sNode( "server/rendering/freemarker/templateLoaders/ftx" )
        .clazz( FmxResourceLoader.class )
      .sEnd()
      ;
  }
  
} /* ENDCLASS */
