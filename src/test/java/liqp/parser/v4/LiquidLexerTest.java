package liqp.parser.v4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.junit.Test;

import liquid.parser.v4.LiquidLexer;

public class LiquidLexerTest {

    // OutStart
    //  : ( {stripSpacesAroundTags}? WhitespaceChar* '{{'
    //    | WhitespaceChar* '{{-'
    //    | '{{'
    //    ) -> pushMode(IN_TAG)
    //  ;
    @Test
    public void testOutStart() {

        assertThat(singleToken("{{").getType(), is(LiquidLexer.OutStart));
        assertThat(singleToken("{{-").getType(), is(LiquidLexer.OutStart));

        // Leading space
        boolean stripSpacesAroundTags = false;
        assertThat(tokenise(" {{", stripSpacesAroundTags, true).get(0).getType(), is(LiquidLexer.Other));
        assertThat(tokenise(" {{", stripSpacesAroundTags, true).get(1).getType(), is(LiquidLexer.OutStart));
        stripSpacesAroundTags = true;
        assertThat(tokenise(" {{", stripSpacesAroundTags, true).get(0).getType(), is(LiquidLexer.OutStart));
    }

    // TagStart
    //  : ( {stripSpacesAroundTags}? WhitespaceChar* '{%'
    //    | WhitespaceChar* '{%-'
    //    | '{%'
    //    ) -> pushMode(IN_TAG)
    //  ;
    @Test
    public void testTagStart() {

        assertThat(singleToken("{%").getType(), is(LiquidLexer.TagStart));
        assertThat(singleToken("{%-").getType(), is(LiquidLexer.TagStart));

        // Leading space
        boolean stripSpacesAroundTags = false;
        assertThat(tokenise(" {%", stripSpacesAroundTags, true).get(0).getType(), is(LiquidLexer.Other));
        assertThat(tokenise(" {%", stripSpacesAroundTags, true).get(1).getType(), is(LiquidLexer.TagStart));
        stripSpacesAroundTags = true;
        assertThat(tokenise(" {%", stripSpacesAroundTags, true).get(0).getType(), is(LiquidLexer.TagStart));
    }

    // Other
    //  : .
    //  ;
    @Test
    public void testNoSpace() {
        assertThat(singleToken("x").getType(), is(LiquidLexer.Other));
        assertThat(singleToken("{").getType(), is(LiquidLexer.Other));
        assertThat(singleToken("?").getType(), is(LiquidLexer.Other));
        assertThat(singleToken(" ").getType(), is(LiquidLexer.Other));
        assertThat(singleToken("\t").getType(), is(LiquidLexer.Other));
        assertThat(singleToken("\r").getType(), is(LiquidLexer.Other));
        assertThat(singleToken("\n").getType(), is(LiquidLexer.Other));
    }

    // mode IN_TAG;
    //
    //   OutStart2 : '{{' -> pushMode(IN_TAG);
    @Test
    public void testOutStart2() {
        assertThat(tokenise("{% include {{").get(4).getType(), is(LiquidLexer.OutStart2));
        assertThat(tokenise("{{ with {{").get(4).getType(), is(LiquidLexer.OutStart2));
    }

    @Test
    public void testInvalidEndCustomTag() {
        HashSet<String> blocks = new HashSet<>();
        blocks.add("one");
        assertThat(tokenise("{%one%}{%endbad%}", blocks, new HashSet<String>()).get(4).getType(), is(LiquidLexer.InvalidEndBlockId));
    }

    @Test
    public void testInvalidCustomBlock() {
        HashSet<String> blocks = new HashSet<>();
        blocks.add("one");
        List<Token> tokens = tokenise("{%other%}{%endother%}", blocks, new HashSet<String>());

        assertThat(tokens.get(1).getType(), is(LiquidLexer.InvalidTagId));
        assertThat(tokens.get(4).getType(), is(LiquidLexer.InvalidEndBlockId));
    }

    @Test
    public void testInvalidCustomBlockEnd() {
        HashSet<String> blocks = new HashSet<>();
        blocks.add("one");
        List<Token> tokens = tokenise("{%other%}{%endone%}", blocks, new HashSet<String>());

        assertThat(tokens.get(1).getType(), is(LiquidLexer.InvalidTagId));
        assertThat(tokens.get(4).getType(), is(LiquidLexer.InvalidEndBlockId));
    }

