package syntactical;

import lexical.Lexeme;
import lexical.LexicalAnalyzer;

public class SyntacticalAnalyzer {

    private LexicalAnalyzer lex;
    private Lexeme cur_token;
    private int error_counter;


    public SyntacticalAnalyzer(LexicalAnalyzer lex){
        this.lex=lex;
        this.cur_token=lex.getToken();
        this.error_counter=0;
    }

    public void run() throws Exception {
        program();
        eat(Lexeme.Type.EOF);
        if(this.error_counter>0){
            throw new Exception("There was "+error_counter+" errors during syntactical analysis");
        }
    }

    private void displayError(String msg, int line){
        this.error_counter++;
        System.err.println("Error at line: "+line+"\n\terr: "+msg);
    }

    private void eat(Lexeme.Type to_eat){
        if (cur_token.getType()==to_eat){
            this.cur_token=lex.getToken();
        }else{
            displayError("Expected token: "+to_eat+" but got: "+cur_token.getType(),this.lex.getCurLine());
            this.cur_token=lex.getToken(); // to continue and avoid loops (the parser fakes that the token was found in order to continue)
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

    private void program(){
        eat(Lexeme.Type.INIT);
        decl_list();
        eat(Lexeme.Type.BEGIN);
        stmt_list();
        eat(Lexeme.Type.STOP);
    }

    private void decl_list(){
        decl();
        eat(Lexeme.Type.DOT_COMMA);
        while(checkToken(Lexeme.Type.IDENTIFIER)){
            decl();
            eat(Lexeme.Type.DOT_COMMA);
        }
    }

    private void decl(){
        ident_list();
        eat(Lexeme.Type.IS);
        type();
    }

    private void ident_list(){
        eat(Lexeme.Type.IDENTIFIER);
        while(checkToken(Lexeme.Type.COMMA)){
            eat(Lexeme.Type.COMMA);
            eat(Lexeme.Type.IDENTIFIER);
        }
    }

    private void type(){
        switch (cur_token.getType()){
            case INTEGER:
                eat(Lexeme.Type.INTEGER);
                break;
            case STRING:
                eat(Lexeme.Type.STRING);
                break;
            case REAL:
                eat(Lexeme.Type.REAL);
                break;
            default:
                displayError("Expected type",lex.getCurLine());
                break;
        }
    }

    private void stmt_list(){
        stmt();
        eat(Lexeme.Type.DOT_COMMA);
        while(checkTokens(new Lexeme.Type[]{Lexeme.Type.IDENTIFIER, Lexeme.Type.IF, Lexeme.Type.DO, Lexeme.Type.READ, Lexeme.Type.WRITE})){
            stmt();
            eat(Lexeme.Type.DOT_COMMA);
        }
    }

    private void stmt(){
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
    }

    private void assign_stmt(){
        eat(Lexeme.Type.IDENTIFIER);
        eat(Lexeme.Type.ASSIGN);
        simple_expr();
    }

    private void if_stmt(){
        eat(Lexeme.Type.IF);
        eat(Lexeme.Type.OPENTHEPAR);
        condition();
        eat(Lexeme.Type.CLOSETHEPAR);
        eat(Lexeme.Type.BEGIN);
        stmt_list();
        eat(Lexeme.Type.END);
        if (checkToken(Lexeme.Type.ELSE)){
            eat(Lexeme.Type.ELSE);
            eat(Lexeme.Type.BEGIN);
            stmt_list();
            eat(Lexeme.Type.END);
        }
    }

    private void condition(){
        expression();
    }

    private void do_stmt(){
        eat(Lexeme.Type.DO);
        stmt_list();
        do_suffix();
    }

    private void do_suffix(){
        eat(Lexeme.Type.WHILE);
        eat(Lexeme.Type.OPENTHEPAR);
        condition();
        eat(Lexeme.Type.CLOSETHEPAR);
    }

    private void read_stmt(){
        eat(Lexeme.Type.READ);
        eat(Lexeme.Type.OPENTHEPAR);
        eat(Lexeme.Type.IDENTIFIER);
        eat(Lexeme.Type.CLOSETHEPAR);
    }

    private void write_stmt(){
        eat(Lexeme.Type.WRITE);
        eat(Lexeme.Type.OPENTHEPAR);
        writable();
        eat(Lexeme.Type.CLOSETHEPAR);
    }

    private void writable(){
        simple_expr();
    }

    private void expression(){
        simple_expr();
        if(checkTokens(new Lexeme.Type[]{Lexeme.Type.EQUAL, Lexeme.Type.GREATHER, Lexeme.Type.GREATHER_EQUAL, Lexeme.Type.LESS, Lexeme.Type.LESS_EQUAL, Lexeme.Type.DIFF})){
            relop();
            simple_expr();
        }
    }

    private void simple_expr(){
        term();
        while(checkTokens(new Lexeme.Type[]{Lexeme.Type.PLUS, Lexeme.Type.MINUS, Lexeme.Type.OR})){
            addop();
            term();
        }
    }

    private void term(){
        factor_a();
        while(checkTokens(new Lexeme.Type[]{Lexeme.Type.TIMES, Lexeme.Type.DIV, Lexeme.Type.AND})){
            mulop();
            factor_a();
        }
    }

    private void factor_a(){
        switch (cur_token.getType()) {
            case NOT:
                eat(Lexeme.Type.NOT);
                break;
            case MINUS:
                eat(Lexeme.Type.MINUS);
                break;
        }
        factor();
    }

    private void factor(){
        switch (cur_token.getType()) {
            case IDENTIFIER:
                eat(Lexeme.Type.IDENTIFIER);
                break;
            // constant start
            case INT_CONSTANT:
                eat(Lexeme.Type.INT_CONSTANT);
                break;
            case REAL_CONSTANT:
                eat(Lexeme.Type.REAL_CONSTANT);
                break;
            case STRING_CONSTANT:
                eat(Lexeme.Type.STRING_CONSTANT);
                break;
            // constant end
            case OPENTHEPAR:
                eat(Lexeme.Type.OPENTHEPAR);
                expression();
                eat(Lexeme.Type.CLOSETHEPAR);
                break;
            default:
                displayError("Expected factor",lex.getCurLine());
                break;
        }
    }

    private void relop(){
        switch (cur_token.getType()) {
            case EQUAL:
                eat(Lexeme.Type.EQUAL);
                break;
            case GREATHER:
                eat(Lexeme.Type.GREATHER);
                break;
            case GREATHER_EQUAL:
                eat(Lexeme.Type.GREATHER_EQUAL);
                break;
            case LESS:
                eat(Lexeme.Type.LESS);
                break;
            case LESS_EQUAL:
                eat(Lexeme.Type.LESS_EQUAL);
                break;
            case DIFF:
                eat(Lexeme.Type.DIFF);
                break;
            default:
                displayError("Expected relop",lex.getCurLine());
                break;
        }
    }

    private void addop(){
        switch (cur_token.getType()) {
            case PLUS:
                eat(Lexeme.Type.PLUS);
                break;
            case MINUS:
                eat(Lexeme.Type.MINUS);
                break;
            case OR:
                eat(Lexeme.Type.OR);
                break;
            default:
                displayError("Expected addop",lex.getCurLine());
                break;
        }
    }

    private void mulop(){
        switch (cur_token.getType()) {
            case TIMES:
                eat(Lexeme.Type.TIMES);
                break;
            case DIV:
                eat(Lexeme.Type.DIV);
                break;
            case AND:
                eat(Lexeme.Type.AND);
                break;
            default:
                displayError("Expected mulop",lex.getCurLine());
                break;
        }
    }

}
