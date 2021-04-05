package code_generator;

import semantic.SemanticAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodeGenerator {
    static final private int INTEGER_SIZE=1;
    static final private int REAL_SIZE=1;
    static final private int STRING_SIZE=1;
    static final private int DEFAULT_INT_VALUE=0;
    static final private float DEFAULT_REAL_VALUE=0;
    static final private String DEFAULT_STRING_VALUE="";

    private List<Instruction> instructions;
    private HashMap<String,Integer> addresses;
    private int incremental_tag_counter;
    private int global_pointer;
    private int local_pointer;
    private boolean has_error;

    public CodeGenerator() {
        instructions=new ArrayList<>();
        addresses=new HashMap<>();
        global_pointer=0;
        local_pointer=0;
        incremental_tag_counter=0;
        has_error=false;
    }

    public String getCode(){
        String code="";
        for (Instruction i:instructions){
            code+=i.toString();
        }
        return code;
    }

    public void startProgram(){
        instructions.add(new Instruction(Instruction.Operation.START));
    }

    public void endProgram(){
        instructions.add(new Instruction(Instruction.Operation.STOP));
    }

    public String getIncrementalTagName(String tag_base_name){
        String tag=tag_base_name+"_"+(incremental_tag_counter++);
        return tag;
    }

    public void setTag(String tag){
        instructions.add(new Instruction(Instruction.Operation.TAG,tag));
    }

    public void jzStack(String tag){
        instructions.add(new Instruction(Instruction.Operation.JZ,tag));
    }

    public void jumpStack(String tag){
        instructions.add(new Instruction(Instruction.Operation.JUMP,tag));
    }

    public void declareVariable(String identifier, SemanticAnalyzer.Type type){
        switch (type){
            case ERROR:
            case VOID:
            case BOOLEAN_OP:
            case NUMERIC_OP:
            case COMPARE_OP:
            case BOOL_AND_NUM_CMP_OP:
                has_error=true;
                break;
            case BOOLEAN:
            case INTEGER:
                addresses.put(identifier,global_pointer);
                global_pointer+=INTEGER_SIZE;
                instructions.add(new Instruction(Instruction.Operation.PUSHI,String.valueOf(DEFAULT_INT_VALUE)));
                break;
            case REAL:
                addresses.put(identifier,global_pointer);
                global_pointer+=REAL_SIZE;
                instructions.add(new Instruction(Instruction.Operation.PUSHF,String.valueOf(DEFAULT_REAL_VALUE)));
                break;
            case STRING:
                addresses.put(identifier,global_pointer);
                global_pointer+=STRING_SIZE;
                instructions.add(new Instruction(Instruction.Operation.PUSHS,"\""+DEFAULT_STRING_VALUE+"\""));
                break;
        }
    }

    public void assignToVariable(String identifier, SemanticAnalyzer.Type type, String value){
        switch (type){
            case ERROR:
            case VOID:
            case BOOLEAN_OP:
            case NUMERIC_OP:
            case COMPARE_OP:
            case BOOL_AND_NUM_CMP_OP:
                has_error=true;
                break;
            case BOOLEAN:
            case INTEGER:
                instructions.add(new Instruction(Instruction.Operation.PUSHI,value));
                instructions.add(new Instruction(Instruction.Operation.STOREG,String.valueOf(addresses.get(identifier))));
                break;
            case REAL:
                instructions.add(new Instruction(Instruction.Operation.PUSHF,value));
                instructions.add(new Instruction(Instruction.Operation.STOREG,String.valueOf(addresses.get(identifier))));
                break;
            case STRING:
                instructions.add(new Instruction(Instruction.Operation.PUSHS,"\""+value+"\""));
                instructions.add(new Instruction(Instruction.Operation.STOREG,String.valueOf(addresses.get(identifier))));
                break;
        }
    }

    public void addValueToStack(SemanticAnalyzer.Type type, String value){
        switch (type){
            case ERROR:
            case VOID:
            case BOOLEAN_OP:
            case NUMERIC_OP:
            case COMPARE_OP:
            case BOOL_AND_NUM_CMP_OP:
                has_error=true;
                break;
            case BOOLEAN:
            case INTEGER:
                instructions.add(new Instruction(Instruction.Operation.PUSHI,value));
                break;
            case REAL:
                instructions.add(new Instruction(Instruction.Operation.PUSHF,value));
                break;
            case STRING:
                instructions.add(new Instruction(Instruction.Operation.PUSHS,"\""+value+"\""));
                break;
        }
    }

    public void addVariableToStack(String identifier){
        instructions.add(new Instruction(Instruction.Operation.PUSHG,String.valueOf(addresses.get(identifier))));
    }

    public void negateStack(){
        instructions.add(new Instruction(Instruction.Operation.NOT));
    }

    public void swapStack(){
        instructions.add(new Instruction(Instruction.Operation.SWAP));
    }

    public void sumIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.ADD));
    }

    public void sumRealStack(){
        instructions.add(new Instruction(Instruction.Operation.FADD));
    }

    public void subtractIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.SUB));
    }

    public void subtractRealStack(){
        instructions.add(new Instruction(Instruction.Operation.FSUB));
    }

    public void divideIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.DIV));
    }

    public void divideRealStack(){
        instructions.add(new Instruction(Instruction.Operation.FDIV));
    }

    public void multiplyIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.MUL));
    }

    public void multiplyRealStack(){
        instructions.add(new Instruction(Instruction.Operation.FMUL));
    }

    public void lessIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.INF));
    }

    public void lessRealStack(){
        instructions.add(new Instruction(Instruction.Operation.FINF));
    }

    public void lessEqualIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.INFEQ));
    }

    public void lessEqualRealStack(){
        instructions.add(new Instruction(Instruction.Operation.FINFEQ));
    }

    public void greaterIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.SUP));
    }

    public void greaterRealStack(){
        instructions.add(new Instruction(Instruction.Operation.FSUP));
    }

    public void greaterEqualIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.SUPEQ));
    }

    public void greaterEqualRealStack(){
        instructions.add(new Instruction(Instruction.Operation.FSUPEQ));
    }

    public void equalIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.SUB));
        instructions.add(new Instruction(Instruction.Operation.NOT));
    }

    public void equalRealStack(){
        instructions.add(new Instruction(Instruction.Operation.FSUB));
        instructions.add(new Instruction(Instruction.Operation.FTOI));
        instructions.add(new Instruction(Instruction.Operation.NOT));
    }

    public void diffIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.SUB));
    }

    public void diffRealStack(){
        instructions.add(new Instruction(Instruction.Operation.FSUB));
        instructions.add(new Instruction(Instruction.Operation.FTOI));
    }

    public void writeIntegerStack(){
        instructions.add(new Instruction(Instruction.Operation.WRITEI));
    }

    public void writeRealStack(){
        instructions.add(new Instruction(Instruction.Operation.WRITEF));
    }

    public void writeStringStack(){
        instructions.add(new Instruction(Instruction.Operation.WRITES));
    }

    public void readToStack(){
        instructions.add(new Instruction(Instruction.Operation.READ));
    }

    public void convertStrToIntStack(){
        instructions.add(new Instruction(Instruction.Operation.ATOI));
    }

    public void convertStrToRealStack(){
        instructions.add(new Instruction(Instruction.Operation.ATOI));
    }

    public void assignToVariableFromStack(String identifier){
        instructions.add(new Instruction(Instruction.Operation.STOREG,String.valueOf(addresses.get(identifier))));
    }

    public void castStack(SemanticAnalyzer.Type dst_type,SemanticAnalyzer.Type src_type){
        if (dst_type==src_type){
            return;
        }
        if (dst_type==SemanticAnalyzer.Type.REAL){
            if (src_type==SemanticAnalyzer.Type.INTEGER){
                instructions.add(new Instruction(Instruction.Operation.ITOF));
            }
            if (src_type==SemanticAnalyzer.Type.STRING){
                instructions.add(new Instruction(Instruction.Operation.ATOF));
            }
        }
        if (dst_type==SemanticAnalyzer.Type.INTEGER){
            if (src_type==SemanticAnalyzer.Type.REAL){
                instructions.add(new Instruction(Instruction.Operation.FTOI));
            }
            if (src_type==SemanticAnalyzer.Type.STRING){
                instructions.add(new Instruction(Instruction.Operation.ATOI));
            }
        }
        if (dst_type==SemanticAnalyzer.Type.STRING){
            if (src_type==SemanticAnalyzer.Type.REAL){
                instructions.add(new Instruction(Instruction.Operation.STRF));
            }
            if (src_type==SemanticAnalyzer.Type.INTEGER){
                instructions.add(new Instruction(Instruction.Operation.STRI));
            }
        }
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public HashMap<String, Integer> getAddresses() {
        return addresses;
    }

    public void setAddresses(HashMap<String, Integer> addresses) {
        this.addresses = addresses;
    }
}
