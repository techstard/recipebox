
import java.io.*;

public class Parser {

    public static void main(String[] args) {
        String[] url = {"http://allrecipes.com/", "http://www.recipe.com/",  "http://www.betterrecipes.com/", 
             "http://www.cooks.com/", "http://www.epicurious.com/", "http://www.recipesource.com/", 
             "http://www.cooksrecipes.com/", "http://www.chow.com/", "http://www.cookingcache.com/", "http://www.foodnetwork.com/"};
        for(int i=1;i<11;i++) {
            parse("page"+i+".txt","output"+i+".txt", url[i-1]);
        }
        //parse("page10.txt","output10.txt","www.allrecipes.com");
    }
    static void parse(String input, String output, String url) {
        FileInputStream in = null;
        String page = "";
        String temp = "";
        int start = 0;
        int end = 0;
        UnitConversion uc = new UnitConversion();
        try {
            in = new FileInputStream(input);
            BufferedReader foo = new BufferedReader(new InputStreamReader(in));

            while (foo.ready() == true) {
                String blah = foo.readLine().toLowerCase();
                if(blah.endsWith("\r\n"))
                    page = page + blah;
                else if(blah.endsWith("\r"))
                    page = page + blah +"\n";
                else page = page + blah + "\r\n";
                //System.out.println(blah);
            }
            foo.close();
            in.close();

            //out = new FileOutputStream(output);
            //p = new PrintStream(out);
            
            //locate the page title
            start = page.indexOf("<title");
            end = page.indexOf("</title>");
            if ((start >= 0) && (end >= 0)) {
                temp = page.substring(start + 7, end).replace("\n", "").replace("\r","").trim();
                
                System.out.println("Title: " + getTitle(temp, url));
            }

            
            //trim the HTML to just the body of the text
            start = page.indexOf("<body");
            end = page.indexOf("</body>");
            if ((start >= 0) && (end >= 0))
                page = page.substring(start, end+7);

            //remove all the tag information
            
            temp = removetags(page);

            //locate the Ingredients
            
            String[] lines = null;
            char lf = 10;
            if(temp.split("\n").length > 0) lines = temp.split("[\\s]*\n");
            else lines = temp.split("[\\s]*"+Character.toString(lf));
            
            for(int i=0;i<lines.length;i++)
            {
                String tempLine = lines[i].trim();
                //System.out.println(tempLine);
                if(tempLine.isEmpty())continue;
                if(Character.isDigit(tempLine.charAt(0))) {
                    if(tempLine.contains(" "))
                    {
                        for(int j=0;j<tempLine.length();j++) {
                            if(Character.isLetter(tempLine.charAt(j)))
                            {
                                System.out.println(tempLine);
                                break;
                            }
                        }
                    }
                }
                else if(i>0 && i<(lines.length-1) &&
                        !lines[i-1].isEmpty() && Character.isDigit(lines[i-1].charAt(0)) &&
                        !lines[i+1].isEmpty() && Character.isDigit(lines[i+1].charAt(0)))
                    System.out.println(lines[i]);
            }
              
            //p.close();
            //out.close();
            System.out.println("Done!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    static String removetags(String page) {
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
    static String getTitle(String title, String url) {
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
            if(var[i].trim().matches("(recipe(s)?)"));          //Single word "recipe(s)" token
            else if(url.contains(var[i].replace(" ", "")));     //token (w/out spaces) is in hostname
            else {
                if(var[i].contains("by")) var[i] = var[i].substring(0,var[i].indexOf("by"));    //Removes author info
                if(var[i].contains("at")) var[i] = var[i].substring(0,var[i].indexOf("at"));    //Removes site info
                return var[i].trim();   //This is the title (Empirically)
            }
        }
        return "";
    }
}