package semantic;

import code_generator.CodeGenerator;
import lexical.Lexeme;
import lexical.LexicalAnalyzer;
import utils.Tuple;

import java.io.PrintStream;

public class SemanticAnalyzer {

    private static PrintStream stderr = System.err;
    private final LexicalAnalyzer lex;
    private final CodeGenerator code;
    private int error_counter;

    public SemanticAnalyzer(LexicalAnalyzer lex,CodeGenerator code) {
        error_counter = 0;
        this.lex = lex;
        this.code=code;
    }

    public static void setStderr(PrintStream stderr) {
        SemanticAnalyzer.stderr = stderr;
    }

    private void displayError(String msg) {
        this.error_counter++;
        stderr.println("Error at line: " + lex.getCurLine() + "\n\terr: " + msg);
    }

    public Boolean assertIfIdentifierIsNotDeclared(Lexeme identifier) {
        if (identifier.getType() != Lexeme.Type.IDENTIFIER) {
            displayError("Expected identifier");
            return false;
        }
        if (lex.getTable().contains(identifier)) {
            Tuple<Lexeme, Type> identifer_table = lex.getTable().get(identifier.getValue());
            if (identifer_table.first.getType() != Lexeme.Type.IDENTIFIER) {
                displayError(identifier.getValue() + " name already in use!");
                return false;
            }
            if (identifer_table.second != Type.VOID) {
                displayError("Identifier already declared");
                return false;
            }
        }
        return true;
    }

    public Type getIdentiferType(Lexeme identifier) {
        if (identifier.getType() != Lexeme.Type.IDENTIFIER) {
            displayError("Expected identifier");
            return Type.ERROR;
        }
        Type type = lex.getTable().getType(identifier.getValue());
        if (type == Type.ERROR || type == Type.VOID) {
            displayError(identifier.getValue() + " was not declared!");
            return Type.ERROR;
        }
        return type;
    }

    public Type validateExpression(Expression expression, Lexeme.Type token_type) {
        Type out_type = Type.ERROR;
        switch (token_type) {
            case NOT:
                if (expression.getType() == Type.BOOLEAN) {
                    out_type = Type.BOOLEAN;
                } else {
                    displayError("Expected boolean type on NOT operator");
                    return Type.ERROR;
                }
                break;
            case MINUS:
                if (expression.getType() == Type.REAL || expression.getType() == Type.INTEGER) {
                    out_type = expression.getType();
                } else {
                    displayError("Expected boolean type on NOT operator");
                    return Type.ERROR;
                }
                break;
            default:
                out_type = expression.getType();
                break;
        }
        return out_type;
    }

    public Expression declareVariables(Expression identifiers, Expression type) {
        for (String identifier : identifiers.getIdentifiers()) {
            lex.getTable().add(Lexeme.Builder.aLexeme().withType(Lexeme.Type.IDENTIFIER).withValue(identifier).build(), type.getType());
            code.declareVariable(identifier,type.getType());
        }
        return Expression.void_();
    }

    public Expression validateOperation(Expression a, Expression op, Expression b) {
        Type out_type = Type.ERROR;
        if (op.getType() == Type.BOOLEAN_OP) {
            if (a.getType() == Type.BOOLEAN && b.getType() == Type.BOOLEAN) {
                out_type = Type.BOOLEAN;
            } else {
                displayError("Expected boolean operands");
                return Expression.error();
            }
        } else if (op.getType() == Type.NUMERIC_OP) {
            boolean is_real = a.getType() == Type.REAL || b.getType() == Type.REAL;
            if ((a.getType() == Type.REAL || a.getType() == Type.INTEGER) && (b.getType() == Type.REAL || b.getType() == Type.INTEGER)) {
                if (is_real) {
                    out_type = Type.REAL;
                } else {
                    out_type = Type.INTEGER;
                }
            } else {
                displayError("Expected numeric operands");
                return Expression.error();
            }
        } else if (op.getType() == Type.COMPARE_OP) {
            if ((a.getType() == Type.REAL || a.getType() == Type.INTEGER) && (b.getType() == Type.REAL || b.getType() == Type.INTEGER)) {
                out_type = Type.BOOLEAN;
            } else {
                displayError("Expected numeric operands");
                return Expression.error();
            }
        } else if (op.getType() == Type.BOOL_AND_NUM_CMP_OP) {
            if (((a.getType() == Type.REAL || a.getType() == Type.INTEGER) && (b.getType() == Type.REAL || b.getType() == Type.INTEGER)) || a.getType() == Type.BOOLEAN && b.getType() == Type.BOOLEAN) {
                out_type = Type.BOOLEAN;
            } else {
                displayError("Expected numeric operands");
                return Expression.error();
            }
        } else {
            displayError("Expected operator");
            return Expression.error();
        }
        return new Expression(out_type);
    }

    public Expression validateCondition(Expression expr) {
        if (expr.getType() == Type.BOOLEAN || expr.getType() == Type.INTEGER) { // c like style
            return new Expression(Type.BOOLEAN);
        }
        displayError("Expected boolean or integer on condition");
        return Expression.error();
    }

    public Expression validateAssign(Expression expr, Expression assign) {
        if ((expr.getType() == assign.getType() || (expr.getType() == Type.REAL && assign.getType() == Type.INTEGER)) && expr.getType() != Type.ERROR && expr.getType() != Type.VOID && expr.getType() != Type.BOOLEAN_OP && expr.getType() != Type.NUMERIC_OP) {
            return Expression.void_();
        }
        displayError("Expected assignable type");
        return Expression.error();
    }

    public int getErrorCounter() {
        return error_counter;
    }

    public enum Type {
        // control types
        ERROR,
        // regular types
        VOID,
        BOOLEAN,
        INTEGER,
        REAL,
        STRING,
        // operators
        BOOLEAN_OP,
        NUMERIC_OP,
        COMPARE_OP,
        BOOL_AND_NUM_CMP_OP,
    }
}
