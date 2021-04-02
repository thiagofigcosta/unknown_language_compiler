package code_generator;

public class Instruction {

    private Operation operation;
    private String argument;

    public Instruction(Operation operation, String argument) {
        this.operation = operation;
        this.argument = argument;
    }

    public Instruction(Operation operation) {
        this.operation = operation;
        this.argument = "";
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public String toString(){
        String out;
        if(operation==Operation.TAG){
            out=argument+":";
        }else{
            out=""+operation;
            if (!argument.equals("")){
                out+=" "+argument;
            }
        }
        return out+"\n";
    }

    public enum Operation{
        // Not an operation
        TAG,
        // Integer operations
        ADD,
        SUB,
        MUL,
        DIV,
        MOD,
        NOT,
        INF,
        INFEQ,
        SUP,
        SUPEQ,
        // Float operations
        FADD,
        FSUB,
        FMUL,
        FDIV,
        FCOS,
        FSIN,
        FINF,
        FINFEQ,
        FSUP,
        FSUPEQ,
        // Address operations
        PADD,
        // String operations
        CONCAT,
        // Heap operations
        ALLOC,
        ALLOCN,
        FREE,
        // Equality operations
        EQUAL,
        // Casting operations
        ATOI,
        ATOF,
        ITOF,
        FTOI,
        STRI,
        STRF,
        // Stack operations
        PUSHI,
        PUSHN,
        PUSHF,
        PUSHS,
        PUSHG,
        PUSHL,
        PUSHSP,
        PUSHFP,
        PUSHGP,
        LOAD,
        LOADN,
        DUP,
        DUPN,
        POP,
        POPN,
        STOREL,
        STOREG,
        STORE,
        STOREN,
        CHECK,
        SWAP,
        // Input-Output operations
        WRITEI,
        WRITEF,
        WRITES,
        READ,
        // Graphics operations
        DRAWPOINT,
        DRAWLINE,
        DRAWCIRCLE,
        OPENDRAWINGAREA,
        CLEARDRAWINGAREA,
        SETCOLOR,
        REFRESH,
        // Control operations
        JUMP,
        JZ,
        PUSHA,
        // Procedure operations
        CALL,
        RETURN,
        // Start and End Operations
        START,
        NOP,
        ERR,
        STOP
    }
}
