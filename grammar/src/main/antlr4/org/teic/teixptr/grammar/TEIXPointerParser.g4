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

// Allowing xpath as an argument to pointers with more than one
// argument (as the one below) is a mess. Since commas may be present
// in xpath selectors, this introduces ambiguity.

// For now, we solute the ambiguity by not allowing the COMMA in CHAR
// and thus not allowing COMMA in text.

// Another strategy of disambiguation would be to describe some parts
// of the XPath syntax, where commas are allowed.

string_index_pointer : STRING_INDEX OP WS* idref_or_xpath WS* COMMA WS* offset WS* CP ;

range_pointer : RANGE OP WS* range_pointer_pair ( WS* COMMA WS* range_pointer_pair )*  WS* CP ;

string_range_pointer : STRING_RANGE OP WS* idref_or_xpath WS* COMMA WS* offset WS* COMMA WS* length ( WS* COMMA WS* offset WS* COMMA WS* length )* WS* CP ;

match_pointer : MATCH OP WS* idref_or_xpath WS* COMMA WS* regex ( WS* COMMA WS* index )? WS* CP ;


range_pointer_pair : range_start WS* COMMA WS* range_end ;

range_start : range_argument ;

range_end : range_argument ;

range_argument
    : xpath_pointer     // we allow this unlike the SATS section
    | left_pointer
    | right_pointer
    | string_index_pointer
    | idref
    | xpath             // allowing this is a mess
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
