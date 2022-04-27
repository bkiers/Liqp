lexer grammar LiquidLexer;

@lexer::members {
  private boolean stripSpacesAroundTags = false;
  private boolean stripSingleLine = false;
  private java.util.LinkedList<Token> tokens = new java.util.LinkedList<>();
  private java.util.Set<String> blocks = new java.util.HashSet<String>();
  private java.util.Set<String> tags = new java.util.HashSet<String>();
  private java.util.Stack<String> customBlockState = new java.util.Stack<String>();

  public LiquidLexer(CharStream charStream, boolean stripSpacesAroundTags, java.util.Set<String> blocks, java.util.Set<String> tags) {
    this(charStream, stripSpacesAroundTags, false, blocks, tags);
  }

  public LiquidLexer(CharStream charStream, boolean stripSpacesAroundTags) {
    this(charStream, stripSpacesAroundTags, false, new java.util.HashSet<String>(), new java.util.HashSet<String>());
  }

  public LiquidLexer(CharStream charStream, boolean stripSpacesAroundTags, boolean stripSingleLine, java.util.Set<String> blocks, java.util.Set<String> tags) {
    this(charStream);
    this.stripSpacesAroundTags = stripSpacesAroundTags;
    this.stripSingleLine = stripSingleLine;
    this.blocks = blocks;
    this.tags = tags;
  }

  @Override
  public void emit(Token t) {
    super.setToken(t);
    tokens.offer(t);
  }

  @Override
  public Token nextToken() {
    Token next = super.nextToken();
    return tokens.isEmpty() ? next : tokens.poll();
  }

  private void handleIdChain(String chain) {
    String[] ids = chain.split("\\.");

    int start = this.getCharIndex() - chain.getBytes().length;

	for (int i = 0; i < ids.length; i++) {
	  int stop = start + ids[i].getBytes().length - 1;

      this.emit(new CommonToken(this._tokenFactorySourcePair, Id, DEFAULT_TOKEN_CHANNEL, start, stop));

      if (i < ids.length - 1) {
        stop += 1;
        this.emit(new CommonToken(this._tokenFactorySourcePair, Dot, DEFAULT_TOKEN_CHANNEL, stop, stop));
      }

      start = stop + 1;
	}
  }

}

// public automatically generated constructor is busted because it doesn't allow for setting block or tags
tokens {
  BlockId,
  EndBlockId,
  SimpleTagId,
  InvalidEndBlockId,
  MisMatchedEndBlockId
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
   ) -> pushMode(IN_TAG), pushMode(IN_TAG_ID)
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

  Contains     : 'contains';
  In           : 'in';
  And          : 'and';
  Or           : 'or';
  True         : 'true';
  False        : 'false';
  Nil          : 'nil' | 'null';
  With         : 'with';
  Offset       : 'offset';
  Continue     : 'continue';
  Reversed     : 'reversed';
  Empty        : 'empty';
  Blank        : 'blank';

  IdChain
   : [a-zA-Z_] [a-zA-Z_0-9]* ( '.' [a-zA-Z_0-9]+ )+ {handleIdChain(getText());} -> skip
   ;

  Id : ( Letter | '_' | Digit) (Letter | '_' | '-' | Digit)*;

mode IN_TAG_ID;
  WS2 : WhitespaceChar+ -> channel(HIDDEN);

  InvalidEndTag
     : ( '}}' SpaceOrTab* LineBreak? {stripSpacesAroundTags && stripSingleLine}?
       | '}}' WhitespaceChar* {stripSpacesAroundTags && !stripSingleLine}?
       | '-}}' WhitespaceChar*
       | '}}'
       | '%}' SpaceOrTab* LineBreak? {stripSpacesAroundTags && stripSingleLine}?
       | '%}' WhitespaceChar* {stripSpacesAroundTags && !stripSingleLine}?
       | '-%}' WhitespaceChar*
       | '%}'
       )
     ;

  CaptureStart : 'capture' -> popMode;
  CaptureEnd   : 'endcapture' -> popMode;
  CommentStart : 'comment' -> popMode;
  CommentEnd   : 'endcomment' -> popMode;
  RawStart     : 'raw' WhitespaceChar* '%}' -> popMode, pushMode(IN_RAW);
  IfStart      : 'if' -> popMode;
  Elsif        : 'elsif' -> popMode;
  IfEnd        : 'endif' -> popMode;
  UnlessStart  : 'unless' -> popMode;
  UnlessEnd    : 'endunless' -> popMode;
  Else         : 'else' -> popMode;
  CaseStart    : 'case' -> popMode;
  CaseEnd      : 'endcase' -> popMode;
  When         : 'when' -> popMode;
  Cycle        : 'cycle' -> popMode;
  ForStart     : 'for' -> popMode;
  ForEnd       : 'endfor' -> popMode;
  TableStart   : 'tablerow' -> popMode;
  TableEnd     : 'endtablerow' -> popMode;
  Assign       : 'assign' -> popMode;
  Include      : 'include' -> popMode;

  InvalidTagId : ( Letter | '_' | Digit ) (Letter | '_' | '-' | Digit)* {
    String text = getText();
    if (blocks.contains(text)) {
      setType(BlockId);
      customBlockState.push(text);
    } else if(tags.contains(text)) {
      setType(SimpleTagId);
    } else {
      int length = text.length();
      if (length > 3 && text.startsWith("end")) {
        String suffix = text.substring(3);
        if (!customBlockState.isEmpty()) {
          String expected = customBlockState.peek();
          if (blocks.contains(suffix)) {
            if (expected.equals(suffix)) {
               customBlockState.pop();
               setType(EndBlockId);
            } else {
              setType(MisMatchedEndBlockId);
              // this is an invalid end because there was something to end, but it didn't match what we had
            }
          } else {
            setType(InvalidEndBlockId);
          }
        } else {
          // this is an invalid END (because there is nothing to end // but we do know what was expected)
          setType(InvalidEndBlockId);
        }
      } else {
        // this is an invalid custom tag
        setType(InvalidTagId);
      }
    }
  } -> popMode;

mode IN_RAW;

  RawEnd : '{%' WhitespaceChar* 'endraw' -> popMode;

  OtherRaw : . ;
