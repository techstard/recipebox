//Chris Stafford
//3/21/2008

import java.io.*;

public class Parser
{
 public static void main (String args[])
 {
   FileInputStream in = null;
   int c = 0;
   int start=0;
   int end=0;
   char first= ' ';
   char second= ' ';
   String page="";
   String test="";
   String ingredient = "";
   boolean ingredients = false;
   FileOutputStream out;
   PrintStream p;
   
   try{
      out=new FileOutputStream("output.txt");
      out.close();
   in = new FileInputStream("Web Page.rtf");
   BufferedReader foo = new BufferedReader(new InputStreamReader(in));
   
   while (foo.ready()==true)
   {
     test=foo.readLine();

     page=page+test;
 
   
   }
   foo.close();
   in.close();
   
   } catch(Exception E) {}
 
  //locate the start of the ingredients block
start = page.indexOf("h2");
page=page.substring(start);
start = page.indexOf("h2");
end=page.indexOf("</ul");
page=page.substring(start, end);


   while(true)
   {
    start=page.indexOf("<li>");
    if(start>=0)
    {
    page=page.substring(start+4);
    end=page.indexOf("</li>");
    ingredient=page.substring(0, end);
    System.out.println(ingredient);
    //Locate the numerical portion of each ingredient
    int x=0;
    int lastnum=0;
    String numeric="";
    while(x<ingredient.length())
    {
     if((ingredient.charAt(x)<=57)&&(ingredient.charAt(x)>=48))
     {
       lastnum=x;
     }
     x++; 
    }
    lastnum++;
    numeric=ingredient.substring(0,lastnum);
    x=0;
    double numvalue=0.0;
    double base=0.0;
    double multiple=0.0;
    double top=0.0;
    double bottom=0.0;
    Boolean mult=false;
    Boolean mixedfraction=false;
    Boolean fraction=false;
    
    try
    {

      out=new FileOutputStream("output.txt", true);
      p=new PrintStream(out, true);

    
    
    
    while(x<numeric.length())
    {
           if((!mult)&&(!mixedfraction)&&(!fraction))
           {
                  if((ingredient.charAt(x)<=57)&&(ingredient.charAt(x)>=48))
                  {
            numvalue=numvalue*10;
            numvalue=numvalue+(ingredient.charAt(x)-48);
                  }
                  else if(ingredient.charAt(x)==40)
                  {
                   mult=true; 
                   base=numvalue;
                   System.out.println("Found a multiplication!");
                  }
                  else if (ingredient.charAt(x)==32)
                  {
                   mixedfraction=true; 
                   base=numvalue;
                   System.out.println("Found a mixed fraction!");
                  }
                  
                  else if (ingredient.charAt(x)==47)
                  {
                    fraction=true;
                    top=numvalue;
                   System.out.println("Found a / sign"); 
                  }
           }
           
           else if ((mult)&&(ingredient.charAt(x)<=57)&&(ingredient.charAt(x)>=48))
           {
             multiple=multiple*10;
             multiple=multiple+(ingredient.charAt(x)-48);
           }
           
           else if ((mixedfraction))
           {
            if(ingredient.charAt(x)==47)
            {
             System.out.println("Found the division in the mixed fraction");
             fraction=true;
            }
            else if(fraction)
            {
             bottom=bottom*10;
             bottom=bottom+(ingredient.charAt(x)-48);
            }
            else
            {
              top=top*10;
              top=top+(ingredient.charAt(x)-48);
            }
            
            
           }
           
           else if ((fraction)&&(ingredient.charAt(x)<=57)&&(ingredient.charAt(x)>=48))
           {
            bottom=bottom*10; 
            bottom=bottom+(ingredient.charAt(x)-48);
           }
           x++;
    }
    
    if(mult)
    {
     numvalue=base*multiple; 
    }
    else if (mixedfraction)
    {
     numvalue=(base)+(top/bottom); 
    }
    else if (fraction)
    {
     numvalue=top/bottom; 
    }
    
    //print to a file
    p.println(ingredient);
    p.println(numvalue);
    p.close();
    out.close();
        }
    catch(Exception e)
    {
     System.out.println("Unable to open file!"); 
    }
      
    
    }
    

       
    else
    {
      break;
    }         
   }
   
 }


}

