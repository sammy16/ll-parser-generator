/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parser3240;
import java.util.*;
import java.io.*;

/**
 * ParserGenerator contains the meat of the code for our project. The Main
 * method feeds the contents of a grammar file to this class, which it uses to
 * construct a list of all the production rules in that file. These production
 * rules then have left-recursion and common-prefix removed, to get them into
 * a format suitable for LL(1) parsing. Next, the First and Follow sets are
 * computed for each non-terminal, followed by computing the Predict set
 * for each production rule. Finally, once we have the Predict set, we can
 * build the ParsingTable, which is what our parser driver (in Main) needs
 * to parse a program!
 * @author mpn
 */
public class ParserGenerator {
    private ArrayList<Nonterminal> nonterminalList;
    private ArrayList<Token> tokenList;
    private Symbol startSymbol;
    private ArrayList<ProductionRule> rules;
    private HashMap<Nonterminal, ArrayList<ProductionRule>> allRules;
    private HashMap<Nonterminal, ArrayList<Token>> firstSets;
    private HashMap<Nonterminal, ArrayList<Token>> followSets;
    private HashMap<ProductionRule, ArrayList<Token>> predictSets;
    
    public ParserGenerator() {
        nonterminalList = new ArrayList<Nonterminal>();
        tokenList = new ArrayList<Token>();
        startSymbol = null;
        rules = new ArrayList<ProductionRule>();
        allRules = new HashMap<Nonterminal, ArrayList<ProductionRule>>();
        firstSets = new HashMap<Nonterminal, ArrayList<Token>>();
        followSets = new HashMap<Nonterminal, ArrayList<Token>>();
        predictSets = new HashMap<ProductionRule, ArrayList<Token>>();
    }
    
    // perhaps rather than grammarFile, this should take a single
    // line as input? That way you could add more rules later, rather
    // than doing it all as a single glob.
    public void feed(String grammarFile)throws Exception {
        // read in the grammar file
        BufferedReader grFileBuf = new BufferedReader(new FileReader(grammarFile));
        String toks; //string to hold the terminals on the first line of the grammar file
        String nonTerms; //string to hold nonterminals on second line of the grammar file
        String grule=""; //holds each grammar rule as it is read from the grammar file
        ArrayList<String> rmLeft; //array to hold the rules after they have had thier left recursion removed
        ArrayList<String> rmCommon;  //the grammar with both left recusion removed and common prefix
        
        toks = grFileBuf.readLine();    //terminals
        Scanner genScanner = new Scanner(toks); //a scanner for breaking up the tokens
        genScanner.next();  //consume unecessary %Tokens word
        
        //add tokens to token list
        while(genScanner.hasNext())
        {
            tokenList.add(new Token(genScanner.next()));
        }
        //test
        System.out.println("token list = " + tokenList);
        
        nonTerms = grFileBuf.readLine();       //nonterminals
        genScanner = new Scanner(nonTerms);
        
        genScanner.next();  //consume unecessary %Non-terminals word
        
        //add the nonterminals to terminal list
        while(genScanner.hasNext())
        {
            nonterminalList.add(new Nonterminal(genScanner.next()));
        }
        //test
        System.out.println("List of nonterminals = "+ nonterminalList);
        
        //grFileBuf.readLine(); //read uneccessary line %Rules
        grule = grFileBuf.readLine();
        
        startSymbol = new Symbol(grule.substring(grule.indexOf(" ")+1,grule.indexOf('>')+1));
        System.out.println("Start symbole = "+ startSymbol);
        //takes each grammar rule one at a time and removes left recursion
        //then adds the new rules created from the recusion removal to a list representing the new grammar 
        
        //gather all the raw grammar rules
        ArrayList<String> gRules = new ArrayList<String>();
        do
        {	
        	if(grule == null)
        	{
        		break;
        	}
        	
        	if(!grule.startsWith("%"))
        	{
        		gRules.add(grule);
        	}
            
        }while((grule = grFileBuf.readLine()) != null);
        
        allRules = removeLeftRecursion(gRules); //RemoveLeftRecursion returns the rules hashed against their nonterminals.
        //test
       
        
        //once left recursion has been removed from the grammar then common prefix must be fixed
        allRules = removeCommonPrefix(allRules);
   
        //finally production rules are created from each rule in the grammar list and added to rules list
       /* for(int i = 0;i< rmCommon.size();i++)
        {
            rules.addAll(getProductionRules(rmCommon.get(i)));
        }*/
       
    }
    
