parser grammar ExprParser;
options { tokenVocab=ExprLexer; }

program
    : PR_BEGIN instructions PR_END EOF
    ;

instructions
    : instruction repeat_instruction
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
    : list_manipulation
    | assignment
    | read_statement
    | print_statement
    | if_statement
    | do_until_statement
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

expression
    :
    ;

assignment
    : IDENTIFICADOR assignment_value
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
    : IDENTIFICADOR PR_ADD LPAREN expression COMMA expression RPAREN
    ;

list_delete
    : IDENTIFICADOR PR_DELETE LPAREN expression RPAREN
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
    : command repeat_commands
    ;

repeat_commands
    : commands
    |
    ;