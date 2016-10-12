package com.kasisoft.mgnl.fmx.freemarker;

import com.kasisoft.libs.common.spi.*;

import javax.annotation.*;

import java.util.*;

import lombok.experimental.*;

import lombok.*;

import freemarker.template.*;
import info.magnolia.freemarker.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
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
    }
  }

  @Override
  public void setSharedVariables( @Nonnull Map<String, Object> configuredSharedVariables ) throws TemplateModelException {
    super.setSharedVariables( configuredSharedVariables );
    getSharedVariables().putAll( directives );
  }

} /* ENDCLASS */
