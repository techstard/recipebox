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
    pageTest(new File(testDir, "page1.txt"), "http://allrecipes.com/", "Cooky Cookies - Allrecipes");
    pageTest(new File(testDir, "page2.txt"), "http://www.recipe.com/", "Creamy Ice Milk - Healthy living - Recipe - Recipe.com");
    pageTest(new File(testDir, "page3.txt"), "http://www.betterrecipes.com/", "White Cheddar Chicken with Apple Raisin Chutney");
    pageTest(new File(testDir, "page4.txt"), "http://www.cooks.com/", "Cooks.com - Recipe - Appetizer Pie");
    pageTest(new File(testDir, "page5.txt"), "http://www.epicurious.com/", "Braised Artichokes Recipe at Epicurious.com");
    pageTest(new File(testDir, "page6.txt"), "http://www.recipesource.com/", "RecipeSource: Achot (Garlicky Farmer's Cheese with Walnuts)");
    pageTest(new File(testDir, "page7.txt"), "http://www.cooksrecipes.com/", "Cooks Recipes | Bacon-Wrapped Shrimp with Quick B&eacute;arnaise Sauce Recipe");
    pageTest(new File(testDir, "page8.txt"), "http://www.chow.com/", "Deviled Eggs with Tarragon Recipe by  - CHOW");
    pageTest(new File(testDir, "page9.txt"), "http://www.cookingcache.com/", "Apple Berry Salsa With Cinnamon Chips Recipe");
    pageTest(new File(testDir, "page10.txt"), "http://www.foodnetwork.com/", "Recipes : Roscommon Rhubarb Pie : Food Network");
    assertTrue(true);
    /*
     * allrecipes:    Page1
 recipe.com: Page2
 betterrecipes.com:  Page3
 cooks.com: Page4
 epicurious.com: Page5
 recipesource.com: Page6
 cooksrecipes.com: Page7
 CHOW.com:  Page8
 cookingcache.com: Page 9
 foodnetwork.com: Page 10
     * */
  }


  public void pageTest(File file, String url, String title)
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