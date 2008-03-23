/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cookbooktokenizertrainer;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
/**
 *
 * @author Matt
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    static Map<String,List> canonicalTerms = new TreeMap();
    static int numTerms = 0;
    public static void main(String[] args) {
        // TODO code application logic here
        try {
        File file = new File(".\\Wikibooks.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();
        NodeList nodeLst = doc.getElementsByTagName("page");
        int numRecipes = 0;

        loadList();
        
        for (int s = 0; s < nodeLst.getLength(); s++) {
            Node fstNode = nodeLst.item(s);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element page = (Element) fstNode;
                NodeList titleNodeList = page.getElementsByTagName("title");
                Element titleNode = (Element) titleNodeList.item(0);
                NodeList title = titleNode.getChildNodes();           
                //System.out.println("Title: '"+title.item(0).getNodeValue()+"'");
                NodeList textNodeList = page.getElementsByTagName("text");
                Element textNode = (Element) textNodeList.item(0);
                NodeList text = textNode.getChildNodes();
                parseCanonicalTerms(text.item(0).getNodeValue(), title.item(0).getNodeValue());
                numRecipes++;
            }
        }
        System.out.println("Parsed items= "+numTerms);
        printList();
        //writeList();
        sortTerms();
        } catch (Exception e) {
        e.printStackTrace();
        e.getCause();
        }
    }
    static void parseCanonicalTerms(String text, String title)
    {
        String wikiLink, canTerm, alias;
        int indx, i=0;
        //System.out.println("title: "+title);
        while(text.indexOf("[[Cookbook:",i) != -1)
        {
            numTerms++;
            indx = text.indexOf("[[Cookbook:",i);
            wikiLink = text.substring(text.indexOf("[[Cookbook:",i),text.indexOf("]]",indx));
            wikiLink = wikiLink.toLowerCase();
            wikiLink = wikiLink.replace('_',' ');
            //Switch based on whether the wikilink has an alias
            if(wikiLink.contains("|"))
            {
                canTerm = wikiLink.substring(11,wikiLink.indexOf('|')).trim();
                alias = wikiLink.substring(wikiLink.indexOf('|')+1,wikiLink.length()).trim();
            }
            else
            {
                canTerm = wikiLink.substring(11,wikiLink.length()).trim().toLowerCase();
                alias = canTerm;
            }
            addToMap(canTerm, alias);
            i = text.indexOf("]]",i)+2;
        }
    }
    static void addToMap(String canTerm, String alias)
    //Prereqs: global Map canonicalTerms
    //Input: a canonical name for use as a key, an alias of that name
    //  Process: If canTerm exists, add alias, else add both        
    //Output: an updated canonicalTerms
    {
        if(!canonicalTerms.containsKey(canTerm))
        {
            List temp = new ArrayList();
            temp.add(canTerm);
            if(!canTerm.equals(alias)) temp.add(alias);
            canonicalTerms.put(canTerm, temp);
        }
        //else(we've seen this term before, check if the alias is also a duplicate)
        else 
        {
            boolean newAlias = true;
            //System.out.println(canonicalTerms.get(canTerm));
            for(int j=0;j<canonicalTerms.get(canTerm).size(); j++)
            {
                if(alias.equals((String)canonicalTerms.get(canTerm).get(j)))
                {
                    newAlias = false;
                }
            }
            //if(it's a new alias) add it to the list
            if(newAlias) canonicalTerms.get(canTerm).add(alias);
        }
    }
    static void printList()
    {
        Object[] keyArray = canonicalTerms.keySet().toArray();
        System.out.println("TOTAL K+V= "+canonicalTerms.values().size());
        for(int i=0;i<keyArray.length;i++)
        {
            System.out.print(keyArray[i]);
            ListIterator valList = canonicalTerms.get(keyArray[i]).listIterator();
            while(valList.hasNext())
            {
                System.out.print(", "+valList.next());
            }
            System.out.println("");
        }
        
    }
    static void loadList()
    {
        try{
            FileReader fr = new FileReader("canTerms.txt");
            BufferedReader br = new BufferedReader(fr, 80);
            String line;
            String[] temp, temp2;

            while((line = br.readLine()) != null)
            {
                temp = line.split(",",2);
                temp2 = temp[1].split(",");
                for(int i=0;i<temp2.length;i++)
                {
                    addToMap(temp[0],temp2[i]);                   
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    static String canTermToString(String key)
    {
        String temp = key;
            ListIterator valList = canonicalTerms.get(key).listIterator();
            while(valList.hasNext())
            {
                temp += ","+valList.next();
            }
            temp += '\n';
            return temp;
    }
    static void writeList()
    {
        Object[] keyArray = canonicalTerms.keySet().toArray();
        //System.out.println("TOTAL K+V= "+canonicalTerms.values().size());
        try {
        FileWriter fs = new FileWriter("canTerms.txt");
        BufferedWriter out = new BufferedWriter(fs);
        for(int i=0;i<keyArray.length;i++)
        {
            out.write(canTermToString(keyArray[i].toString()));
        }
        out.close();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    static void sortTerms()
    {
        /*Don't do this very often!!!
         * User interface and file I/O for sorting canonical terms
         * 
         * prereqs: populated canonicalTerms
         * output: multiple flat files
         * */
        try{
            FileWriter ing = new FileWriter("ingredients.txt");
            FileWriter unit = new FileWriter("units.txt");
            FileWriter tool = new FileWriter("tools.txt");
            FileWriter meth = new FileWriter("methods.txt");
            FileWriter other = new FileWriter("other.txt");
            BufferedWriter ingOut = new BufferedWriter(ing);
            BufferedWriter unitOut = new BufferedWriter(unit);
            BufferedWriter toolOut = new BufferedWriter(tool);
            BufferedWriter methOut = new BufferedWriter(meth);
            BufferedWriter otherOut = new BufferedWriter(other);
            Object[] keyArray = canonicalTerms.keySet().toArray();
            for(int i=0;i<keyArray.length;i++)
            {
                System.out.println(canTermToString((String)keyArray[i]));
                System.out.println("   1) Ingredients");
                System.out.println("   2) Units");
                System.out.println("   3) Cooking Tools");
                System.out.println("   4) Cooking Methods");
                System.out.println("   5) Other\n");
                switch(System.in.read())
                {
                    case 1: ingOut.write(canTermToString(keyArray[i].toString()));
                    case 2: unitOut.write(canTermToString(keyArray[i].toString()));
                    case 3: toolOut.write(canTermToString(keyArray[i].toString()));
                    case 4: methOut.write(canTermToString(keyArray[i].toString()));
                    case 5: otherOut.write(canTermToString(keyArray[i].toString()));
                }
            }
            ingOut.close();
            unitOut.close();
            toolOut.close();
            methOut.close();
            otherOut.close();
        }catch(Exception e) {e.printStackTrace();}
    }
}