    @Test
    public void testMismatchedEndCustomTag() {
        HashSet<String> blocks = new HashSet<>();
        blocks.add("one");
        blocks.add("bad");
        assertThat(tokenise("{%one%}{%endbad%}", blocks, new HashSet<String>()).get(4).getType(), is(LiquidLexer.MisMatchedEndBlockId));
    }

    @Test
    public void testInvalidEndTag() {
        List<Token> tagTokens = tokenise("{%%}");
        assertThat(tagTokens.get(0).getType(), is(LiquidLexer.TagStart));
        assertThat(tagTokens.get(1).getType(), is(LiquidLexer.InvalidEndTag));

        List<Token> outTokens = tokenise("{%}}");
        assertThat(outTokens.get(0).getType(), is(LiquidLexer.TagStart));
        assertThat(outTokens.get(1).getType(), is(LiquidLexer.InvalidEndTag));
    }

    //   OutEnd
    //    : ( {stripSpacesAroundTags}? '}}' WhitespaceChar*
    //      | '-}}' WhitespaceChar*
    //      | '}}'
    //      ) -> popMode
    //    ;
    @Test
    public void testOutEnd() {

        assertThat(tokenise("{%if}}").get(2).getType(), is(LiquidLexer.OutEnd));
        assertThat(tokenise("{{}}").get(1).getType(), is(LiquidLexer.OutEnd));

        assertThat(tokenise("{%-if-}}").get(2).getType(), is(LiquidLexer.OutEnd));
        assertThat(tokenise("{{--}}").get(1).getType(), is(LiquidLexer.OutEnd));

        // Trailing spaces

        boolean stripSpacesAroundTags = false;
        Token token = tokenise("{{}} ", stripSpacesAroundTags, true).get(2);
        assertThat(token.getType(), is(LiquidLexer.Other));
        assertThat(token.getText(), is(" "));

        stripSpacesAroundTags = true;
        token = tokenise("{{}} ", stripSpacesAroundTags, true).get(1);
        assertThat(token.getType(), is(LiquidLexer.OutEnd));
        assertThat(token.getText(), is("}} "));
    }

    //   TagEnd
    //    : ( {stripSpacesAroundTags}? '%}' WhitespaceChar*
    //      | '-%}' WhitespaceChar*
    //      | '%}'
    //      ) -> popMode
    //    ;
    @Test
    public void testTagEnd() {

        assertThat(tokenise("{%if%}").get(2).getType(), is(LiquidLexer.TagEnd));
        assertThat(tokenise("{{%}").get(1).getType(), is(LiquidLexer.TagEnd));

        assertThat(tokenise("{%-if-%}").get(2).getType(), is(LiquidLexer.TagEnd));
        assertThat(tokenise("{{--%}").get(1).getType(), is(LiquidLexer.TagEnd));

        // Trailing spaces

        boolean stripSpacesAroundTags = false;
        Token token = tokenise("{{%} ", stripSpacesAroundTags, true).get(2);
        assertThat(token.getType(), is(LiquidLexer.Other));
        assertThat(token.getText(), is(" "));

        stripSpacesAroundTags = true;
        token = tokenise("{{%} ", stripSpacesAroundTags, true).get(1);
        assertThat(token.getType(), is(LiquidLexer.TagEnd));
        assertThat(token.getText(), is("%} "));
    }

    //   Str : SStr | DStr;
    @Test
    public void testStr() {
        assertThat(tokenise("{{'dasdasdas'").get(1).getType(), is(LiquidLexer.Str));
        assertThat(tokenise("{{\"\n\"").get(1).getType(), is(LiquidLexer.Str));
    }

    //   DotDot    : '..';
    @Test
    public void testDotDot() {
        assertThat(tokenise("{{..").get(1).getType(), is(LiquidLexer.DotDot));
        assertThat(tokenise("{{1..9").get(2).getType(), is(LiquidLexer.DotDot));
    }

    //   Dot       : '.';
    @Test
    public void testDot() {
        assertThat(tokenise("{{.").get(1).getType(), is(LiquidLexer.Dot));
    }

