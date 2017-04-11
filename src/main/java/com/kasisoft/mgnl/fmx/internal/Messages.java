package com.kasisoft.mgnl.fmx.internal;

import com.kasisoft.libs.common.i18n.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class Messages {

  @I18N("directive: %s -> %s")
  public static I18NFormatter     debug_directive;
  
  @I18N("installing freemarker fmx extension")
  public static String            install_freemarker_extension;

  @I18N("the fmx template loader will be positioned as the first one")
  public static String            install_positioning_loader;
  
  static {
    I18NSupport.initialize( Messages.class );
  }

} /* ENDCLASS */
