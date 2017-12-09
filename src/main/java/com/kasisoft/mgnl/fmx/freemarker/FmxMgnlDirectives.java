package com.kasisoft.mgnl.fmx.freemarker;

import com.kasisoft.mgnl.fmx.internal.*;

import java.util.*;

import freemarker.template.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FmxMgnlDirectives extends HashMap<String, TemplateDirectiveModel> {

  public FmxMgnlDirectives() {
    put( "clean", new CleanDirective() );
  }
  
} /* ENDCLASS */