    //   NEq       : '!=' | '<>';
    @Test
    public void testNEq() {
        assertThat(tokenise("{{!=").get(1).getType(), is(LiquidLexer.NEq));
    }

    //   Eq        : '==';
    @Test
    public void testEq() {
        assertThat(tokenise("{{==").get(1).getType(), is(LiquidLexer.Eq));
    }

    //   EqSign    : '=';
    @Test
    public void testEqSign() {
        assertThat(tokenise("{{=").get(1).getType(), is(LiquidLexer.EqSign));
    }

    //   GtEq      : '>=';
    @Test
    public void testGtEq() {
        assertThat(tokenise("{{>=").get(1).getType(), is(LiquidLexer.GtEq));
    }

    //   Gt        : '>';
    @Test
    public void testGt() {
        assertThat(tokenise("{{>").get(1).getType(), is(LiquidLexer.Gt));
    }

    //   LtEq      : '<=';
    @Test
    public void testLtEq() {
        assertThat(tokenise("{{<=").get(1).getType(), is(LiquidLexer.LtEq));
    }

    //   Lt        : '<';
    @Test
    public void testLt() {
        assertThat(tokenise("{{<").get(1).getType(), is(LiquidLexer.Lt));
    }

    //   Minus     : '-';
    @Test
    public void testMinus() {
        assertThat(tokenise("{{ -").get(2).getType(), is(LiquidLexer.Minus));
    }

    //   Pipe      : '|';
    @Test
    public void testPipe() {
        assertThat(tokenise("{{|").get(1).getType(), is(LiquidLexer.Pipe));
    }

    //   Col       : ':';
    @Test
    public void testCol() {
        assertThat(tokenise("{{:").get(1).getType(), is(LiquidLexer.Col));
    }

    //   Comma     : ',';
    @Test
    public void testComma() {
        assertThat(tokenise("{{,").get(1).getType(), is(LiquidLexer.Comma));
    }

    //   OPar      : '(';
    @Test
    public void testOPar() {
        assertThat(tokenise("{{(").get(1).getType(), is(LiquidLexer.OPar));
    }

    //   CPar      : ')';
    @Test
    public void testCPar() {
        assertThat(tokenise("{{)").get(1).getType(), is(LiquidLexer.CPar));
    }

    //   OBr       : '[';
    @Test
    public void testOBr() {
        assertThat(tokenise("{{[").get(1).getType(), is(LiquidLexer.OBr));
    }

    //   CBr       : ']';
    @Test
    public void testCBr() {
        assertThat(tokenise("{{]").get(1).getType(), is(LiquidLexer.CBr));
    }

    //   QMark     : '?';
    @Test
    public void testQMark() {
        assertThat(tokenise("{{?").get(1).getType(), is(LiquidLexer.QMark));
    }

    //   DoubleNum
    //    : '-'? Digit+ '.' Digit+
    //    | '-'? Digit+ '.' {_input.LA(1) != '.'}?
    //    ;
    @Test
    public void testDoubleNum() {

        assertThat(tokenise("{{1.").get(1).getType(), is(LiquidLexer.DoubleNum));
        assertThat(tokenise("{{123.45").get(1).getType(), is(LiquidLexer.DoubleNum));
        assertThat(tokenise("{{-1.").get(1).getType(), is(LiquidLexer.DoubleNum));
        assertThat(tokenise("{{-123.45").get(1).getType(), is(LiquidLexer.DoubleNum));

        // Not a DoubleNum!
        assertThat(tokenise("{{1..").get(1).getType(), not(is(LiquidLexer.DoubleNum)));
    }

    //   LongNum   : '-'? Digit+;
    @Test
    public void testLongNum() {
        assertThat(tokenise("{{1").get(1).getType(), is(LiquidLexer.LongNum));
        assertThat(tokenise("{{-123456789").get(1).getType(), is(LiquidLexer.LongNum));
    }

    //   CaptureStart : 'capture';
    @Test
    public void testCaptureStart() {
        assertThat(tokenise("{%capture").get(1).getType(), is(LiquidLexer.CaptureStart));
    }

    //   CaptureEnd   : 'endcapture';
    @Test
    public void testCaptureEnd() {
        assertThat(tokenise("{%endcapture").get(1).getType(), is(LiquidLexer.CaptureEnd));
    }

