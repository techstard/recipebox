//Chris Stafford
//3/21/2008

import java.io.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

public class Parser
{
 public static void main (String args[])
 {
   try
   {
    DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    dbf.setIgnoringElementContentWhitespace(true);
    dbf.setValidating(false);
    db=dbf.newDocumentBuilder();
    Document page;
    String reader="";
    int x=0;
    int y=0;
    FileInputStream in=new FileInputStream("Page2.txt");
    BufferedReader foo= new BufferedReader (new InputStreamReader(in));
    FileOutputStream output=new FileOutputStream("output.txt");
    PrintStream p=new PrintStream(output, true);
    
    while(foo.ready()==true)
    {
      reader=reader+foo.readLine();
    }
    
    x=reader.indexOf("<body>");
    y=reader.indexOf("</body>");
    System.out.println(x+" "+y);
    //reader=reader.substring(x, y+7);
    p.println(reader);
    page=db.parse(new InputSource(new StringReader(reader)));
   }
   
   catch(Exception e){System.out.println("HAD AN ERROR!");}
 }

}

