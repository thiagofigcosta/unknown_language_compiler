import lexical.LexicalAnalyzer;
import syntactical.SyntacticalAnalyzer;

import java.io.FileNotFoundException;

public class test {

    public static void lexTest(int n){
        String path=test.class.getClassLoader().getResource("tests_2_lex-fixed/test_"+n+".lang").getPath().replaceAll("%20", " ");;
        try {
            LexicalAnalyzer lex_anal=new LexicalAnalyzer(path);
            System.out.println(lex_anal.getAllTokensString());
            System.out.println(lex_anal.getTable());
        } catch (FileNotFoundException e) {
            System.err.println("File ("+path+") not found");
            e.printStackTrace();
        }
    }

    public static void part_1(){
        for (int i=1;i<=8;i++){
            System.out.println("Running test: "+i);
            lexTest(i);
            System.out.println("------------------------------------------");
        }
    }

    public static void synTest(int n){
        String path = test.class.getClassLoader().getResource("tests_3_syn-fixed/test_" + n + ".lang").getPath().replaceAll("%20", " ");
        try {
            LexicalAnalyzer lex_anal = new LexicalAnalyzer(path);
            SyntacticalAnalyzer syn_anal = new SyntacticalAnalyzer(lex_anal);
            syn_anal.run();
            System.out.println("File ("+path+") is ok syntactically;");
        } catch (FileNotFoundException e) {
            System.err.println("File ("+path+") not found");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void part_2(){
        for (int i=1;i<=8;i++){
            System.out.println("Running test: "+i);
            synTest(i);
            System.out.println("------------------------------------------");
        }
    }

    public static void main(String[] args){
        part_2();
    }
}
