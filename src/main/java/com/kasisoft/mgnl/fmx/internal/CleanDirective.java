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
  
  @Override
  public void execute( Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body ) throws TemplateException, IOException {
    StringWriter writer = null;
    try {
      writer = WRITERS.allocate();
      body.render( writer );
      env.getOut().write( writer.toString().replaceAll( "([\n]\\s*[\n])", "\n" ) );
    } finally {
      if( writer != null ) {
        WRITERS.free( writer );
      }
    }
  }

} /* ENDCLASS */
