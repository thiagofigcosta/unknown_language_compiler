package syntactical;

import lexical.Lexeme;
import lexical.LexicalAnalyzer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*
program     ::= init [decl-list] begin stmt-list stop
decl-list   ::= decl ";" { decl ";"}
decl        ::= ident-list is type
ident-list  ::= identifier {"," identifier}
type        ::= integer
                | string
                | real
stmt-list   ::= stmt ";" { stmt ";" }
stmt        ::= assign-stmt
                | if-stmt
                | do-stmt
                | read-stmt
                | write-stmt
assign-stmt ::= identifier ":=" simple_expr
if-stmt     ::= if "(" condition ")" begin stmt-list end
                | if "(" condition ")" begin stmt-list end else begin stmt-list end
condition   ::= expression
do-stmt     ::= do stmt-list do-suffix
do-suffix   ::= while "(" condition ")"
read-stmt   ::= read "(" identifier ")"
write-stmt  ::= write "(" writable ")"
writable    ::= simple-expr
expression  ::= simple-expr
                | simple-expr relop simple-expr
simple-expr ::= term
                | simple-expr addop term
term        ::= factor-a
                | term mulop factor-a
factor-a    ::= factor
                | not factor
                | "-" factor
factor      ::= identifier
                | constant
                | "(" expression ")"
relop       ::= "=" | ">" | ">=" | "<" | "<=" | "<>"
addop       ::= "+" | "-" | or
mulop       ::= "*" | "/" | and
 */

public class SyntacticalAnalyzer {

    private final LexicalAnalyzer lex;
    private Lexeme cur_token;
    private int error_counter;
    private final Stack<GrammarRule> parse_stack;
    private static PrintStream stderr=System.err;

    public SyntacticalAnalyzer(LexicalAnalyzer lex){
        this.lex=lex;
        this.cur_token=lex.getToken();
        this.error_counter=0;
        this.parse_stack =new Stack<>();
    }

    public void run() throws Exception {
        program();
        eat(Lexeme.Type.EOF);
        if(this.error_counter>0){
            throw new Exception("There was "+error_counter+" errors during syntactical analysis");
        }
    }

    public static void setStderr(PrintStream stderr) {
        SyntacticalAnalyzer.stderr = stderr;
    }

    private static Lexeme.Type[] listToNativeArray(List<Lexeme.Type> list){
        Lexeme.Type[] out=new Lexeme.Type[list.size()];
        for (int i=0;i<list.size();i++)
            out[i]=list.get(i);
        return out;
    }

    private void displayError(String msg, int line){
        this.error_counter++;
        stderr.println("Error at line: "+line+"\n\terr: "+msg);
    }

