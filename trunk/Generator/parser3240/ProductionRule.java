/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parser3240;

import java.util.*;

/**
 * ProductionRule stores a pair of Nonterminal and ArrayList<Symbol>.
 * @author mpn
 */
public class ProductionRule {
    private Nonterminal N;
    private ArrayList<Symbol> rule;
    
    public ProductionRule(Nonterminal NIn, ArrayList<Symbol> ruleIn) {
        N = NIn;
        rule = ruleIn;
    }
    
    public Nonterminal getNonterminal() { return N; }
    public ArrayList<Symbol> getRule() { return rule; }
    
    public void setNonterminal(Nonterminal NIn) { N = NIn; }
    public void setRule(ArrayList<Symbol> ruleIn) { rule = ruleIn; }
}
