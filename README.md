# unknown_language_compiler

To run, pass as argument to the main class a single test file, it will output error messages if the program does not match with the grammar, or it will create a ".a" file with an assembly text.

Grammar:
```
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
```