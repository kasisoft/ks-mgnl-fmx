package com.kasisoft.mgnl.fmx.internal;

import info.magnolia.module.model.*;

import info.magnolia.module.delta.*;

import info.magnolia.module.*;

import info.magnolia.jcr.util.*;

import com.kasisoft.mgnl.fmx.internal.tasks.*;

import org.apache.commons.lang3.*;

import javax.annotation.*;
import javax.jcr.*;

import java.util.*;

import lombok.experimental.*;

import lombok.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FmxVersionHandler implements ModuleVersionHandler {
  
  private static final String PN_VERSION    = "version";
  
  private static final String FMT_MODULES   = "/modules/%s";
  
  private IllegalStateException wrap(Exception ex) {
    if (ex instanceof IllegalStateException) {
      return (IllegalStateException) ex;
    } else {
      return new IllegalStateException(ex);
    }
  }
  
  @Override 
  @Nonnull
  public final List<Delta> getDeltas( @Nonnull InstallContext ctx, @Nonnull Version from ) {
    try { 
      return getDeltasImpl(ctx, from);
    } catch (Exception ex) {
      throw wrap(ex);
    }
  }

  private List<Delta> getDeltasImpl(InstallContext ctx, Version from) throws Exception {
    
    List<Delta> result        = new ArrayList<>(3);
    Version     version       = ctx.getCurrentModuleDefinition().getVersion();
    String      moduleName    = ctx.getCurrentModuleDefinition().getName();
    boolean     installation  = from == Version.UNDEFINED_FROM;
    
    if (installation) {
      // in case of an installation we might need to setup the basic module structure
      result.add(db(from, version, moduleName).addTask(new GrantModuleTask(moduleName)));
    }
    
    result.add(db(from, version, moduleName).addTask(new JcrConfigurationTask(new FreemarkerExtension())));
    
    result.add(db(from, version, moduleName).addTask(new KsSetPropertyTask(String.format("/modules/%s@%s", moduleName, PN_VERSION), version.toString())));
    
    return result;
    
  }
  
  private DeltaBuilder db( Version current, Version toVersion, String moduleName) {
    if (current == Version.UNDEFINED_FROM) {
      return DeltaBuilder.install(toVersion, String.format("Installing module '%s' with version '%s'", moduleName, toVersion));
    } else {
      return DeltaBuilder.update(toVersion, String.format("Updating module '%s' to version '%s' (from '%s')", moduleName, toVersion, current));
    }
  }
  
  @Override 
  @Nonnull 
  public final Version getCurrentlyInstalled( @Nonnull InstallContext ctx ) {
    
    Version result = Version.UNDEFINED_FROM;
    
    try {
      
      // log.debug( msg_testing_version.format( ctx.getCurrentModuleDefinition().getName() ) );

      Node module = SessionUtil.getNode( ctx.getConfigJCRSession(), getModulePath( ctx ) );
      if( module != null ) {

        String version = StringUtils.trimToNull( PropertyUtil.getString( module, PN_VERSION ) );
        if( version != null ) {
          result = Version.parseVersion( version );
        }
        
      }
      
    } catch( Exception ex ) {
      throw wrap(ex);
    }
    
    return result;
    
  }

  @SuppressWarnings("deprecation")
  @Override
  public Delta getStartupDelta(@Nonnull InstallContext ctx) {
    return DeltaBuilder.startup(ctx.getCurrentModuleDefinition(), Collections.emptyList());
  }

  private static String getModulePath(InstallContext ctx) {
    return String.format(FMT_MODULES, ctx.getCurrentModuleDefinition().getName());
  }
  
} /* ENDCLASS */
