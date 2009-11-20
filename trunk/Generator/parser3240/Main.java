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
    public static void main(String[] args) {
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
