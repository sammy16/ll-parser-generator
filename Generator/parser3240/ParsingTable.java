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
public class ParsingTable {
    HashMap<String,ArrayList<ProductionRule>>[][] table; //2d array that will hold hashtable productionrules as entries
    private int rows;
    private int columns,largestEntrySize,largestNonterminal;
    ArrayList<Token> tokens;
    ArrayList<Nonterminal> nonTerms;
    
    //initialize number of rows and columns in the table to the number of nonterminals and terminals
    public ParsingTable(int numTokens, int numNonterminals,ArrayList<Token> myTokens,ArrayList<Nonterminal> myNonTerms)
    {
        rows = numNonterminals;
        columns = numTokens;
        tokens = myTokens;
        nonTerms = myNonTerms;
        table = (HashMap<String,ArrayList<ProductionRule>>[][]) new HashMap[numTokens][numNonterminals];
        for(int x=0;x<columns;x++)
        {
            if(largestNonterminal < nonTerms.get(x).getName().length())
            {
                largestNonterminal = nonTerms.get(x).getName().length();
            }
            for(int y=0;y<rows;y++)
            {
                
                table[x][y] = new HashMap<String,ArrayList<ProductionRule>>();
                table[x][y].put(myNonTerms.get(y).getName()+","+myTokens.get(x).getName(),new ArrayList<ProductionRule>());
            }
        }
        
    }
    // add rule P to M[A, a] in the table
    public void addEntry(Nonterminal A, Token a, ProductionRule P) {
        String cordinate = A.getName()+","+a.getName(); //the entry[A,a] in table
        int numRules;   //use these to figure out space padding for printing the table
        //iterate to entry [A,a] in table and add the production rule P to that entry
        for(int x=0;x<columns;x++)
        {
            numRules = 0;
            for(int y=0;y<rows;y++)
            {
                //test
                //System.out.println(table[x][y].keySet());
                //System.out.println("Does add to this cordinate? "+ table[x][y].containsKey(cordinate));
                if(table[x][y].containsKey(cordinate))
                {
                    table[x][y].get(cordinate).add(P);
                    numRules++;
                    if(P.getRule().toString().length()*numRules > largestEntrySize)
                    {
                      largestEntrySize = P.getRule().toString().length()*numRules;   
                    }
                    //test
                    // System.out.println("added " + P.getNonterminal()+"-> "+P.getRule());
                    //System.out.println("Table entry is now: " + table[x][y].get(cordinate));
                }
            }
           
        }
        return;
    }
    
    public ProductionRule getEntry(Nonterminal N, Token T) {
        return table[tokens.indexOf(T)][nonTerms.indexOf(N)].get(N+","+T).get(0);
    }
    
    //get private variable table
    public HashMap<String,ArrayList<ProductionRule>>[][] getTable()
    {
        return table;
    }
    
    //printing method to display table in a grid, first row is terminals and first column is nonterminals
    public void printTable()
    {
        String nontermHeader,terminalHeader,rowRule;
        largestEntrySize = largestEntrySize + 2*columns;
        //print the row of terminals
        System.out.print(String.format("%1$-" + largestNonterminal + "s"," ") + "  ");
        for(int y=0;y<columns;y++)
        {
            //test
           terminalHeader =table[y][0].keySet().toString().substring(table[y][0].keySet().toString().indexOf(",")+1,table[y][0].keySet().toString().length()-1);
           System.out.print(String.format("%1$-" + largestEntrySize + "s",terminalHeader));

        }
        System.out.println();
        for(int x=0;x<rows;x++)
        {
            //print nonterminal for the row
            nontermHeader = table[0][x].keySet().toString().substring(1,table[0][x].keySet().toString().indexOf(","));
            System.out.print(String.format("%1$-" + largestNonterminal + "s",nontermHeader) + "  ");
            
            for(int y=0;y<columns;y++)
            {
                //print row of rules in coresponding location
                rowRule = table[y][x].get(table[y][x].keySet().toArray()[0]).toString();
                if(rowRule.length()==0)
                {
                    //System.out.print("\t");
                }
                System.out.print(String.format("%1$-" + largestEntrySize + "s",rowRule));
                
            }
            System.out.println();
        }
    }

    /**public void printHTMLTable()
    {
        String html = "<table>";
        
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                
                
            }
        }
    }**/
}