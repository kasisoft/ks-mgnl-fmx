package com.kasisoft.mgnl.fmx.internal.tasks;

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
public class NodeProducer {

  Session   session;
  
  public NodeProducer( Session jcrSession ) {
    session = jcrSession;
  }
  
  private IllegalStateException wrap(Exception ex) {
    if (ex instanceof IllegalStateException) {
      return (IllegalStateException) ex;
    } else {
      return new IllegalStateException(ex);
    }
  }
  
  public Node getRootNode() {
    try {
      return session.getRootNode();
    } catch( Exception ex ) {
      throw wrap( ex );
    }
  }
  
  public Node getChild( String parentPath, Node parent, String name, String nodeType, boolean fail ) {
    try {
      return getChildImpl( parentPath, parent, name, nodeType, fail );
    } catch( Exception ex ) {
      throw wrap( ex );
    }
  }

  private Node getChildImpl( String parentPath, Node parent, String name, String nodeType, boolean fail ) throws RepositoryException {
    Node result = null;
    if( parent.hasNode( name ) ) {
      result = parent.getNode( name );
      if( ! result.isNodeType( nodeType ) ) {
        if( parentPath.endsWith("/") ) {
          if( parentPath.length() == 1 ) {
            parentPath = "";
          } else {
            parentPath = parentPath.substring( 0, parentPath.length() - 1 );
          }
        }
        throw new IllegalStateException(String.format("Detected an invalid node type. Parent=%s, Name=%s. Current=%s, Expected=%s", parentPath, name, result.getPrimaryNodeType().getName(), nodeType));
      }
    } else {
      result = parent.addNode( name, nodeType );
    }
    return result;
  }
  
  public void setBasicProperty( Node node, String key, Object value ) {
    try {
      if( (value instanceof Character) || (value instanceof Byte) || (value instanceof Short) ) {
        value = String.valueOf( value );
      } else if( value instanceof Float ) {
        value = ((Float) value).doubleValue();
      }
      PropertyUtil.setProperty( node, key, value );
    } catch( Exception ex ) {
      throw wrap( ex );
    }
  }

} /* ENDCLASS */
