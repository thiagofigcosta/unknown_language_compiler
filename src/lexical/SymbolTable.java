package lexical;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SymbolTable {

    private HashMap<String, Lexeme.Type> table;
    private static final boolean CASE_SENSITIVE=false;

    public SymbolTable() {
        this.table = new HashMap<>();
        this.add("init",Lexeme.Builder.aLexeme().withType(Lexeme.Type.INIT).build());
        this.add("stop",Lexeme.Builder.aLexeme().withType(Lexeme.Type.STOP).build());
        this.add("is",Lexeme.Builder.aLexeme().withType(Lexeme.Type.IS).build());
        this.add("if",Lexeme.Builder.aLexeme().withType(Lexeme.Type.IF).build());
        this.add("begin",Lexeme.Builder.aLexeme().withType(Lexeme.Type.BEGIN).build());
        this.add("end",Lexeme.Builder.aLexeme().withType(Lexeme.Type.END).build());
        this.add("else",Lexeme.Builder.aLexeme().withType(Lexeme.Type.ELSE).build());
        this.add("do",Lexeme.Builder.aLexeme().withType(Lexeme.Type.DO).build());
        this.add("while",Lexeme.Builder.aLexeme().withType(Lexeme.Type.WHILE).build());
        this.add("read",Lexeme.Builder.aLexeme().withType(Lexeme.Type.READ).build());
        this.add("write",Lexeme.Builder.aLexeme().withType(Lexeme.Type.WRITE).build());
        this.add("integer",Lexeme.Builder.aLexeme().withType(Lexeme.Type.INTEGER).build());
        this.add("real",Lexeme.Builder.aLexeme().withType(Lexeme.Type.REAL).build());
        this.add("string",Lexeme.Builder.aLexeme().withType(Lexeme.Type.STRING).build());
        this.add("not",Lexeme.Builder.aLexeme().withType(Lexeme.Type.NOT).build());
        this.add("or",Lexeme.Builder.aLexeme().withType(Lexeme.Type.OR).build());
        this.add("and",Lexeme.Builder.aLexeme().withType(Lexeme.Type.AND).build());
    }

    public void add(String id,Lexeme token){
        if (!CASE_SENSITIVE){
            id=id.toLowerCase(Locale.ROOT);
        }
        this.table.put(id,token.getType());
    }

    public void add(Lexeme token){
        String id=token.getValue();
        if (!CASE_SENSITIVE){
            id=id.toLowerCase(Locale.ROOT);
        }
        this.table.put(id,token.getType());
    }

    public boolean contains(String id){
        if (!CASE_SENSITIVE){
            id=id.toLowerCase(Locale.ROOT);
        }
        return this.table.containsKey(id);
    }

    public boolean contains(Lexeme token){
        String id=token.getValue();
        if (!CASE_SENSITIVE){
            id=id.toLowerCase(Locale.ROOT);
        }
        return this.table.containsKey(id);
    }

    public Lexeme get(String id){
        if (!CASE_SENSITIVE){
            id=id.toLowerCase(Locale.ROOT);
        }
        if (contains(id)){
            return Lexeme.Builder.aLexeme().withType(this.table.get(id)).withValue(id).build();
        }else{
            return Lexeme.Builder.aLexeme().withType(Lexeme.Type.ERROR).withValue("Symbol table does not contains "+id).build();
        }
    }

    @Override
    public String toString() {
        StringBuilder out= new StringBuilder("SymbolTable{\n");
        for (Map.Entry<String, Lexeme.Type> entry : table.entrySet()) {
            out.append("\t").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        out.append("}");
        return out.toString();
    }
}