    ///Takes in the Hashed Production Rules that have had Immediate Left Recursion removed
    private HashMap<Nonterminal, ArrayList<ProductionRule>> removeCommonPrefix(
			HashMap<Nonterminal, ArrayList<ProductionRule>> map) {
		// TODO Auto-generated method stub
    
    	ArrayList<Nonterminal> keyList = new ArrayList<Nonterminal>();
    	keyList.addAll(map.keySet());
    	for (Nonterminal key : keyList )
    	{	
    		ArrayList<ProductionRule> matchingRules = map.get(key);
    		System.out.println(getMaximalCommonPrefixProductionRules(matchingRules));
    	}
		return null;
	}
    
    private ArrayList<ProductionRule> getMaximalCommonPrefixProductionRules(ArrayList<ProductionRule> rules){
    	
    	if(rules.size() == 1 || rules.size() == 0){
    		return new ArrayList<ProductionRule>(); // if there can't be matches, return an empty list
    	}
    	
    	
    	ArrayList<ArrayList<Symbol>> foundPrefix = new ArrayList<ArrayList<Symbol>>();
    	for(ProductionRule outer : rules){
    		for(ProductionRule inner : rules){    		
    			ArrayList<Symbol> commonPrefix = new ArrayList<Symbol>();
    			getCommonPrefix(commonPrefix, outer.getRule(), inner.getRule());
    			if(!containsPrefix(foundPrefix, commonPrefix)){
	    			if(commonPrefix.size() > 0){   				
	    				foundPrefix.add(commonPrefix);
	    			}
    			}
    		}
    	}
    	
    	ArrayList<Symbol> maxPrefix = prefixOfMaximalLength(foundPrefix);
    	return getProductionRulesStartingWith(maxPrefix, rules);
    }
    
    private ArrayList<ProductionRule> getProductionRulesStartingWith(ArrayList<Symbol> prefix, ArrayList<ProductionRule> rules){
    	ArrayList<ProductionRule> retColl = new ArrayList<ProductionRule>();
    	if(prefix != null){
	    	for(ProductionRule pr : rules){
		    	boolean mismatch = false;
		    	if(pr.getRule().size() >= prefix.size()){
		    		for(int i=0; i < prefix.size() - 1; i++){
			    		if(!prefix.get(i).equals(pr.getRule().get(i))){
			    			mismatch = true;
			    			break;
			    		}
			    	}
		    		if(!mismatch){
		    			retColl.add(pr);
		    		}
		    	}
	    	}
    	}
    	return retColl;
    }
    
    private ArrayList<Symbol> prefixOfMaximalLength(ArrayList<ArrayList<Symbol>> prefixes){
    	int maxSize = 0;
    	ArrayList<Symbol> maxP = null;
    	for(ArrayList<Symbol> p : prefixes){
    		if(maxSize < p.size()){
    			maxSize = p.size();
    			maxP = p;
    		}
    	}
    	
    	return maxP;
    }
    