    //   CommentStart : 'comment';
    @Test
    public void testCommentStart() {
        assertThat(tokenise("{%comment").get(1).getType(), is(LiquidLexer.CommentStart));
    }

    //   CommentEnd   : 'endcomment';
    @Test
    public void testCommentEnd() {
        assertThat(tokenise("{%endcomment").get(1).getType(), is(LiquidLexer.CommentEnd));
    }

    //   RawStart     : 'raw' WhitespaceChar* '%}' -> pushMode(IN_RAW);
    @Test
    public void testRawStart() {
        assertThat(tokenise("{%raw%}").get(1).getType(), is(LiquidLexer.RawStart));
        assertThat(tokenise("{%raw  %}").get(1).getType(), is(LiquidLexer.RawStart));
    }

    //   IfStart      : 'if';
    @Test
    public void testIfStart() {
        assertThat(tokenise("{%if").get(1).getType(), is(LiquidLexer.IfStart));
    }

    //   Elsif        : 'elsif';
    @Test
    public void testElsif() {
        assertThat(tokenise("{%elsif").get(1).getType(), is(LiquidLexer.Elsif));
    }

    //   IfEnd        : 'endif';
    @Test
    public void testIfEnd() {
        assertThat(tokenise("{%endif").get(1).getType(), is(LiquidLexer.IfEnd));
    }

    //   UnlessStart  : 'unless';
    @Test
    public void testUnlessStart() {
        assertThat(tokenise("{%unless").get(1).getType(), is(LiquidLexer.UnlessStart));
    }

    //   UnlessEnd    : 'endunless';
    @Test
    public void testUnlessEnd() {
        assertThat(tokenise("{%endunless").get(1).getType(), is(LiquidLexer.UnlessEnd));
    }

    //   Else         : 'else';
    @Test
    public void testElse() {
        assertThat(tokenise("{%else").get(1).getType(), is(LiquidLexer.Else));
    }

    //   Contains     : 'contains';
    @Test
    public void testContains() {
        assertThat(tokenise("{{contains").get(1).getType(), is(LiquidLexer.Contains));
    }

    //   CaseStart    : 'case';
    @Test
    public void testCaseStart() {
        assertThat(tokenise("{%case").get(1).getType(), is(LiquidLexer.CaseStart));
    }

    //   CaseEnd      : 'endcase';
    @Test
    public void testCaseEnd() {
        assertThat(tokenise("{%endcase").get(1).getType(), is(LiquidLexer.CaseEnd));
    }

    //   When         : 'when';
    @Test
    public void testWhen() {
        assertThat(tokenise("{%when").get(1).getType(), is(LiquidLexer.When));
    }

    //   Cycle        : 'cycle';
    @Test
    public void testCycle() {
        assertThat(tokenise("{%cycle").get(1).getType(), is(LiquidLexer.Cycle));
    }

    //   ForStart     : 'for';
    @Test
    public void testForStart() {
        assertThat(tokenise("{%for").get(1).getType(), is(LiquidLexer.ForStart));
    }

    //   ForEnd       : 'endfor';
    @Test
    public void testForEnd() {
        assertThat(tokenise("{%endfor").get(1).getType(), is(LiquidLexer.ForEnd));
    }

    //   In           : 'in';
    @Test
    public void testIn() {
        assertThat(tokenise("{{in").get(1).getType(), is(LiquidLexer.In));
    }

    //   And          : 'and';
    @Test
    public void testAnd() {
        assertThat(tokenise("{{and").get(1).getType(), is(LiquidLexer.And));
    }

    //   Or           : 'or';
    @Test
    public void testOr() {
        assertThat(tokenise("{{or").get(1).getType(), is(LiquidLexer.Or));
    }

    //   TableStart   : 'tablerow';
    @Test
    public void testTableStart() {
        assertThat(tokenise("{%tablerow").get(1).getType(), is(LiquidLexer.TableStart));
    }

    //   TableEnd     : 'endtablerow';
    @Test
    public void testTableEnd() {
        assertThat(tokenise("{%endtablerow").get(1).getType(), is(LiquidLexer.TableEnd));
    }

