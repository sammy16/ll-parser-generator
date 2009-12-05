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
    private ParsingTable LL1table;
    
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
    
    public Symbol getStartSymbol() {
        return startSymbol;
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
        
        toks = grFileBuf.readLine();    //terminals
        Scanner genScanner = new Scanner(toks); //a scanner for breaking up the tokens
        genScanner.next();  //consume unecessary %Tokens word
        
        //add tokens to token list
        while(genScanner.hasNext())
        {
            tokenList.add(new Token(genScanner.next()));
        }
        System.out.println("token list = " + tokenList);
        
        // consume possible blank line
        nonTerms = grFileBuf.readLine();
        
        if (nonTerms.equals(""))
            nonTerms = grFileBuf.readLine();       //nonterminals
        genScanner = new Scanner(nonTerms);
        
        genScanner.next();  //consume unecessary %Non-terminals word
        
        //add the nonterminals to terminal list
        while(genScanner.hasNext())
        {
            nonterminalList.add(new Nonterminal(genScanner.next()));
        }
      
        System.out.println("List of nonterminals = "+ nonterminalList);
        
        // consume possible blank line
        grule = grFileBuf.readLine();
        
        if (grule.equals(""))
            grule = grFileBuf.readLine();
        
        startSymbol = new Symbol(grule.substring(grule.indexOf(" ")+1,grule.indexOf('>')+1));
        System.out.println("Start symbol = "+ startSymbol);
        
        //System.out.println("!!!" + getProductionRules("<compilation> : IDENT | "));
        
        
        
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
        	
                // ignore %Rules line as well as blank lines or lines w/ only spaces
        	if(!grule.startsWith("%") && !grule.trim().equals(""))
        	{
                    //insert Epsilon token into the right places in the grammar rule
                    //and add the grammar rule to the list
        		//gRules.add(insertEpsilon(grule));
                    gRules.add(grule);
        	}
            
        }while((grule = grFileBuf.readLine()) != null);
        //for (String gr : gRules)
        //    System.out.println(gr);
        
        System.out.println("... after removing left recursion, we have:");
        allRules = removeLeftRecursion(gRules); //RemoveLeftRecursion returns the rules hashed against their nonterminals.    
        //printGrammar();
        
        //once left recursion has been removed from the grammar then common prefix must be fixed
        allRules = removeCommonPrefix(allRules);
        System.out.println("... and, finally, after removing common prefix:");
        //printGrammar();
        
        // okay, now all our rules are fixed.
        // allRules is the hashmap from nonterminal to a list of production rules
        // we use allRules to create "rules", which is a straight-up list of rules
        ArrayList<Nonterminal> keyList = new ArrayList<Nonterminal>();
        keyList.addAll(allRules.keySet());

        for (Nonterminal key : keyList) {
            rules.addAll(allRules.get(key));
            
            if (!nonterminalList.contains(key))
                nonterminalList.add(key);
        }
       
    }
    public String insertEpsilon(String rule)
    {
        //if the string ends in a space followed by no other chars
        //insert Epsilon ot the end
        if(rule.matches(".*[\\s.*$]"))
        {
            return rule = rule + "EPSILON";
        }
        else if(rule.matches(".*: \\|.*")) //if a rule has a : followed by an | insertEpsilon afer the space
        {
            return rule.replaceAll(": \\|", ": EPSILON \\|");
        }
        else if(rule.matches(".*\\| \\|.*"))
        {
            return rule.replaceAll("\\| \\|", "\\| EPSILON \\|");
        }
        
        return rule;
    }
    ///Takes in the Hashed Production Rules that have had Immediate Left Recursion removed
    private HashMap<Nonterminal, ArrayList<ProductionRule>> removeCommonPrefix(
			HashMap<Nonterminal, ArrayList<ProductionRule>> map) {
		// TODO Auto-generated method stub
    
    	boolean madeChange = false;
    	ArrayList<Nonterminal> keyList = new ArrayList<Nonterminal>();
    	keyList.addAll(map.keySet());
    	for (Nonterminal key : keyList )
    	{	
    		ArrayList<ProductionRule> matchingRules = map.get(key);
    		ArrayList<Symbol> cp = new ArrayList<Symbol>();
    		ArrayList<ProductionRule> commonPrefRules = getMaximalCommonPrefixProductionRules(matchingRules, cp);
    		if(commonPrefRules.size() > 1){ // if we've found some production rules that have commonPrefix
    			madeChange = true;
    			//replace with A => cp Beta1 | cp Beta2 |... We use the hashcode for the star
    			// with A => cp A*
    			//		A* =>Beta1 | Beta2 | EPSILON
    			Nonterminal aStar = new Nonterminal(key.getName() + key.getName().hashCode());
    			
    			//create the new rule that replaces all the production rules associated with the common prefix
    			ArrayList<Symbol> replacementRule = new ArrayList<Symbol>();
    			replacementRule.addAll(cp);
    			replacementRule.add(aStar);
    			ProductionRule replacementPR = new ProductionRule(key, replacementRule);
    			matchingRules.removeAll(commonPrefRules);
    			matchingRules.add(replacementPR);
    			
    			ArrayList<ProductionRule> aStarPRS = new ArrayList<ProductionRule>();
    			for(ProductionRule r : commonPrefRules){
    				ArrayList<Symbol> newStarRule = new ArrayList<Symbol>();
    				for(int i = cp.size(); i < r.getRule().size(); i++){
    					newStarRule.add(r.getRule().get(i));
    				}
    				if(newStarRule.size() == 0){
    					//newStarRule.add(Symbol.EPSILON);
                                        newStarRule.add(new Token("EPSILON"));
    				}
    				ProductionRule newStarPR = new ProductionRule(aStar, newStarRule);
    				aStarPRS.add(newStarPR);
    			}
    			map.put(aStar, aStarPRS);
    		}
    	}
    	
		if(madeChange){
			removeCommonPrefix(map);
		}
		else{
			return map;
		}
		return map; // should never get here
	}
    
    private ArrayList<ProductionRule> getMaximalCommonPrefixProductionRules(ArrayList<ProductionRule> rules,
    		ArrayList<Symbol> cp){
    	
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
    	if(maxPrefix != null){
    		cp.addAll(maxPrefix);
    	}
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
    			//aPrimeEpsilonRule.getRule().add(Symbol.EPSILON); // add epsilon rule to aPrime
                        aPrimeEpsilonRule.getRule().add(new Token("EPSILON")); // add epsilon rule to aPrime
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
                    //System.out.println(pr);
                    Symbol startingSymbol = pr.getRule().get(0);
                    if(startingSymbol.equals(key)) //this means that we are dealing with a rule that has Left
                    {												//recursion	
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
           rSideSyms = new ArrayList<Symbol>();
           // special case: the rule is blank space. This means epsilon
           if (rightSyms.trim().equals(""))
           {
               rSideSyms.add(new Token("EPSILON"));
           }
           // otherwise, the rule is broken down into its individual symbols
           else
           {
               symScan = new Scanner(rightSyms);
               //traverses symbols
               while(symScan.hasNext())
               {
                   sym = symScan.next().trim();
                   //System.out.println("sym = " + sym);
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
           }
           productions.add(new ProductionRule(nonTerm,rSideSyms));
        }
        
        return productions;
    }

    public ParsingTable buildParsingTable() {
        // compute first() sets
        System.out.println("Computing First sets...");
        firstSets = computeFirstSets();
        
        // compute follow() sets
        System.out.println("Computing Follow sets...");
        followSets = computeFollowSets();
        
        // compute predict() sets using first() and follow() sets
        System.out.println("Computing Predict sets...");
        predictSets = computePredictSets();
        
        // testing
        // print First, Follow, and Predict sets
        for (Nonterminal N : firstSets.keySet())
            System.out.println("first(" + N + ") = " + firstSets.get(N));
        
        for (Nonterminal N : followSets.keySet())
            System.out.println("follow(" + N + ") = " + followSets.get(N));
        
        for (ProductionRule R : predictSets.keySet())
            System.out.println("predict(" + R + ") = " + predictSets.get(R));
        
        // initialize new ParsingTable
        LL1table = new ParsingTable(tokenList.size(),nonterminalList.size(),tokenList,nonterminalList);

        //iterator for the rules of the predict set
        Collection ruls = predictSets.keySet();
        Iterator r = ruls.iterator();
        
        // iterate through [rule, predict_set] key-value pairs in hashtable
        ProductionRule nonT;
        ArrayList<Token> tks;
        while(r.hasNext())
        {
            nonT = (ProductionRule)r.next();
            tks = predictSets.get(nonT);   //list of tokens for each rule
            // iterate through tokens of predict set and add entry in parse table for the production rule
            for(Token t: tks)
            {
                LL1table.addEntry(nonT.getNonterminal(), t, nonT);
            }
        }
        return LL1table;
    }

    private HashMap<Nonterminal, ArrayList<Token>> computeFirstSets() {
        HashMap<Nonterminal, ArrayList<Token>> firstSetsLocal = new HashMap<Nonterminal, ArrayList<Token>>();
        for (Nonterminal N : nonterminalList) {
            ArrayList<Token> temp = first(N);
            firstSetsLocal.put(N, temp);
        }
        return firstSetsLocal;
    }
    
    private ArrayList<Token> first(Symbol S) {
        // S is any terminal (which includes epsilon)
        if (S instanceof Token) {
            ArrayList<Token> singleton = new ArrayList<Token>();
            singleton.add((Token)S);
            return singleton;
        }
        
        HashSet<Token> ret = new HashSet<Token>();
        // okay, so it's a nonterminal
        // iterate through each rule for this nonterminal
        for (ProductionRule R : allRules.get((Nonterminal)S)) {
            // okay, we are on a particular rule
            ArrayList<Symbol> symbols = R.getRule();
            boolean hasEpsilon = false;
            // now, for each symbol in that rule sequence...
            for (Symbol X_i : symbols) {
                hasEpsilon = false;
                // we add the First set the current symbol
                // if that First set contains epsilon, we note that, so that
                // we can get the First set of the NEXT symbol, too.
                for (Token T : first(X_i)) {
                    if (T.getName().equals("EPSILON"))
                        hasEpsilon = true;
                    else
                        ret.add(T);
                }
                if (!hasEpsilon)
                    break;
            }
            // if hasEpsilon is true, this implies that every symbol in the
            // sequence had epsilon in its First set, and so the First set
            // for the whole rule must contain epsilon, as well.
            if (hasEpsilon)
                ret.add(new Token("EPSILON"));
        }
        ArrayList<Token> retAsList = new ArrayList<Token>();
        retAsList.addAll(ret);
        return retAsList;
    }
    
    /**
     * Louden page 168
     * This method assumes computeFirstSets() has already been called,
     * creating a cache of first(N) for each nonterminal.
     * @param alpha
     * @return
     */
    private ArrayList<Token> first(ArrayList<Symbol> alpha) {
        
        if (alpha.size() == 0) {   
            ArrayList<Token> temp = new ArrayList<Token>();
            //temp.add(new Token("EPSILON"));
            return temp;
        }
       
       // we use a set so that duplicates are automatically ignored
       HashSet<Token> ret = new HashSet<Token>();
       ArrayList<Token> firstAlpha = first(alpha.get(0));
       ret.addAll(firstAlpha);
       
       boolean hasEpsilon = ret.contains(new Token("EPSILON"));
       ret.remove(new Token("EPSILON"));
       int i = 1;
       while (hasEpsilon && i < alpha.size()) {
           ArrayList<Token> nextFirstSet = null;
           Symbol nextSymbol = alpha.get(i);
           // if it's a nonterminal, it's already computed and cached
           if (nextSymbol instanceof Nonterminal) {
               nextFirstSet = firstSets.get((Nonterminal)nextSymbol);
           }
           // if it's a terminal, it will be computed instantly anyway
           else
               nextFirstSet = first(nextSymbol);
           
           if (!nextFirstSet.contains(new Token("EPSILON")))
               hasEpsilon = false;
           for (Token T : nextFirstSet)
               ret.add(T);
           ret.remove(new Token("EPSILON"));
           i++;
       }
       
       //return removeDups(ret);
       ArrayList<Token> retAsList = new ArrayList<Token>();
       retAsList.addAll(ret);
       return retAsList;
    }

    // Compute Follow sets for all nonterminals
    private HashMap<Nonterminal, ArrayList<Token>> computeFollowSets() {
        HashMap<Nonterminal, ArrayList<Token>> followSetsLocal = new HashMap<Nonterminal, ArrayList<Token>>();
        
        // initialize follow sets here
        // follow(startSymbol) = {$}, and the rest are empty
        for (Nonterminal N : nonterminalList) {
            if (N.getName().equals(startSymbol.getName())) {
                ArrayList<Token> temp = new ArrayList<Token>();
                temp.add(new Token("$"));
                followSetsLocal.put(N, temp);
            }
            else {
                followSetsLocal.put(N, new ArrayList<Token>());
            }
        }
        
        ArrayList<Token> ret = new ArrayList<Token>();
        
        // we keep track of whether any changes are made to the follow sets during each pass
        // when no changes have been made, we're at stasis and we terminate
        boolean changed = true;
        // while the follow sets are still "active" (changing), keep making passes
        while (changed) {
            changed = false;
            for (Nonterminal N : nonterminalList) {
                for (ProductionRule prodn : allRules.get(N)) {
                    ArrayList<Symbol> prodelements = prodn.getRule();
                    int k = prodelements.size();
                    for (int i = 0; i < k; i++) {
                        Symbol S = prodelements.get(i);
                        if (S instanceof Nonterminal) {
                            Nonterminal X = (Nonterminal)S;
                            ArrayList<Token> xFollow = followSetsLocal.get(X);
                            ArrayList<Symbol> prodend = new ArrayList<Symbol>(prodelements.subList(i+1, k));
                            
                            if (allNullable(prodend)) {
                                ArrayList<Token> nFollow = followSetsLocal.get(N);
                                // add N follow set to X follow set; check for changes
                                boolean changed2 = false;
                                for (Token T : nFollow) {
                                    if (!xFollow.contains(T)) {
                                        changed2 = true;
                                        xFollow.add(T);
                                    }
                                }
                                changed = changed || changed2;
                            }
                            // first set
                            ArrayList<Symbol> followSymbols = new ArrayList<Symbol>(prodelements.subList(i+1, k));
                            ArrayList<Token> first = first(followSymbols);
                            // add first set to xFollow; check for changes
                            boolean changed3 = false;
                            for (Token T : first) {
                                if (!xFollow.contains(T)) {
                                    changed3 = true;
                                    xFollow.add(T);
                                }
                            }
                            changed = changed || changed3;
                            followSetsLocal.put(X, xFollow);
                        }//end if
                    }
                }
            }
        }
        return followSetsLocal;
    }
    
    // a symbol string is nullable if the whole thing can through some
    // series of productions be mapped to epsilon
    private boolean allNullable(ArrayList<Symbol> symbols) {
        for (Symbol S : symbols) {
            if (S instanceof Token && !S.getName().equals("EPSILON"))
                return false;
            if (S instanceof Nonterminal) {
                if (!firstSets.get((Nonterminal)S).contains(new Token("EPSILON")))
                    return false;
            }
        }
        return true;
    }
    
    // page 178 Louden
    private HashMap<ProductionRule, ArrayList<Token>> computePredictSets() {
        HashMap<ProductionRule, ArrayList<Token>> predictSetsLocal = new HashMap<ProductionRule, ArrayList<Token>>();
        // we compute the predict set for each production rule in the grammar
        for (ProductionRule P : rules) {
            ArrayList<Token> temp = new ArrayList<Token>();
            
            Nonterminal A = P.getNonterminal();
            ArrayList<Symbol> alpha = P.getRule();
            // to start, we get the first set of the right-hand side
            ArrayList<Token> first = first(alpha);
            for (Token T : first)
                temp.add(T);
            
            // if that first set contains alpha, then we also add the follow
            // set to the predict set.
            if (first.contains(new Token("EPSILON"))) {
                for (Token T : followSets.get(A))
                    temp.add(T);
            }
            // hash it up!
            predictSetsLocal.put(P, temp);
        }
        
        return predictSetsLocal;
    }
    
    public void printGrammar() {
        for (Nonterminal N : allRules.keySet()) {
            for (ProductionRule R : allRules.get(N)) {
                System.out.println(R);
            }
        }
    }
    
}//end class
