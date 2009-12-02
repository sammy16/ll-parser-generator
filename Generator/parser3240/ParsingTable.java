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
    private int columns;
    
    //initialize number of rows and columns in the table to the number of nonterminals and terminals
    public ParsingTable(int numTokens, int numNonterminals,ArrayList<Token> tokens,ArrayList<Nonterminal> nonTerms)
    {
        rows = numNonterminals;
        columns = numTokens;
        table = (HashMap<String,ArrayList<ProductionRule>>[][]) new HashMap[numTokens][numNonterminals];
        for(int x=0;x<columns;x++)
        {
            for(int y=0;y<rows;y++)
            {
                //test
                //System.out.println(x+" "+y);
                table[x][y] = new HashMap<String,ArrayList<ProductionRule>>();
                table[x][y].put(nonTerms.get(y).getName()+","+tokens.get(x).getName(),new ArrayList<ProductionRule>());
                //test
                //System.out.println(table[x][y].keySet());
            }
        }
        
    }
    // add rule P to M[A, a] in the table
    public void addEntry(Nonterminal A, Token a, ProductionRule P) {
        String cordinate = A.getName()+","+a.getName(); //the entry[A,a] in table
        //test
        //System.out.println("\nAttemptong to add at cordinate"+cordinate);
        //iterate to entry [A,a] in table and add the production rule P to that entry
        for(int x=0;x<columns;x++)
        {
            for(int y=0;y<rows;y++)
            {
                //test
                //System.out.println(table[x][y].keySet());
                //System.out.println("Does add to this cordinate? "+ table[x][y].containsKey(cordinate));
                if(table[x][y].containsKey(cordinate))
                {
                    table[x][y].get(cordinate).add(P);
                    //test
                   // System.out.println("added " + P.getNonterminal()+"-> "+P.getRule());
                    //System.out.println("Table entry is now: " + table[x][y].get(cordinate));
                }
            }
        }
        return;
    }
    
    //get private variable table
    public HashMap<String,ArrayList<ProductionRule>>[][] getTable()
    {
        return table;
    }
    
    //printing method to display table in a grid, first row is are terminals and first column is nonterminals
    public void printTable()
    {
        String nontermHeader,terminalHeader,rowRule;
        //print the row of terminals
        for(int y=0;y<columns;y++)
        {
            //test
           terminalHeader =table[y][0].keySet().toString().substring(table[y][0].keySet().toString().indexOf(",")+1,table[y][0].keySet().toString().length()-1);
           System.out.print(" "+terminalHeader);

        }
        System.out.println();
        for(int x=0;x<rows;x++)
        {
            //print nonterminal for the row
            nontermHeader = table[0][x].keySet().toString().substring(1,table[0][x].keySet().toString().indexOf(","));
            System.out.print(nontermHeader+"  ");
            
            for(int y=0;y<columns;y++)
            {
                //print row of rules in coresponding location
                rowRule = table[y][x].get(table[y][x].keySet().toArray()[0]).toString();
                if(rowRule.length()==0)
                {
                    //System.out.print("\t");
                }
                System.out.print(rowRule+"  ");
                
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
