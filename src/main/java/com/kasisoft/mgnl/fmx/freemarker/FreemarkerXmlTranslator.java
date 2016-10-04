package com.kasisoft.mgnl.fmx.freemarker;

import com.kasisoft.libs.common.text.*;

import org.w3c.dom.*;

import javax.xml.bind.*;

import java.util.function.*;

import java.util.*;

import java.io.*;

import lombok.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class FreemarkerXmlTranslator {

  static final String FMX_NAMESPACE   = "https://kasisoft.com/namespaces/fmx/0.1";
  static final String FMX_NAMESPACE_S = "https://kasisoft.com/namespaces/fmx/0.1/";
  
  public String convert( Reader reader ) {
    StringFBuilder builder = new StringFBuilder();
    Node doc = (Node) JAXB.unmarshal( reader, Object.class );
    doc.normalize();
    serialize( builder, new StringBuilder(), doc );
    return builder.toString();
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
    
    List<Attr>  standardAttributes  = new ArrayList<>();
    List<Attr>  fmxAttributes       = new ArrayList<>();
    
    collectAttributes( node.getAttributes(), standardAttributes, fmxAttributes );
    
    FmxList     list      = FmxList     . parse( fmxAttributes );
    FmxIterator iterator  = FmxIterator . parse( fmxAttributes );
    FmxDepends  depends   = FmxDepends  . parse( fmxAttributes );
    
    List<Node> children = getChildren( node );
    openDepends( builder, indention, depends );
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
    closeList( builder, indention, list );
    closeDepends( builder, indention, depends );
    
  }

  private void closeList( StringFBuilder builder, StringBuilder indention, FmxList list ) {
    if( list != null ) {
      builder.appendF( "%s[/#list]\n", indention );
    }
  }

  private void openList( StringFBuilder builder, StringBuilder indention, FmxList list, FmxIterator iterator ) {
    if( list != null ) {
      String it = "it";
      if( iterator != null ) {
        it = iterator.getExpression();
      }
      builder.appendF( "%s[#list %s as %s]\n", indention, list.getExpression(), it );
    }
  }

  private void closeDepends( StringFBuilder builder, StringBuilder indention, FmxDepends depends ) {
    if( depends != null ) {
      builder.appendF( "%s[/#if]\n", indention );
    }
  }

  private void openDepends( StringFBuilder builder, StringBuilder indention, FmxDepends depends ) {
    if( depends != null ) {
      builder.appendF( "%s[#if ", indention );
      for( String expr : depends.getExpressions() ) {
        builder.appendF( "%s?has_content && ", expr );
      }
      builder.setLength( builder.length() - " && ".length() );
      builder.appendF( "]\n" );
    }
  }
  
  private void serializeOrdinaryNode( StringFBuilder builder, StringBuilder indention, Node node ) {
    List<Node> children           = getChildren( node );
    List<Attr> standardAttributes = new ArrayList<>();
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
      for( Attr attr : attributes ) {
        serializeAttribute( builder, attr );
      }
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
    return isFmxNamespace( node.getNamespaceURI() );
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

  @AllArgsConstructor
  private static class FmxList {
    
    static final String NAME = "list";
    
    @Getter
    String   expression;
    
    public static FmxList parse( List<Attr> attributes ) {
      FmxList result = null;
      for( int i = attributes.size() - 1; i >= 0; i-- ) {
        Attr attr = attributes.get(i);
        if( NAME.equals( attr.getLocalName() ) ) {
          attributes.remove(i);
          String value = StringFunctions.cleanup( attr.getNodeValue() );
          result       = new FmxList( value );
        }
      }
      return result;
    }
    
  } /* ENDCLASS */

  @AllArgsConstructor
  private static class FmxIterator {
    
    static final String NAME = "it";
    
    @Getter
    String   expression;
    
    public static FmxIterator parse( List<Attr> attributes ) {
      FmxIterator result = null;
      for( int i = attributes.size() - 1; i >= 0; i-- ) {
        Attr attr = attributes.get(i);
        if( NAME.equals( attr.getLocalName() ) ) {
          attributes.remove(i);
          String value = StringFunctions.cleanup( attr.getNodeValue() );
          result       = new FmxIterator( value );
        }
      }
      return result;
    }
    
  } /* ENDCLASS */

  @AllArgsConstructor
  private static class FmxDepends {
    
    static final String NAME = "depends";
    
    @Getter
    List<String>   expressions;
    
    public static FmxDepends parse( List<Attr> attributes ) {
      FmxDepends result = null;
      for( int i = attributes.size() - 1; i >= 0; i-- ) {
        Attr attr = attributes.get(i);
        if( NAME.equals( attr.getLocalName() ) ) {
          attributes.remove(i);
          String value = StringFunctions.cleanup( attr.getNodeValue() );
          if( value != null ) {
            List<String> values = new ArrayList<>( Arrays.asList( value.split( "," ) ) );
            for( int j = values.size() - 1; j >= 0; j-- ) {
              String v = StringFunctions.cleanup( values.get(j) );
              if( v == null ) {
                values.remove(j);
              } else {
                values.set( j, v );
              }
            }
            if( ! values.isEmpty() ) {
              result = new FmxDepends( values );
            }
          }
        }
      }
      return result;
    }
    
  } /* ENDCLASS */

} /* ENDCLASS */