    private boolean containsPrefix(ArrayList<ArrayList<Symbol>> prefixes, ArrayList<Symbol> p){
    	for(ArrayList<Symbol> containedPre : prefixes){
    		if(p.equals(containedPre)){
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private void getCommonPrefix(ArrayList<Symbol> commonPrefix, ArrayList<Symbol> rule1, ArrayList<Symbol> rule2){
    	
    	if(rule1.size() == 0 || rule2.size() == 0 || rule1.equals(rule2) ){
    		return;
    	}
    	
    	if( rule1.get(0).equals(rule2.get(0))){
    		ArrayList<Symbol> r1Clone = new ArrayList<Symbol>(rule1);
    		ArrayList<Symbol> r2Clone = new ArrayList<Symbol>(rule2);
    		r1Clone.remove(0);
    		r2Clone.remove(0);
    		commonPrefix.add(rule1.get(0));
    	    getCommonPrefix(commonPrefix, r1Clone, r2Clone);
    	}
    }
    
	///Hashes the rules on the basis of the nonterminal of the rule
    private HashMap<Nonterminal, ArrayList<ProductionRule>> hashRulesByNonterminal(ArrayList<ProductionRule> prRules)
    {
    	HashMap<Nonterminal, ArrayList<ProductionRule>> hashed = new HashMap<Nonterminal, ArrayList<ProductionRule>>();
        Nonterminal nontermSym;
        int n=0;
        
        while(n<prRules.size())
        {
            nontermSym = prRules.get(n).getNonterminal();
            ArrayList<ProductionRule> newRules = new ArrayList<ProductionRule>();      //list of production rules that belong to the same nonterminal
            while(prRules.get(n).getNonterminal().getName().equals(nontermSym.getName()))
            {
                newRules.add(prRules.get(n));
                n++; 
                if(n>= prRules.size())
                {
                    break;
                }
            }
            hashed.put(nontermSym, newRules);
        }
    	return hashed;
    }
    

    ///Removes the instances of immediate left recursion from the grammar
    ///we use the General immediate left recursion removal method described on
    /// pg. 158 of Louden Chap 4 (I have an older edition of the book, so it might
    /// be the case that your page numbers may not match up.
    private HashMap<Nonterminal, ArrayList<ProductionRule>> removeLeftRecursion(ArrayList<String> gRules) {
		
    	ArrayList<ProductionRule> prRules = new ArrayList<ProductionRule>();
    	for(String lineRule : gRules )
        {
        	ArrayList<ProductionRule> prodRules = this.getProductionRules(lineRule);
        	prRules.addAll(prodRules);
        }
    	
    	HashMap<Nonterminal, ArrayList<ProductionRule>> map = this.hashRulesByNonterminal(prRules);
    	ArrayList<Nonterminal> keyList = new ArrayList<Nonterminal>();
    	keyList.addAll(map.keySet());
    	for (Nonterminal key : keyList )
    	{	
    		ArrayList<ProductionRule> matchingRules = map.get(key);
    		ArrayList<ProductionRule> newMatchingRules = new ArrayList<ProductionRule>();
    		ArrayList<ProductionRule> primeRules = new ArrayList<ProductionRule>();
    		boolean hasLeftRecursion = this.NonterminalHasLeftRecursion(key, matchingRules);
    		
    		if(hasLeftRecursion)
    		{
    			//we create a new rule to act as the prime value for the ProductionRule
    		    Nonterminal aPrimeSymbol = new Nonterminal(key.getName() + "'");
    			ProductionRule aPrimeEpsilonRule = new ProductionRule(aPrimeSymbol, new ArrayList<Symbol>());
    			aPrimeEpsilonRule.getRule().add(Symbol.EPSILON); // add epsilon rule to aPrime
    			ArrayList<ProductionRule> aPrimeRules = new ArrayList<ProductionRule>();
    			aPrimeRules.add(aPrimeEpsilonRule);
    			
    			map.put(aPrimeSymbol, aPrimeRules);
    			
    			for (ProductionRule pr : ( (ArrayList<ProductionRule>) matchingRules.clone())) 
    			{	// clone so we can edit matchingRules
    			
    				Symbol startingSymbol = pr.getRule().get(0);
    			
    				if(startingSymbol.equals(key)) //this is a rule that commits left recursion
    				{
    					matchingRules.remove(pr); // remove the offending production rule
    					pr.getRule().remove(0); //remove the recursive call
    					ArrayList<Symbol> newRule = new ArrayList<Symbol>();
    			
    					newRule.addAll(pr.getRule());
    					newRule.add(aPrimeSymbol);
    					ProductionRule newPrimeRule = new ProductionRule(aPrimeSymbol, newRule );
    					aPrimeRules.add(newPrimeRule);
    				}
    				else // the rule does not have left recursion 
    				{
    					matchingRules.remove(pr);
    					
    					ArrayList<Symbol> newRule = new ArrayList<Symbol>();
    	    			
    					newRule.addAll(pr.getRule());
    					newRule.add(aPrimeSymbol);
    					ProductionRule newARule = new ProductionRule(key, newRule);
    					matchingRules.add(newARule);
    				}
    			}
    		}
    	}
		return map;
	}
    
    private boolean NonterminalHasLeftRecursion(Nonterminal key, ArrayList<ProductionRule> matchingRules)
    {
    	boolean hasLeftRecursion = false;
		for(ProductionRule pr : matchingRules)//clone so there's no iterator mishap
		{
			Symbol startingSymbol = pr.getRule().get(0);
			
			if(startingSymbol.equals(key)) //this means that we are dealing with a rule that has Left
			{												//recursion
				System.out.println(pr);	
    			hasLeftRecursion = true;            		
			}
		}
		return hasLeftRecursion;
    }
    
    

	//Takes a grammar rule in the for of a string as a parameter and produces/returns a list of production rules
    public ArrayList<ProductionRule> getProductionRules(String grammarRule)
    {
        Scanner gScan = new Scanner(grammarRule).useDelimiter(":");
        Scanner symScan;
        Nonterminal nonTerm; //the symbol on the left side of the arrow for the rule
        String rightSyms;
        String sym;
        nonTerm = new Nonterminal(gScan.next());  //get the nonterminal on left side of the arrow
        ArrayList<Symbol> rSideSyms;     //list of symbols that go on the right side of the production rule
        ArrayList<ProductionRule> productions = new ArrayList<ProductionRule>();
        gScan = new Scanner(gScan.next());
        //grabs the sections of the rule bordered by "|" and takes those symbols to create a production rule
        //Example grammar rule = <S> -> <T> d | b | c  the first production rule made will be <S> -> <T> d
        gScan.useDelimiter("\\|");
        while(gScan.hasNext())
        {
           rightSyms = gScan.next();
           //test
           //System.out.println("The right hand symbol is "+ rightSyms);
           rSideSyms = new ArrayList<Symbol>();
           symScan = new Scanner(rightSyms);
           //traverses symbols
           while(symScan.hasNext())
           {
               sym = symScan.next().trim();
               //test
               //System.out.println(sym);
               //checks to see if the symbol is a nonterminal or a terminal then adds
               //that symbol to the right side of the production rule
               if(tokenList.contains((new Symbol(sym))))
               {
                 rSideSyms.add(new Token(sym));
               }
               else if(nonterminalList.contains((new Symbol(sym))))
               {
                   rSideSyms.add(new Nonterminal(sym));
               }
           }
           productions.add(new ProductionRule(nonTerm,rSideSyms));
        }
        
        return productions;
    }

//    public ParsingTable buildParsingTable() {
//        // compute first() sets
//        // (create hashtable from each nonterminal to its first() set)
//        System.out.println("Computing First sets...");
//        computeFirstSets();
//        
//        // compute follow() sets
//        // (create hashtable from each nonterminal to its follow() set)
//        System.out.println("Computing Follow sets...");
//        computeFollowSets();
//        
//        // compute predict() sets using first() and follow() sets
//        // (create hashtable from each production rule to its predict() set)
//        System.out.println("Computing Predict sets...");
//        computePredictSets();
//        
//        // initialize new ParsingTable
//        
//        // iterate through [rule, predict_set] key-value pairs in hashtable
//        // and add entry in parse table for each
//        return null;
//    }
//    
//    private ProductionRule removeLeftRecursion(ProductionRule rule) {
//        return null;
//    }
//    
//    private ProductionRule removeCommonPrefix(ProductionRule rule) {
//        return null;
//    }
//    
//    private void computeFirstSets() {
//        for (Nonterminal N : nonterminalList)
//            firstSets.put(N, first(N));
//    }
//    
//    // can we use Set<Token> here?
//    private ArrayList<Token> first(Symbol S) {
//        // S is any terminal (which includes epsilon)
//        if (S instanceof Token) {
//            ArrayList<Token> singleton = new ArrayList<Token>();
//            singleton.add((Token)S);
//            return singleton;
//        }
//        
//        ArrayList<Token> ret = new ArrayList<Token>();
//        // okay, so it's a nonterminal
//        // iterate through each rule for this nonterminal
//        for (ProductionRule R : allRules.get((Nonterminal) S)) {
//            // okay, we are on a particular rule
//            ArrayList<Symbol> symbols = R.getRule();
//            boolean hasEpsilon = false;
//            // now, for each symbol in that rule sequence...
//            for (Symbol X_i : symbols) {
//                hasEpsilon = false;
//                // we add the First set the current symbol
//                // if that First set contains epsilon, we note that, so that
//                // we can get the First set of the NEXT symbol, too.
//                for (Token T : first(X_i)) {
//                    if (T.getName().equals("EPSILON"))
//                        hasEpsilon = true;
//                    else
//                        ret.add(T);
//                }
//                if (!hasEpsilon)
//                    break;
//            }
//            // if hasEpsilon is true, this implies that every symbol in the
//            // sequence had epsilon in its First set, and so the First set
//            // for the whole rule must contain epsilon, as well.
//            if (hasEpsilon)
//                ret.add(new Token("EPSILON"));
//        }
//        return removeDups(ret);
//    }
//    
//    /**
//     * Louden page 168
//     * This method assumes computeFirstSets() has already been called,
//     * creating a cache of first(N) for each nonterminal.
//     * @param alpha
//     * @return
//     */
//    private ArrayList<Token> first(ArrayList<Symbol> alpha) {
//       ArrayList<Token> ret = first(alpha.get(0));
//       boolean hasEpsilon = ret.contains(new Token("EPSILON"));
//       ret.remove(new Token("EPSILON"));
//       int i = 1;
//       while (hasEpsilon && i < alpha.size()) {
//           ArrayList<Token> nextFirstSet = null;
//           Symbol nextSymbol = alpha.get(i);
//           // if it's a nonterminal, it's already computed and cached
//           if (nextSymbol instanceof Nonterminal)
//               nextFirstSet = firstSets.get((Nonterminal)nextSymbol);
//           // if it's a terminal, it will be computed instantly anyway
//           else
//               nextFirstSet = first(nextSymbol);
//           
//           if (!nextFirstSet.contains(new Token("EPSILON")))
//               hasEpsilon = false;
//           for (Token T : nextFirstSet)
//               ret.add(T);
//           ret.remove(new Token("EPSILON"));
//           i++;
//       }
//       
//       return removeDups(ret);
//    }
//    
//    
//     /**
//     * Remove duplicates from an ArrayList<Token>.
//     * Probably not the most efficient way.
//     * @param list
//     * @return
//     */
//    private ArrayList<Token> removeDups (ArrayList<Token> list) {
//        ArrayList<Token> goodSet = new ArrayList<Token>();
//        for (Token T : list)
//            if (!goodSet.contains(T))
//                goodSet.add(T);
//        return goodSet;
//    }
//
//    private void computeFollowSets() {
//        for (Nonterminal N : nonterminalList)
//            followSets.put(N, follow(N));
//    }
//    
//    // Compute the follow() set for a given nonterminal N by
//    // iterating through every single production rule.
//    //
//    // This is wickedly inefficient, but simple and it works.
//    // If there's extra time, I'll rewrite it.
//    private ArrayList<Token> follow(Nonterminal N) {
//        ArrayList<Token> ret = new ArrayList<Token>();
//        
//        // for every single production rule in the grammar...
//        for (ProductionRule P : rules) {
//            // current nonterminal
//            Nonterminal A = P.getNonterminal();
//            // current rule
//            ArrayList<Symbol> rule = P.getRule();
//            // for each symbol in the rule
//            for (int i = 0; i < rule.size(); i++) {
//                Symbol S = rule.get(i);
//                // if it's a nonterminal, it might be the one we're looking for
//                if (S instanceof Nonterminal) {
//                    // indeed, we found our sought-after nonterminal in the sequence!
//                    if (N.equals((Nonterminal)S)) {
//                        // thus, we add its First set to the Follow set result
//                        ArrayList<Token> first = first(new ArrayList<Symbol>(rule.subList(i+1, rule.size())));
//                        boolean hasEpsilon = first.contains(new Token("EPSILON"));
//                        first.remove(new Token("EPSILON"));
//                        for (Token T : first)
//                            ret.add(T);
//                        // if the First set contains epsilon, then we also have to
//                        // add the Follow set of the current nonterminal to this Follow set.
//                        if (hasEpsilon) {
//                            for (Token T : follow(A))
//                                ret.add(T);
//                        }
//                    }//end if
//                }//end if
//            }//end for
//        }//end for
//        return ret;
//    }
//    
//    // page 178 Louden
//    private void computePredictSets() {
//        for (ProductionRule P : rules) {
//            ArrayList<Token> temp = new ArrayList<Token>();
//            
//            Nonterminal A = P.getNonterminal();
//            ArrayList<Symbol> alpha = P.getRule();
//            ArrayList<Token> first = first(alpha);
//            for (Token T : first)
//                temp.add(T);
//            
//            if (first.contains(new Token("EPSILON"))) {
//                for (Token T : follow(A))
//                    temp.add(T);
//            }
//            
//            predictSets.put(P, temp);
//        }
//    }
    
}//end class
