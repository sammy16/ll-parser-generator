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
    //HashMap<String,ArrayList<ProductionRule>>[][] table; //2d array that will hold hashtable productionrules as entries
    HashMap<Nonterminal, HashMap<Token, ProductionRule>> table;
    private int rows;
    private int columns, largestEntrySize;
    //private int columns,largestEntrySize,largestNonterminal;
    ArrayList<Token> tokens;
    ArrayList<Nonterminal> nonTerms;
    
    //initialize number of rows and columns in the table to the number of nonterminals and terminals
    public ParsingTable(int numTokens, int numNonterminals,ArrayList<Token> myTokens,ArrayList<Nonterminal> myNonTerms)
    {
        rows = numNonterminals;
        columns = numTokens;
        tokens = myTokens;
        nonTerms = myNonTerms;
        //table = (HashMap<String,ArrayList<ProductionRule>>[][]) new HashMap[numTokens][numNonterminals];
        table = new HashMap<Nonterminal, HashMap<Token, ProductionRule>>();
        /**for(int x=0;x<columns;x++)
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
        }**/
        
    }
    // add rule P to M[A, a] in the table
    public void addEntry(Nonterminal A, Token a, ProductionRule P) {
        if (table.get(A) == null)
            table.put(A, new HashMap<Token, ProductionRule>());
        table.get(A).put(a, P);
        
        /**String cordinate = A.getName()+","+a.getName(); //the entry[A,a] in table
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
        }**/

        return;
    }
    
    public ProductionRule getEntry(Nonterminal N, Token T) {
        HashMap<Token, ProductionRule> tableRow = table.get(N);
        if (tableRow == null)
            return null;
        return tableRow.get(T);
    }
    
    //get private variable table
    public HashMap<Nonterminal, HashMap<Token, ProductionRule>> getTable()
    {
        return table;
    }
    
    //printing method to display table in a grid, first row is terminals and first column is nonterminals
    public void printTable()
    {
        System.out.println("Parsing Table");
        System.out.println("(one entry at a time)");
        // HashMap<Nonterminal, HashMap<Token, ProductionRule>>
        for (Nonterminal N : table.keySet()) {
            for (Token T : table.get(N).keySet()) {
                System.out.println(N+","+T + " = " + table.get(N).get(T));
            }
        }
        
        /**String nontermHeader,terminalHeader,rowRule;
        largestEntrySize = largestEntrySize + 2*columns-2;
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
        }**/
    }

    public void printHTMLTable()
    {
        String html = "<html><head><title>Parsing Table</title></head><body><table border=1>";
        html += "<tr><td>M[N,T]</td>";
        // print the list of tokens across the top row
        for (Token T : tokens)
            html += "<td>" + T + "</td>";
        html += "</tr>";
        
        for (Nonterminal N : table.keySet()) {
            html += "<tr>";
            // the first entry in this row is the nonterminal for the row
            html += "<td>" + fixHTML(N.toString()) + "</td>";
            // now we print every entry in the current row
            for (Token T : table.get(N).keySet()) {
                html += "<td>" + fixHTML(table.get(N).get(T).toString()) + "</td>";
            }
            html += "</tr>";
        }
        html += "</table></body></html>";
        System.out.println();
        System.out.println("The following is the HTML output. Paste it into a file and save as ptable.html and then open in a web browser.");
        System.out.println("It is much easier to read than the text output.");
        System.out.println();
        System.out.print(html);
    }
    
    // nonterminals like "<exp>" look like HTML tags, so we have to replace
    // > and < with metacharacters &lt; and &gt;
    private String fixHTML(String tag) {
        return tag.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
