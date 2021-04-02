package lexical;

public class Lexeme {
    private Type type;
    private String value;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void appendValue(String value) {
        if (this.value == null)
            this.value = "";
        this.value = this.value + value;
    }

    public void appendValue(char value) {
        if (this.value == null)
            this.value = "";
        this.value = this.value + value;
    }

    @Override
    public String toString() {
        if (value == null) {
            return "Lexeme{" +
                    "type=" + type +
                    '}';
        } else {
            return "Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public enum Type {
        // special tokens
        INVALID,
        ERROR,
        EOF,

        // variables
        STRING_CONSTANT,
        INT_CONSTANT,
        REAL_CONSTANT,
        IDENTIFIER,

        // reserved words
        INIT,
        STOP,
        IS,
        IF,
        BEGIN,
        END,
        ELSE,
        DO,
        WHILE,
        READ,
        WRITE,

        // types
        INTEGER,
        REAL,
        STRING,

        // symbols
        ASSIGN,//:=
        DOT_COMMA,//;
        COMMA, //,
        OPENTHEPAR,//(
        CLOSETHEPAR,//)

        // operators
        EQUAL,//=
        DIFF,//<>
        LESS,//<
        GREATHER,//>
        LESS_EQUAL,//<=
        GREATHER_EQUAL,//>=
        PLUS,//+
        MINUS,//-
        TIMES,//*
        DIV,///
        NOT,
        OR,
        AND
    }

    public static class Builder {
        private Type type;
        private String value;

        public static Builder aLexeme() {
            return new Builder();
        }

        public Builder withType(Type t) {
            this.type = t;
            return this;
        }

        public Builder withValue(String v) {
            this.value = v;
            return this;
        }

        public Lexeme build() {
            Lexeme lex = new Lexeme();
            lex.setType(type);
            lex.setValue(value);
            return lex;
        }
    }
}