    private void panic(){
        if (this.parse_stack.size()<=0){
            return;
        }
        List<Lexeme.Type> panic_at_the_parser=new ArrayList<>();
        panic_at_the_parser.add(Lexeme.Type.EOF);
        switch (this.parse_stack.peek()){ // FOLLOW OF NON-TERMINAL PROCEDURES
            case PROGRAM:
                break;
            case DECL_LIST:
                panic_at_the_parser.add(Lexeme.Type.BEGIN);
                break;
            case DECL:
            case TYPE:
            case STMT:
            case ASSIGN_STMT:
            case IF_STMT:
            case DO_STMT:
            case DO_SUFFIX:
            case READ_STMT:
            case WRITE_STMT:
                panic_at_the_parser.add(Lexeme.Type.DOT_COMMA);
                break;
            case IDENT_LIST:
                panic_at_the_parser.add(Lexeme.Type.IS);
                break;
            case STMT_LIST:
                panic_at_the_parser.add(Lexeme.Type.STOP);
                panic_at_the_parser.add(Lexeme.Type.END);
                panic_at_the_parser.add(Lexeme.Type.WHILE);
                break;
            case CONDITITON:
            case WRITABLE:
            case EXPRESSION:
                panic_at_the_parser.add(Lexeme.Type.CLOSETHEPAR);
                break;
            case SIMPLE_EXPR:
                panic_at_the_parser.add(Lexeme.Type.CLOSETHEPAR);
                panic_at_the_parser.add(Lexeme.Type.LESS);
                panic_at_the_parser.add(Lexeme.Type.LESS_EQUAL);
                panic_at_the_parser.add(Lexeme.Type.GREATHER);
                panic_at_the_parser.add(Lexeme.Type.GREATHER_EQUAL);
                panic_at_the_parser.add(Lexeme.Type.EQUAL);
                panic_at_the_parser.add(Lexeme.Type.DIFF);
                panic_at_the_parser.add(Lexeme.Type.PLUS);
                panic_at_the_parser.add(Lexeme.Type.MINUS);
                panic_at_the_parser.add(Lexeme.Type.OR);
                break;
            case TERM:
            case FACTOR_A:
            case FACTOR:
                panic_at_the_parser.add(Lexeme.Type.CLOSETHEPAR);
                panic_at_the_parser.add(Lexeme.Type.LESS);
                panic_at_the_parser.add(Lexeme.Type.LESS_EQUAL);
                panic_at_the_parser.add(Lexeme.Type.GREATHER);
                panic_at_the_parser.add(Lexeme.Type.GREATHER_EQUAL);
                panic_at_the_parser.add(Lexeme.Type.EQUAL);
                panic_at_the_parser.add(Lexeme.Type.DIFF);
                panic_at_the_parser.add(Lexeme.Type.PLUS);
                panic_at_the_parser.add(Lexeme.Type.MINUS);
                panic_at_the_parser.add(Lexeme.Type.OR);
                panic_at_the_parser.add(Lexeme.Type.TIMES);
                panic_at_the_parser.add(Lexeme.Type.DIV);
                panic_at_the_parser.add(Lexeme.Type.AND);
                return;
            case RELOP:
            case ADDOP:
            case MULOP:
                panic_at_the_parser.add(Lexeme.Type.NOT);
                panic_at_the_parser.add(Lexeme.Type.MINUS);
                panic_at_the_parser.add(Lexeme.Type.IDENTIFIER);
                panic_at_the_parser.add(Lexeme.Type.INT_CONSTANT);
                panic_at_the_parser.add(Lexeme.Type.REAL_CONSTANT);
                panic_at_the_parser.add(Lexeme.Type.STRING_CONSTANT);
                panic_at_the_parser.add(Lexeme.Type.OPENTHEPAR);
                return;
        }
        while(!checkTokens(listToNativeArray(panic_at_the_parser))){
            this.cur_token=lex.getToken();
        }
    }

    private boolean eat(Lexeme.Type to_eat) throws Exception {
        if (cur_token.getType()==to_eat){
            this.cur_token=lex.getToken();
            if (this.cur_token.getType()==Lexeme.Type.ERROR||this.cur_token.getType()==Lexeme.Type.INVALID){
                throw new Exception("Lexical Error: Gotten INVALID or ERROR token! There was "+error_counter+" errors during syntactical analysis");
            }
            return true;
        }else{
            displayError("Expected token: "+to_eat+" but got: "+cur_token.getType(),this.lex.getCurLine());
            panic(); // discard tokens
            parse_stack.pop(); // discard procedure
            return false; // make sure that who called eat() will `return'
        }
    }

    private boolean checkToken(Lexeme.Type to_check){
        return cur_token.getType()==to_check;
    }

    private boolean checkTokens(Lexeme.Type[] to_check_list){
        for (Lexeme.Type to_check:to_check_list){
            if (cur_token.getType()==to_check)
                return true;
        }
        return false;
    }

    private void program() throws Exception {
        parse_stack.push(GrammarRule.PROGRAM);
        if(!eat(Lexeme.Type.INIT))
            return;
        decl_list();
        if(!eat(Lexeme.Type.BEGIN))
            return;
        stmt_list();
        if(!eat(Lexeme.Type.STOP))
            return;
        parse_stack.pop();
    }

    private void decl_list() throws Exception {
        parse_stack.push(GrammarRule.DECL_LIST);
        decl();
        if(!eat(Lexeme.Type.DOT_COMMA))
            return;
        while(checkToken(Lexeme.Type.IDENTIFIER)){
            decl();
            if(!eat(Lexeme.Type.DOT_COMMA))
                return;
        }
        parse_stack.pop();
    }

    private void decl() throws Exception {
        parse_stack.push(GrammarRule.DECL);
        ident_list();
        if(!eat(Lexeme.Type.IS))
            return;
        type();
        parse_stack.pop();
    }

    private void ident_list() throws Exception {
        parse_stack.push(GrammarRule.IDENT_LIST);
        if(!eat(Lexeme.Type.IDENTIFIER))
            return;
        while(checkToken(Lexeme.Type.COMMA)){
            if(!eat(Lexeme.Type.COMMA))
                return;
            if(!eat(Lexeme.Type.IDENTIFIER))
                return;
        }
        parse_stack.pop();
    }

