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

xpathPointer : 'xpath' OP Whitespace* pathexpr Whitespace* CP ;

leftPointer : 'left' OP Whitespace* idrefOrPathexpr Whitespace* CP ;

rightPointer : 'right' OP Whitespace* idrefOrPathexpr Whitespace* CP ;

stringIndexPointer : 'string-index' OP Whitespace* idrefOrPathexpr Whitespace* COMMA Whitespace* offset Whitespace* CP ;

rangePointer : 'range' OP Whitespace* rangePointerPair ( Whitespace* COMMA Whitespace* rangePointerPair )*  Whitespace* CP ;

stringRangePointer : 'string-range' OP Whitespace* idrefOrPathexpr Whitespace* COMMA Whitespace* offset Whitespace* COMMA Whitespace* length ( Whitespace* COMMA Whitespace* offset Whitespace* COMMA Whitespace* length )* Whitespace* CP ;

matchPointer : 'match' OP Whitespace* idrefOrPathexpr Whitespace* COMMA Whitespace* regex ( Whitespace* COMMA Whitespace* index )? Whitespace* CP ;


rangePointerPair : rangeStart Whitespace* COMMA Whitespace* rangeEnd ;

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
    | QName  // above NCName in lexer
    ;

offset : IntegerLiteral ;

length : IntegerLiteral ;

index : IntegerLiteral ;

regex : StringLiteral ;
