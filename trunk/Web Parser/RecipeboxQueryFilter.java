package org.apache.nutch.parse.recipebox;

import org.apache.nutch.searcher.FieldQueryFilter;

import java.util.logging.Logger;

// Commons imports
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class RecipeboxQueryFilter extends FieldQueryFilter {
    private static final Log LOG = LogFactory.getLog(RecipeboxQueryFilter.class.getName());

    public RecipeboxQueryFilter() {
        /*I'm not sure this'll be necessary, but it might be nice
         *
         */  
        super("recommended", 5f);
        LOG.info("Added a recommended query");
    }
  
}