    private void type() throws Exception {
        parse_stack.push(GrammarRule.TYPE);
        switch (cur_token.getType()){
            case INTEGER:
                if(!eat(Lexeme.Type.INTEGER))
                    return;
                break;
            case STRING:
                if(!eat(Lexeme.Type.STRING))
                    return;
                break;
            case REAL:
                if(!eat(Lexeme.Type.REAL))
                    return;
                break;
            default:
                displayError("Expected type",lex.getCurLine());
                break;
        }
        parse_stack.pop();
    }

    private void stmt_list() throws Exception {
        parse_stack.push(GrammarRule.STMT_LIST);
        stmt();
        if(!eat(Lexeme.Type.DOT_COMMA))
            return;
        while(checkTokens(new Lexeme.Type[]{Lexeme.Type.IDENTIFIER, Lexeme.Type.IF, Lexeme.Type.DO, Lexeme.Type.READ, Lexeme.Type.WRITE})){
            stmt();
            if(!eat(Lexeme.Type.DOT_COMMA))
                return;
        }
        parse_stack.pop();
    }

    private void stmt() throws Exception {
        parse_stack.push(GrammarRule.STMT);
        switch (cur_token.getType()){
            case IDENTIFIER:
                assign_stmt();
                break;
            case IF:
                if_stmt();
                break;
            case DO:
                do_stmt();
                break;
            case READ:
                read_stmt();
                break;
            case WRITE:
                write_stmt();
                break;
            default:
                displayError("Expected stmt",lex.getCurLine());
                break;
        }
        parse_stack.pop();
    }

    private void assign_stmt() throws Exception {
        parse_stack.push(GrammarRule.ASSIGN_STMT);
        if(!eat(Lexeme.Type.IDENTIFIER))
            return;
        if(!eat(Lexeme.Type.ASSIGN))
            return;
        simple_expr();
        parse_stack.pop();
    }

    private void if_stmt() throws Exception {
        parse_stack.push(GrammarRule.IF_STMT);
        if(!eat(Lexeme.Type.IF))
            return;
        if(!eat(Lexeme.Type.OPENTHEPAR))
            return;
        condition();
        if(!eat(Lexeme.Type.CLOSETHEPAR))
            return;
        if(!eat(Lexeme.Type.BEGIN))
            return;
        stmt_list();
        if(!eat(Lexeme.Type.END))
            return;
        if (checkToken(Lexeme.Type.ELSE)){
            if(!eat(Lexeme.Type.ELSE))
                return;
            if(!eat(Lexeme.Type.BEGIN))
                return;
            stmt_list();
            if(!eat(Lexeme.Type.END))
                return;
        }
        parse_stack.pop();
    }

    private void condition() throws Exception {
        parse_stack.push(GrammarRule.CONDITITON);
        expression();
        parse_stack.pop();
    }

    private void do_stmt() throws Exception {
        parse_stack.push(GrammarRule.DO_STMT);
        if(!eat(Lexeme.Type.DO))
            return;
        stmt_list();
        do_suffix();
        parse_stack.pop();
    }

    private void do_suffix() throws Exception {
        parse_stack.push(GrammarRule.DO_SUFFIX);
        if(!eat(Lexeme.Type.WHILE))
            return;
        if(!eat(Lexeme.Type.OPENTHEPAR))
            return;
        condition();
        if(!eat(Lexeme.Type.CLOSETHEPAR))
            return;
        parse_stack.pop();
    }

    private void read_stmt() throws Exception {
        parse_stack.push(GrammarRule.READ_STMT);
        if(!eat(Lexeme.Type.READ))
            return;
        if(!eat(Lexeme.Type.OPENTHEPAR))
            return;
        if(!eat(Lexeme.Type.IDENTIFIER))
            return;
        if(!eat(Lexeme.Type.CLOSETHEPAR))
            return;
        parse_stack.pop();
    }

    private void write_stmt() throws Exception {
        parse_stack.push(GrammarRule.WRITE_STMT);
        if(!eat(Lexeme.Type.WRITE))
            return;
        if(!eat(Lexeme.Type.OPENTHEPAR))
            return;
        writable();
        if(!eat(Lexeme.Type.CLOSETHEPAR))
            return;
        parse_stack.pop();
    }

    private void writable() throws Exception {
        parse_stack.push(GrammarRule.WRITABLE);
        simple_expr();
        parse_stack.pop();
    }

    private void expression() throws Exception {
        parse_stack.push(GrammarRule.EXPRESSION);
        simple_expr();
        if(checkTokens(new Lexeme.Type[]{Lexeme.Type.EQUAL, Lexeme.Type.GREATHER, Lexeme.Type.GREATHER_EQUAL, Lexeme.Type.LESS, Lexeme.Type.LESS_EQUAL, Lexeme.Type.DIFF})){
            relop();
            simple_expr();
        }
        parse_stack.pop();
    }

