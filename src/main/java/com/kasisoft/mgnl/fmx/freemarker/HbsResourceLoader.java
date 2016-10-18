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
public class HbsResourceLoader extends ResourceTemplateLoader {
  
  static final String SUFFIX = ".hbs";
  
  FreemarkerHbsTranslator   translator;
  
  @Inject
  public HbsResourceLoader( @Nonnull ResourceOrigin origin ) {
    super( origin );
    translator = new FreemarkerHbsTranslator();
  }
  
  @Override
  public Object findTemplateSource( @Nonnull String name ) throws IOException {
    FmxRecord result = null;
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
  public Reader getReader( @Nullable Object templateSource, @Nullable String encoding ) throws IOException {
    Reader result = null;
    if( templateSource instanceof FmxRecord ) {
      FmxRecord fmxRecord = (FmxRecord) templateSource;
      result              = new StringReader( fmxRecord.translation );
    }
    return result;
  }

  @Override
  public void closeTemplateSource( @Nullable Object templateSource ) throws IOException {
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
    String result = translator.convert( reader );
    return result;
  }
  
  @AllArgsConstructor
  private static class FmxRecord {
    
    Resource    resource;
    String      translation;
    
  } /* ENDCLASS */
  
} /* ENDCLASS */
