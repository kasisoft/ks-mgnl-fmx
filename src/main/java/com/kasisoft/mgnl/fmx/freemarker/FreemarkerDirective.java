package com.kasisoft.mgnl.fmx.freemarker;

import javax.validation.constraints.*;

import freemarker.template.*;

/**
 * Directive implementation for Freemarker which will be loaded through SPI.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
public interface FreemarkerDirective {

  /**
   * Returns the name for this directive.
   * 
   * @return   The name for this directive. Neither <code>null</code> nor empty.
   */
  @NotNull
  String getName();
  
  /**
   * Returns the {@link TemplateModel} instance used to render some content.
   * 
   * @return   The {@link TemplateModel} instance used to render some content. Not <code>null</code>.
   */
  @NotNull
  TemplateDirectiveModel getTemplateModel();
  
} /* ENDINTERFACE */
