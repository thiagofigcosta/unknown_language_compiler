import code_generator.CodeGenerator;
import lexical.LexicalAnalyzer;
import syntactical.SyntacticalAnalyzer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static java.lang.System.exit;

public class main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please provide just one argument not " + args.length + "!");
            exit(1);
        }
        String input_path = args[0];
        String output_path = input_path.substring(0, input_path.lastIndexOf('.')) + ".a";
        try {
            LexicalAnalyzer lex_anal = new LexicalAnalyzer(input_path);
            SyntacticalAnalyzer syn_anal = new SyntacticalAnalyzer(lex_anal);
            syn_anal.run();
            CodeGenerator code_gen = syn_anal.getCodeGenerator();
            try (PrintWriter out = new PrintWriter(output_path)) {
                out.println(code_gen.getCode());
            }
            System.out.println("File parsed successfully!");
        } catch (FileNotFoundException e) {
            System.err.println("File (" + input_path + ") not found!");
            e.printStackTrace();
            exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            exit(1);
        }
    }
}
