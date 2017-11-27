package com.kasisoft.mgnl.fmx.tasks;

import static com.kasisoft.mgnl.fmx.internal.Messages.*;

import info.magnolia.module.delta.*;

import com.kasisoft.mgnl.fmx.freemarker.*;
import com.kasisoft.mgnl.versionhandler.*;

import java.util.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FreemarkerExtension implements TreeBuilderProvider {

  private static final String PATH_FMX = "server/rendering/freemarker/templateLoaders/fmx";

  @Override
  public String getDescription() {
    return install_freemarker_extension;
  }

  @Override
  public TreeBuilder create() {
    return new TreeBuilder()
      
        // use our modified config
      .sContentNode( "server/rendering/freemarker" )
        .clazz( ExtendedFreemarkerConfig.class )
      .sEnd()
      
      // add the fmx template loader
      .sContentNode( PATH_FMX )
        .clazz( MgnlFmxTemplateLoader.class )
        .property( "debug", false )
      .sEnd()
      
      // setup a node for shared variables
      .sContentNode( "server/rendering/freemarker/sharedVariables" )
      .sEnd()
      
      ;
  }

  @Override
  public List<Task> postExecute() {
    // we need to position our loader at the beginning so it always comes first
    return Arrays.asList( new OrderNodeToFirstPositionTask( install_positioning_loader, PATH_FMX ) );
  }
  
} /* ENDCLASS */
