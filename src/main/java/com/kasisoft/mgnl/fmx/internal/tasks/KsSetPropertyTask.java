package com.kasisoft.mgnl.fmx.internal.tasks;

import static com.kasisoft.mgnl.fmx.internal.Messages.*;

import info.magnolia.repository.*;

import info.magnolia.module.delta.*;

import info.magnolia.module.*;

import info.magnolia.jcr.util.*;

import com.kasisoft.mgnl.fmx.internal.*;

import javax.jcr.*;
import javax.validation.constraints.*;

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
  String    workspace;
  String    defaultNodeType;

  public KsSetPropertyTask(@NotNull String path, @NotNull String propValue) {
    super(KsSetPropertyTask.class.getSimpleName(), String.format("Setting property '%s' to '%s'", path, propValue));
    workspace       = RepositoryConstants.CONFIG;
    defaultNodeType = NodeTypes.ContentNode.NAME;
    propertyPath    = path;
    value           = propValue;
  }
  
  public KsSetPropertyTask withWorkspace(@NotNull String workspace) {
    this.workspace = workspace;
    return this;
  }

  public KsSetPropertyTask withDefaultNodeType(@NotNull String defaultNodeType) {
    this.defaultNodeType = defaultNodeType;
    return this;
  }

  @Override
  public void execute(@NotNull InstallContext ctx) throws TaskExecutionException {
    try {
      executeImpl(ctx);
    } catch (TaskExecutionException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new TaskExecutionException(ex.getLocalizedMessage(), ex);
    }
  }
  
  private void verifyPath() throws Exception {
    
    if (propertyPath.charAt(0) != '/') {
      throw new TaskExecutionException(String.format(error_unexpected_relpath, propertyPath));
    }

    int idx = propertyPath.indexOf('@');
    if( (idx < 2) || (idx == propertyPath.length() - 1) ) {
      throw new TaskExecutionException(String.format(error_invalid_property_path, propertyPath));
    }
    
  }
  
  private void executeImpl(InstallContext ctx) throws Exception {
    
    verifyPath();
    
    int    idx    = propertyPath.indexOf('@');
    
    String path   = propertyPath.substring(0, idx);
    String name   = propertyPath.substring(idx + 1);
    
    Node   node   = MgnlFmxUtils.grantNode(MgnlFmxUtils.getSession(workspace), path, defaultNodeType);
    node.setProperty(name, value);
    
  }
  
} /* ENDCLASS */
