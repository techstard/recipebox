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

import java.text.DecimalFormat;

public class RecipeboxParser implements HtmlParseFilter {

    private Configuration conf;
    private String ingredientString;
    private String amtString;

    public Parse filter(Content con, Parse p, HTMLMetaTags m, DocumentFragment df) {
        ingredientString = "";
        amtString = "";
        try {

            FileOutputStream out = new FileOutputStream("C:\\NutchOutput.txt");
            PrintStream ps = new PrintStream(out, true);
            ps.print("Beginning Function on page: " + con.getUrl());
            int start = 0;
            int end = 0;
            String page = "";
            BufferedReader foo = new BufferedReader(new StringReader(new String(con.getContent())));
            /*
            while (foo.ready()) {
                String blah = foo.readLine().toLowerCase();
                if (blah.endsWith("\r\n")) {
                    page = page + blah;
                } else if (blah.endsWith("\r")) {
                    page = page + blah + "\n";
                } else {
                    page = page + blah + "\r\n";
                }
              
            //System.out.println(blah);
            }
            */
            foo.close();
            page = new String(con.getContent());

            //Following parsing these values will be set
            String title = "";

            //These variables are reused, no output is guarenteed
            String substring = "";

            //ps.println("<!-----------CONTENT----------->");
            //ps.println(page);
            //ps.println("<------------END CONTENT----------------->");

            /*
             * TITLE PARSING
             */
            start = page.toLowerCase().indexOf("<title");
            if (start < 0) {
                ps.println("No title for the webpage");
            }

            substring = page.substring(page.indexOf(">",start)+1);
            end = substring.toLowerCase().indexOf("</title>");
            title = substring.substring(0, end).trim();
            title = title.replace("\r","");
            System.out.println("Title: '"+title+"'");

            //TITLE ADDED TO INDEX
            p.getData().getContentMeta().set("title", title);
            /*
             * END TITLE PARSING
             */
            /*
             * START INGREDIENT PARSING
             */
            ps.println("beginning ingredient parse");
            //trim the HTML to just the body of the text
            page = page.toLowerCase();
            
            start = page.indexOf("<body");
            end = page.indexOf("</body>");
            if ((start >= 0) && (end >= 0)) {
                page = page.substring(start, end + 7);
            }

            //remove all the tag information
            String temp = removetags(page);

            //locate the Ingredients

            String[] lines = null;
            char lf = 10;
            if (temp.split("\n").length > 0) {
                lines = temp.split("\n");
            } else {
                lines = temp.split(Character.toString(lf));
            }

            for (int i = 0; i < lines.length; i++) {
                String tempLine = lines[i].trim();
                if (tempLine.equals("")) {
                    continue;
                }
                if (Character.isDigit(tempLine.charAt(0))) {
                    if (tempLine.contains(" ")) {
                        for (int j = 0; j < tempLine.length(); j++) {
                            if (Character.isLetter(tempLine.charAt(j))) {
                                p = addIngredToParse(tempLine, p);
                                break;
                            }
                        }
                    }
                } else if (i > 0 && i < (lines.length - 1) &&
                        !lines[i - 1].equals("") && Character.isDigit(lines[i - 1].charAt(0)) &&
                        !lines[i + 1].equals("") && Character.isDigit(lines[i + 1].charAt(0))) {
                    p = addIngredToParse(lines[i], p);
                }
            }

            

            //semicolon delimited ingredient string
            p.getData().getContentMeta().set("ingredient", ingredientString);
            //semicolon delimited value string (index corresponds to ingredient)
            p.getData().getContentMeta().set("values", amtString);

            /*
             * END INGREDIENT PARSE
             */
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
        System.out.println(ingredient);
        DecimalFormat fourPlaces = new DecimalFormat("0.####");

        Pattern pattern = Pattern.compile("(\\d+){1}(( (\\d+)){0,1}(/(\\d+)){0,1})");
        Matcher matcher = pattern.matcher(ingredient);

        matcher.find();
        String wholeNumStr = matcher.group(1);
        String numeratorStr = matcher.group(4);
        String denominatorStr = matcher.group(6);
        Double value = 0.0;
        if (numeratorStr == null && denominatorStr == null) {
            value = Integer.parseInt(wholeNumStr) / 1.0;
        } else if (numeratorStr == null) {
            value = (Integer.parseInt(wholeNumStr) / 1.0) / (Integer.parseInt(denominatorStr) / 1.0);
        } else if (denominatorStr == null) {
            //ERROR CONDITION, strings of the form "x y" slip through the regex
            return p;
        } else {
            value = Integer.parseInt(wholeNumStr) +
                    ((Integer.parseInt(numeratorStr) / 1.0) / (Integer.parseInt(denominatorStr) / 1.0));
        }

        ingredient = ingredient.substring(matcher.end() + 1);
        //Find optional "(double unit)" field
        if (ingredient.indexOf('(') != -1) {
            String innerValue = ingredient.substring(ingredient.indexOf('(') + 1, ingredient.indexOf(')'));
            String[] split = innerValue.split(" ");
            if(Character.isDigit(split[0].charAt(0))) {
                Double doubVal = Double.parseDouble(split[0]);
                String unit = split[1];
                //System.out.printf("Value %f\nUnit %s\n", doubVal, unit);
                ingredient = ingredient.substring(ingredient.indexOf(')') + 1);
            }
        }
        //Find unit
        if (ingredient.indexOf(" ", 1) > 0) {
            //missing this block means the unit is implicit
            String realUnit = ingredient.substring(0, ingredient.indexOf(" ", 1)).trim();
            //System.out.printf("realUnit %s\n", realUnit);
        }
        //Rest
        ingredient = ingredient.substring(ingredient.indexOf(" ") + 1);
        //System.out.printf("ingredient %s\n", ingredient);


        //p.getData().getContentMeta().set(ingredient, value.toString());
        //p.getData().getContentMeta().set("ingredient", ingredient);
        ingredientString += ingredient + ";";
        amtString += fourPlaces.format(value) + ";";
        return p;
    }
    private String removetags(String page) {
        String temp = "";
        temp = page.replaceAll(".\n", "\r\n");
        int startScript = 0;
        int endScript = 0;
        while(temp.indexOf("<script",startScript) != -1)
        {
            startScript = temp.indexOf("<script");
            endScript = temp.indexOf("</script>",startScript);
            //temp = temp.replaceFirst("<script.*?</script>"," ");
            if(endScript == -1){
                startScript++;
                continue;
            }
            temp = temp.substring(0, startScript)+temp.substring(endScript, temp.length()-1);
        }
        temp = temp.replaceAll("<.*?>", "\n");

        return temp;
    }
}

