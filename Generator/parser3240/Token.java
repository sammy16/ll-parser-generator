/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parser3240;

/**
 *
 * @author mpn
 */
public class Token {
    private String name;
    
    public Token(String nameIn) { name = nameIn; }
    
    public String getName() { return name; }
    public void setName(String nameIn) { name = nameIn; }
    
    public boolean compareTo (Object otherObj) {
        Token T2 = (Token) otherObj;
        return (this.name.equals(T2.getName()));
    }
}
