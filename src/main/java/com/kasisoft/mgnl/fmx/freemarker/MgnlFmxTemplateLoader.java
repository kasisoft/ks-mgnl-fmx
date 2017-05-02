package com.kasisoft.mgnl.fmx.freemarker;

import static com.kasisoft.mgnl.fmx.internal.Messages.*;

import com.kasisoft.libs.fmx.*;

import java.io.*;

import lombok.extern.slf4j.*;

import lombok.experimental.*;

import lombok.*;

import freemarker.cache.*;
import info.magnolia.freemarker.*;
import info.magnolia.objectfactory.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class MgnlFmxTemplateLoader extends FmxTemplateLoader {

  public MgnlFmxTemplateLoader() {
    super( new DefaultLoader(), null, MgnlFmxTemplateLoader::transformDirective );
  }
  
  private static String transformDirective( String name ) {
    return name.replace( '-', '.' );
  }
  
  private static class DefaultLoader implements TemplateLoader {

    TemplateLoader    loader;
    
    private synchronized TemplateLoader loader() {
      if( loader == null ) {
        FreemarkerConfig config = Components.getComponent( FreemarkerConfig.class );
        for( TemplateLoader l : config.getTemplateLoaders() ) {
          if( ! (l instanceof MgnlFmxTemplateLoader) ) {
            if( loader == null ) {
              loader = l;
            } else {
              log.error( error_conflicting_loaders.format( loader.getClass().getName(), l.getClass().getName() ) );
            }
          }
        }
      }
      return loader;
    }
    
    @Override
    public Object findTemplateSource( String name ) throws IOException {
      TemplateLoader loader = loader();
      Object         result = null;
      if( loader != null ) {
        result = loader.findTemplateSource( name );
      }
      return result;
    }

    @Override
    public long getLastModified( Object templateSource ) {
      TemplateLoader loader = loader();
      long           result = 0L;
      if( loader != null ) {
        result = loader.getLastModified( templateSource );
      }
      return result;
    }

    @Override
    public Reader getReader( Object templateSource, String encoding ) throws IOException {
      TemplateLoader loader = loader();
      Reader         result = null;
      if( loader != null ) {
        result = loader.getReader( templateSource, encoding );
      }
      return result;
    }

    @Override
    public void closeTemplateSource( Object templateSource ) throws IOException {
      TemplateLoader loader = loader();
      if( loader != null ) {
        loader.closeTemplateSource( templateSource );
      }
    }
    
  } /* ENDCLASS */
  
} /* ENDCLASS */