    //   Assign       : 'assign';
    @Test
    public void testAssign() {
        assertThat(tokenise("{%assign").get(1).getType(), is(LiquidLexer.Assign));
    }

    //   True         : 'true';
    @Test
    public void testTrue() {
        assertThat(tokenise("{{true").get(1).getType(), is(LiquidLexer.True));
    }

    //   False        : 'false';
    @Test
    public void testFalse() {
        assertThat(tokenise("{{false").get(1).getType(), is(LiquidLexer.False));
    }

    //   Nil          : 'nil' | 'null';
    @Test
    public void testNil() {
        assertThat(tokenise("{{nil").get(1).getType(), is(LiquidLexer.Nil));
    }

    //   Include      : 'include';
    @Test
    public void testInclude() {
        assertThat(tokenise("{%include").get(1).getType(), is(LiquidLexer.Include));
    }

    // IncludeRelative : 'include_relative' { conditional };
    @Test
    public void testIncludeRelative() {
        assertThat("tag 'include_relative' is defined only in Jekyll style",
            tokenise("{%include_relative").get(1).getType(), is(LiquidLexer.InvalidTagId));
    }

    @Test
    public void testIncludeRelativeCustomTag() {
        HashSet<String> tags = new HashSet<>();
        tags.add("include_relative");
        List<Token> tokens = tokenise("{%include_relative%}", new HashSet<String>(), tags);

        assertThat("Custom tag or block 'include_relative' can be defined in Liquid style",
            tokens.get(1).getType(), is(LiquidLexer.SimpleTagId));
    }

    //   With         : 'with';
    @Test
    public void testWith() {
        assertThat(tokenise("{{with").get(1).getType(), is(LiquidLexer.With));
    }

    //   Empty        : 'empty';
    @Test
    public void testEmpty() {
        assertThat(tokenise("{{empty").get(1).getType(), is(LiquidLexer.Empty));
    }

    //   Blank        : 'blank';
    @Test
    public void testBlank() {
        assertThat(tokenise("{{blank").get(1).getType(), is(LiquidLexer.Blank));
    }

    @Test
    public void testInvalidEndBlockId() {
        // with no registered tags, this is not a valid end blockId
        assertThat(tokenise("{%endfoo").get(1).getType(), is(LiquidLexer.InvalidEndBlockId));
    }

    @Test
    public void testBlockId() {
        HashSet<String> tags = new HashSet<>();
        tags.add("simple");
        HashSet<String> blocks = new HashSet<>();
        blocks.add("block");

        List<Token>tokens = tokenise("{%block%}{%simple%}{%endblock%}", blocks, tags);
        assertThat(tokens.get(1).getType(), is(LiquidLexer.BlockId));

        assertThat(tokens.get(4).getType(), is(LiquidLexer.SimpleTagId));
        assertThat(tokens.get(7).getType(), is(LiquidLexer.EndBlockId));

        List<Token>tokens2 = tokenise("{%if stuff = \"hi\"%}{%endif%}");
        assertThat(tokens2.get(1).getType(), is(LiquidLexer.IfStart));

        assertThat(tokens2.get(3).getType(), is(LiquidLexer.Id));
        assertThat(tokens2.get(10).getType(), is(LiquidLexer.IfEnd));
    }

    //   Id : ( Letter | '_' | Digit ) (Letter | '_' | '-' | Digit)*;
    @Test
    public void testId() {
        assertThat(tokenise("{{fubar").get(1).getType(), is(LiquidLexer.Id));
        // ruby liquid identifiers can start with a number
        assertThat(tokenise("{{3ubar").get(1).getType(), is(LiquidLexer.Id));
    }

    // mode IN_RAW;
    //
    //   RawEnd : '{%' WhitespaceChar* 'endraw' -> popMode;
    @Test
    public void testRawEnd() {
        assertThat(tokenise("{%raw%}{%endraw").get(2).getType(), is(LiquidLexer.RawEnd));
        assertThat(tokenise("{%raw%}{%    endraw").get(2).getType(), is(LiquidLexer.RawEnd));
    }

    //   OtherRaw : . ;
    @Test
    public void testOtherRaw() {
        assertThat(tokenise("{%raw%}?").get(2).getType(), is(LiquidLexer.OtherRaw));
    }

