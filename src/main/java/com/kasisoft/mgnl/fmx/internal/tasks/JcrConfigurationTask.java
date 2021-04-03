package com.kasisoft.mgnl.fmx.internal.tasks;

import info.magnolia.module.delta.*;

import info.magnolia.module.*;

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
public class JcrConfigurationTask extends AbstractRepositoryTask {

  TreeBuilderProvider   tbProvider;
  
  /**
   * Initializes this task for a certain {@link TreeBuilderProvider} instance.
   * 
   * @param tbProvider   The {@link TreeBuilderProvider} containing all necessary infos.
   */
  public JcrConfigurationTask(@Nonnull TreeBuilderProvider tbProvider) {
    super(tbProvider.getTitle(), tbProvider.getDescription());
    this.tbProvider = tbProvider;
  }

  @Override
  protected void doExecute(@Nonnull InstallContext ctx) throws RepositoryException, TaskExecutionException {
    TreeBuilder ntBuilder  = tbProvider.create();
    Session     jcrSession = ctx.getConfigJCRSession();
    ntBuilder.build(new NodeProducer(jcrSession));
    tbProvider.postExecute().forEach($ -> exe($, ctx));
    jcrSession.save();
  }

  private void exe(Task t, InstallContext ctx) {
    try {
      t.execute(ctx);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

} /* ENDCLASS */
