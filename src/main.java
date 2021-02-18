import lexical.LexicalAnalyzer;

import java.io.FileNotFoundException;

public class main {
    public static void main(String[] args){
        String filepath = args[0];
        try {
            LexicalAnalyzer lex_anal=new LexicalAnalyzer(filepath);
            System.out.println(lex_anal.getAllTokensString());
            System.out.println(lex_anal.getTable());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
