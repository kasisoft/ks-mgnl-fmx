package com.kasisoft.mgnl.fmx.internal.tasks;

import info.magnolia.jcr.util.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.builder.*;

import javax.annotation.*;
import javax.jcr.*;

import java.util.function.*;

import java.util.*;

import lombok.experimental.*;

import lombok.*;

/**
 * Transferred from my old 'ks-mgnl-versionhandler' module which isn't active yet and needs to get some updates.
 * That's why this code is currently in progress as it's just a quick and dirty update.
 * 
 * This convenience class allows to setup a tree in order to transform it for various purposes.
 * Be aware that each method prefixed with a lower case 's' opens a scope and thus requires to be closed
 * using an {@link #sEnd()}. The last sequence of {@link #sEnd()} doesn't need to be provided explicitly
 * as it's called automatically.
 * Be aware that misuse of the tree construction might cause an error you have to deal with.
 * 
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TreeBuilder {

  private static final String PN_CLASS                  = "class";
  private static final String PN_IMPLEMENTATION_CLASS   = "implementationClass";
  private static final String PN_ID                     = "id";
  private static final String PN_KEY                    = "key";
  private static final String PN_NAME                   = "name";
  
  NodeDescriptor          root;
  Stack<NodeDescriptor>   current;

  public TreeBuilder() {
    current         = new Stack<>();
    root            = newNodeDescriptor( "ROOT", NodeTypes.Content.NAME );
    current.push( root );
  }

  /**
   * Opens a toplevel node.
   *  
   * @param name   The name of the toplevel node.
   * 
   * @return   this
   */
  @Nonnull
  public TreeBuilder sNode( @Nonnull String name, @Nonnull String nodeType ) {
    if( name.startsWith("/") ) {
      name = name.substring(1);
    }
    NodeDescriptor descriptor = newNodeDescriptor( name, nodeType );
    if( current().subnodes.isEmpty() ) {
      current().subnodes = new ArrayList<>();
    }
    current().subnodes.add( descriptor );
    current.push( descriptor );
    return this;
  }

  private NodeDescriptor current() {
    return current.peek();
  }
  
  private NodeDescriptor newNodeDescriptor( String name, String nodeType ) {
    NodeDescriptor result = new NodeDescriptor();
    result.name           = name;
    result.properties     = Collections.emptyMap();
    result.subnodes       = Collections.emptyList();
    result.nodeType       = nodeType;
    return result;
  }

  /**
   * Opens a node on a sublevel with the node type <code>NodeTypes.Folder.NAME</code>.
   *  
   * @param name   The name of the sublevel node.
   * 
   * @return   this
   */
  @Nonnull
  public TreeBuilder sFolder( @Nonnull String name ) {
    return sNode( name, NodeTypes.Folder.NAME );
  }

  /**
   * Opens a node on a sublevel with the node type <code>NodeTypes.Content.NAME</code>.
   *  
   * @param name   The name of the sublevel node.
   * 
   * @return   this
   */
  @Nonnull
  public TreeBuilder sContent( @Nonnull String name ) {
    return sNode( name, NodeTypes.Content.NAME );
  }

  /**
   * Opens a node on a sublevel with the node type <code>NodeTypes.ContentNode.NAME</code>.
   *  
   * @param name   The name of the sublevel node.
   * 
   * @return   this
   */
  @Nonnull
  public TreeBuilder sContentNode( @Nonnull String name ) {
    return sNode( name, NodeTypes.ContentNode.NAME );
  }

  /**
   * Closes the current scope.
   * 
   * @return   this
   */
  @Nonnull
  public TreeBuilder sEnd() {
    current.pop();
    return this;
  }

  /**
   * Changes the nodetype for the current node.
   * 
   * @param nodetype   The new nodetype.
   * 
   * @return   this
   */
  @Nonnull
  private TreeBuilder nodetype( @Nonnull String nodetype ) {
    NodeDescriptor record = current();
    record.nodeType       = nodetype;
    return this;
  }
  
  /**
   * Like {@link #property(String, Object)} with the difference that this variety allows the argument to be
   * a formatting String.
   * 
   * @param name    The name of the property.  
   * @param fmt     The formatting string.
   * @param args    The arguments for the formatting string.
   * 
   * @return   this
   */
  public TreeBuilder propertyF( @Nonnull String name, @Nullable String fmt, Object ... args ) {
    String val = fmt;
    if( (fmt != null) && (args != null) && (args.length > 0) ) {
      val = String.format( fmt, args );
    }
    return property( name, val );
  }
  
  /**
   * Changes a property for the current node. The value will be processed in the following way:
   * 
   * <ul>
   *   <li>Map - The map structure will be considered as a complete subtree</li>
   *   <li>List - The list will be considered as a complete subsequence</li>
   *   <li>Supplier - The supplier will be called to provide the value</li>
   *   <li>An actual value.</li>
   * </ul>
   * 
   * @param name    The name of the property.
   * @param value   The value of the property.
   * 
   * @return   this
   */
  @Nonnull
  public TreeBuilder property( @Nonnull String name, @Nullable Object value ) {
    NodeDescriptor record = current();
    if( record.properties.isEmpty() ) {
      record.properties = new HashMap<>();
    }
    record.properties.put( name, value );
    return this;
  }
  
  @Nonnull
  public TreeBuilder clazz( @Nullable Class<?> clazz ) {
    if( clazz != null ) {
      property( PN_CLASS, clazz.getName() );
    }
    return this;
  }

  @Nonnull
  public TreeBuilder implementationClass( @Nullable Class<?> clazz ) {
    if( clazz != null ) {
      property( PN_IMPLEMENTATION_CLASS, clazz.getName() );
    }
    return this;
  }

  private String toName( Map<String, Object> map ) {
    String result = StringUtils.trimToNull( (String) map.get( PN_NAME ) );
    if( result == null ) {
      result = StringUtils.trimToNull( (String) map.get( PN_ID ) );
    }
    if( result == null ) {
      result = StringUtils.trimToNull( (String) map.get( PN_KEY ) );
    }
    return result;
  }
  
  private IllegalStateException errorHandler( Exception ex ) {
    if( ex instanceof IllegalStateException ) {
      return (IllegalStateException) ex;
    } else {
      return new IllegalStateException(ex);
    }
  }

  /**
   * Produces a data structure according to the current tree configuration.
   * 
   * @param producer   The {@link Producer} instance used to drive the creation process.
   * 
   * @return   The root strcture of the creation process.
   */
  @Nonnull
  public Node build( @Nonnull NodeProducer producer ) {
    return build( producer, false );
  }
  
  /**
   * Produces a data structure according to the current tree configuration.
   * 
   * @param producer   The {@link Producer} instance used to drive the creation process.
   * @param fail       <code>true</code> <=> Consider inconsistent nodetypes as an error.
   * 
   * @return   The root strcture of the creation process.
   */
  @Nonnull
  public Node build( @Nonnull NodeProducer producer, boolean fail ) {
    try {
      Node              result = producer.getRootNode();
      StringBuilder path   = new StringBuilder("/");
      for( NodeDescriptor record : root.subnodes ) {
        addNode( producer, result, record, fail, path );
      }
      return result;
    } catch( Exception ex ) {
      throw errorHandler(ex);
    }
  }

  private void addNode( NodeProducer producer, Node parent, NodeDescriptor child, boolean fail, StringBuilder path ) {
    // create/get the node
    int    pathlen   = path.length();
    String nodename  = child.name;
    if( nodename.indexOf('/') != -1 ) {
      String[] parts = nodename.split("/");
      for( int i = 0; i < parts.length - 1; i++ ) {
        parent = getChild( producer, path.toString(), parent, parts[i], child.nodeType, fail );
        path.append(extractName( parts[i] ) ).append('/');
      }
      nodename = parts[ parts.length - 1 ];
    }
    Node childNode = getChild( producer, path.toString(), parent, nodename, child.nodeType, fail );
    path.append(extractName( nodename ) ).append('/');
    // set the properties
    Map<String, Object> allProperties = child.properties;
    for( Map.Entry<String, Object> entry : allProperties.entrySet() ) {
      setProperty( producer, childNode, entry.getKey(), entry.getValue(), fail, path );
    }
    // process child elements
    List<NodeDescriptor> children = child.subnodes;
    for( NodeDescriptor record : children ) {
      addNode( producer, childNode, record, fail, path );
    }
    path.setLength( pathlen );
  }
  
  private void setProperty( NodeProducer producer, Node node, String key, Object value, boolean fail, StringBuilder path ) {
    if( value instanceof Map ) {
      setMapProperty( producer, node, key, value, fail, path );
    } else if( value instanceof List ) {
      setListProperty( producer, node, key, value, fail, path );
    } else if( value instanceof Supplier ) {
      Object newvalue = ((Supplier) value).get();
      if( newvalue != value ) {
        setProperty( producer, node, key, newvalue, fail, path );
      }
    } else {
      producer.setBasicProperty( node, key, value );
    }
  }

  private <R> void setListProperty( NodeProducer producer, Node node, String key, Object value, boolean fail, StringBuilder path ) {
    List list      = (List) value;
    Node    childNode = getChild( producer, path.toString(), node, key, NodeTypes.ContentNode.NAME, fail );
    if( ! list.isEmpty() ) {
      boolean basictypes = !(list.get(0) instanceof Map);
      if( basictypes ) {
        int i = 0;
        for( Object obj : list ) {
          setProperty( producer, childNode, String.valueOf(i), obj, fail, path );
          i++;
        }
      } else {
        for( Object obj : list ) {
          Map    map  = (Map) obj;
          String name = toName( map );
          if( name == null ) {
            throw errorHandler( new IllegalStateException( String.format("Failed to determine name. Path='%s'", path) ) );
          }
          setProperty( producer, childNode, name, map, fail, path );
        }
      }
    }
  }

  private void setMapProperty( NodeProducer producer, Node node, String key, Object value, boolean fail, StringBuilder path ) {
    Map<String, Object> map       = (Map<String, Object>) value;
    Node                   childNode = getChild( producer, path.toString(), node, key, NodeTypes.ContentNode.NAME, fail );
    for( Map.Entry<String, Object> pair : map.entrySet() ) {
      setProperty( producer, childNode, pair.getKey(), pair.getValue(), fail, path );
    }
  }
  
  private Node getChild( NodeProducer producer, String path, Node node, String name, String nodetype, boolean fail ) {
    nodetype  = resolveNodeType( name, nodetype );
    name      = extractName( name );
    return producer.getChild( path, node, name, nodetype, fail );
  }

  private String extractName( String name ) {
    int open  = name.indexOf('{');
    int close = name.indexOf('}');
    if( (open != -1) && (close > open) ) {
      return name.substring( 0, open );
    } else {
      return name;
    }
  }
  
  private String resolveNodeType( String name, String nodetype ) {
    int open  = name.indexOf('{');
    int close = name.indexOf('}');
    if( (open != -1) && (close > open) ) {
      return name.substring( open + 1, close );
    } else {
      return nodetype;
    }
  }
  
  private static class NodeDescriptor {

    String                 name;
    String                 nodeType;
    Map<String, Object>    properties;
    List<NodeDescriptor>   subnodes;

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString( this, ToStringStyle.MULTI_LINE_STYLE );
    }
    
  } /* ENDCLASS */

} /* ENDCLASS */
