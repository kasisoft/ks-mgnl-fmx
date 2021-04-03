package com.kasisoft.mgnl.fmx.internal;

import info.magnolia.module.delta.*;

import info.magnolia.context.*;

import javax.jcr.*;
import javax.validation.constraints.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class MgnlFmxUtils {

  public static @NotNull Node grantNode(Session session, String path, String defaultNodeType) throws Exception {
    try {
      // maybe the node doesn't exist yet so create it
      String[] segments = path.substring(1).split("/");
      Node     result   = session.getRootNode();
      for (int i = 0; i < segments.length; i++) {
        String nodeName = segments[i];
        String nodeType = defaultNodeType;
        int    open     = nodeName.indexOf('{');
        if (open != -1) {
          int close = nodeName.indexOf('{', open);
          if ((close == -1) || (close == open + 1)) {
            throw new TaskExecutionException(String.format("Invalid path '%s' (segment: '%s')", path, segments[i]));
          }
          nodeType = nodeName.substring(open + 1, close);
          nodeName = nodeName.substring(0, open);
        }
        if (result.hasNode(nodeName)) {
          result = result.getNode(nodeName);
        } else {
          result = result.addNode(nodeName, nodeType);
        }
      }
      return result;
    } catch (Exception ex) {
      throw new TaskExecutionException(String.format("Failed to create node for path '%s' !", path), ex);
    }
  }
  
  public static @NotNull Session getSession(@NotNull String workspace) throws TaskExecutionException {
    try {
      return MgnlContext.getJCRSession(workspace);
    } catch (Exception ex) {
      throw new TaskExecutionException(String.format("Failed to access workspace '%s' !", workspace), ex);
    }
  }

} /* ENDCLASS */
