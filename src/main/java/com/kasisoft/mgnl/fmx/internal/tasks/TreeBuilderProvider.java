package com.kasisoft.mgnl.fmx.internal.tasks;

import info.magnolia.module.delta.*;

import javax.annotation.*;

import java.util.*;

/**
 * Transferred from my old 'ks-mgnl-versionhandler' module which isn't active yet and needs to get some updates.
 * That's why this code is currently in progress as it's just a quick and dirty update.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
public interface TreeBuilderProvider {

  /**
   * Returns the title to be used while generating the tree structure.
   * 
   * @return   The title to be used while generating the tree structure.
   */
  @Nonnull
  default String getTitle() {
    return getClass().getSimpleName();
  }
  
  /**
   * Returns the description to be used while generating the tree structure.
   * 
   * @return   The description to be used while generating the tree structure.
   */
  @Nonnull
  String getDescription();
  
  /**
   * Returns the builder to generate the tree structure. Each implementor is supposed to raise an exception in
   * case of an issue (f.e. misconfiguration).
   * 
   * @return   The builder to generate the tree structure.
   */
  @Nonnull
  TreeBuilder create();

  /**
   * Returns a list of tasks that will be executed after the jcr structure had been configured (mostly for
   * ordering purposes).
   * 
   * @return   A list of tasks that will be executed after the jcr structure had been configured.
   */
  @Nonnull
  default List<Task> postExecute() {
    return Collections.emptyList();
  }

  
} /* ENDINTERFACE */
