/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parser3240;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author mpn
 */
public class Main {

	private static ArrayList<String> GetTestProgTokens(){
		//a run of the scanner
    	String progLoc = "C:\\test-prog3.txt";
    	TINYLexer scanner = TINYLexer.GetLexer(progLoc);
    	
    	//the tokens gotten from the program.
    	//reset the scanner
    	scanner.yypushback(scanner.yylength());
    	ArrayList<String> programTokens = new ArrayList<String>();
    	  do {
              try {
				programTokens.add(scanner.yylex());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	  } while (!scanner.isZzAtEOF());
    	  return programTokens;
	}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
    	System.out.println("Hello world");
    	// if you parameterize this main function
    	// This is the Main method of JLex which allows us to generate 
    	// a scanner
    	// JLex.Main.main(arg)
    	
    	ArrayList<String> programTokens = Main.GetTestProgTokens();
          
        System.out.println("programTokens = " + programTokens);
        
        ParserGenerator parserGen = new ParserGenerator();
      
        ArrayList<Token> tokens = new ArrayList<Token>();
        
        for (String s : programTokens) {
            if (s == null)
                tokens.add(new Token("$"));
            else
                tokens.add(new Token(s));
        }
        //package body IDENT is separate SEMICOLON $
        /**tokens.add(new Token("package"));
        tokens.add(new Token("body"));
        tokens.add(new Token("IDENT"));
        tokens.add(new Token("is"));
        tokens.add(new Token("separate"));
        tokens.add(new Token("SEMICOLON"));
        tokens.add(new Token("$"));**/
        //tokens = lexer.process(fileContents);
        
        // sample tiny program token stream
        // begin ID := ID ; end $
        /**tokens.add(new Token("begin"));
        tokens.add(new Token("ID"));
        tokens.add(new Token(":="));
        tokens.add(new Token("ID"));
        tokens.add(new Token(";"));
        tokens.add(new Token("end"));
        tokens.add(new Token("$"));**/
        
        String grammar = "";
        //parserGen.feed("C:\\final.ll1");
        //parserGen.feed("C:\\louden_pg178.ll1");
        //parserGen.feed("C:\\tiny.ll1");
        parserGen.feed("C:\\tiny_grammar.ll1");
        ParsingTable parseTable = parserGen.buildParsingTable();
        //parseTable.printTable();
        parseTable.printHTMLTable();
        
        // parser driver
        // see Pg 155 of Louden
        int i = 0;
        Stack<Symbol> stack = new Stack<Symbol>();
        stack.push(new Token("$"));
        // push start symbol onto the stack
        stack.push(new Nonterminal(parserGen.getStartSymbol().getName()));
        int dep = 0;
        while (!stack.peek().equals(new Token("$")) && i < tokens.size()) {
            System.out.println("Current token: " + tokens.get(i));
            dep++;
            //if (dep >= 10) break;
            System.out.println("stack: " + stack);
            // case 1: top of stack is a token
            if (stack.peek() instanceof Token) {
                System.out.println("Found token " + stack.peek() + " at top of stack.");
                // special case: token is epsilon: just pop and discard
                if (stack.peek().equals(new Token("EPSILON"))) {
                    System.out.println("Found EPSILON and consumed it.");
                    stack.pop();
                }
                else {
                    // try to match token at top of stack with current input token
                    if (stack.peek().equals(tokens.get(i))) {
                        System.out.println("Matched token " + stack.peek() + " with top of stack.");
                        stack.pop();
                        i++;
                    }
                    // tokens don't match, so parsing fails
                    else {
                        break;
                    }
                }
            }
            // case 2: top of stack is a nonterminal
            // we have a nonterminal on top of the stack, so we expand it using
            // the parse table to push the next production onto the stack
            else if (stack.peek() instanceof Nonterminal) {
                System.out.println("Found nonterminal " + stack.peek() + " at top of stack.");
                ProductionRule rule = parseTable.getEntry((Nonterminal)stack.peek(), tokens.get(i));
                System.out.println("Next production is " + rule);
                // if parse table entry is empty, then ERROR
                if (rule == null) break;
                stack.pop();
                ArrayList<Symbol> symbols = rule.getRule();
                for (int x = symbols.size() - 1; x >= 0; x--)
                    stack.push(symbols.get(x));
            }
        }
        // we were able to consume the whole input, and we
        // have a $ at the top of the stack -- accept!
        if (stack.peek().equals(new Token("$")) && i == tokens.size()-1)
            System.out.println("Successful parse!");
        else
            System.out.println("Parse error!");
    }

}
