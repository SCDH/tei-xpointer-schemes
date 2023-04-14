grammar TEIXPointer;

import XPath31;

// fragment is a reserved keyword in ANTLR parser grammars
fragm : POUND pointer Whitespace* ;

pointer
    : xpathPointer
    | leftPointer
    | rightPointer
    | stringIndexPointer
    | rangePointer
    | stringRangePointer
    | matchPointer
    | idref
    ;

xpathPointer : XPATH OP Whitespace* pathexpr Whitespace* CP ;

leftPointer : LEFT OP Whitespace* idrefOrPathexpr Whitespace* CP ;

rightPointer : RIGHT OP Whitespace* idrefOrPathexpr Whitespace* CP ;

stringIndexPointer : STRINGINDEX OP Whitespace* idrefOrPathexpr Whitespace* COMMA Whitespace* offset Whitespace* CP ;

rangePointer : RANGE OP Whitespace* rangePointerPair ( Whitespace* COMMA Whitespace* rangePointerPair )*  Whitespace* CP ;

stringRangePointer : STRINGRANGE OP Whitespace* idrefOrPathexpr Whitespace* COMMA Whitespace* stringRangePointerPair ( Whitespace* COMMA Whitespace* stringRangePointerPair )* Whitespace* CP ;

matchPointer : MATCH OP Whitespace* idrefOrPathexpr Whitespace* COMMA Whitespace* regex ( Whitespace* COMMA Whitespace* index )? Whitespace* CP ;


rangePointerPair : rangeStart Whitespace* COMMA Whitespace* rangeEnd ;

stringRangePointerPair : offset Whitespace* COMMA Whitespace* length ;

rangeStart : rangeArgument ;

rangeEnd : rangeArgument ;

rangeArgument
    : xpathPointer     // we allow this unlike the SATS spec
    | leftPointer
    | rightPointer
    | stringIndexPointer
    | idref
    | pathexpr
    ;


idrefOrPathexpr
    : idref
    | pathexpr
    ;

idref
    // TODO: production of Name (https://www.w3.org/TR/REC-xml/#NT-Name) allows ':', while NCNAME does not
    : NCName
    | QName         // above NCName in lexer
    | pointerName   // an IDREF may equal a pointer name
    ;

offset : IntegerLiteral ;

length : IntegerLiteral ;

index : IntegerLiteral ;

regex : StringLiteral ;

pointerName : XPATH | LEFT | RIGHT | STRINGINDEX | RANGE | STRINGRANGE | MATCH ;


// overriding XPath31 grammar rules

stepexpr : postfixexpr | axisstep | pointerName ;
// the following does not work instead of overriding stepexpr
// NCName : FragmentNCName | XPATH | LEFT | RIGHT | STRINGINDEX | RANGE | STRINGRANGE | MATCH ;


// lexer rules

XPATH : 'xpath' | 'XPATH' ;
LEFT : 'left' | 'LEFT' ;
RIGHT : 'right' | 'RIGHT' ;
STRINGINDEX : 'string-index' | 'STRING-INDEX' ;
RANGE : 'range' | 'RANGE' ;
STRINGRANGE : 'string-range' | 'STRING-RANGE' ;
MATCH : 'match' | 'MATCH' ;
