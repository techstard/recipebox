/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cookbooktokenizertrainer;

import java.io.File;
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
            //Switch based on whether the wikilink has an alias
            if(wikiLink.contains("|"))
            {
                canTerm = wikiLink.substring(11,wikiLink.indexOf('|')).trim().toLowerCase();
                alias = wikiLink.substring(wikiLink.indexOf('|')+1,wikiLink.length()).trim().toLowerCase();
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
            temp.add(alias);
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
}