package lexical;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LexicalAnalyzer {

    private final int NOT_STORED_BUFFER = -666;

    private final SymbolTable table;
    private final String source_path;
    private FileReader source_file_reader;
    private int read_buffer;
    private int cur_line;
    private State cur_state;

    public LexicalAnalyzer(String source_path) throws FileNotFoundException {
        this.source_path = source_path;
        this.table = new SymbolTable();
        this.init();
    }

    private void init() throws FileNotFoundException {
        this.source_file_reader = new FileReader(source_path);
        this.cur_line = 1;
        this.read_buffer = NOT_STORED_BUFFER;
    }

    public String getAllTokensString() {
        StringBuilder out = new StringBuilder();
        Lexeme lex;
        while (true) {
            lex = getToken();
            out.append(lex.toString());
            if (lex.getType() == Lexeme.Type.EOF)
                break;
            else
                out.append("\n");
        }
        try {
            this.init();
        } catch (IOException ignored) {

        }
        return out.toString();
    }

    public Lexeme getToken() {
        Lexeme out = Lexeme.Builder.aLexeme().withType(Lexeme.Type.INVALID).build();
        try {
            char cur_char;
            this.cur_state = State.START;
            while (this.cur_state.keepProcessing()) {
                if (this.read_buffer == NOT_STORED_BUFFER) {
                    read_buffer = source_file_reader.read();
                }
                if (read_buffer == -1) {
                    switch (cur_state) {
                        case START:
                        case EOF:
                            out.setType(Lexeme.Type.EOF);
                            source_file_reader.close();
                            break;
                        case COMMENTARY:
                        case ESCAPE:
                        case LESS_THAN:
                        case GREATER_THAN:
                        case UNKNOWN_TYPE:
                        case TWO_DOTS:
                            out.setType(Lexeme.Type.ERROR);
                            out.setValue("Unexpected EOF at line: " + cur_line);
                            break;
                        case INT_CONSTANT:
                            out.setType(Lexeme.Type.ERROR);
                            out.setValue("Unexpected EOF at line: " + cur_line);
                        case REAL_CONSTANT:
                        case STRING_CONSTANT:
                        case IDENTIFIER:
                            cur_state = State.KNOWN_TYPE;
                            break;
                    }
                    break;
                }
                cur_char = (char) read_buffer;
                read_buffer = NOT_STORED_BUFFER;
                switch (cur_state) {
                    case START:
                        switch (cur_char) {
                            case '\n':
                                cur_line++;
                            case '\t':
                            case '\r':
                            case ' ':
                                cur_state = State.START;
                                break;
                            case '%':
                                cur_state = State.COMMENTARY;
                                break;
                            case '\"':
                                out.setType(Lexeme.Type.STRING_CONSTANT);
                                cur_state = State.STRING_CONSTANT;
                                break;
                            case ':':
                                out.appendValue(cur_char);
                                cur_state = State.TWO_DOTS;
                                break;
                            case ';':
                                out.setType(Lexeme.Type.DOT_COMMA);
                                cur_state = State.KNOWN_TYPE;
                                break;
                            case ',':
                                out.setType(Lexeme.Type.COMMA);
                                cur_state = State.KNOWN_TYPE;
                                break;
                            case '(':
                                out.setType(Lexeme.Type.OPENTHEPAR);
                                cur_state = State.KNOWN_TYPE;
                                break;
                            case ')':
                                out.setType(Lexeme.Type.CLOSETHEPAR);
                                cur_state = State.KNOWN_TYPE;
                                break;
                            case '=':
                                out.setType(Lexeme.Type.EQUAL);
                                cur_state = State.KNOWN_TYPE;
                                break;
                            case '<':
                                out.appendValue(cur_char);
                                cur_state = State.LESS_THAN;
                                break;
                            case '>':
                                out.appendValue(cur_char);
                                cur_state = State.GREATER_THAN;
                                break;
                            case '+':
                                out.setType(Lexeme.Type.PLUS);
                                cur_state = State.KNOWN_TYPE;
                                break;
                            case '-':
                                out.setType(Lexeme.Type.MINUS);
                                cur_state = State.KNOWN_TYPE;
                                break;
                            case '*':
                                out.setType(Lexeme.Type.TIMES);
                                cur_state = State.KNOWN_TYPE;
                                break;
                            case '/':
                                out.setType(Lexeme.Type.DIV);
                                cur_state = State.KNOWN_TYPE;
                                break;
                            default:
                                if (Character.isLetter(cur_char)) {
                                    out.setType(Lexeme.Type.IDENTIFIER);
                                    out.appendValue(cur_char);
                                    cur_state = State.IDENTIFIER;
                                } else if (Character.isDigit(cur_char)) {
                                    out.setType(Lexeme.Type.INT_CONSTANT);
                                    out.appendValue(cur_char);
                                    cur_state = State.INT_CONSTANT;
                                } else {
                                    out.appendValue(cur_char);
                                    cur_state = State.UNKNOWN_TYPE;
                                }
                                break;
                        }
                        break;
                    case COMMENTARY:
                        switch (cur_char) {
                            case '\n':
                                cur_line++;
                                break;
                            case '%':
                                cur_state = State.START;
                                break;
                        }
                        break;
                    case INT_CONSTANT:
                        if (Character.isDigit(cur_char)) {
                            out.appendValue(cur_char);
                        } else if (cur_char == '.') {
                            out.appendValue(cur_char);
                            out.setType(Lexeme.Type.REAL_CONSTANT);
                            cur_state = State.REAL_CONSTANT;
                        } else {
                            read_buffer = cur_char;
                            cur_state = State.KNOWN_TYPE;
                        }
                        break;
                    case REAL_CONSTANT:
                        if (Character.isDigit(cur_char)) {
                            out.appendValue(cur_char);
                        } else if (cur_char == '.') {
                            out.appendValue(cur_char);
                            cur_state = State.UNKNOWN_TYPE;
                        } else {
                            read_buffer = cur_char;
                            cur_state = State.KNOWN_TYPE;
                        }
                        break;
                    case STRING_CONSTANT:
                        if (cur_char == '\\') {
                            cur_state = State.ESCAPE;
                        } else if (cur_char == '"') {
                            cur_state = State.KNOWN_TYPE;
                        } else if (cur_char == '\n') { // avoid multiline str, check with the professor
                            cur_state = State.UNKNOWN_TYPE;
                        } else {
                            out.appendValue(cur_char);
                        }
                        break;
                    case ESCAPE:
                        cur_state = State.STRING_CONSTANT;
                        switch (cur_char) {
                            case 'n':
                            case 't':
                            case 'b':
                            case 'r':
                            case 'a':
                            case '0':
                            case '\\':
                            case '\'':
                            case '\"':
                                out.appendValue(cur_char);
                                break;
                            default:
                                out.appendValue(cur_char);
                                cur_state = State.UNKNOWN_TYPE;
                                break;
                        }
                        break;
                    case IDENTIFIER:
                        if (Character.isLetterOrDigit(cur_char) || cur_char == '_') {
                            out.appendValue(cur_char);
                        } else {
                            read_buffer = cur_char;
                            cur_state = State.KNOWN_TYPE;
                        }
                        break;
                    case TWO_DOTS:
                        if (cur_char == '=') {
                            out.setValue(null);
                            out.setType(Lexeme.Type.ASSIGN);
                            cur_state = State.KNOWN_TYPE;
                        } else {
                            out.appendValue(cur_char);
                            cur_state = State.UNKNOWN_TYPE;
                        }
                        break;
                    case LESS_THAN:
                        if (cur_char == '>') {
                            out.setValue(null);
                            out.setType(Lexeme.Type.DIFF);
                        } else if (cur_char == '=') {
                            out.setType(Lexeme.Type.LESS_EQUAL);
                        } else {
                            read_buffer = cur_char;
                            out.setType(Lexeme.Type.LESS);
                        }
                        cur_state = State.KNOWN_TYPE;
                        break;
                    case GREATER_THAN:
                        if (cur_char == '=') {
                            out.setValue(null);
                            out.setType(Lexeme.Type.GREATHER_EQUAL);
                        } else {
                            read_buffer = cur_char;
                            out.setType(Lexeme.Type.GREATHER);
                        }
                        cur_state = State.KNOWN_TYPE;
                        break;
                }

            }
        } catch (IOException e) {
            out.setType(Lexeme.Type.ERROR);
            out.setValue("Erro at line: " + cur_line + "\n" + e.toString());
        }
        if (cur_state == State.UNKNOWN_TYPE) {
            out.setType(Lexeme.Type.INVALID);
            out.setValue("Unknown Type (\"" + out.getValue() + "\") at line: " + cur_line);
        } else if (cur_state == State.KNOWN_TYPE) {
            if (out.getType() == Lexeme.Type.IDENTIFIER) {
                if (table.contains(out)) {
                    out = table.getLexeme(out.getValue());
                } else {
                    table.add(out);
                }
            }
        }
        return out;
    }

    public int getCurLine() {
        return cur_line;
    }

    public SymbolTable getTable() {
        return table;
    }

    public enum State {
        START,
        COMMENTARY,
        ESCAPE,
        LESS_THAN,
        GREATER_THAN,
        KNOWN_TYPE,
        UNKNOWN_TYPE,
        TWO_DOTS,
        INT_CONSTANT,
        REAL_CONSTANT,
        STRING_CONSTANT,
        IDENTIFIER,
        EOF;

        public boolean keepProcessing() {
            return !(this == EOF || this == KNOWN_TYPE || this == UNKNOWN_TYPE);
        }
    }
}
