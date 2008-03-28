package org.apache.nutch.parse.recipebox;

import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseUtil;
import org.apache.nutch.protocol.Content;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.util.NutchConfiguration;


import java.util.Properties;
import java.io.*;
import java.net.URL;

import junit.framework.TestCase;
import junit.textui.ResultPrinter;
import java.io.*;
/*
 * Loads test page recommended.html and verifies that the recommended 
 * meta tag has recommended-content as its value.
 *
 */
public class TestRecipeboxParser extends TestCase {

  //private static final 
  

  public void testPages() throws Exception {
    System.out.println("matt was here");
    File testDir =new File(System.getProperty("test.data"));
    System.out.println(testDir.getAbsolutePath());
    pageTest(new File(testDir, "test.htm"), "http://foo.com/",
            "\"Sweet pecans and cranberries add to the color and textures of this mixed-greens salad tossed with sliced pears and the kick of KRAFT Light Zesty Italian dressing.\"",
             "Mixed Greens and Pear Salad - Allrecipes");
    assertTrue(true);
  }


  public void pageTest(File file, String url, String description, String title)
    throws Exception {

    String contentType = "text/html";
    InputStream in = new FileInputStream(file);
    ByteArrayOutputStream out = new ByteArrayOutputStream((int)file.length());
    byte[] buffer = new byte[1024];
    int i;
    while ((i = in.read(buffer)) != -1) {
      out.write(buffer, 0, i);
    }
    in.close();
    byte[] bytes = out.toByteArray();
    Configuration conf = NutchConfiguration.create();

    Content content =
      new Content(url, url, bytes, contentType, new Metadata(), conf);
    //THE IMPORTANT PART
    Parse parse = new ParseUtil(conf).parseByExtensionId("parse-html",content);


    Metadata metadata = parse.getData().getContentMeta();
    assertEquals(metadata.get("title"), title);
    //assertEquals(metadata.get("description"),description);
    System.out.println("printing metadata names:");
    for(int j=0;j<metadata.size();j++)
    {
        System.out.println(metadata.names()[j]+": "+metadata.get(metadata.names()[j]));
    }
    //assertTrue("somesillycontent" != metadata.get("Recommended"));
  }
}