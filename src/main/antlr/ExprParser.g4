parser grammar ExprParser;
options { tokenVocab=ExprLexer; }

program
    : stat EOF
    | def EOF
    ;

stat: IDENTIFICADOR '=' expr ';'
    | expr ';'
    ;

def : IDENTIFICADOR '(' IDENTIFICADOR (',' IDENTIFICADOR)* ')' ;

expr: IDENTIFICADOR
    | PR_INT
    | func
    | 'not' expr
    | expr 'and' expr
    | expr 'or' expr
    ;

func : IDENTIFICADOR '(' expr (',' expr)* ')' ;