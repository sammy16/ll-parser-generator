/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parser3240;

/**
 * This Symbol class is the superclass of the Nonterminal and Token classes.
 * This allows us to create an ArrayList<Symbol>, which is useful for doing
 * things like storing the right-hand side of a production rule after it is
 * read in from a grammar file. The Symbol class itself only stores a name
 * and stubs out an equals() method. The Nonterminal and Token classes each
 * are responsible for actually implementing the equals() method.
 * @author mpn
 */
public class Symbol {
    private String name;
    
    public Symbol(String nameIn) { name = nameIn; }
    
    public String getName() { return name; }
    public void setName(String nameIn) { name = nameIn; }
    
    public boolean equals (Object otherObj) {
        Symbol S2 = (Symbol) otherObj;
        return (S2.getName().equals(this.getName()));
    }
}
