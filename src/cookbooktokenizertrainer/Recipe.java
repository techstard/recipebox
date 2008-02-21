/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cookbooktokenizertrainer;

/**
 *
 * @author Matt
 */
public class Recipe {
    String title;
    String description;
    String instructions;
    
    String getTitle()
    {
        return title;
    }
    String getDesc()
    {
        return description;
    }
    String getInst()
    {
        return instructions;
    }
    void setTitle(String newTitle)
    {
        title = newTitle;
    }
    void setDesc(String newDesc)
    {
        description = newDesc;
    }
    void setInst(String newInst)
    {
        instructions = newInst;
    }
}
