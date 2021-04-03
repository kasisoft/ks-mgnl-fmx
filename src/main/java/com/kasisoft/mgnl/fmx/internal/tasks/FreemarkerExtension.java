package com.kasisoft.mgnl.fmx.internal.tasks;

import static com.kasisoft.mgnl.fmx.internal.Messages.*;

import info.magnolia.module.delta.*;

import com.kasisoft.mgnl.fmx.freemarker.*;

import java.util.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FreemarkerExtension implements TreeBuilderProvider {

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
      .sContentNode( "server/rendering/freemarker/templateLoaders/fmx" )
        .clazz( MgnlFmxTemplateLoader.class )
        .property( "debug", false )
      .sEnd()
      
      // setup a node for shared variables
      .sContentNode( "server/rendering/freemarker/sharedVariables" )
      .sEnd()
      
      .sContentNode( "modules/rendering/renderers/freemarker/contextAttributes/fmx" )
        .property( "componentClass", FmxMgnlDirectives.class.getName() )
        .property( "name", "fmx" )
      .sEnd()
      
      ;
  }

  @Override
  public List<Task> postExecute() {
    // we need to position our loader at the beginning so it always comes first
    return Arrays.asList( new OrderNodeToFirstPositionTask( install_positioning_loader, "server/rendering/freemarker/templateLoaders/fmx" ) );
  }
  
} /* ENDCLASS */
