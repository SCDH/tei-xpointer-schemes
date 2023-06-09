// First grammar for the TEI XPointer scheme
// This was replaced with TEIXPointer.g4

lexer grammar TEIXPointer1Lexer;

WS : [\n\f\r ] ;

OP : '(' ;

CP : ')' ;

COMMA : ',' ;

FRAGMENT : '#' ;

XPATH : 'xpath' | 'XPATH' ;

LEFT : 'left' | 'LEFT' ;

RIGHT : 'right' | 'RIGHT' ;

STRING_INDEX : 'string-index' | 'STRING-INDEX' ;

RANGE : 'range' | 'RANGE' ;

STRING_RANGE : 'string-range' | 'STRING-RANGE' ;

MATCH : 'match' | 'MATCH' ;

DIGIT : [0-9] ;

IDREF : [a-zA-Z_] [a-zA-Z0-9_.+-]* ;

CHAR : ~[,] ;
