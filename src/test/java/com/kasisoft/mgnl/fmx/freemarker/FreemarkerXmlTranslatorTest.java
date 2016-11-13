package com.kasisoft.mgnl.fmx.freemarker;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import com.kasisoft.libs.common.io.*;

import org.testng.annotations.*;

import java.util.*;

import lombok.experimental.*;

import lombok.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FreemarkerXmlTranslatorTest {

  private static final String[] TESTCASES = new String[] {
    "example1",
    "example2",
    "example3",
    "example4",
    "example5",
    "example6",
    "example7",
    "example8",
  };
  
  FreemarkerXmlTranslator translator = new FreemarkerXmlTranslator();
  
  @DataProvider(name = "convertData")
  public Object[][] convertData() {
    List<Object[]> list = new ArrayList<>( TESTCASES.length );
    ClassLoader    cl   = Thread.currentThread().getContextClassLoader();
    for( String testcase : TESTCASES ) {
      list.add( new Object[] {
        IoFunctions.readTextFully( cl.getResource( testcase + ".fmx" ) ),
        IoFunctions.readTextFully( cl.getResource( testcase + ".ftl" ) )
      } );
    }
    return list.toArray( new Object[ list.size() ][2] );
  }
  
  @Test(dataProvider = "convertData")
  public void convert( String fmxContent, String ftlContent ) {
    String converted  = translator.convert( fmxContent );
    assertThat( converted, is( ftlContent ) );
  }

} /* ENDCLASS */
