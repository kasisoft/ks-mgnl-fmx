package com.kasisoft.mgnl.fmx.freemarker;

import com.kasisoft.libs.common.text.*;

import com.kasisoft.libs.common.util.*;

import org.w3c.dom.*;

import javax.xml.bind.*;

import java.util.function.*;

import java.util.*;

import java.io.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FreemarkerXmlTranslator {

  private static final String FMX_NAMESPACE       = "https://kasisoft.com/namespaces/fmx/0.1";
  private static final String FMX_NAMESPACE_S     = "https://kasisoft.com/namespaces/fmx/0.1/";
  
  private static final String DEFAULT_PREFIX      = "fmx";
  
  private static final String WRAPPER             = "<fmx:wrappingElement xmlns:fmx=\"%s\">%s</fmx:wrappingElement>";
  
  private static final String FMX_LIST            = "list";
  private static final String FMX_IT              = "it";
  private static final String FMX_DEPENDS         = "depends";
  private static final String FMX_DISABLE_DEPENDS = "disable-depends";
  
  private static final Bucket<StringFBuilder> STRINGFBUILDER = BucketFactories.newStringFBuilderBucket();
  private static final Bucket<List<Attr>>     LIST_ATTR      = BucketFactories.newArrayListBucket();
  
  public String convert( String xmlInput ) {
    return convert( new StringReader( String.format( WRAPPER, FMX_NAMESPACE, xmlInput ) ) );
  }
  
  private String convert( Reader reader ) {
    return STRINGFBUILDER.forInstance( this::convertImpl, reader );
  }

  private String convertImpl( StringFBuilder builder, Reader reader ) {
    Node doc = (Node) JAXB.unmarshal( reader, Object.class );
    doc      = getFirstElement( doc );
    doc.normalize();
    serialize( builder, new StringBuilder(), doc );
    return builder.toString();
  }

  private Node getFirstElement( Node parent ) {
    Node     result   = null;
    NodeList children = parent.getChildNodes();
    if( children != null ) {
      for( int i = 0; i < children.getLength(); i++ ) {
        if( children.item(i).getNodeType() == Node.ELEMENT_NODE ) {
          result = children.item(i);
          break;
        }
      }
    }
    return result;
  }
  
  private List<Node> getChildren( Node parent ) {
    List<Node> result   = Collections.emptyList();
    NodeList   children = parent.getChildNodes();
    if( children != null ) {
      result = new ArrayList<>( children.getLength() );
      for( int i = 0; i < children.getLength(); i++ ) {
        Node node = children.item(i);
        if( node.getNodeType() == Node.TEXT_NODE ) {
          Text text = (Text) node;
          if( StringFunctions.cleanup( text.getTextContent() ) == null ) {
            continue;
          }
        }
        result.add( node );
      }
    }
    return result;
  }
  
  private void serialize( StringFBuilder builder, StringBuilder indention, Node node ) {
    if( hasFmxRelevantPortion( node ) ) {
      serializeFmxNode( builder, indention, node );
    } else {
      serializeOrdinaryNode( builder, indention, node );
    }
  }
  
  private void serializeFmxNode( StringFBuilder builder, StringBuilder indention, Node node ) {
    
    List<Attr>  standardAttributes  = LIST_ATTR.allocate();
    List<Attr>  fmxAttributes       = LIST_ATTR.allocate();
    
    try {
    
      collectAttributes( node.getAttributes(), standardAttributes, fmxAttributes );
      
      String       list            = parseSingle  ( FMX_LIST            , fmxAttributes );
      String       iterator        = parseSingle  ( FMX_IT              , fmxAttributes );
      List<String> depends         = parseMany    ( FMX_DEPENDS         , fmxAttributes );
      List<String> disableDepends  = parseMany    ( FMX_DISABLE_DEPENDS , fmxAttributes );
      
      List<Node> children = getChildren( node );
      openDepends( builder, indention, depends, disableDepends );
      openList( builder, indention, list, iterator );
      if( isFmxRelevant( node ) ) {
        String name = node.getLocalName().replace( '-', '.' );
        if( isEmpty( children ) ) {
          if( node.getNodeType() == Node.TEXT_NODE ) {
            builder.append( node.getNodeValue() );
          } else {
            builder.appendF( "%s[@%s", indention, name );
            serializeAttributes( builder, standardAttributes );
            builder.appendF( " /]\n" );
          }
        } else {
          builder.appendF( "%s[@%s", indention, name );
          serializeAttributes( builder, standardAttributes );
          builder.appendF( "]" );
          serializeChildren( builder, indention, children );
          builder.appendF( "[/@%s]\n", name );
        }
      } else {
        serializeOrdinaryNode( builder, indention, node );
      }
      closeFtlTag( builder, indention, "list", list );
      closeFtlTag( builder, indention, "if", depends );

    } finally {
      LIST_ATTR.free( standardAttributes );
      LIST_ATTR.free( fmxAttributes      );
    }
    
  }

  private void closeFtlTag( StringFBuilder builder, StringBuilder indention, String tag, Object test ) {
    if( test != null ) {
      builder.appendF( "%s[/#%s]\n", indention, tag );
    }
  }
  
  private void openList( StringFBuilder builder, StringBuilder indention, String list, String iterator ) {
    if( list != null ) {
      String it = "it";
      if( iterator != null ) {
        it = iterator;
      }
      builder.appendF( "%s[#list %s as %s]\n", indention, list, it );
    }
  }

  private void openDepends( StringFBuilder builder, StringBuilder indention, List<String> depends, List<String> disableDepends ) {
    if( depends != null ) {
      builder.appendF( "%s[#if ", indention );
      if( disableDepends != null ) {
        builder.append("(");
        for( String expr : disableDepends ) {
          builder.appendF( "%s || ", expr, expr, expr );
        }
        builder.setLength( builder.length() - " || ".length() );
        builder.append(") || ");
      }
      for( String expr : depends ) {
        builder.appendF( "%s && ", expr, expr, expr );
      }
      builder.setLength( builder.length() - " && ".length() );
      builder.appendF( "]\n" );
    }
  }
  
  private void serializeOrdinaryNode( StringFBuilder builder, StringBuilder indention, Node node ) {
    List<Node> children           = getChildren( node );
    List<Attr> standardAttributes = LIST_ATTR.allocate();
    try {
      collectAttributes( node.getAttributes(), standardAttributes, null );
      if( isEmpty( children ) ) {
        if( node.getNodeType() == Node.TEXT_NODE ) {
          builder.append( node.getNodeValue() );
        } else {
          builder.appendF( "%s<%s", indention, node.getNodeName() );
          serializeAttributes( builder, standardAttributes );
          builder.appendF( " />\n" );
        }
      } else {
        builder.appendF( "%s<%s", indention, node.getNodeName() );
        serializeAttributes( builder, standardAttributes );
        builder.appendF( ">" );
        serializeChildren( builder, indention, children );
        builder.appendF( "</%s>\n", node.getNodeName() );
      }
    } finally {
      LIST_ATTR.free( standardAttributes );
    }
  }
  
  private void serializeChildren( StringFBuilder builder, StringBuilder indention, List<Node> children ) {
    boolean indent = (children.size() > 1) || (children.get(0).getNodeType() != Node.TEXT_NODE);
    int     len = -1;
    if( indent ) {
      builder.appendF( "\n" );
      len = indention.length();
      indention.append( "  ");
    }
    for( Node child : children ) {
      serialize( builder, indention, child );
    }
    if( indent ) {
      indention.setLength( len );
      builder.append( indention );
    }
  }
  
  private void collectAttributes( NamedNodeMap attributes, List<Attr> normal, List<Attr> fmx ) {
    Consumer<Attr> normalAdd = normal != null ? normal::add : a -> {};
    Consumer<Attr> fmxAdd    = fmx    != null ? fmx::add    : a -> {};
    if( ! isEmpty( attributes ) ) {
      for( int i = 0; i < attributes.getLength(); i++ ) {
        Attr attr = (Attr) attributes.item(i);
        if( isFmxRelevant( attr ) ) {
          fmxAdd.accept( attr );
        } else if( ! isFmxNamespace( attr.getNodeValue() ) ) {
          normalAdd.accept( attr );
        }
      }
    }
    Collections.sort( normal, this::compareAttributes );
  }
  
  private void serializeAttributes( StringFBuilder builder, List<Attr> attributes ) {
    if( ! isEmpty( attributes ) ) {
      attributes.forEach( $ -> serializeAttribute( builder, $ ) );
    }
  }
  
  private void serializeAttribute( StringFBuilder builder, Attr attr ) {
    builder.appendF( " %s=\"%s\"", attr.getNodeName(), attr.getNodeValue() );
  }
  
  private int compareAttributes( Attr attr1, Attr attr2 ) {
    String name1 = attr1.getNodeName();
    String name2 = attr2.getNodeName();
    return name1.compareTo( name2 );
  }
  
  private boolean hasFmxRelevantPortion( Node node ) {
    boolean result = isFmxRelevant( node );
    if( ! result ) {
      NamedNodeMap attributes = node.getAttributes(); 
      if( ! isEmpty( attributes ) ) {
        for( int i = 0; i < attributes.getLength(); i++ ) {
          result = isFmxRelevant( attributes.item(i) );
          if( result ) {
            break;
          }
        }
      }
    }
    return result;
  }

  private boolean isFmxRelevant( Node node ) {
    return isFmxNamespace( node.getNamespaceURI() ) || DEFAULT_PREFIX.equals( node.getPrefix() );
  }

  private boolean isEmpty( List list ) {
    return (list == null) || list.isEmpty();
  }

  private boolean isEmpty( NamedNodeMap map ) {
    return (map == null) || (map.getLength() == 0);
  }

  private boolean isFmxNamespace( String text ) {
    return FMX_NAMESPACE.equals( text ) || FMX_NAMESPACE_S.equals( text );
  }
  
  private String parseSingle( String name, List<Attr> attributes ) {
    String result = null;
    for( int i = attributes.size() - 1; i >= 0; i-- ) {
      Attr attr = attributes.get(i);
      if( name.equals( attr.getLocalName() ) ) {
        attributes.remove(i);
        result = StringFunctions.cleanup( attr.getNodeValue() );
      }
    }
    return result;
  }

  private List<String> parseMany( String name, List<Attr> attributes ) {
    List<String> result = null;
    for( int i = attributes.size() - 1; i >= 0; i-- ) {
      Attr attr = attributes.get(i);
      if( name.equals( attr.getLocalName() ) ) {
        attributes.remove(i);
        List<String> values = getValues( attr );
        if( ! values.isEmpty() ) {
          result = values;
        }
      }
    }
    return result;
  }

  private List<String> getValues( Node node ) {
    List<String> result = Collections.emptyList();
    String value = StringFunctions.cleanup( node.getNodeValue() );
    if( value != null ) {
      result = new ArrayList<>( Arrays.asList( value.split( "," ) ) );
      for( int j = result.size() - 1; j >= 0; j-- ) {
        String v = StringFunctions.cleanup( result.get(j) );
        if( v == null ) {
          result.remove(j);
        } else {
          result.set( j, v );
        }
      }
    }
    return result;
  }

} /* ENDCLASS */
