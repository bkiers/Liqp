grammar Liquid;

options {
  output=AST;
  ASTLabelType=CommonTree;
}

tokens {
  ASSIGNMENT;
  ATTRIBUTES;
  BLOCK;
  CAPTURE;
  CASE;
  COMMENT;
  CYCLE; 
  ELSE;
  FILTERS;
  FILTER;
  FOR_ARRAY;
  FOR_RANGE;
  GROUP;
  IF;
  INCLUDE;
  LOOKUP;
  OUTPUT;
  PARAMS;
  PLAIN;
  RAW;
  TABLE;
  UNLESS;
  WHEN;
  WITH;
}

@parser::header {
  package liqp;
}

@lexer::header {
  package liqp;
}

@parser::members {
  @Override
  public void reportError(RecognitionException e) {
    throw new RuntimeException(e); 
  }
}

@lexer::members {
  private boolean inTag = false;
  
  private boolean openTagAhead() {
    return input.LA(1) == '{' && (input.LA(2) == '{' || input.LA(2) == '\u0025');
  }
  
  @Override
  public void reportError(RecognitionException e) {
    throw new RuntimeException(e); 
  }
}

/* parser rules */
parse
 : block EOF -> block
   //(t=. {System.out.printf("\%-20s '\%s'\n", tokenNames[$t.type], $t.text);})* EOF
 ;

block
 : (options{greedy=true;}: atom)* -> ^(BLOCK atom*)
 ;

atom
 : tag
 | output
 | assignment
 | Other -> ^(PLAIN Other)
 ;

tag
 : raw_tag
 | comment_tag
 | if_tag
 | case_tag
 | cycle_tag
 | for_tag
 | table_tag
 | capture_tag
 | include_tag
 ;

raw_tag
 : TagStart RawStart TagEnd raw_body TagStart RawEnd TagEnd 
   -> ^(RAW raw_body)
 ;

raw_body
 : ~TagStart*
 ;

comment_tag
 : TagStart CommentStart TagEnd comment_body TagStart CommentEnd TagEnd 
   -> ^(COMMENT comment_body)
 ;

comment_body
 : ~TagStart*
 ;

if_tag
 : TagStart IfStart expr TagEnd block else_tag? TagStart IfEnd TagEnd 
   -> ^(IF expr block ^(ELSE else_tag?))
 ;

else_tag
 : TagStart Else TagEnd block 
   -> block
 ;

case_tag
 : TagStart CaseStart expr TagEnd when_tag+ else_tag? TagStart CaseEnd TagEnd 
   -> ^(CASE expr when_tag+ ^(ELSE else_tag?))
 ;

when_tag
 : TagStart When expr TagEnd block 
   -> ^(WHEN expr block)
 ;

cycle_tag
 : TagStart Cycle cycle_group? expr (Comma expr)* TagEnd 
   -> ^(CYCLE ^(GROUP cycle_group?) expr+)
 ;

cycle_group
 : expr Col -> expr
 ;

for_tag
 : for_array
 | for_range    
 ;

for_array // attributes must be 'limit' or 'offset'!
 : TagStart ForStart Id In lookup attribute* TagEnd block TagStart ForEnd TagEnd
   -> ^(FOR_ARRAY Id lookup ^(ATTRIBUTES attribute*) block)
 ;

attribute 
 : Id Col expr -> ^(Id expr)
 ;

for_range
 : TagStart ForStart Id In OPar expr DotDot expr CPar TagEnd block TagStart ForEnd TagEnd
   -> ^(FOR_RANGE Id expr expr block)
 ;

table_tag // attributes must be 'limit' or 'cols'!
 : TagStart TableStart Id In Id attribute* TagEnd block TagStart TableEnd TagEnd
   -> ^(TABLE Id Id ^(ATTRIBUTES attribute*) block)
 ;

capture_tag
 : TagStart CaptureStart Id TagEnd block TagStart CaptureEnd TagEnd
   -> ^(CAPTURE Id block)
 ;

include_tag
 : TagStart Include a=Str (With b=Str)? TagEnd 
   -> ^(INCLUDE $a ^(WITH $b?))
 ;

