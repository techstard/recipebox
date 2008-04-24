
import java.io.*;

public class Parser {

    public static void main(String[] args) {
        for(int i=39;i<40;i++) {
          System.out.println("Parsing page: "+i);
            parse("page"+i+".txt","output"+i+".txt");
            
        }
    }
    static void parse(String input, String output) {
        FileInputStream in = null;
        FileOutputStream out = null;
        String page = "";
        String temp = "";
        PrintStream p = null;
        int start = 0;
        int end = 0;
        int parameter=200;

        try {
            in = new FileInputStream(input);
            BufferedReader foo = new BufferedReader(new InputStreamReader(in));

            while (foo.ready() == true) {
                String blah = foo.readLine();
                if(blah.endsWith("\r\n"))
                    page = page + blah;
                else if(blah.endsWith("\r"))
                    page = page + blah +"\n";
                else page = page + blah + "\r\n";
            }
            foo.close();
            in.close();

            out = new FileOutputStream(output);
            p = new PrintStream(out);
            page=page.toLowerCase();
            //locate the page title
            start = page.indexOf("<title>");
            end = page.indexOf("</title>");
            if ((start >= 0) && (end >= 0)) {
                temp = page.substring(start + 7, end);
                p.println("Title: " + temp.trim());
            }

            //trim the HTML to just the body of the text
            start = page.indexOf("<body");
            end = page.indexOf("</body>");
            if ((start >= 0) && (end >= 0))
                page = page.substring(start, end+7);


            
            //remove all the tag information
            temp = page;
            temp = removetags(temp);

            //locate the Ingredients
            

            int loop;
            boolean begin = false;
            boolean text = false;
            char test = 0;
            String line = "";
            String[] lines = null;
            char lf = 10;
            int number=0;
            if(temp.split("\n").length > 0) lines = temp.split("[\\s]*\n"); 
            else lines = temp.split("[\\s]*"+Character.toString(lf));
            
            for(loop=0; loop<lines.length; loop++)
            {
              lines[loop]=lines[loop].trim();
              
            }
            
            String [] ingredients = new String[lines.length];
            boolean [] status=new boolean[lines.length];
            int [] position = new int[lines.length];
            int [] distance = new int[lines.length];
            int offset=0;
            
            for(int i=0;i<lines.length;i++)
            {
              start=0;
              end=0;
              offset=0;
              String tempLine=lines[i];
                if(tempLine.length()==0)continue;
                if(Character.isDigit(tempLine.charAt(0))) {
                    if(tempLine.contains(" "))
                    {
                        for(int j=0;j<tempLine.length();j++) {
                            if(Character.isLetter(tempLine.charAt(j)))
                            {

                              
                                ingredients[number]=lines[i];
                                
                                status[number]=true;
                                number++;
                                break;
                            }
                        }
                    }
                }
                //catch an ingredient in between two ingredients
                else if(i>0 && i<(lines.length-1) &&
                        !(lines[i-1].length()==0) && Character.isDigit(lines[i-1].charAt(0)) &&
                        !(lines[i+1].length()==0) && Character.isDigit(lines[i+1].charAt(0)))
                {
                                if(i>0)                  
                                 if(number>0)                               
                                 
                                start=page.indexOf(lines[i],end);
                                offset=lines[i].length();
                                                              
                                ingredients[number]=lines[i];
                                
                                status[number]=false;
                                number++;
                }
                //catch ingredients nestled two-deep between ingredients
                else if(i>1 && i<(lines.length-1) &&
                        !(lines[i-2].length()==0) && Character.isDigit(lines[i-2].charAt(0)) &&
                        !(lines[i+1].length()==0) && Character.isDigit(lines[i+1].charAt(0)))
                {

                                ingredients[number]=lines[i];                                
                                status[number]=false;
                                number++;
                }
                //catch ingredients located two-deep within ingredients
                else if(i>0 && i<(lines.length-2) &&
                        !(lines[i-1].length()==0) && Character.isDigit(lines[i-1].charAt(0)) &&
                        !(lines[i+2].length()==0) && Character.isDigit(lines[i+2].charAt(0)))
                {
                                ingredients[number]=lines[i];
                                status[number]=false;
                                number++;
                }
                
            }
            
            int lastingredient=0;
            int firstingredient=-1;
            loop=0;
            for(loop=0; loop<number; loop++)
            {
              if(is_ingredient(ingredients[loop]))
              {
               status[loop]=true;
               if(firstingredient<0)
                 firstingredient=loop;
               
               lastingredient=loop;
              }
              else
              {
               status[loop]=false; 
              }

           }
            if(firstingredient<0)
              firstingredient=0;

            //make the ingredients block continuous
            for(loop=firstingredient; loop<lastingredient+1; loop++)
            {
              status[loop]=true;
            }
            
            if(lastingredient==0)
              lastingredient=-1;
            
            //check for any terms that are most definitely not ingredients
            for(loop=firstingredient; loop<lastingredient+1; loop++)
            {
              if(isnotingredient(ingredients[loop] ))
              {
               status[loop]=false; 
              }
            }
            
            temp="";
            //set the position measurement
            for(loop=firstingredient; loop<lastingredient+1; loop++)
            {
              start=0;
             if(loop==firstingredient)
             {
               temp=ingredients[loop];
               start=page.indexOf(temp);
               while(start<0)
               {
                 temp=temp.substring(0,temp.length()-2);
                 start=page.indexOf(temp);
               }
               position[loop]=start;
             }
               
               else
               {
               temp=ingredients[loop];
               start=page.indexOf(temp);
               while(start<0)
               {
                 temp=temp.substring(0,temp.length()-2);
                 start=page.indexOf(temp, position[loop-1]);
               }
               position[loop]=start;
               }
              
            }
            start=-1;
            end=-1;
            //set the distance measurement
            for(loop=firstingredient; loop<lastingredient; loop++)
            {
              if(status[loop])
              {
                 start=end;
                 end=loop;
                 if(start>=0)
                 {
                 distance[start]=position[end]-position[start]-ingredients[start].length();
                 }
              }
              
              
            }
            
            
            for(loop=firstingredient; loop<number; loop++)
            {
             if(status[loop])
             {
               //ingrediengts tend to be short(er)              
                 if(ingredients[loop].length()<100)
                 {
              p.println(ingredients[loop]);
                 }
                 
                 //check to see if there's any extraneous text showing
                 //up from the bottom of the HTML
                 if(distance[loop]>10000)
                 {
                  break; 
                 }
             }
            }
            
            
            
            p.close();
            out.close();
            System.out.println("Done!");
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    static String removetags(String page) {
        String temp = "";
        temp = page.replaceAll(".\n", "\r\n");
        temp = page.replaceAll("&nbsp;", "");
        int length = temp.length();
        int startScript = 0;
        int endScript = 0;
        while(temp.indexOf("<script",startScript) != -1)
        {
            startScript = temp.indexOf("<script");
            endScript = temp.indexOf("</script>",startScript);
            if(endScript == -1){
                startScript++;
                continue;
            }
            temp = temp.substring(0, startScript)+temp.substring(endScript, temp.length()-1);
            length = temp.length();
        }
        temp = temp.replaceAll("<li>", "\n");
        temp = temp.replaceAll("<br>", "\n");
        temp = temp.replaceAll("</a>", "");
        temp = temp.replaceAll("</.*?>", "\n");
        temp = temp.replaceAll("<.*?>", "");

        return temp;
    }

    static boolean is_ingredient(String line)
    {
     boolean result=false;
     result=(result||(line.indexOf("cup")>=1));
     result=(result||(line.indexOf("teasp")>=1));
     result=(result||(line.indexOf("tablesp")>=1));
     result=(result||(line.indexOf(" t ")>=1));
     result=(result||(line.indexOf("pound")>=1));
     result=(result||(line.indexOf("lbs")>=1));
     result=(result||(line.indexOf("lb")>=1));
     result=(result||(line.indexOf("pint")>=1));
     result=(result||(line.indexOf("quart")>=1));
     result=(result||(line.indexOf("qt")>=1));
     result=(result||(line.indexOf("halve")>=1));
     result=(result||(line.indexOf("pinch")>=1));
     result=(result||(line.indexOf("to taste")>=1));
     result=(result||(line.indexOf("tsp")>=1));
     result=(result||(line.indexOf("tbsp")>=1));
     result=(result||(line.indexOf("diced")>=1));
     result=(result||(line.indexOf("minced")>=1));
     result=(result||(line.indexOf("shredded")>=1));
     result=(result||(line.indexOf("thawed")>=1));
     result=(result||(line.indexOf("warmed")>=1));
     result=(result||(line.indexOf("oz")>=1));
     result=(result||(line.indexOf("chopped")>=1));
     result=(result||(line.indexOf("sliced")>=1));
     result=(result||(line.indexOf("peeled")>=1));
     result=(result||(line.indexOf("mashed")>=1));
     result=(result||(line.indexOf(" c.")>=1));
     result=(result||(line.indexOf(" c ")>=1));
     result=(result||(line.indexOf(" tb ")>=1));
     result=(result||(line.indexOf(" ts ")>=1));
     result=(result||(line.indexOf("pkg")>=1));
     result=(result||(line.indexOf("box")>=1));
     result=(result||(line.indexOf("packs")>=1));
     result=(result||(line.indexOf("ounce")>=1));
     result=(result||(line.indexOf("slices")>=1));
     result=(result||(line.indexOf("gallon")>=1));
     result=(result||(line.indexOf("dash")>=1));
     result=(result||(line.indexOf("can")>=1));
     result=(result||(line.indexOf("small")>=1));
     result=(result||(line.indexOf("medium")>=1));
     result=(result||(line.indexOf("large")>=1));
     result=(result||(line.indexOf(" sm")>=1));
     result=(result||(line.indexOf(" med")>=1));
     result=(result||(line.indexOf(" lg")>=1));
     result=(result||(line.indexOf(" g ")>=1));
   
     if(line.indexOf("dudes")>=1)
     {
       return false;
     }
     
     if(line.indexOf("minute meals")>=1)
     {
      return false; 
     }
     
    return result;
  }
    
  static  boolean isnotingredient(String line)
   {
boolean result=false;
result=(result||(line.indexOf("minute")>=1));
result=(result||(line.indexOf("yield")>=1));
result=(result||(line.indexOf("time")>=0));
result=(result||(line.indexOf("review")>=0));
result=(result||(line.indexOf(" cook ")>=0));
result=(result||(line.indexOf("until")>=0));
result=(result||(line.indexOf("combine")>=0));
result=(result||(line.indexOf(".jpg")>=0));
result=(result||(line.indexOf("combine")>=0));
result=(result||(line.indexOf("working")>=0));
result=(result||(line.indexOf("preparation")>=0));


if(line.indexOf(",")==0)
  {
  return true;
  }

  return result;
   }
}

