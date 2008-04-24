
import java.io.*;

public class Parser {

    public static void main(String[] args) {
        FileInputStream in = null;
        FileOutputStream out = null;
        String page = "";
        String temp = "";
        PrintStream p = null;
        int start = 0;
        int end = 0;

        try {
            in = new FileInputStream("page10.txt");
            BufferedReader foo = new BufferedReader(new InputStreamReader(in));

            while (foo.ready() == true) {
                String blah = foo.readLine();
                if(blah.endsWith("\r\n"))
                    page = page + blah;
                else if(blah.endsWith("\r"))
                    page = page + blah +"\n";
                else page = page + blah + "\r\n";
                //System.out.println(blah);
            }
            foo.close();
            in.close();

            out = new FileOutputStream("output10.txt");
            p = new PrintStream(out);
            
            //locate the page title
            start = page.indexOf("<title>");
            end = page.indexOf("</title>");
            if ((start >= 0) && (end >= 0)) {
                temp = page.substring(start + 7, end);
                p.println("Title: " + temp);
            }

            //trim the HTML to just the body of the text
            start = page.toLowerCase().indexOf("<body");
            end = page.toLowerCase().indexOf("</body>");
            if ((start >= 0) && (end >= 0))
                page = page.substring(start, end+7);

            //remove all the tag information
            temp = removetags(page);

            //locate the Ingredients
            temp = temp.toLowerCase();

            int loop;
            boolean begin = false;
            boolean text = false;
            char test = 0;
            String line = "";
            String[] lines = null;
            char lf = 10;
            if(temp.split("\n").length > 0) lines = temp.split("\n");
            else lines = temp.split(Character.toString(lf));
            
            for(int i=0;i<lines.length;i++)
            {
                lines[i] = lines[i].trim();
                if(lines[i].isEmpty())continue;
                if(Character.isDigit(lines[i].charAt(0))) {
                    p.println(lines[i]);
                }
                else if(i>0 && i<(lines.length-1) &&
                        !lines[i-1].isEmpty() && Character.isDigit(lines[i-1].charAt(0)) &&
                        !lines[i+1].isEmpty() && Character.isDigit(lines[i+1].charAt(0)))
                    p.println(lines[i]);
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
        int loop = 0;
        boolean copy = true;
        String temp = "";
        temp = page.replaceAll(".\n", "\r\n");
        temp = page.replaceAll("<script.*?</script>","");
        temp = temp.replaceAll("<.*?>", "\n");

        return temp;
    }
}