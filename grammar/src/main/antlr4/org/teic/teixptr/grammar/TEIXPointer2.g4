grammar TEIXPointer2;

import XPath31;

// fragment is a reserved keyword in ANTLR parser grammars
fragm : POUND pointer Whitespace* ;

pointer
    : xpath_pointer
    | left_pointer
    | right_pointer
    | stringindex_pointer
    | range_pointer
    | stringrange_pointer
    | match_pointer
    | idref
    ;

xpath_pointer : 'xpath' OP Whitespace* pathexpr Whitespace* CP ;

left_pointer : 'left' OP Whitespace* idref_or_pathexpr Whitespace* CP ;

right_pointer : 'right' OP Whitespace* idref_or_pathexpr Whitespace* CP ;

stringindex_pointer : 'string-index' OP Whitespace* idref_or_pathexpr Whitespace* COMMA Whitespace* offset Whitespace* CP ;

range_pointer : 'range' OP Whitespace* range_pointer_pair ( Whitespace* COMMA Whitespace* range_pointer_pair )*  Whitespace* CP ;

stringrange_pointer : 'string-range' OP Whitespace* idref_or_pathexpr Whitespace* COMMA Whitespace* offset Whitespace* COMMA Whitespace* length ( Whitespace* COMMA Whitespace* offset Whitespace* COMMA Whitespace* length )* Whitespace* CP ;

match_pointer : 'match' OP Whitespace* idref_or_pathexpr Whitespace* COMMA Whitespace* regex ( Whitespace* COMMA Whitespace* index )? Whitespace* CP ;


range_pointer_pair : range_start Whitespace* COMMA Whitespace* range_end ;

range_start : range_argument ;

range_end : range_argument ;

range_argument
    : xpath_pointer     // we allow this unlike the SATS spec
    | left_pointer
    | right_pointer
    | stringindex_pointer
    | idref
    | pathexpr
    ;


idref_or_pathexpr
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
