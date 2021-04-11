package com.kasisoft.mgnl.fmx.freemarker;

import static com.kasisoft.mgnl.fmx.internal.Messages.*;

import info.magnolia.freemarker.*;
import info.magnolia.objectfactory.*;

import com.kasisoft.libs.fmx.*;

import java.util.function.*;

import java.util.*;

import java.io.*;

import lombok.extern.log4j.*;

import lombok.experimental.*;

import lombok.*;

import freemarker.cache.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class MgnlFmxTemplateLoader extends FmxTemplateLoader {

  public MgnlFmxTemplateLoader() {
    super(new DefaultLoader(), null, MgnlFmxTemplateLoader::transformDirective, getAttributeMappers(), true);
  }
  
  private static Map<String, BiFunction<String, String, String>> getAttributeMappers() {
    Map<String, BiFunction<String, String, String>> result = new HashMap<>();
    result.put("cms.area", MgnlFmxTemplateLoader::mapCmsArea);
    return result;
  }
  
  private static String mapCmsArea(String attrLocalName, String value) {
    // we don't want to use inner quotes, so instead of <fmx:cms-area name="'main'"/> we can write
    // <fmx:cms-area name="main"/> which looks much better.
    return String.format("\"%s\"", value);
  }
  
  private static String transformDirective(String name) {
    return name.replace('-', '.');
  }
  
  private static class DefaultLoader implements TemplateLoader {

    TemplateLoader    loader;
    
    private synchronized TemplateLoader loader() {
      if (loader == null) {
        FreemarkerConfig config = Components.getComponent(FreemarkerConfig.class);
        for (TemplateLoader l : config.getTemplateLoaders()) {
          if (!(l instanceof MgnlFmxTemplateLoader)) {
            if (loader == null) {
              loader = l;
            } else {
              log.error(String.format(error_conflicting_loaders, loader.getClass().getName(), l.getClass().getName()));
            }
          }
        }
      }
      return loader;
    }
    
    @Override
    public Object findTemplateSource(String name) throws IOException {
      TemplateLoader loader = loader();
      Object         result = null;
      if (loader != null) {
        result = loader.findTemplateSource(name);
      }
      return result;
    }

    @Override
    public long getLastModified(Object templateSource) {
      TemplateLoader loader = loader();
      long           result = 0L;
      if (loader != null) {
        result = loader.getLastModified(templateSource);
      }
      return result;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
      TemplateLoader loader = loader();
      Reader         result = null;
      if (loader != null) {
        result = loader.getReader(templateSource, encoding);
      }
      return result;
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
      TemplateLoader loader = loader();
      if (loader != null) {
        loader.closeTemplateSource(templateSource);
      }
    }
    
  } /* ENDCLASS */
  
} /* ENDCLASS */
