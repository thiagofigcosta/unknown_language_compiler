import lexical.LexicalAnalyzer;

import java.io.FileNotFoundException;

public class test {

    public static void lexTest(int n){
        try {
            String path=test.class.getClassLoader().getResource("test_"+n+".lang").getPath().replaceAll("%20", " ");;
            LexicalAnalyzer lex_anal=new LexicalAnalyzer(path);
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
