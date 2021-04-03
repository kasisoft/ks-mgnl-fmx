package com.kasisoft.mgnl.fmx.internal.tasks;

import info.magnolia.repository.*;

import info.magnolia.module.delta.*;

import info.magnolia.module.*;

import info.magnolia.jcr.util.*;

import javax.annotation.*;
import javax.jcr.*;

import lombok.experimental.*;

import lombok.*;

/**
 * Transferred from my old 'ks-mgnl-versionhandler' module which isn't active yet and needs to get some updates.
 * That's why this code is currently in progress as it's just a quick and dirty update.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KsSetPropertyTask extends AbstractTask {
  
  String    propertyPath;
  String    value;

  public KsSetPropertyTask(@Nonnull String path, @Nonnull String propValue) {
    super(KsSetPropertyTask.class.getSimpleName(), String.format("Setting property '%s' to '%s'", path, propValue));
    propertyPath  = path;
    value         = propValue;
  }
  
  @Override
  public void execute(@Nonnull InstallContext ctx) throws TaskExecutionException {
    try {
      executeImpl( ctx );
    } catch (Exception ex) {
      throw new TaskExecutionException( ex.getLocalizedMessage(), ex );
    }
  }
  
  private void verifyPropertyPath() throws Exception {
    if (propertyPath.charAt(0) != '/') {
      throw new TaskExecutionException(String.format("Invalid property path '%s'. Must start with a '/' character", propertyPath));
    }
    int idx = propertyPath.indexOf('@');
    if( (idx < 2) || (idx == propertyPath.length() - 1) ) {
      throw new TaskExecutionException(String.format("Invalid property path '%s'. There must be a segment before the '@' character and the name after it"));
    }
  }
  
  
  private void executeImpl(InstallContext ctx) throws Exception {
    
    verifyPropertyPath();
    
    if (propertyPath.charAt(0) != '/') {
      throw new TaskExecutionException(String.format("Invalid property path '%s'. Must start with a '/' character", propertyPath));
    }
    
    int    idx    = propertyPath.indexOf('@');
    
    String path   = propertyPath.substring(0, idx);
    String name   = propertyPath.substring(idx + 1);
    
    Node   node   = SessionUtil.getNode(RepositoryConstants.CONFIG, path);
    if (node == null) {
      node = ctx.getConfigJCRSession().getRootNode().addNode(path.substring(1));
    }
    
    PropertyUtil.setProperty(node, name, value);
    
  }
  
} /* ENDCLASS */
