package com.kasisoft.mgnl.fmx.freemarker;

import static com.kasisoft.mgnl.fmx.internal.Messages.*;

import com.kasisoft.libs.common.spi.*;

import javax.annotation.*;

import java.util.*;

import lombok.extern.slf4j.*;

import lombok.experimental.*;

import lombok.*;

import freemarker.template.*;
import info.magnolia.freemarker.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExtendedFreemarkerConfig extends FreemarkerConfig {

  Map<String, TemplateDirectiveModel>   directives = Collections.emptyMap();
  
  @PostConstruct
  public void init() {
    SPILoader<FreemarkerDirective> loader = SPILoader.<FreemarkerDirective>builder()
      .serviceType( FreemarkerDirective.class )
      .build();
    List<FreemarkerDirective> records = loader.loadServices();
    if( ! records.isEmpty() ) {
      directives = new HashMap<>();
      records.stream().forEach( $ -> directives.put( $.getName(), $.getTemplateModel() ) );
      directives.forEach( (n, m) -> log.debug( debug_directive.format( n, m ) ) );
    }
  }

  @Override
  public void setSharedVariables( @Nonnull Map<String, Object> configuredSharedVariables ) throws TemplateModelException {
    super.setSharedVariables( configuredSharedVariables );
    getSharedVariables().putAll( directives );
  }

} /* ENDCLASS */
