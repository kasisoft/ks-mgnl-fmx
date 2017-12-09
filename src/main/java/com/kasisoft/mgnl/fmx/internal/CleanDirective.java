package com.kasisoft.mgnl.fmx.internal;

import com.kasisoft.libs.common.util.*;

import java.util.*;

import java.io.*;

import freemarker.core.*;
import freemarker.template.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class CleanDirective  implements TemplateDirectiveModel {

  private static final Bucket<StringWriter> WRITERS = new Bucket<>( BucketFactories.newStringWriterFactory() );
  
  private boolean isTrim( Map params ) throws TemplateException {
    TemplateBooleanModel model = (TemplateBooleanModel) params.get( "trim" );
    boolean result = false;
    if( model != null ) {
      result = model.getAsBoolean();
    }
    return result;
  }
  
  @Override
  public void execute( Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body ) throws TemplateException, IOException {
    StringWriter writer = null;
    try {
      writer = WRITERS.allocate();
      body.render( writer );
      String content = writer.toString().replaceAll( "([\n]\\s*[\n])", "\n" );
      if( isTrim( params ) ) {
        content = content.trim();
      }
      env.getOut().write( content );
    } finally {
      if( writer != null ) {
        WRITERS.free( writer );
      }
    }
  }

} /* ENDCLASS */
