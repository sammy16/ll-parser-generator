/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parser3240;
import java.util.*;

/**
 *
 * @author mpn
 */
public class ParserGenerator {
    ArrayList<ProductionRule> rules;
    // private HashMap<Nonterminal, ArrayList<Token>> firstSets;
    // private HashMap<Nonterminal, ArrayList<Token>> followSets;
    // private HashMap<Nonterminal, ProductionRule> allRules;
    
    public ParserGenerator() {
        rules = new ArrayList<ProductionRule>();
    }
    
    public void feed(String grammarFile) {
        // read line 1 - %Tokens
        // read line 2 - %%Non-terminals
        // read line 3 - %%%Rules
        // read each production rule
        // run removeLeftRecursion and removeCommonPrefix on each production rule
        //
        // add each production rule to the rules ArrayList
        //
        // also add each rule to the allRules hashtable, so we have them
        // bucketed by nonterminal, which will be useful when computing the
        // first() and follow() sets.
    }

    public ParsingTable buildParsingTable() {
        // compute first() set
        // (create hashtable from each nonterminal to its first() set)
        // compute follow() set
        // (create hashtable from each nonterminal to its follow() set)
        // compute predict() sets using first() and follow() sets
        // (create hashtable from each production rule to its predict() set)
        // initialize new ParsingTable
        
        // iterate through [rule, predict_set] key-value pairs in hashtable
        // and add entry in parse table for each
        return null;
    }
    
    private ProductionRule removeLeftRecursion(ProductionRule rule) {
        return null;
    }
    
    private ProductionRule removeCommonPrefix(ProductionRule rule) {
        return null;
    }
    
    // can we use Set<Token> here?
    private ArrayList<Token> first(Nonterminal N) {
        return null;
    }
    
    private ArrayList<Token> follow(Nonterminal N) {
        return null;
    }
    
    private ArrayList<Token> predict(ProductionRule rule) {
        return null;
    }
}
