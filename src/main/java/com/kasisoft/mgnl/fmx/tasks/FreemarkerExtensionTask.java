package com.kasisoft.mgnl.fmx.tasks;

import com.kasisoft.mgnl.fmx.freemarker.*;
import com.kasisoft.mgnl.versionhandler.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FreemarkerExtensionTask extends JcrConfigurationTask {

  public FreemarkerExtensionTask() {
    super( "Freemarker extension", "Installing freemarker extension" );
    register( extendedConfig  () );
    register( ftxLoader       () );
    register( sharedVariables () );
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
      .sNode( "server/rendering/freemarker/templateLoaders/fmx" )
        .clazz( FmxResourceLoader.class )
      .sEnd()
      ;
  }

  private TreeBuilder sharedVariables() {
    return new TreeBuilder()
      .sNode( "server/rendering/freemarker/sharedVariables" )
      .sEnd()
      ;
  }
  
} /* ENDCLASS */
