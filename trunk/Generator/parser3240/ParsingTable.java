/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parser3240;
import java.util.*;
import java.io.*;
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
        
        // find largest nonterminal by name length
        for (int y=0;y<rows;y++) {
            if(largestNonterminal < nonTerms.get(y).getName().length())
                largestNonterminal = nonTerms.get(y).getName().length();
        }
        
        // loop through and initialize table
        for(int x=0;x<columns;x++)
        {
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
                if(table[x][y].containsKey(cordinate))
                {
                    table[x][y].get(cordinate).add(P);
                    numRules++;
                    if(P.getRule().toString().length()*numRules > largestEntrySize)
                    {
                      largestEntrySize = P.toString().length()*numRules;  
                      System.out.println(P.toString());
                    }
                }
            }
           
        }
        return;
    }
    
    public ProductionRule getEntry(Nonterminal N, Token T) {
        try {
            return table[tokens.indexOf(T)][nonTerms.indexOf(N)].get(N+","+T).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
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
        largestEntrySize = largestEntrySize + 2*(columns-3);
        //print the row of terminals
        System.out.print(String.format("%1$-" + largestNonterminal + "s"," ") + "  ");
        for(int y=0;y<columns;y++)
        {
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
                System.out.print(String.format("%1$-" + largestEntrySize + "s",rowRule));
                
            }
            System.out.println();
        }
    }

    public void printHTMLTable()
    {
        String html = "<table border=1>";
        html += "<tr><td>M[N,T]</td>";
        
        for (Token T : tokens) {
            html += "<td>" + T + "</td>";
        }
        html += "</tr>";
        
        for(int x=0;x<rows;x++) {
            html += "<tr>";
            //first entry in the row is the row's nonterminal
            html += "<td>" + fixHTML(nonTerms.get(x).toString()) + "</td>";
            
            
            for(int y=0;y<columns;y++) {
                //print row of rules in coresponding location
                String rowRule = table[y][x].get(table[y][x].keySet().toArray()[0]).toString();
                html += "<td>" + fixHTML(rowRule) + "</td>";
                
            }
            html += "</tr>";
        }
        html += "</table>";
        try {
            FileWriter fw = new FileWriter(new File("ptable.html"));
            fw.write(html);
            System.out.println("The parse table has been written to ptable.html in the project directory.");
        } catch (IOException e) {
            System.out.println("File writing failed. Please make sure you have write access to the project directory.");
        }
        //System.out.print(html);
    }
    
    // nonterminals like "<exp>" look like HTML tags, so we have to replace
    // > and < with metacharacters &gt; and &lt;
    private String fixHTML(String tag) {
        return tag.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

}
