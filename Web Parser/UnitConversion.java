/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package regextestharness;

import java.util.*;
/**
 *
 * @author Matt
 */
public class UnitConversion {

    private static Map<String,Double> conversion = new TreeMap();
    public UnitConversion()
    {
        /*
        //mass (weight) "a pound's a pint the world round"
        //Currently, conversion is to volume
        //conversion.put("pound", 2.0);
        //conversion.put("ounce", 0.0125);
        //conversion.put("kilogram", );
        conversion.put("gram", );
        
        //volume
        conversion.put("cup", );
        conversion.put("gallon", );
        conversion.put("liter", );
        conversion.put("pint", 2);
        conversion.put("quart", );
        conversion.put("tablespoon", );
        conversion.put("teaspoon", );
         */ 
    }
    public double toCommonUnit(double value, String unit)
    {
        //Common unit is cup, but you don't need to know that
        return 0.0;
    }
}
