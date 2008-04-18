package org.apache.nutch.parse.recipebox;

// JDK import
import java.util.logging.Logger;

// Commons imports
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


// Nutch imports
import org.apache.nutch.util.LogUtil;
import org.apache.nutch.fetcher.FetcherOutput;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.parse.Parse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;

// Lucene imports
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;

public class RecipeboxIndexer implements IndexingFilter {
    
  public static final Log LOG = LogFactory.getLog(RecipeboxIndexer.class.getName());
  
  private Configuration conf;
  
  public RecipeboxIndexer() {
  }

  public Document filter(Document doc, Parse parse, Text url, 
    CrawlDatum datum, Inlinks inlinks)
    throws IndexingException {
    /* data flow: pull all the fields out of the parse object as in:
            String recommendation = parse.getData().getMeta("Recommended");
     * store them in doc objects as Store.YES and Index.UN_TOKENIZED as in:
     *             Field recommendedField = 
     *           new Field("recommended", recommendation, 
     *              Field.Store.YES, Field.Index.UN_TOKENIZED);
     *       recommendedField.setBoost(5.0f);
     */
      String ingStr = parse.getData().getContentMeta().get("ingredient");
      String amtStr = parse.getData().getContentMeta().get("values");
      if(ingStr != null)
      {
          String[] ingArray = ingStr.split(";");
          String[] amtArray = amtStr.split(";");
          for(int i=0;i<ingArray.length;i++)
          {
              //String field = parse.getData().getContentMeta().names()[i];
              String field = "ingredient";
              String value = ingArray[i];
              doc.add(new Field(field, value, Field.Store.YES, Field.Index.UN_TOKENIZED));
              doc.add(new Field(ingArray[i], amtArray[i], Field.Store.YES, Field.Index.UN_TOKENIZED));
              System.out.println("Indexer: Recipebox added ingredient "+value);
          }
      }
      else {System.out.println("Indexer: Recipebox skipping");}

    return doc;
  }
  
  public void setConf(Configuration conf) {
    this.conf = conf;
  }

  public Configuration getConf() {
    return this.conf;
  }  
}