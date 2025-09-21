lexer grammar ExprLexer;

// ----------------------------
// DEFINIÇÕES DE FRAGMENTOS
// ----------------------------
fragment DIG: [0-9] ;
fragment MAI: [A-Z] ;
fragment MIN: [a-z] ;

fragment LINHA : '#' ~[\n]* ;
fragment BLOCO : '{' (~[/{])* '}' ;

fragment INTEIRO : [1-9] DIG? DIG? DIG? DIG? | '0' ;

fragment DECIMAL : DIG? DIG? DIG? DIG? [1-9] | '0' ;

// ----------------------------
// PALAVRAS RESERVADAS (devem vir antes do IDENTIFICADOR)
// ----------------------------
PR_ADD       : 'add' ;
PR_AND       : 'and' ;
PR_BEGIN     : 'begin' ;
PR_BOOL      : 'bool' ;
PR_COUNT     : 'count' ;
PR_DELETE    : 'delete' ;
PR_DO        : 'do' ;
PR_ELEMENTOF : 'elementOf' ;
PR_ELSE      : 'else' ;
PR_END       : 'end' ;
PR_FALSE     : 'false' ;
PR_FLOAT     : 'float' ;
PR_IF        : 'if' ;
PR_INT       : 'int' ;
PR_LIST      : 'list' ;
PR_NOT       : 'not' ;
PR_OR        : 'or' ;
PR_PRINT     : 'print' ;
PR_READ      : 'read' ;
PR_SIZE      : 'size' ;
PR_STRING    : 'string' ;
PR_TRUE      : 'true' ;
PR_UNTIL     : 'until' ;

// ----------------------------
// TOKENS
// ----------------------------
IDENTIFICADOR : (MAI MIN | MIN)+ ('_' DIG+)? ;

CINT : INTEIRO ;

CFLOAT : INTEIRO '.' DECIMAL ;

STRING : '"' (~[\n"\\])* '"' ;

// ----------------------------
// SÍMBOLOS ESPECIAIS
// ----------------------------
PLUS        : '+'  ;
MINUS       : '-'  ;
TIMES       : '*'  ;
DIV         : '/'  ;
EQEQ        : '==' ;
NEQ         : '~=' ;
LT          : '<'  ;
GT          : '>'  ;
EQ          : '='  ;
ASSIGN      : '<-' ;
LPAREN      : '('  ;
RPAREN      : ')'  ;
SEMI        : ';'  ;
COMMA       : ','  ;

// ----------------------------
// IGNORAR ESPAÇOS/FORMATOS
// ----------------------------
WS : [ \t\r\n]+ -> skip ;

// ----------------------------
// COMENTÁRIOS
// ----------------------------
LINE_COMMENT : LINHA -> skip ;

BLOCK_COMMENT : BLOCO -> skip ;
