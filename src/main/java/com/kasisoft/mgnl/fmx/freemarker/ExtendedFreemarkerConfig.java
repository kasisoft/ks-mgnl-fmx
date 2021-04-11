package com.kasisoft.mgnl.fmx.freemarker;

import static com.kasisoft.mgnl.fmx.internal.Messages.*;

import info.magnolia.freemarker.*;

import javax.validation.constraints.*;

import java.util.*;

import lombok.extern.log4j.*;

import lombok.experimental.*;

import lombok.*;

import freemarker.template.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExtendedFreemarkerConfig extends FreemarkerConfig {

  Map<String, TemplateDirectiveModel>   directives;
  
  public ExtendedFreemarkerConfig() {
    directives                                       = new HashMap<>();
    ServiceLoader<FreemarkerDirective> serviceLoader = ServiceLoader.load(FreemarkerDirective.class);
    for (FreemarkerDirective fmd : serviceLoader) {
      directives.put(fmd.getName(), fmd.getTemplateModel());
      log.debug(String.format(msg_detected_directive, fmd.getName()));
    }
  }

  @Override
  public void setSharedVariables(@NotNull Map<String, Object> configuredSharedVariables) throws TemplateModelException {
    super.setSharedVariables(configuredSharedVariables);
    getSharedVariables().putAll(directives);
  }

} /* ENDCLASS */
