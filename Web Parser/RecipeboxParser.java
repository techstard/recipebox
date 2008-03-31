//Chris Stafford
//3/21/2008
package org.apache.nutch.parse.recipebox;

// JDK imports
import java.util.Enumeration;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.protocol.Content;

// Commons imports
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// W3C imports
import org.w3c.dom.DocumentFragment;
// I/O imports
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeboxParser implements HtmlParseFilter {

    private Configuration conf;
    private String ingredientString;

    public Parse filter(Content con, Parse p, HTMLMetaTags m, DocumentFragment df) {

        try {
        
            FileOutputStream out = new FileOutputStream("C:\\NutchOutput.txt");
            PrintStream ps = new PrintStream(out, true);
            ps.print("Beginning Function on page: " + con.getUrl());
            int i = 0;
            int start = 0;
            int end = 0;
            String page = new String(con.getContent());

            //Following parsing these values will be set
            String title = "";
            String description = "";
            String directions = "";

            //These variables are reused, no output is guarenteed
            String ingredient = "";
            String substring = "";

            //ps.println("<!-----------CONTENT----------->");
            //ps.println(page);
            //ps.println("<------------END CONTENT----------------->");

            /*
             * TITLE PARSING
             */
            start = page.indexOf("<title>");
            if (start < 0) {
                ps.println("No title for the webpage");
            }

            substring = page.substring(start + 7);
            end = substring.indexOf("</title>");
            title = substring.substring(0, end);
            ps.println("Title:");
            ps.println(title);

            //TITLE ADDED TO INDEX
            p.getData().getContentMeta().set("title", title);

            start = page.indexOf("h2");
            if (start < 0) {
                ps.println("h2 not found");
                return p;
            }
            substring = page.substring(start);
            start = substring.indexOf("h2");
            if (start < 0) {
                ps.println("second h2 not found");
                return p;
            }
            end = substring.indexOf("</ul");
            substring = substring.substring(start, end);




            ps.println("beginning ingredient parse");
            while (true) {
                start = substring.indexOf("<li>");
                if (start >= 0) {
                    substring = substring.substring(start + 4);
                    end = substring.indexOf("</li>");
                    ingredient = substring.substring(0, end);
                    System.out.println(ingredient);
                    //Locate the numerical portion of each ingredient
                    int x = 0;
                    int lastnum = 0;
                    String numeric = "";
                    while (x < ingredient.length()) {
                        if ((ingredient.charAt(x) <= 57) && (ingredient.charAt(x) >= 48)) {
                            lastnum = x;
                        }
                        x++;
                    }
                    lastnum++;
                    numeric = ingredient.substring(0, lastnum);
                    x = 0;

                    p = addIngredToParse(ingredient, p);

                //print to a file
                //ps.println(ingredient);
                //ps.println(numvalue);

                } else {
                    break;
                }
            }
            //semicolon delimited ingredient string
            p.getData().getContentMeta().set("ingredient", ingredientString);
            //begin direction parse
            start = page.indexOf("h2");
            substring = page.substring(start + 1);
            for (i = 1; i < 3; i++) {
                start = substring.indexOf("h2");
                substring = substring.substring(start + 1);
            }

            end = substring.indexOf("</ol>");
            substring = substring.substring(0, end);
            while (true) {
                start = substring.indexOf("<span>");
                if (start < 0) {
                    break;
                }
                substring = substring.substring(start + 6);
                end = substring.indexOf("</span>");
                directions = directions + substring.substring(0, end) + System.getProperty("line.separator");
            }
            ps.println("Directions:");
            ps.println(directions);

            //locate the description
            start = page.indexOf("<!-- DESCRIPTION -->");
            substring = page.substring(start + 20);
            end = substring.indexOf("</span");
            description = substring.substring(0, end);
            p.getData().getContentMeta().set("description", description);
            ps.println("Description:");
            ps.println(description);

            ps.close();
            out.close();
            return p;
        } catch (Exception e) {
            System.out.println("CAUGHT EXCEPTION: ");
            System.out.println(e.getCause());
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(e.getClass());
            return p;
        }
        
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public Configuration getConf() {
        return this.conf;
    }

    private Parse addIngredToParse(String ingredient, Parse p) {
        Pattern pattern = Pattern.compile("(\\d+){1}(( (\\d+)){0,1}(/(\\d+)){0,1})");
        Matcher matcher = pattern.matcher(ingredient);

        matcher.find();
        String wholeNumStr = matcher.group(1);
        String numeratorStr = matcher.group(4);
        String denominatorStr = matcher.group(6);
        Double value = 0.0;
        if(numeratorStr == null && denominatorStr == null)
        {
            value = Integer.parseInt(wholeNumStr)/1.0;
        }
        else if(numeratorStr == null)
        {
            value = (Integer.parseInt(wholeNumStr)/1.0)/(Integer.parseInt(denominatorStr)/1.0);
        }
        else if(denominatorStr == null)
        {
            //ERROR CONDITION, strings of the form "x y" slip through the regex
            return p;
        }
        else
        {
            value = Integer.parseInt(wholeNumStr)+
                    ((Integer.parseInt(numeratorStr)/1.0)/(Integer.parseInt(denominatorStr)/1.0));
        }

        ingredient = ingredient.substring(matcher.end() + 1);
        //Find optional "(double unit)" field
        if (ingredient.indexOf('(') != -1) {
            String innerValue = ingredient.substring(ingredient.indexOf('(') + 1, ingredient.indexOf(')'));
            String[] split = innerValue.split(" ");
            Double doubVal = Double.parseDouble(split[0]);
            String unit = split[1];
            System.out.printf("Value %f\nUnit %s\n", doubVal, unit);
            ingredient = ingredient.substring(ingredient.indexOf(')') + 1);
        }
        //Find unit
        if(ingredient.indexOf(" ",1)>0)
        {
            //missing this block means the unit is implicit
        String realUnit = ingredient.substring(0, ingredient.indexOf(" ", 1)).trim();
        System.out.printf("realUnit %s\n", realUnit);
        }
        //Rest
        ingredient = ingredient.substring(ingredient.indexOf(" ") + 1);
        System.out.printf("ingredient %s\n", ingredient);

        
        //p.getData().getContentMeta().set(ingredient, value.toString());
        //p.getData().getContentMeta().set("ingredient", ingredient);
        ingredientString += ingredient + ";";
        return p;
    }
}

