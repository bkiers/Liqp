parser grammar LiquidParser;

@parser::header {
    // add java imports here
import liqp.TemplateParser;
}

@parser::members {
    private boolean liquidStyleInclude = true;
    private boolean evaluateInOutputTag = false;
    private TemplateParser.ErrorMode errorMode = TemplateParser.ErrorMode.LAX;

    private boolean isLiquidStyleInclude(){
        return liquidStyleInclude;
    }

    private boolean isJekyllStyleInclude(){
        return !liquidStyleInclude;
    }

    private boolean isEvaluateInOutputTag() {
        return evaluateInOutputTag;
    }

    private boolean isStrict() {
        return errorMode == TemplateParser.ErrorMode.STRICT;
    }
    private boolean isWarn() {
        return errorMode == TemplateParser.ErrorMode.WARN;
    }

    private boolean isLax() {
        return errorMode == TemplateParser.ErrorMode.LAX;
    }

    public LiquidParser(TokenStream input, boolean isLiquidStyleInclude, boolean evaluateInOutputTag, TemplateParser.ErrorMode errorMode) {
        this(input);
        this.liquidStyleInclude = isLiquidStyleInclude;
        this.evaluateInOutputTag = evaluateInOutputTag;
        this.errorMode = errorMode;
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
 | include_relative_tag
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
 : TagStart ForStart id In lookup Reversed? for_attribute* TagEnd
   for_block
   TagStart ForEnd TagEnd
 ;

for_range
 : TagStart ForStart id In OPar from=expr DotDot to=expr CPar Reversed? for_attribute* TagEnd
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
 : TagStart TableStart id In lookup attribute* TagEnd block TagStart TableEnd TagEnd
 ;

capture_tag
 : TagStart CaptureStart id TagEnd block TagStart CaptureEnd TagEnd  #capture_tag_Id
 | TagStart CaptureStart Str TagEnd block TagStart CaptureEnd TagEnd #capture_tag_Str
 ;

include_tag
 : {isLiquidStyleInclude()}? TagStart liquid=Include expr (With Str)? TagEnd
 | {isJekyllStyleInclude()}? TagStart jekyll=Include file_name_or_output (jekyll_include_params)* TagEnd
 ;

// include_relative available only in jekyll and uses only jekyll style
// let's treat the flag 'isJekyllStyleInclude' as also switcher for include_relative
// so in liquid style it will throw error
include_relative_tag
 : {isJekyllStyleInclude()}? TagStart IncludeRelative file_name_or_output (jekyll_include_params)* TagEnd
 | TagStart IncludeRelative file_name_or_output (jekyll_include_params)* TagEnd { reportTokenError("include_relative is not supported in Liquid Style"); }
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
 : {isEvaluateInOutputTag()}? outStart evaluate=expr filter* OutEnd
 | {isStrict()}? outStart term filter* OutEnd
 | {isWarn() || isLax()}? outStart term filter* unparsed=not_out_end? OutEnd
 ;

not_out_end
 : ( ~OutEnd )+
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
 : TagStart Assign id EqSign expr filter* TagEnd
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
