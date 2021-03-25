import lexical.LexicalAnalyzer;
import syntactical.SyntacticalAnalyzer;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class test {

    private static final boolean USE_ONLY_STDOUT=true; // intellij has a bug that mixed stdout and stderr

    public static PrintStream getStdout(){
        return System.out;
    }

    public static PrintStream getStderr(){
        if (USE_ONLY_STDOUT)
            return getStdout();
        return System.err;
    }

    public static void lexTest(int n){
        String path=test.class.getClassLoader().getResource("tests_2_lex-fixed/test_"+n+".lang").getPath().replaceAll("%20", " ");;
        try {
            LexicalAnalyzer lex_anal=new LexicalAnalyzer(path);
            getStdout().println(lex_anal.getAllTokensString());
            getStdout().println(lex_anal.getTable());
        } catch (FileNotFoundException e) {
            getStderr().println("File ("+path+") not found");
            e.printStackTrace(getStderr());
        }
    }

    public static void part_1(){
        for (int i=1;i<=8;i++){
            getStdout().println("Running test: "+i);
            lexTest(i);
            getStdout().println("------------------------------------------");
        }
    }

    public static void synTest(int n){
        String base_folder="tests_3_syn-fixed";
//        base_folder="tests_2_lex-fixed";  // uncomment to parse files with erros
        String path = test.class.getClassLoader().getResource(base_folder+"/test_" + n + ".lang").getPath().replaceAll("%20", " ");
        try {
            LexicalAnalyzer lex_anal = new LexicalAnalyzer(path);
            SyntacticalAnalyzer syn_anal = new SyntacticalAnalyzer(lex_anal);
            syn_anal.run();
            getStdout().println("File ("+path+") is ok syntactically;");
        } catch (FileNotFoundException e) {
            getStderr().println("File ("+path+") not found");
            e.printStackTrace(getStderr());
        } catch (Exception e) {
            e.printStackTrace(getStderr());
        } finally {
            getStderr().flush();
            getStdout().flush();
        }
    }

    public static void part_2(){
        SyntacticalAnalyzer.setStderr(getStderr());
        for (int i=1;i<=8;i++){
            getStdout().println("Running test: "+i);
            getStdout().println("------------------------------------------");
            synTest(i);
            getStdout().println("------------------------------------------");
        }
    }

    public static void main(String[] args){
        part_2();
    }
}
