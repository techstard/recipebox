/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.nutch.parse.recipebox;

import java.util.Map;
import java.util.HashMap;
/**
 *
 * @author Matt
 */
public class UnitConversion {

    private static Map<String,Double> conversion = new HashMap();
    public UnitConversion()
    {
        
        //mass (weight) "a pound's a pint the world round"
        //Currently, conversion is to volume
        conversion.put("pound", 2.0);
		conversion.put(" lb", 2.0);
        conversion.put("ounce", 0.125);
		conversion.put("oz", 0.125);
        conversion.put("kilogram", .2378);
        conversion.put("gram", .0002378);
        conversion.put("clove", 0.25);
        
        //volume
        conversion.put("cup", 1.0);
		conversion.put(" c ", 1.0);
		conversion.put(" c.", 1.0);
        conversion.put("pint", 2.0);
        conversion.put("quart", 4.0);
        	conversion.put("qt.", 4.0);
        conversion.put("liter", 4.0);
        conversion.put("gallon", 16.0);
        conversion.put("tablespoon", .0625);
		conversion.put("tablesp", .0625);
		conversion.put("tbl", .0625);
		conversion.put("tb", .0625);
		conversion.put(" T ", .0625);
		conversion.put("tbsp", .0625);
        conversion.put("teaspoon", .02083);
		conversion.put("teasp", .02083);
		conversion.put(" ts ", .02083);
		conversion.put(" t ", .02083);
		conversion.put("tsp", .02083);
        conversion.put("large", 3.0);
        conversion.put("jumbo", 4.0);
        conversion.put("medium", 2.0);
        conversion.put("whole", 2.0);
        conversion.put("small", 1.0);
        conversion.put("dash", .002604);
        
          
    }
    public static double toCommonUnit(double value, String ingredient)
    {
        //Common unit is cup, but you don't need to know that        
        String[] tokens = ingredient.split(" ");
        Object[] keys = conversion.keySet().toArray();
        for(int i=0; i<tokens.length; i++) {
            for(int j=0;j<keys.length;j++) {
                if((" "+tokens[i]).contains(keys[j].toString())){
                    System.out.println("unit: '"+keys[j]+"' found in '"+ingredient+"'");
                    return value * conversion.get(keys[j]);
                }
            }
        }
        return value * conversion.get("medium");
    }
}
