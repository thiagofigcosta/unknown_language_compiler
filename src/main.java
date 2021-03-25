import lexical.LexicalAnalyzer;
import syntactical.SyntacticalAnalyzer;

import java.io.FileNotFoundException;

import static java.lang.System.exit;

public class main {
    public static void main(String[] args){
        if (args.length!=1){
            System.err.println("Please provide just one argument not "+args.length+"!");
            exit(1);
        }
        String filepath = args[0];
        try {
            LexicalAnalyzer lex_anal = new LexicalAnalyzer(filepath);
            SyntacticalAnalyzer syn_anal = new SyntacticalAnalyzer(lex_anal);
            syn_anal.run();
            System.out.println("File analyzed successfully!");
        } catch (FileNotFoundException e) {
            System.err.println("File ("+filepath+") not found!");
            e.printStackTrace();
            exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            exit(1);
        }
    }
}
