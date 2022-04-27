parser grammar LiquidParser;

@parser::members {
    private boolean isLiquid = true;

    private boolean isLiquid(){
        return isLiquid;
    }

    private boolean isJekyll(){
        return !isLiquid;
    }

    public LiquidParser(TokenStream input, boolean isLiquid) {
        this(input);
        this.isLiquid = isLiquid;
    }

    public void reportTokenError(String message, Token token) {
        notifyErrorListeners(token, message + ": '" + token.getText() + "'", null);
    }

    public void reportTokenError(String message) {
        notifyErrorListeners(message);
    }
}

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
 | continue_tag
 | other_tag
 | simple_tag
 ;

continue_tag
 : TagStart Continue TagEnd
 ;

other_tag
  : TagStart BlockId other_tag_parameters? TagEnd atom*? TagStart EndBlockId TagEnd
  | error_other_tag
  ;

error_other_tag
  : TagStart BlockId other_tag_parameters? TagEnd atom*? TagStart MisMatchedEndBlockId TagEnd {
    reportTokenError("Mismatched End Tag", _localctx.MisMatchedEndBlockId().getSymbol());
  }
  | TagStart BlockId other_tag_parameters? TagEnd atom*? TagStart InvalidEndBlockId TagEnd {
     reportTokenError("Invalid End Tag", _localctx.InvalidEndBlockId().getSymbol());
  }
  | TagStart BlockId other_tag_parameters? TagEnd atom*? { reportTokenError("Missing End Tag"); }
  | TagStart InvalidTagId other_tag_parameters? TagEnd {
    reportTokenError("Invalid Tag", _localctx.InvalidTagId().getSymbol());
  }
  | TagStart InvalidEndTag { reportTokenError("Invalid Empty Tag"); }
  ;

simple_tag
  : TagStart SimpleTagId other_tag_parameters? TagEnd
  ;

raw_tag
 : TagStart RawStart raw_body RawEnd TagEnd
 ;

raw_body
 : OtherRaw*
 ;

comment_tag
 : TagStart CommentStart TagEnd .*? TagStart CommentEnd TagEnd
 ;

other_than_tag_start
 : ~( TagStart )*
 ;

if_tag
 : TagStart IfStart expr TagEnd block elsif_tag* else_tag? TagStart IfEnd TagEnd
 ;

elsif_tag
 : TagStart Elsif expr TagEnd block
 ;

else_tag
 : TagStart Else TagEnd block
 ;

unless_tag
 : TagStart UnlessStart expr TagEnd block else_tag? TagStart UnlessEnd TagEnd
 ;

case_tag
 : TagStart CaseStart expr TagEnd other? when_tag+ else_tag? TagStart CaseEnd TagEnd
 ;

when_tag
 : TagStart When term ((Or | Comma) term)* TagEnd block
 ;

cycle_tag
 : TagStart Cycle cycle_group expr (Comma expr)* TagEnd
 ;

cycle_group
 : (expr Col)?
 ;

for_tag
 : for_array
 | for_range
 ;

for_array
 : TagStart ForStart Id In lookup Reversed? for_attribute* TagEnd
   for_block
   TagStart ForEnd TagEnd
 ;

for_range
 : TagStart ForStart Id In OPar from=expr DotDot to=expr CPar Reversed? for_attribute* TagEnd
   block
   TagStart ForEnd TagEnd
 ;

for_block
 : a=block (TagStart Else TagEnd b=block)?
 ;

for_attribute
 : Offset Col Continue
 | Offset Col expr
 | Id Col expr
 ;

attribute
 : Offset Col expr
 | Id Col expr
 ;

table_tag
 : TagStart TableStart Id In lookup attribute* TagEnd block TagStart TableEnd TagEnd
 ;

capture_tag
 : TagStart CaptureStart Id TagEnd block TagStart CaptureEnd TagEnd  #capture_tag_Id
 | TagStart CaptureStart Str TagEnd block TagStart CaptureEnd TagEnd #capture_tag_Str
 ;

include_tag
 : {isLiquid()}? TagStart liquid=Include expr (With Str)? TagEnd
 | {isJekyll()}? TagStart jekyll=Include file_name_or_output (jekyll_include_params)* TagEnd
 ;

// only valid for Flavor.JEKYLL
file_name_or_output
 : output   #jekyll_include_output
 | filename #jekyll_include_filename
 ;

// only valid for Flavor.JEKYLL
jekyll_include_params
 : id '=' expr
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
 : TagStart Assign Id EqSign expr filter* TagEnd
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
 : Empty              #lookup_empty
 | id index* QMark?   #lookup_id_indexes
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
 | Offset
 | Continue
 | Reversed
 | BlockId
 | EndBlockId
 | SimpleTagId
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

filename
 : ( . )+?
 ;

outStart
 : OutStart
 | OutStart2
 ;

other
 : Other+
 ;
