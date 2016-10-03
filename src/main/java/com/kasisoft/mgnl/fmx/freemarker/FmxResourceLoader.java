package com.kasisoft.mgnl.fmx.freemarker;

import javax.annotation.*;
import javax.inject.*;

import java.io.*;

import lombok.experimental.*;

import lombok.*;

import info.magnolia.freemarker.loaders.*;
import info.magnolia.resourceloader.*;
import info.magnolia.resourceloader.Resource;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FmxResourceLoader extends ResourceTemplateLoader {
  
  static final String SUFFIX = ".fmx";
  
  @Inject
  public FmxResourceLoader( @Nonnull ResourceOrigin origin ) {
    super( origin );
  }
  
  @Override
  public Object findTemplateSource( @Nonnull String name ) throws IOException {
    Object result = null;
    if( (name != null) && name.endsWith( SUFFIX ) ) {
      Resource resource = (Resource) super.findTemplateSource( name );
      if( resource != null ) {
        result = new FmxRecord( resource, loadTranslation( resource ) );
      }
    }
    return result;
  }

  @Override
  public long getLastModified( @Nonnull Object templateSource ) {
    long result = 0L;
    if( templateSource instanceof FmxRecord ) {
      FmxRecord fmxRecord = (FmxRecord) templateSource;
      result              = super.getLastModified( fmxRecord.resource );
    }
    return result;
  }

  @Override
  public Reader getReader( Object templateSource, String encoding ) throws IOException {
    Reader result = null;
    if( templateSource instanceof FmxRecord ) {
      FmxRecord fmxRecord = (FmxRecord) templateSource;
      result              = new StringReader( fmxRecord.translation );
    }
    return result;
  }

  @Override
  public void closeTemplateSource( Object templateSource ) throws IOException {
    if( templateSource instanceof FmxRecord ) {
      FmxRecord fmxRecord = (FmxRecord) templateSource;
      super.closeTemplateSource( fmxRecord.resource );
    }
  }
  
  private String loadTranslation( Resource resource ) throws IOException {
    try( Reader reader = resource.openReader() ) {
      return loadTranslation( reader );
    }
  }

  private String loadTranslation( Reader reader ) {
    return null;
  }
  
  @AllArgsConstructor
  private static class FmxRecord {
    
    Resource    resource;
    String      translation;
    
  } /* ENDCLASS */
  
} /* ENDCLASS */
