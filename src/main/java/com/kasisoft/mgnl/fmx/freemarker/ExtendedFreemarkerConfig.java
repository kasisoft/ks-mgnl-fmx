package com.kasisoft.mgnl.fmx.freemarker;

import info.magnolia.freemarker.*;

import javax.validation.constraints.*;

import java.util.*;

import lombok.experimental.*;

import lombok.*;

import freemarker.template.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExtendedFreemarkerConfig extends FreemarkerConfig {

  Map<String, TemplateDirectiveModel>   directives = Collections.emptyMap();
  
  public void init() {
    /*
    SPILoader<FreemarkerDirective> loader = SPILoader.<FreemarkerDirective>builder()
      .serviceType( FreemarkerDirective.class )
      .build();
    List<FreemarkerDirective> records = loader.loadServices();
    if( ! records.isEmpty() ) {
      directives = new HashMap<>();
      records.stream().forEach( $ -> directives.put( $.getName(), $.getTemplateModel() ) );
      directives.forEach( (n, m) -> log.debug(String.format( debug_directive, n, m ) ) );
    }
    */
  }

  @Override
  public void setSharedVariables(@NotNull Map<String, Object> configuredSharedVariables) throws TemplateModelException {
    super.setSharedVariables(configuredSharedVariables);
    getSharedVariables().putAll(directives);
  }

} /* ENDCLASS */
