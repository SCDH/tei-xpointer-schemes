parser grammar TEIXPointerParser ;

options { tokenVocab=TEIXPointerLexer; }

// fragment is a reserved token in ANTLR parser grammars
fragm : FRAGMENT pointer WS* ;

pointer
    : xpath_pointer
    | left_pointer
    | right_pointer
    | string_index_pointer
    | range_pointer
    | string_range_pointer
    | match_pointer
    | idref
    ;

xpath_pointer : XPATH OP WS* xpath WS* CP ;

left_pointer : LEFT OP WS* idref_or_xpath WS* CP ;

right_pointer : RIGHT OP WS* idref_or_xpath WS* CP ;

string_index_pointer : STRING_INDEX OP WS* idref_or_xpath WS* COMMA WS* offset WS* CP ;

range_pointer : RANGE OP WS* range_pointer_pair ( WS* COMMA WS* range_pointer_pair )*  WS* CP ;

string_range_pointer : STRING_RANGE OP WS* idref_or_xpath WS* COMMA WS* offset WS* COMMA WS* length ( WS* COMMA WS* offset WS* COMMA WS* length )* WS* CP ;

match_pointer : MATCH OP WS* idref_or_xpath WS* COMMA WS* regex ( WS* COMMA WS* index )? WS* CP ;


range_pointer_pair : range_start WS* COMMA WS* range_end ;

range_start : range_argument ;

range_end : range_argument ;

range_argument
    : xpath_pointer
    | left_pointer
    | right_pointer
    | string_index_pointer
    | idref
    ;


idref_or_xpath
    : idref
    | xpath
    ;

idref : IDREF ;

offset : DIGIT+ ;

length : DIGIT+ ;

index : DIGIT+ ;

xpath : text+ ;

regex : text+ ;

text
    : OP
    | CP
    | WS
    | FRAGMENT
    | XPATH
    | LEFT
    | RIGHT
    | STRING_INDEX
    | RANGE
    | STRING_RANGE
    | MATCH
    | DIGIT
    | IDREF
    | CHAR
    ;