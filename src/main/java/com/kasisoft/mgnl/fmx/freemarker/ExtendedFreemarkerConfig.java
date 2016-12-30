package com.kasisoft.mgnl.fmx.freemarker;

import com.kasisoft.libs.common.spi.*;

import org.slf4j.*;

import javax.annotation.*;

import java.util.*;

import freemarker.template.*;
import info.magnolia.freemarker.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class ExtendedFreemarkerConfig extends FreemarkerConfig {

  private static final Logger log = LoggerFactory.getLogger( ExtendedFreemarkerConfig.class );
  
  private Map<String, TemplateDirectiveModel>   directives = Collections.emptyMap();
  
  @PostConstruct
  public void init() {
    SPILoader<FreemarkerDirective> loader = SPILoader.<FreemarkerDirective>builder()
      .serviceType( FreemarkerDirective.class )
      .build();
    List<FreemarkerDirective> records = loader.loadServices();
    if( ! records.isEmpty() ) {
      directives = new HashMap<>();
      records.stream().forEach( $ -> directives.put( $.getName(), $.getTemplateModel() ) );
      directives.forEach( (n, m) -> log.debug( "directive: {} -> {}", n, m ) );
    }
  }

  @Override
  public void setSharedVariables( @Nonnull Map<String, Object> configuredSharedVariables ) throws TemplateModelException {
    super.setSharedVariables( configuredSharedVariables );
    getSharedVariables().putAll( directives );
  }

} /* ENDCLASS */
