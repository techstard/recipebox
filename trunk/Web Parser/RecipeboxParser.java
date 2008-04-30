package org.apache.nutch.parse.recipebox;

// JDK imports
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.protocol.Content;

// W3C imports
import org.w3c.dom.DocumentFragment;

// I/O imports
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

import java.text.DecimalFormat;

public class RecipeboxParser implements HtmlParseFilter {

    private Configuration conf;
    private String ingredientString;
    private String amtString;
    private Double sumTotal;

    public Parse filter(Content con, Parse p, HTMLMetaTags m, DocumentFragment df) {
        ingredientString = "";
        amtString = "";
        sumTotal = 0.0;
        try {
            int start = 0;
            int end = 0;

            String page = new String(con.getContent());

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

            substring = page.substring(page.indexOf(">",start)+1);
            end = substring.toLowerCase().indexOf("</title>");
            title = substring.substring(0, end).trim();
            title = title.replace("\r","");
            title = getTitle(title, con.getUrl());
            System.out.println("Title: '"+title+"'");

            //TITLE ADDED TO INDEX
            p.getData().getContentMeta().set("recipeTitle", title);
            /*
             * END TITLE PARSING
             */
            /*
             * START INGREDIENT PARSING
             */
            //trim the HTML to just the body of the text
            //System.out.println("Test: "+uc.toCommonUnit(1.0,""));
            
            page = page.toLowerCase();
            
            start = page.indexOf("<body");
            end = page.indexOf("</body>");
            if ((start >= 0) && (end >= 0)) {
                page = page.substring(start, end + 7);
            }

            //remove all the tag information
            String temp = removetags(page);

            //locate the line breaks which might signify ingredients
            String[] lines = null;
            char lf = 10;

            if (temp.split("\n").length > 0) {
                lines = temp.split("[\\s]*\n");
            } else {
                lines = temp.split("[\\s]*" + Character.toString(lf));
            }
            for (int i = 0; i < lines.length; i++) {
                lines[i] = lines[i].trim();
                if(lines[i].matches(".*[\\S]*\r[\\s]*[\\S]+.*"))
                    lines[i] = lines[i].replace("\r", "");
            }
            
            int numIngredients = 0;
            HashMap<Integer,String> ingredients = new HashMap<Integer,String>();

            for (int i = 0; i < lines.length; i++) {
                start = 0;
                end = 0;
                String tempLine = lines[i];
                if (tempLine.length() == 0) {
                    continue;
                }
                if (Character.isDigit(tempLine.charAt(0))) {
                    if (tempLine.contains(" ")) {
                        for (int j = 0; j < tempLine.length(); j++) {
                            if (Character.isLetter(tempLine.charAt(j))) {
                                ingredients.put(numIngredients, lines[i]);
                                numIngredients++;
                                break;
                            }
                        }
                    }
                } //catch an ingredient in between two ingredients
                else if (i > 0 && i < (lines.length - 1) &&
                        !(lines[i - 1].length() == 0) && Character.isDigit(lines[i - 1].charAt(0)) &&
                        !(lines[i + 1].length() == 0) && Character.isDigit(lines[i + 1].charAt(0))) {
                    if (i > 0) {
                        if (numIngredients > 0) {
                            start = page.indexOf(lines[i], end);
                        }
                    }
                    ingredients.put(numIngredients, lines[i]);
                    numIngredients++;
                } //catch ingredients nestled two-deep between ingredients
                else if (i > 1 && i < (lines.length - 1) &&
                        !(lines[i - 2].length() == 0) && Character.isDigit(lines[i - 2].charAt(0)) &&
                        !(lines[i + 1].length() == 0) && Character.isDigit(lines[i + 1].charAt(0))) {

                    ingredients.put(numIngredients, lines[i]);
                    numIngredients++;
                } //catch ingredients located two-deep within ingredients
                else if (i > 0 && i < (lines.length - 2) &&
                        !(lines[i - 1].length() == 0) && Character.isDigit(lines[i - 1].charAt(0)) &&
                        !(lines[i + 2].length() == 0) && Character.isDigit(lines[i + 2].charAt(0))) {
                    ingredients.put(numIngredients, lines[i]);
                    numIngredients++;
                }
            }
            if(numIngredients == 0) return p; //No possible ingredient lines found
            
            int lastingredient = 0;
            int firstingredient = -1;

            boolean[] status = new boolean[numIngredients];
            int[] position = new int[numIngredients];
            int[] distance = new int[numIngredients];
            
            for (int i = 0; i < numIngredients; i++) {
                if (is_ingredient(ingredients.get(i))) {
                    status[i] = true;
                    if (firstingredient < 0) {
                        firstingredient = i;
                    }
                    lastingredient = i;
                } else {
                    status[i] = false;
                }
            }
            if(firstingredient == -1 || lastingredient == 0)return p; //No ingredients found


            for (int i = firstingredient; i <= lastingredient; i++) {
                //Verify the status of a possible ingredient line
                if (isnotingredient(ingredients.get(i))) {
                    status[i] = false;
                }
                else status[i] = true;
                
                //Set the position measurement
                start = 0;
                if (i == firstingredient) {
                    temp = ingredients.get(i);
                    start = page.indexOf(temp);
                    while (start < 0) {
                        temp = temp.substring(0, temp.length() - 2);
                        start = page.indexOf(temp);
                    }
                    position[i] = start;
                } else {
                    temp = ingredients.get(i);
                    start = page.indexOf(temp);
                    while (start < 0) {
                        temp = temp.substring(0, temp.length() - 2);
                        start = page.indexOf(temp, position[i - 1]);
                    }
                    position[i] = start;
                }
            }

            start = -1;
            end = -1;
            //set the distance measurement
            for (int i = firstingredient; i < lastingredient; i++) {
                if (status[i]) {
                    start = end;
                    end = i;
                    if (start >= 0) {
                        distance[start] = position[end] - position[start] - ingredients.get(start).length();
                    }
                }
            }
            for (int i = firstingredient; i < numIngredients; i++) {
                if (status[i]) {
                    //ingrediengts tend to be short(er)              
                    if (ingredients.get(i).length() < 100) {
                        addIngredToParse(ingredients.get(i),p);
                    }

                    //check to see if there's any extraneous text showing
                    //up from the bottom of the HTML
                    if (distance[i] > 10000) {
                        break;
                    }
                }
            }

            

            //semicolon delimited ingredient string
            p.getData().getContentMeta().set("ingredient", ingredientString);
            //semicolon delimited value string (index corresponds to ingredient)
            p.getData().getContentMeta().set("values", amtString);
            p.getData().getContentMeta().set("totVolume", Double.toString(sumTotal));

            /*
             * END INGREDIENT PARSE
             */

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
        System.out.println("\t'"+ingredient+"'");
        DecimalFormat fourPlaces = new DecimalFormat("0.####");

        Pattern pattern = Pattern.compile("(\\d+){1}(( (\\d+)){0,1}(/(\\d+)){0,1})");
        Matcher matcher = pattern.matcher(ingredient);

        if(!matcher.find()) return p;
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
                    ((Integer.parseInt(numeratorStr) + 0.0) / (Integer.parseInt(denominatorStr) + 0.0));
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
        UnitConversion uc = new UnitConversion();
        value = uc.toCommonUnit(value, ingredient);
        ingredient = ingredient.substring(ingredient.indexOf(" ") + 1);

        ingredientString += ingredient + ";";
        amtString += fourPlaces.format(value) + ";";
        sumTotal += value;
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
    private String getTitle(String title, String url) {
        /*
         * Inputs: HTML Title String and url of the page
         * Output: Recipe Title 
         * 
         * Process: Removes common elements of titles, remaining uncommon
         *  elements are assumed to the be title, this has been shown to be true
         *  empirically.
         */ 
        if(url.contains(".com"))
            url = url.substring(0,url.indexOf(".com")+4);
        
        String[] var;
        if(title.contains("|"))
            var = title.split("\\|");
        else if(title.contains(":"))
            var = title.split(":");
        else
            var = title.split("-");

        for(int i=0;i<var.length;i++) {
            if(var[i].trim().matches("([Rr]ecipe(s)?)"));          //Single word "recipe(s)" token
            else if(url.contains(var[i].replace(" ", "").toLowerCase()));     //token (w/out spaces) is in hostname
            else {
                if(var[i].contains(" by ")) var[i] = var[i].substring(0,var[i].indexOf("by"));    //Removes author info
                if(var[i].contains(" at ")) var[i] = var[i].substring(0,var[i].indexOf("at"));    //Removes site info
                return var[i].trim();   //This is the title (Empirically)
            }
        }
        return "";
    }
    static boolean is_ingredient(String line) {
        boolean result = false;
        result = (result || (line.indexOf("cup") >= 1));
        result = (result || (line.indexOf("teasp") >= 1));
        result = (result || (line.indexOf("tablesp") >= 1));
        result = (result || (line.indexOf(" t ") >= 1));
        result = (result || (line.indexOf("pound") >= 1));
        result = (result || (line.indexOf("lbs") >= 1));
        result = (result || (line.indexOf("lb") >= 1));
        result = (result || (line.indexOf("pint") >= 1));
        result = (result || (line.indexOf("quart") >= 1));
        result = (result || (line.indexOf("qt") >= 1));
        result = (result || (line.indexOf("halve") >= 1));
        result = (result || (line.indexOf("pinch") >= 1));
        result = (result || (line.indexOf("to taste") >= 1));
        result = (result || (line.indexOf("tsp") >= 1));
        result = (result || (line.indexOf("tbsp") >= 1));
        result = (result || (line.indexOf("diced") >= 1));
        result = (result || (line.indexOf("minced") >= 1));
        result = (result || (line.indexOf("shredded") >= 1));
        result = (result || (line.indexOf("thawed") >= 1));
        result = (result || (line.indexOf("warmed") >= 1));
        result = (result || (line.indexOf("oz") >= 1));
        result = (result || (line.indexOf("chopped") >= 1));
        result = (result || (line.indexOf("sliced") >= 1));
        result = (result || (line.indexOf("peeled") >= 1));
        result = (result || (line.indexOf("mashed") >= 1));
        result = (result || (line.indexOf(" c.") >= 1));
        result = (result || (line.indexOf(" c ") >= 1));
        result = (result || (line.indexOf(" tb ") >= 1));
        result = (result || (line.indexOf(" ts ") >= 1));
        result = (result || (line.indexOf("pkg") >= 1));
        result = (result || (line.indexOf("box") >= 1));
        result = (result || (line.indexOf("packs") >= 1));
        result = (result || (line.indexOf("ounce") >= 1));
        result = (result || (line.indexOf("slices") >= 1));
        result = (result || (line.indexOf("gallon") >= 1));
        result = (result || (line.indexOf("dash") >= 1));
        result = (result || (line.indexOf("can") >= 1));
        result = (result || (line.indexOf("small") >= 1));
        result = (result || (line.indexOf("medium") >= 1));
        result = (result || (line.indexOf("large") >= 1));
        result = (result || (line.indexOf(" sm") >= 1));
        result = (result || (line.indexOf(" med") >= 1));
        result = (result || (line.indexOf(" lg") >= 1));
        result = (result || (line.indexOf(" g ") >= 1));

        if (line.indexOf("dudes") >= 1) {
            return false;
        }

        if (line.indexOf("minute meals") >= 1) {
            return false;
        }

        return result;
    }
    static boolean isnotingredient(String line) {
        boolean result = false;
        result = (result || (line.indexOf("minute") >= 1));
        result = (result || (line.indexOf("yield") >= 1));
        result = (result || (line.indexOf("time") >= 0));
        result = (result || (line.indexOf("review") >= 0));
        result = (result || (line.indexOf(" cook ") >= 0));
        result = (result || (line.indexOf("until") >= 0));
        result = (result || (line.indexOf("combine") >= 0));
        result = (result || (line.indexOf(".jpg") >= 0));
        result = (result || (line.indexOf("combine") >= 0));
        result = (result || (line.indexOf("working") >= 0));
        result = (result || (line.indexOf("preparation") >= 0));


        if (line.indexOf(",") == 0) {
            return true;
        }

        return result;
    }
}

