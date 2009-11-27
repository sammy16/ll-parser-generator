/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parser3240;

/**
 * The Token class is used to store token symbols. This class and the Nonterminal
 * class are both subclasses of Symbol, which allows us to do things
 * like create an ArrayList<Symbol>, which can hold the right-hand side of
 * production rules after they are read in from a grammar file.
 * @author mpn
 */
public class Token extends Symbol {
    
    public Token(String nameIn) { super(nameIn); }
    
    @Override
    public boolean equals (Object otherObj) {
        Token T2 = (Token) otherObj;
        return (super.getName().equals(T2.getName()));
    }
}