    // https://github.com/bkiers/Liqp/issues/317
    @Test
    public void testInlineComment() {

        // single lines
        assertThat(tokenise("{% # content %}").get(2).getText(), is("# content "));
        assertThat(tokenise("{% # content -%}").get(2).getText(), is("# content "));
        assertThat(tokenise("{%\n# content \n%}").get(2).getText(), is("# content \n"));

        // multi line
        String source = "{%\n" +
                "  ###############################-\n" +
                "  #- This is a comment\n" +
                "  #- across multiple lines\n" +
                "  ###############################-\n" +
                "%}";

        assertThat(tokenise(source).get(2).getText(), is("###############################-\n" +
                "  #- This is a comment\n" +
                "  #- across multiple lines\n" +
                "  ###############################-\n"));
    }

    // https://github.com/bkiers/Liqp/issues/317
    @Test
    public void testInlineCommentInTag() {
        String source =
                "{% # for i in (1..3) -%}\n" +
                "{{ i }}\n" +
                "{% # endfor %}";

        List<Token> tokens = tokenise(source);

        assertThat(tokens.size(), is(14));

        Object[][] expected = {
                new Object[] { LiquidLexer.TagStart, "{%"},
                new Object[] { LiquidLexer.WS2, " "},
                new Object[] { LiquidLexer.CommentInTagId, "# for i in (1..3) "},
                new Object[] { LiquidLexer.TagEnd, "-%}\n"},
                new Object[] { LiquidLexer.OutStart, "{{"},
                new Object[] { LiquidLexer.WS, " "},
                new Object[] { LiquidLexer.Id, "i"},
                new Object[] { LiquidLexer.WS, " "},
                new Object[] { LiquidLexer.OutEnd, "}}"},
                new Object[] { LiquidLexer.Other, "\n"},
                new Object[] { LiquidLexer.TagStart, "{%"},
                new Object[] { LiquidLexer.WS2, " "},
                new Object[] { LiquidLexer.CommentInTagId, "# endfor "},
                new Object[] { LiquidLexer.TagEnd, "%}"}
        };

        int index = 0;

        for (Object[] test : expected) {

            Token token = tokens.get(index);

            assertThat(token.getType(), is(test[0]));
            assertThat(token.getText(), is(test[1]));

            index++;
        }
    }

    private static Token singleToken(String source) {
        return singleToken(source, false, true);
    }

    private static Token singleToken(String source, boolean stripSpacesAroundTags, boolean discardEof) {
        List<Token> tokens = tokenise(source, stripSpacesAroundTags, discardEof);

        if (tokens.size() != 1) {
            throw new RuntimeException("expected 1 token in '" + source + "', found " + tokens.size() + ": " + tokens);
        }

        return tokens.get(0);
    }

    private static List<Token> tokenise(String source) {
        return tokenise(source, new HashSet<String>(), new HashSet<String>());
    }

    private static List<Token> tokenise(String source, Set<String> blocks, Set<String> tags) {
        return tokenise(source, false, true, blocks, tags);
    }

    private static List<Token> tokenise(String source, boolean stripSpacesAroundTags, boolean discardEof) {
        return tokenise(source, stripSpacesAroundTags, discardEof, new HashSet<String>(), new HashSet<String>());
    }

    private static List<Token> tokenise(String source, boolean stripSpacesAroundTags, boolean discardEof, Set<String> blocks, Set<String> tags) {
        CommonTokenStream tokenStream = commonTokenStream(source, stripSpacesAroundTags, blocks, tags);
        tokenStream.fill();
        List<Token> tokens = tokenStream.getTokens();

        if (discardEof) {
            tokens.remove(tokens.size() - 1);
        }

        return tokens;
    }

    static CommonTokenStream commonTokenStream(String source, boolean stripSpacesAroundTags, Set<String> blocks, Set<String> tags) {
        boolean isLiquidStyleInclude = true; // No tests for Jekyll style of includes, yet
        LiquidLexer lexer = new LiquidLexer(CharStreams.fromString(source), isLiquidStyleInclude, stripSpacesAroundTags, blocks, tags);

        lexer.addErrorListener(new BaseErrorListener(){
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new RuntimeException(e);
            }
        });

        return new CommonTokenStream(lexer);
    }

}
