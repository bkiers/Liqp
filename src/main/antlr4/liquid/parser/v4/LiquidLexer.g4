lexer grammar LiquidLexer;

@lexer::members {
  private boolean stripSpacesAroundTags = false;
  private boolean stripSingleLine = false;

  public LiquidLexer(CharStream charStream, boolean stripSpacesAroundTags) {
    this(charStream, stripSpacesAroundTags, false);
  }

  public LiquidLexer(CharStream charStream, boolean stripSpacesAroundTags, boolean stripSingleLine) {
      this(charStream);
      this.stripSpacesAroundTags = stripSpacesAroundTags;
      this.stripSingleLine = stripSingleLine;
    }
}

OutStart
 : ( SpaceOrTab* '{{' {stripSpacesAroundTags && stripSingleLine}?
   | WhitespaceChar* '{{' {stripSpacesAroundTags && !stripSingleLine}?
   | WhitespaceChar* '{{-'
   | '{{'
   ) -> pushMode(IN_TAG)
 ;

TagStart
 : ( SpaceOrTab* '{%' {stripSpacesAroundTags && stripSingleLine}?
   | WhitespaceChar* '{%' {stripSpacesAroundTags && !stripSingleLine}?
   | WhitespaceChar* '{%-'
   | '{%'
   ) -> pushMode(IN_TAG)
 ;

Other
 : .
 ;

fragment SStr           : '\'' ~'\''* '\'';
fragment DStr           : '"' ~'"'* '"';
fragment WhitespaceChar : [ \t\r\n];
fragment SpaceOrTab     : [ \t];
fragment LineBreak      : '\r'? '\n' | '\r';
fragment Letter         : [a-zA-Z];
fragment Digit          : [0-9];

mode IN_TAG;

  OutStart2 : '{{' -> pushMode(IN_TAG);
  TagStart2 : '{%' -> pushMode(IN_TAG);

  OutEnd
   : ( '}}' SpaceOrTab* LineBreak? {stripSpacesAroundTags && stripSingleLine}?
     | '}}' WhitespaceChar* {stripSpacesAroundTags && !stripSingleLine}?
     | '-}}' WhitespaceChar*
     | '}}'
     ) -> popMode
   ;

  TagEnd
   : ( '%}' SpaceOrTab* LineBreak? {stripSpacesAroundTags && stripSingleLine}?
     | '%}' WhitespaceChar* {stripSpacesAroundTags && !stripSingleLine}?
     | '-%}' WhitespaceChar*
     | '%}'
     ) -> popMode
   ;

  Str : SStr | DStr;

  DotDot    : '..';
  Dot       : '.';
  NEq       : '!=' | '<>';
  Eq        : '==';
  EqSign    : '=';
  GtEq      : '>=';
  Gt        : '>';
  LtEq      : '<=';
  Lt        : '<';
  Minus     : '-';
  Pipe      : '|';
  Col       : ':';
  Comma     : ',';
  OPar      : '(';
  CPar      : ')';
  OBr       : '[';
  CBr       : ']';
  QMark     : '?';
  PathSep   : [/\\];

  DoubleNum
   : '-'? Digit+ '.' Digit+
   | '-'? Digit+ '.' {_input.LA(1) != '.'}?
   ;

  LongNum   : '-'? Digit+;

  WS : WhitespaceChar+ -> channel(HIDDEN);

  CaptureStart : 'capture';
  CaptureEnd   : 'endcapture';
  CommentStart : 'comment';
  CommentEnd   : 'endcomment';
  RawStart     : 'raw' WhitespaceChar* '%}' -> pushMode(IN_RAW);
  IfStart      : 'if';
  Elsif        : 'elsif';
  IfEnd        : 'endif';
  UnlessStart  : 'unless';
  UnlessEnd    : 'endunless';
  Else         : 'else';
  Contains     : 'contains';
  CaseStart    : 'case';
  CaseEnd      : 'endcase';
  When         : 'when';
  Cycle        : 'cycle';
  ForStart     : 'for';
  ForEnd       : 'endfor';
  In           : 'in';
  And          : 'and';
  Or           : 'or';
  TableStart   : 'tablerow';
  TableEnd     : 'endtablerow';
  Assign       : 'assign';
  True         : 'true';
  False        : 'false';
  Nil          : 'nil' | 'null';
  Include      : 'include';
  With         : 'with';
  Empty        : 'empty';
  Blank        : 'blank';
  EndId        : 'end' Id;

  Id : ( Letter | '_' ) (Letter | '_' | '-' | Digit)*;

mode IN_RAW;

  RawEnd : '{%' WhitespaceChar* 'endraw' -> popMode;

  OtherRaw : . ;
