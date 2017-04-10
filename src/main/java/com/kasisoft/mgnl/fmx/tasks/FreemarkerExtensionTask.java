package com.kasisoft.mgnl.fmx.tasks;

import info.magnolia.module.delta.*;

import com.kasisoft.libs.fmx.*;
import com.kasisoft.mgnl.fmx.freemarker.*;
import com.kasisoft.mgnl.versionhandler.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FreemarkerExtensionTask extends JcrConfigurationTask {

  private static final String PATH_FMX = "server/rendering/freemarker/templateLoaders/fmx";

  public FreemarkerExtensionTask() {
    super( "Freemarker extension", "Installing freemarker extension" );
    register( extendedConfig  () );
    register( ftxLoader       () );
    register( sharedVariables () );
    register( new OrderNodeToFirstPositionTask( "Positioning fmx loader", PATH_FMX ) );
  }
  
  private TreeBuilder extendedConfig() {
    return new TreeBuilder()
      .sContentNode( "server/rendering/freemarker" )
        .clazz( ExtendedFreemarkerConfig.class )
      .sEnd()
      ;
  }
  
  private TreeBuilder ftxLoader() {
    return new TreeBuilder()
      .sContentNode( PATH_FMX )
        .clazz( FmxTemplateLoader.class )
      .sEnd()
      ;
  }

  private TreeBuilder sharedVariables() {
    return new TreeBuilder()
      .sContentNode( "server/rendering/freemarker/sharedVariables" )
      .sEnd()
      ;
  }
  
} /* ENDCLASS */
