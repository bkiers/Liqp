package liqp.parser.v4;

import liquid.parser.v4.LiquidParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class LiquidParserTest {

    // custom_tag
    //  : tagStart Id custom_tag_parameters? TagEnd custom_tag_block?
    //  ;
    //
    // custom_tag_block
    //  : atom+? tagStart EndId TagEnd
    //  ;
    //
    // custom_tag_parameters
    //  : other_than_tag_end
    //  ;
    //
    // other_than_tag_end
    //  : ~TagEnd+
    //  ;
    @Test
    public void testCustom_tag() {

        assertThat(
                texts("{% mu %}", "other_tag"),
                equalTo(array("{%", "mu", "%}"))
        );

        assertThat(
                texts("{% mu | 42 %}", "other_tag"),
                equalTo(array("{%", "mu", "|42", "%}"))
        );

        assertThat(
                texts("{% mu %} . {% endmu %}", "other_tag"),
                equalTo(array("{%", "mu", "%}", " . {%endmu%}"))
        );
    }

    // raw_tag
    //  : tagStart RawStart raw_body RawEnd TagEnd
    //  ;
    //
    // raw_body
    //  : OtherRaw*
    //  ;
    @Test
    public void testRaw_tag() {

        assertThat(
                texts("{% raw %}fubar{% endraw %}", "raw_tag"),
                equalTo(array("{%", "raw %}", "fubar", "{% endraw", "%}"))
        );
    }

    // comment_tag
    //  : tagStart CommentStart TagEnd .*? tagStart CommentEnd TagEnd
    //  ;
    @Test
    public void testComment_tag() {

        assertThat(
                texts("{% comment %}fubar{% endcomment %}", "comment_tag"),
                equalTo(array("{%", "comment", "%}", "f", "u", "b", "a", "r", "{%", "endcomment", "%}"))
        );
    }

    // if_tag
    //  : tagStart IfStart expr TagEnd block elsif_tag* else_tag? tagStart IfEnd TagEnd
    //  ;
    //
    // elsif_tag
    //  : tagStart Elsif expr TagEnd block
    //  ;
    //
    // else_tag
    //  : tagStart Else TagEnd block
    //  ;
    @Test
    public void testIf_tag() {

        assertThat(
                texts("{% if true or false %}a{% endif %}", "if_tag"),
                equalTo(array("{%", "if", "trueorfalse", "%}", "a", "{%", "endif", "%}"))
        );

        assertThat(
                texts("{% if true or false %}a{% elsif false %}b{% endif %}", "if_tag"),
                equalTo(array("{%", "if", "trueorfalse", "%}", "a", "{%elsiffalse%}b", "{%", "endif", "%}"))
        );

        assertThat(
                texts("{% if true or false %}a{% elsif false %}b{% else %}c{% endif %}", "if_tag"),
                equalTo(array("{%", "if", "trueorfalse", "%}", "a", "{%elsiffalse%}b", "{%else%}c", "{%", "endif", "%}"))
        );
    }

    // unless_tag
    //  : tagStart UnlessStart expr TagEnd block else_tag? tagStart UnlessEnd TagEnd
    //  ;
    @Test
    public void testUnless_tag() {

        assertThat(
                texts("{% unless something %}a{% endunless %}", "unless_tag"),
                equalTo(array("{%", "unless", "something", "%}", "a", "{%", "endunless", "%}"))
        );

        assertThat(
                texts("{% unless something %}a{% else %}b{% endunless %}", "unless_tag"),
                equalTo(array("{%", "unless", "something", "%}", "a", "{%else%}b", "{%", "endunless", "%}"))
        );
    }

    // case_tag
    //  : tagStart CaseStart expr TagEnd other? when_tag+ else_tag? tagStart CaseEnd TagEnd
    //  ;
    //
    // other
    //  : Other+
    //  ;
    //
    // when_tag
    //  : tagStart When term ((Or | Comma) term)* TagEnd block
    //  ;
    @Test
    public void testCase_tag() {

        assertThat(
                texts("{% case x %}{% when 1 %}a{% endcase %}", "case_tag"),
                equalTo(array("{%", "case", "x", "%}", "{%when1%}a", "{%", "endcase", "%}"))
        );

        assertThat(
                texts("{% case x %}...{% when 1 %}a{% endcase %}", "case_tag"),
                equalTo(array("{%", "case", "x", "%}", "...", "{%when1%}a", "{%", "endcase", "%}"))
        );

        assertThat(
                texts("{% case x %}{% when 1 %}a{% when 2 %}b{% endcase %}", "case_tag"),
                equalTo(array("{%", "case", "x", "%}", "{%when1%}a", "{%when2%}b", "{%", "endcase", "%}"))
        );

        assertThat(
                texts("{% case x %}{% when 1 %}a{% when 2 %}b{% else %}c{% endcase %}", "case_tag"),
                equalTo(array("{%", "case", "x", "%}", "{%when1%}a", "{%when2%}b", "{%else%}c", "{%", "endcase", "%}"))
        );
    }

    // cycle_tag
    //  : tagStart Cycle cycle_group expr (Comma expr)* TagEnd
    //  ;
    //
    // cycle_group
    //  : (expr Col)?
    //  ;
    @Test
    public void testCycle_tag() {

        assertThat(
                texts("{% cycle 1 %}", "cycle_tag"),
                equalTo(array("{%", "cycle", "", "1", "%}"))
        );

        assertThat(
                texts("{% cycle a: 1 %}", "cycle_tag"),
                equalTo(array("{%", "cycle", "a:", "1", "%}"))
        );

        assertThat(
                texts("{% cycle 1,2,3 %}", "cycle_tag"),
                equalTo(array("{%", "cycle", "", "1", ",", "2", ",", "3", "%}"))
        );

        assertThat(
                texts("{% cycle a: 1, 2, 3 %}", "cycle_tag"),
                equalTo(array("{%", "cycle", "a:", "1", ",", "2", ",", "3", "%}"))
        );
    }

    // for_array
    //  : tagStart ForStart Id In lookup attribute* TagEnd
    //    for_block
    //    tagStart ForEnd TagEnd
    //  ;
    @Test
    public void testFor_array() {

        assertThat(
                texts("{% for item in array %}{{ item }}{% endfor %}", "for_array"),
                equalTo(array("{%", "for", "item", "in", "array", "%}", "{{item}}", "{%", "endfor", "%}"))
        );

        assertThat(
                texts("{% for i in array limit:4 OFFSET:2 %}{{forloop.rindex0}}{% endfor %}", "for_array"),
                equalTo(array("{%", "for", "i", "in", "array", "limit:4", "OFFSET:2", "%}", "{{forloop.rindex0}}", "{%", "endfor", "%}"))
        );

        assertThat(
                texts("{% for item in a.b['c'][0]? %}{{ item }}{% endfor %}", "for_array"),
                equalTo(array("{%", "for", "item", "in", "a.b['c'][0]?", "%}", "{{item}}", "{%", "endfor", "%}"))
        );
    }

    // for_range
    //  : tagStart ForStart Id In OPar from=expr DotDot to=expr CPar attribute* TagEnd
    //    block
    //    tagStart ForEnd TagEnd
    //  ;
    @Test
    public void testFor_range() {

        assertThat(
                texts("{% for i in (1 .. item.quantity) %}{{ i }}{% endfor %}", "for_range"),
                equalTo(array("{%", "for", "i", "in", "(", "1", "..", "item.quantity", ")", "%}", "{{i}}", "{%", "endfor", "%}"))
        );

        assertThat(
                texts("{% for i in (1 .. item.quantity) offset:2 %}{{ i }}{% endfor %}", "for_range"),
                equalTo(array("{%", "for", "i", "in", "(", "1", "..", "item.quantity", ")", "offset:2", "%}", "{{i}}", "{%", "endfor", "%}"))
        );
    }

    // table_tag
    //  : tagStart TableStart Id In lookup attribute* TagEnd block tagStart TableEnd TagEnd
    //  ;
    @Test
    public void testTable_tag() {

        assertThat(
                texts("{% tablerow r in rows %}a{% endtablerow %}", "table_tag"),
                equalTo(array("{%", "tablerow", "r", "in", "rows", "%}", "a", "{%", "endtablerow", "%}"))
        );

        assertThat(
                texts("{% tablerow r in rows key:value %}a{% endtablerow %}", "table_tag"),
                equalTo(array("{%", "tablerow", "r", "in", "rows", "key:value", "%}", "a", "{%", "endtablerow", "%}"))
        );
    }

    // capture_tag
    //  : tagStart CaptureStart Id TagEnd block tagStart CaptureEnd TagEnd  #capture_tag_Id
    //  | tagStart CaptureStart Str TagEnd block tagStart CaptureEnd TagEnd #capture_tag_Str
    //  ;
    @Test
    public void testCapture_tag() {

        assertThat(
                texts("{% capture MU %}a{% endcapture %}", "capture_tag"),
                equalTo(array("{%", "capture", "MU", "%}", "a", "{%", "endcapture", "%}"))
        );

        assertThat(
                texts("{% capture 'MU' %}a{% endcapture %}", "capture_tag"),
                equalTo(array("{%", "capture", "'MU'", "%}", "a", "{%", "endcapture", "%}"))
        );
    }

    // include_tag
    //  : tagStart Include file_name_or_output (With Str)? TagEnd
    //  ;
    @Test
    public void testInclude_tag() {

        assertThat(
                texts("{% include 'somefile.ext' %}", "include_tag"),
                equalTo(array("{%", "include", "'somefile.ext'", "%}"))
        );

        assertThat(
                texts("{% include 'some-file.ext' wihk 'something-esse' %}", "include_tag"),
                equalTo(array("{%", "include", "'some-file.ext'wihk'something-esse'", "%}"))
        );

        assertThat(
                texts("{% include some-file.ext with mu %}", "include_tag"),
                equalTo(array("{%", "include", "some-file.extwithmu", "%}"))
        );

        assertThat(
                texts("{% include {{variable}} %}", "include_tag"),
                equalTo(array("{%", "include", "{{variable}}", "%}"))
        );
    }

    // output
    //  : outStart expr filter* OutEnd
    //  ;
    @Test
    public void testOutput() {

        assertThat(
                texts("{{ true }}", "output"),
                equalTo(array("{{", "true", "}}"))
        );

        assertThat(
                texts("{{ 'some string here' | uppercase }}", "output"),
                equalTo(array("{{", "'some string here'", "|uppercase", "}}"))
        );
    }

    // assignment
    //  : tagStart Assign Id EqSign expr filter? TagEnd
    //  ;
    @Test
    public void testAssignment() {

        assertThat(
                texts("{% assign mu = 'foo' %}", "assignment"),
                equalTo(array("{%", "assign", "mu", "=", "'foo'", "%}"))
        );

        assertThat(
                texts("{% assign mu = 'foo' and NIL %}", "assignment"),
                equalTo(array("{%", "assign", "mu", "=", "'foo'andNIL", "%}"))
        );

        assertThat(
                texts("{% assign mu = 'foo' | filter %}", "assignment"),
                equalTo(array("{%", "assign", "mu", "=", "'foo'", "|filter", "%}"))
        );
    }

    private static String[] texts(String source, String ruleName) {

        ParseTree tree = parse(source, ruleName);

        return texts(tree);
    }

    private static String[] texts(ParseTree tree) {

        String[] childrenTexts = new String[tree.getChildCount()];

        for (int i = 0; i < childrenTexts.length; i++) {
            childrenTexts[i] = tree.getChild(i).getText();
        }

        return childrenTexts;
    }

    private static ParseTree parse(String source, String ruleName) {

        LiquidParser parser = new LiquidParser(LiquidLexerTest.commonTokenStream(source, false));

        parser.addErrorListener(new BaseErrorListener(){
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            Method method = parser.getClass().getMethod(ruleName);
            return (ParseTree) method.invoke(parser);
        }
        catch (Exception e) {
            throw new RuntimeException("could not parse source '" + source + "' using rule: " + ruleName);
        }
    }

    private static String[] array(String... values) {
        return Arrays.asList(values).toArray(new String[values.length]);
    }
}
