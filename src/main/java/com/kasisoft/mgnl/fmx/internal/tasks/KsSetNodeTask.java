package com.kasisoft.mgnl.fmx.internal.tasks;

import static com.kasisoft.mgnl.fmx.internal.Messages.*;

import info.magnolia.repository.*;

import info.magnolia.module.delta.*;

import info.magnolia.module.*;

import info.magnolia.jcr.util.*;

import com.kasisoft.mgnl.fmx.internal.*;

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
public class KsSetNodeTask extends AbstractTask {
  
  String    nodePath;
  String    workspace;
  String    defaultNodeType;
  
  public KsSetNodeTask(@NotNull String path) {
    super(KsSetNodeTask.class.getSimpleName(), String.format(label_setting_node, path));
    workspace       = RepositoryConstants.CONFIG;
    defaultNodeType = NodeTypes.ContentNode.NAME;
    nodePath        = path;
  }
  
  public KsSetNodeTask withWorkspace(@NotNull String workspace) {
    this.workspace = workspace;
    return this;
  }

  public KsSetNodeTask withDefaultNodeType(@NotNull String defaultNodeType) {
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
    if (nodePath.charAt(0) != '/') {
      throw new TaskExecutionException(String.format(error_unexpected_relpath, nodePath));
    }
  }
  
  private void executeImpl(InstallContext ctx) throws Exception {
    verifyPath();
    MgnlFmxUtils.grantNode(MgnlFmxUtils.getSession(workspace), nodePath, defaultNodeType);
  }
  
} /* ENDCLASS */
