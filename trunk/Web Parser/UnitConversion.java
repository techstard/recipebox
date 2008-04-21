/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.nutch.parse.recipebox;

import java.util.Map;
import java.util.TreeMap;
/**
 *
 * @author Matt
 */
public class UnitConversion {

    private static Map<String,Double> conversion = new TreeMap();
    public UnitConversion()
    {
        
        //mass (weight) "a pound's a pint the world round"
        //Currently, conversion is to volume
        conversion.put("pound", 2.0);
        conversion.put("ounce", 0.125);
        conversion.put("kilogram", 4.205);
        conversion.put("gram", 4205.0);
        
        //volume
        conversion.put("cup", 1.0);
        conversion.put("gallon", 16.0);
        conversion.put("liter", 4.0);
        conversion.put("pint", 2.0);
        conversion.put("quart", 4.0);
        conversion.put("tablespoon", 16.0);
        conversion.put("teaspoon", 48.0);
          
    }
    public static double toCommonUnit(double value, String unit)
    {
        //Common unit is cup, but you don't need to know that
        return 0.0;
    }
}
