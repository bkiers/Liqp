parser grammar LiquidParser;

options {
  tokenVocab=LiquidLexer;
}

parse
 : block EOF
 ;

block
 : atom*
 ;

atom
 : tag        #atom_tag
 | output     #atom_output
 | assignment #atom_assignment
 | other      #atom_others
 ;

tag
 : raw_tag
 | comment_tag
 | if_tag
 | unless_tag
 | case_tag
 | cycle_tag
 | for_tag
 | table_tag
 | capture_tag
 | include_tag
 | other_tag
 ;

other_tag
 : tagStart Id other_tag_parameters? TagEnd other_tag_block?
 ;

other_tag_block
 : atom*? tagStart EndId TagEnd
 ;

raw_tag
 : tagStart RawStart raw_body RawEnd TagEnd
 ;

raw_body
 : OtherRaw*
 ;

comment_tag
 : tagStart CommentStart TagEnd .*? tagStart CommentEnd TagEnd
 ;

other_than_tag_start
 : ~( TagStart | TagStart2 )*
 ;

if_tag
 : tagStart IfStart expr TagEnd block elsif_tag* else_tag? tagStart IfEnd TagEnd
 ;

elsif_tag
 : tagStart Elsif expr TagEnd block
 ;

else_tag
 : tagStart Else TagEnd block
 ;

unless_tag
 : tagStart UnlessStart expr TagEnd block else_tag? tagStart UnlessEnd TagEnd
 ;

case_tag
 : tagStart CaseStart expr TagEnd other? when_tag+ else_tag? tagStart CaseEnd TagEnd
 ;

when_tag
 : tagStart When term ((Or | Comma) term)* TagEnd block
 ;

cycle_tag
 : tagStart Cycle cycle_group expr (Comma expr)* TagEnd
 ;

cycle_group
 : (expr Col)?
 ;

for_tag
 : for_array
 | for_range
 ;

for_array
 : tagStart ForStart Id In lookup attribute* TagEnd
   for_block
   tagStart ForEnd TagEnd
 ;

for_range
 : tagStart ForStart Id In OPar from=expr DotDot to=expr CPar attribute* TagEnd
   block
   tagStart ForEnd TagEnd
 ;

for_block
 : a=block (tagStart Else TagEnd b=block)?
 ;

attribute
 : Id Col expr
 ;

table_tag
 : tagStart TableStart Id In lookup attribute* TagEnd block tagStart TableEnd TagEnd
 ;

capture_tag
 : tagStart CaptureStart Id TagEnd block tagStart CaptureEnd TagEnd  #capture_tag_Id
 | tagStart CaptureStart Str TagEnd block tagStart CaptureEnd TagEnd #capture_tag_Str
 ;

include_tag
 : tagStart Include file_name_or_output (With Str)? TagEnd
 ;

file_name_or_output
 : Str                          #file_name_or_output_Str
 | output                       #file_name_or_output_output // only valid for Flavor.JEKYLL
 | other_than_tag_end_out_start #file_name_or_output_other_than_tag_end_out_start // only valid for Flavor.JEKYLL
 ;

output
 : outStart expr filter* OutEnd
 ;

filter
 : Pipe Id params?
 ;

params
 : Col param_expr ( Comma param_expr )*
 ;

param_expr
 : id2 Col expr #param_expr_key_value
 | expr         #param_expr_expr
 ;

assignment
 : tagStart Assign Id EqSign expr filter* TagEnd
 ;

expr
 : lhs=expr op=(LtEq | Lt | GtEq | Gt) rhs=expr  #expr_rel
 | lhs=expr op=(Eq | NEq) rhs=expr               #expr_eq
 | lhs=expr Contains rhs=expr                    #expr_contains
 | <assoc=right> lhs=expr op=(And | Or) rhs=expr #expr_logic
 | term                                          #expr_term
 ;

term
 : DoubleNum      #term_DoubleNum
 | LongNum        #term_LongNum
 | Str            #term_Str
 | True           #term_True
 | False          #term_False
 | Nil            #term_Nil
 | lookup         #term_lookup
 | Empty          #term_Empty
 | Blank          #term_Blank
 | OPar expr CPar #term_expr
 ;

lookup
 : id index* QMark?   #lookup_id_indexes
 | OBr Str CBr QMark? #lookup_Str
 | OBr Id CBr QMark?  #lookup_Id
 ;

id
 : Id
 | CaptureStart
 | CaptureEnd
 | CommentStart
 | CommentEnd
 | RawStart
 | RawEnd
 | IfStart
 | Elsif
 | IfEnd
 | UnlessStart
 | UnlessEnd
 | Else
 | Contains
 | CaseStart
 | CaseEnd
 | When
 | Cycle
 | ForStart
 | ForEnd
 | In
 | And
 | Or
 | TableStart
 | TableEnd
 | Assign
 | Include
 | With
 | EndId
 ;

id2
 : id
 | Empty
 | Nil
 | True
 | False
 ;

index
 : Dot id2
 | OBr expr CBr
 ;

other_tag_parameters
 : other_than_tag_end
 ;

other_than_tag_end
 : ~TagEnd+
 ;

other_than_tag_end_out_start
 : ~(TagEnd | OutStart | OutStart2)+
 ;

tagStart
 : TagStart
 | TagStart2
 ;

outStart
 : OutStart
 | OutStart2
 ;

other
 : Other+
 ;
