package com.kasisoft.mgnl.fmx.internal.tasks;

import info.magnolia.module.delta.*;

import info.magnolia.module.*;

import info.magnolia.jcr.util.*;

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
public class GrantModuleTask extends AbstractRepositoryTask {
  
  String modulePath;

  public GrantModuleTask(String moduleName) {
    super(String.format("Granting Module: %s", moduleName), String.format("Makes sure that the basic config structure for '%s' is properly setup", moduleName));
    modulePath = "/modules/" + moduleName;
  }

  @Override
  protected void doExecute(InstallContext ctx) throws RepositoryException, TaskExecutionException {
    Node module = SessionUtil.getNode(ctx.getConfigJCRSession(), modulePath);
    if (module == null) {
      module = NodeUtil.createPath(ctx.getConfigJCRSession().getRootNode(), modulePath, NodeTypes.Content.NAME);
    }
  }
  
} /* ENDCLASS */
