import lexical.LexicalAnalyzer;
import syntactical.SyntacticalAnalyzer;

import java.io.FileNotFoundException;

public class main {
    public static void main(String[] args){
        String filepath = args[0];
        try {
            LexicalAnalyzer lex_anal = new LexicalAnalyzer(filepath);
            SyntacticalAnalyzer syn_anal = new SyntacticalAnalyzer(lex_anal);
            syn_anal.run();
        } catch (FileNotFoundException e) {
            System.err.println("File ("+filepath+") not found!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
