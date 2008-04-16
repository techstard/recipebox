import java.io.*;

public class Parser
{
  public static void main(String[] args)
  {
    FileInputStream in = null;
    FileOutputStream out=null;
    String page="";
    String temp= "";
    String ingredient = "";
    PrintStream p = null;
    int start=0;
    int end=0;
    
    
    
    
    try{
      in = new FileInputStream("Page10.txt");
      BufferedReader foo = new BufferedReader(new InputStreamReader(in));
      
      while(foo.ready()==true)
      {
       page=page+foo.readLine(); 
      }
      foo.close();
      in.close();
      
      out=new FileOutputStream("output.txt");
      p = new PrintStream(out);
      
      //locate the page title
      start=page.indexOf("<title>");
      end=page.indexOf("</title>");
      if((start>=0)&&(end>=0))
           {
            temp=page.substring(start+7, end);
            p.println("Title: "+temp);
        
           }
      
      
      //trim the HTML to just the body of the text
      start=page.indexOf("<body>");
      end=page.indexOf("</body>");
      if((start>=0)&&(end>=0))
      {
       page=page.substring(start+6, end);
      }
      
      //remove all the tag information
      temp=removetags(page);
      
      
      //locate the Ingredients
      
      temp=temp.toLowerCase();
      
          int loop;
          boolean begin=false;
          boolean text=false;
          int test=0;
          String line="";
      for(loop=0; loop<temp.length();loop++)
      {
        test=temp.charAt(loop);
        
        if((test>=48)&&(test<=57))
        {
            begin=true;
            
            if(text)
            {
             text=false;
             if(line.length()<100)
             {
             p.println(line);
             }
             line="";
            }
        }
        
        else if((test>=97)&&(test<=122))
        {
         text=true; 
        }
        
        else if(test==64)
        {
         if(begin)
         {
         begin=false; 
         if(line.length()<100)
         {
          p.println(line);
         }
          line="";
         }
        }

        
        if(begin)
        {
         line=line+temp.charAt(loop); 
        }
        
      }
      
      p.close();
      out.close();
      System.out.println("Done!");
    }
    catch(Exception e){}
  }
  
  
  static String removetags(String page)
  {
    int loop = 0;
    boolean copy = true;
    String temp = "";
    for (loop=0; loop<page.length(); loop++)
    {
      if (page.charAt(loop)=='<')
      {
        copy = false;
      }
      
      else if(page.charAt(loop)=='>')
      {
       copy=true; 
       temp=temp+"@";
      }
      
      else if (copy)
      {
       temp=temp.concat(String.valueOf(page.charAt(loop)));
      }
    }
    
   return temp; 
  }
  
}