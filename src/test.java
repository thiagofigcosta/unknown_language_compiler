import lexical.LexicalAnalyzer;

import java.io.FileNotFoundException;

public class test {

    public static void lexTest(int n){
        try {
            LexicalAnalyzer lex_anal=new LexicalAnalyzer("test_"+n+".lang");
            System.out.println(lex_anal.getAllTokensString());
            System.out.println(lex_anal.getTable());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        for (int i=1;i<=6;i++){
            System.out.println("Running test: "+i);
            lexTest(i);
            System.out.println("------------------------------------------");
        }
    }
}
