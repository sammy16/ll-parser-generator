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
// usage: java Main.java [GRAMMAR FILE] [PROGRAM FILE]
// e.g. $ java Main.java tiny.gram adder.tiny
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
    	System.out.println("Hello world");
    	// if you parameterize this main function
    	// This is the Main method of JLex which allows us to generate 
    	// a scanner
    	// JLex.Main.main(arg)
	
        ParserGenerator pg = new ParserGenerator();
        //testing
       //pg.feed("/Users/Jurojin/CS3240/Generator/parser3240/grammar.txt");
        //ArrayList<ProductionRule> pr;
        //pr= pg.getProductionRules("statement-list -> + - | (");
        //System.out.println(pr);
        
        
        // just testing subversion commit
        
//        ParserGenerator parserGen = new ParserGenerator();
//        ILexer lexer = new Lexer();
//        String fileContents = "";
//        ArrayList<Token> tokens = lexer.process(fileContents);
//                
//        String grammar = "";
//        parserGen.feed(grammar);
//        ParsingTable parseTable = parserGen.buildParsingTable();
//        
//        // parser driver
//        // see Pg 155 of Louden
//        int i = 0;
//        Stack<Symbol> stack = new Stack<Symbol>();
//        while (!stack.empty() && i < tokens.size()) {
//            if (stack.peek() instanceof Token) {
//                if (stack.top().equals(tokens.get(i))) {
//                    stack.pop();
//                    i++;
//                }
//            }
//            else if (stack.top() instanceof Nonterminal) {
//                ProductionRule rule = parseTable.getEntry(stack.top(), tokens.get(i));
//                stack.pop();
//                ArrayList<Symbol> symbols = rule.getSymbols();
//                for (int x = symbols.size() - 1; x >= 0; x--)
//                    stack.push(symbols.get(x));
//                // else error
//            }
//        }
//        if (stack.empty() && i == tokens.size())
//            System.out.println("Successful parse!");
//        else
//            System.out.println("Parse error!");
    }

}
