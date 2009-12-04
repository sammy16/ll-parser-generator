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
        
        //hiron grammar test
        //pg.feed("C:\\Users\\Hiron\\Documents\\Gatech09Fall\\Compilers\\Final Project\\leftfac.ll1");
        //pg.feed("C:\\leftfac.ll1");

        //pg.feed("C:\\louden_pg178.ll1");

        //pg.feed("C:\\leftrec.ll1");
        //pg.feed("C:\\louden_pg178.ll1");
        //pg.feed("C:\\beta_testcase.ll1");
        //pg.feed("C:\\tiny.ll1");

        //rachel test
        //pg.feed("/Users/Jurojin/CS3240/Generator/parser3240/louden_pg178.txt");
        //pg.feed("/Users/Jurojin/CS3240/Generator/parser3240/leftfac-3.txt");
        //pg.feed("/Users/Jurojin/CS3240/Generator/parser3240/grammar.txt");
        
        //ParsingTable parseTable = pg.buildParsingTable();
        //parseTable.printTable();
  
        // just testing subversion commit
        
        ParserGenerator parserGen = new ParserGenerator();
        ILexer lexer = new Lexer();
        String fileContents = "";
        ArrayList<Token> tokens = new ArrayList<Token>();
        //EPSILON package body IDENT is separate SEMICOLON EPSILON
        tokens.add(new Token("$"));
        tokens.add(new Token("package"));
        tokens.add(new Token("body"));
        tokens.add(new Token("IDENT"));
        tokens.add(new Token("is"));
        tokens.add(new Token("separate"));
        tokens.add(new Token("SEMICOLON"));
        tokens.add(new Token("$"));
        //tokens = lexer.process(fileContents);
        
        String grammar = "";
        parserGen.feed("C:\\final grammar.ll1");
        //parserGen.feed("C:\\louden_pg178.ll1");
        
        ParsingTable parseTable = parserGen.buildParsingTable();
        //parseTable.printTable();
        parseTable.printHTMLTable();
        
        // parser driver
        // see Pg 155 of Louden
//        int i = 0;
//        Stack<Symbol> stack = new Stack<Symbol>();
//        // push start symbol onto the stack
//        stack.push(new Nonterminal(parserGen.getStartSymbol().getName()));
//        int dep = 0;
//        while (!stack.peek().equals(new Token("$")) && i < tokens.size()) {
//            dep++;
//            if (dep >= 10) break;
//            System.out.print("stack: " + stack);
//            // case 1: top of stack is a token
//            if (stack.peek() instanceof Token) {
//                System.out.println("Found token " + stack.peek() + " at top of stack.");
//                // try to match token at top of stack with current input token
//                if (stack.peek().equals(tokens.get(i))) {
//                    stack.pop();
//                    i++;
//                }
//                // tokens don't match, so parsing fails
//                else {
//                    break;
//                }
//            }
//            // case 2: top of stack is a nonterminal
//            // we have a nonterminal on top of the stack, so we expand it using
//            // the parse table to push the next production onto the stack
//            else if (stack.peek() instanceof Nonterminal) {
//                System.out.println("Found nonterminal " + stack.peek() + " at top of stack.");
//                ProductionRule rule = parseTable.getEntry((Nonterminal)stack.peek(), tokens.get(i));
//                System.out.println("Next production is " + rule);
//                // if parse table entry is empty, then ERROR
//                if (rule == null) break;
//                stack.pop();
//                ArrayList<Symbol> symbols = rule.getRule();
//                for (int x = symbols.size() - 1; x >= 0; x--)
//                    stack.push(symbols.get(x));
//            }
//        }
//        // we were able to consume the whole input, and we
//        // have a $ at the top of the stack -- accept!
//        if (stack.peek().equals(new Token("$")) && i == tokens.size())
//            System.out.println("Successful parse!");
//        else
//            System.out.println("Parse error!");
    }

}
