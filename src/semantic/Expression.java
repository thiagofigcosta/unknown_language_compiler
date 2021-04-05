package semantic;

import lexical.Lexeme;

import java.util.ArrayList;
import java.util.List;

public class Expression {
    private final List<String> identifiers;
    private SemanticAnalyzer.Type type;
    private Lexeme.Type token;

    public Expression(SemanticAnalyzer.Type type) {
        this.type = type;
        identifiers = new ArrayList<>();
    }
    public Expression(SemanticAnalyzer.Type type, Lexeme.Type token) {
        this.type = type;
        this.token = token;
        identifiers = new ArrayList<>();
    }

    public static Expression error() {
        return new Expression(SemanticAnalyzer.Type.ERROR);
    }

    public static Expression void_() {
        return new Expression(SemanticAnalyzer.Type.VOID);
    }

    public Lexeme.Type getToken() {
        return token;
    }

    public void setToken(Lexeme.Type token) {
        this.token = token;
    }

    public SemanticAnalyzer.Type getType() {
        return type;
    }

    public void setType(SemanticAnalyzer.Type type) {
        this.type = type;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void appendIdentifier(String id) {
        identifiers.add(id);
    }

    public void appendIdentifier(Lexeme id) {
        identifiers.add(id.getValue());
    }
}