output
 : OutStart expr filter* OutEnd 
   -> ^(OUTPUT expr ^(FILTERS filter*))
 ;

filter
 : Pipe Id params? 
   -> ^(FILTER Id ^(PARAMS params?))
 ;

params
 : Col expr (Comma expr)*  -> expr+
 ;

assignment
 : TagStart Assign Id EqSign expr TagEnd 
   -> ^(ASSIGNMENT Id expr)
 ;

expr
 : or_expr
 ;

or_expr
 : and_expr (Or^ and_expr)*
 ;

and_expr
 : eq_expr (And^ eq_expr)*
 ;

eq_expr
 : rel_expr ((Eq | NEq)^ rel_expr)*
 ;

rel_expr
 : term ((LtEq | Lt | GtEq | Gt)^ term)?
 ;

term
 : Num
 | Str
 | True
 | False
 | Nil
 | lookup
 ;

lookup
 : Id (Dot Id)* -> ^(LOOKUP Id+)
 ;

/* lexer rules */
OutStart : '{{' {inTag=true;};
OutEnd   : '}}' {inTag=false;};
TagStart : '{%' {inTag=true;};
TagEnd   : '%}' {inTag=false;};

CommentStart : {inTag}?=> 'comment';
CommentEnd   : {inTag}?=> 'endcomment';
RawStart     : {inTag}?=> 'raw';
RawEnd       : {inTag}?=> 'endraw';
IfStart      : {inTag}?=> 'if';
IfEnd        : {inTag}?=> 'endif';
UnlessStart  : {inTag}?=> 'unless';
UnlessEnd    : {inTag}?=> 'endunless';
Else         : {inTag}?=> 'else';
CaseStart    : {inTag}?=> 'case';
CaseEnd      : {inTag}?=> 'endcase';
When         : {inTag}?=> 'when';
Cycle        : {inTag}?=> 'cycle';
ForStart     : {inTag}?=> 'for';
ForEnd       : {inTag}?=> 'endfor';
In           : {inTag}?=> 'in';
And          : {inTag}?=> 'and';
Or           : {inTag}?=> 'or';
TableStart   : {inTag}?=> 'tablerow';
TableEnd     : {inTag}?=> 'endtablerow';
Assign       : {inTag}?=> 'assign';
True         : {inTag}?=> 'true';
False        : {inTag}?=> 'false';
Nil          : {inTag}?=> 'nil';
Include      : {inTag}?=> 'include';
With         : {inTag}?=> 'with';
CaptureStart : {inTag}?=> 'capture';
CaptureEnd   : {inTag}?=> 'endcapture';

Str : {inTag}?=> (SStr | DStr);

DotDot : {inTag}?=> '..';
Dot    : {inTag}?=> '.';
NEq    : {inTag}?=> '!=';
Eq     : {inTag}?=> '==';
EqSign : {inTag}?=> '=';
GtEq   : {inTag}?=> '>=';
Gt     : {inTag}?=> '>';
LtEq   : {inTag}?=> '<=';
Lt     : {inTag}?=> '<';
Pipe   : {inTag}?=> '|';
Col    : {inTag}?=> ':';
Comma  : {inTag}?=> ',';
OPar   : {inTag}?=> '(';
CPar   : {inTag}?=> ')';
Id     : {inTag}?=> (Letter | '_') (Letter | '_' | Digit)*;
Num    : {inTag}?=> Digit+;
WS     : {inTag}?=> (' ' | '\t' | '\r' | '\n')+ {skip();};

Other
 : ({!inTag && !openTagAhead()}?=> . )+
   {
     String s = getText().replaceAll("\\s+", " ").trim();
     if(s.isEmpty()) {
       skip();
     }
     else {
       setText(s);
     }
   }
 ;

/* fragment rules */
fragment Letter : 'a'..'z' | 'A'..'Z';
fragment Digit  : '0'..'9';
fragment SStr   : '\'' ~'\''* '\'';
fragment DStr   : '"' ~'"'* '"';
