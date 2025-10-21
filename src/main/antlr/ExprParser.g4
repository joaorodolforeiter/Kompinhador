parser grammar ExprParser;
options { tokenVocab=ExprLexer; }

program
    : PR_BEGIN instructions PR_END EOF
    ;

instructions
    : instruction SEMI repeat_instruction
    ;

repeat_instruction
    : instructions
    |
    ;

instruction
    : def_var
    | command
    ;

command
    : IDENTIFICADOR var_manipulation
    | read_statement
    | print_statement
    | if_statement
    | do_until_statement
    ;

var_manipulation
    : list_manipulation
    | assignment_value
    ;

def_var
    : type identifiers
    ;

primitive
    : PR_BOOL
    | PR_INT
    | PR_FLOAT
    | PR_STRING
    ;

type
    : primitive
    | PR_LIST LPAREN primitive COMMA CINT RPAREN
    ;

identifiers
    : IDENTIFICADOR repeat_identifiers
    ;

repeat_identifiers
    : COMMA identifiers
    | 
    ;

assignment_value
    : EQ expression
    | ASSIGN expression
    ;

list_manipulation
    : list_add
    | list_delete
    ;

list_add
    : PR_ADD LPAREN expression COMMA expression RPAREN
    ;

list_delete
    : PR_DELETE LPAREN expression RPAREN
    ;

read_statement
    : PR_READ LPAREN input_list RPAREN
    ;

input_list
    : opt_string IDENTIFICADOR extra_input_identifiers
    ;

opt_string
    : STRING COMMA
    |
    ;

extra_input_identifiers
    : COMMA input_list
    |
    ;

print_statement
    : PR_PRINT LPAREN expressions RPAREN
    ;

expressions
    : expression repeat_expression
    ;

repeat_expression
    : COMMA expressions
    |
    ;

if_statement
    : PR_IF expression commands else_statement PR_END
    ;

else_statement
    : PR_ELSE commands
    |
    ;

do_until_statement
    : PR_DO commands PR_UNTIL expression
    ;

commands
    : command SEMI repeat_commands
    ;

repeat_commands
    : commands
    |
    ;

expression
    : valor expression_
    ;

expression_
    : PR_AND valor expression_
    | PR_OR valor expression_
    |
    ;

valor
    : relacional
    | PR_TRUE
    | PR_FALSE
    | PR_NOT valor
    ;

relacional
    : aritmetica relacional_
    ;

relacional_
    : operador_relacional aritmetica
    |
    ;

operador_relacional
    : EQEQ
    | NEQ
    | LT
    | GT
    ;

aritmetica
    : termo aritmetica_
    ;

aritmetica_
    : PLUS termo aritmetica_
    | MINUS termo aritmetica_
    |
    ;

termo
    : fator termo_
    ;

termo_
    : TIMES fator termo_
    | DIV fator termo_
    |
    ;

fator
    : IDENTIFICADOR fator_
    | CINT
    | CFLOAT
    | STRING
    | LPAREN expression RPAREN
    | PLUS fator
    | MINUS fator
    ;

fator_
    : PR_COUNT
    | PR_SIZE
    | PR_ELEMENTOF LPAREN expression RPAREN
    |
    ;