    private void simple_expr() throws Exception {
        parse_stack.push(GrammarRule.SIMPLE_EXPR);
        term();
        while(checkTokens(new Lexeme.Type[]{Lexeme.Type.PLUS, Lexeme.Type.MINUS, Lexeme.Type.OR})){
            addop();
            term();
        }
        parse_stack.pop();
    }

    private void term() throws Exception {
        parse_stack.push(GrammarRule.TERM);
        factor_a();
        while(checkTokens(new Lexeme.Type[]{Lexeme.Type.TIMES, Lexeme.Type.DIV, Lexeme.Type.AND})){
            mulop();
            factor_a();
        }
        parse_stack.pop();
    }

    private void factor_a() throws Exception {
        parse_stack.push(GrammarRule.FACTOR_A);
        switch (cur_token.getType()) {
            case NOT:
                if(!eat(Lexeme.Type.NOT))
                    return;
                break;
            case MINUS:
                if(!eat(Lexeme.Type.MINUS))
                    return;
                break;
        }
        factor();
        parse_stack.pop();
    }

    private void factor() throws Exception {
        parse_stack.push(GrammarRule.FACTOR);
        switch (cur_token.getType()) {
            case IDENTIFIER:
                if(!eat(Lexeme.Type.IDENTIFIER))
                    return;
                break;
            // constant start
            case INT_CONSTANT:
                if(!eat(Lexeme.Type.INT_CONSTANT))
                    return;
                break;
            case REAL_CONSTANT:
                if(!eat(Lexeme.Type.REAL_CONSTANT))
                    return;
                break;
            case STRING_CONSTANT:
                if(!eat(Lexeme.Type.STRING_CONSTANT))
                    return;
                break;
            // constant end
            case OPENTHEPAR:
                if(!eat(Lexeme.Type.OPENTHEPAR))
                    return;
                expression();
                if(!eat(Lexeme.Type.CLOSETHEPAR))
                    return;
                break;
            default:
                displayError("Expected factor",lex.getCurLine());
                break;
        }
        parse_stack.pop();
    }

    private void relop() throws Exception {
        parse_stack.push(GrammarRule.RELOP);
        switch (cur_token.getType()) {
            case EQUAL:
                if(!eat(Lexeme.Type.EQUAL))
                    return;
                break;
            case GREATHER:
                if(!eat(Lexeme.Type.GREATHER))
                    return;
                break;
            case GREATHER_EQUAL:
                if(!eat(Lexeme.Type.GREATHER_EQUAL))
                    return;
                break;
            case LESS:
                if(!eat(Lexeme.Type.LESS))
                    return;
                break;
            case LESS_EQUAL:
                if(!eat(Lexeme.Type.LESS_EQUAL))
                    return;
                break;
            case DIFF:
                if(!eat(Lexeme.Type.DIFF))
                    return;
                break;
            default:
                displayError("Expected relop",lex.getCurLine());
                break;
        }
        parse_stack.pop();
    }

    private void addop() throws Exception {
        parse_stack.push(GrammarRule.ADDOP);
        switch (cur_token.getType()) {
            case PLUS:
                if(!eat(Lexeme.Type.PLUS))
                    return;
                break;
            case MINUS:
                if(!eat(Lexeme.Type.MINUS))
                    return;
                break;
            case OR:
                if(!eat(Lexeme.Type.OR))
                    return;
                break;
            default:
                displayError("Expected addop",lex.getCurLine());
                break;
        }
        parse_stack.pop();
    }

    private void mulop() throws Exception {
        parse_stack.push(GrammarRule.MULOP);
        switch (cur_token.getType()) {
            case TIMES:
                if(!eat(Lexeme.Type.TIMES))
                    return;
                break;
            case DIV:
                if(!eat(Lexeme.Type.DIV))
                    return;
                break;
            case AND:
                if(!eat(Lexeme.Type.AND))
                    return;
                break;
            default:
                displayError("Expected mulop",lex.getCurLine());
                break;
        }
        parse_stack.pop();
    }

    private enum GrammarRule {
        PROGRAM,
        DECL_LIST,
        DECL,
        IDENT_LIST,
        TYPE,
        STMT_LIST,
        STMT,
        ASSIGN_STMT,
        IF_STMT,
        CONDITITON,
        DO_STMT,
        DO_SUFFIX,
        READ_STMT,
        WRITE_STMT,
        WRITABLE,
        EXPRESSION,
        SIMPLE_EXPR,
        TERM,
        FACTOR_A,
        FACTOR,
        RELOP,
        ADDOP,
        MULOP;
    }
}
