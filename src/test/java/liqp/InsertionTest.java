package liqp;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.blocks.Block;
import liqp.nodes.LNode;
import liqp.tags.Tag;

public class InsertionTest {
    @Test
    public void testNestedCustomTagsAndBlocks() {
        ParseSettings parseSettings = new ParseSettings.Builder() //
                .with(new Block("block") {
                    @Override
                    public Object render(TemplateContext context, LNode... nodes) {
                        String data = (nodes.length >= 2 ? nodes[1].render(context) : nodes[0].render(
                                context)).toString();

                        return "blk[" + data + "]";
                    }
                }).with(new Tag("simple") {
                    @Override
                    public Object render(TemplateContext context, LNode... nodes) {
                        return "(sim)";
                    }
                }).build();

        String templateString = "{% block %}a{% simple %}b{% block %}c{% endblock %}d{% endblock %}";
        Template template = new TemplateParser.Builder().withParseSettings(parseSettings).build().parse(
                templateString);
        assertThat("blk[a(sim)bblk[c]d]", is(template.render()));
    }

    @Test
    public void testNestedCustomTagsAndBlocksAsOneCollection() {
        String templateString = "{% block %}a{% simple %}b{% block %}c{% endblock %}d{% endblock %}";
        
        TemplateParser parser = new TemplateParser.Builder().withParseSettings(
                new ParseSettings.Builder().with(new Block("block") {
                    @Override
                    public Object render(TemplateContext context, LNode... nodes) {
                        String data = (nodes.length >= 2 ? nodes[1].render(context) : nodes[0].render(
                                context)).toString();

                        return "blk[" + data + "]";
                    }
                }).with(new Tag("simple") {
                    @Override
                    public Object render(TemplateContext context, LNode... nodes) {
                        return "(sim)";
                    }
                }).build()).build();
        
        Template template = parser.parse(templateString);
        assertThat("blk[a(sim)bblk[c]d]", is(template.render()));
    }

    @Test
    public void testCustomTag() throws RecognitionException {
        ParseSettings parseSettings = new ParseSettings.Builder().with(new Tag("twice") {
            @Override
            public Object render(TemplateContext context, LNode... nodes) {
                Double number = super.asNumber(nodes[0].render(context)).doubleValue();
                return number * 2;
            }
        }).build();
        
        TemplateParser parser = new TemplateParser.Builder().withParseSettings(parseSettings).build();
        
        Template template = parser.parse("{% twice 10 %}");
        String rendered = String.valueOf(template.render());

        assertThat(rendered, is("20.0"));
    }

    @Test
    public void testCustomTagBlock() throws RecognitionException {
        ParseSettings parseSettings = new ParseSettings.Builder().with(new Block("twice") {
            @Override
            public Object render(TemplateContext context, LNode... nodes) {
                LNode blockNode = nodes[nodes.length - 1];
                String blockValue = super.asString(blockNode.render(context), context);
                return blockValue + " " + blockValue;
            }
        }).build();
        
        TemplateParser parser = new TemplateParser.Builder().withParseSettings(parseSettings).build();

        Template template = parser.parse("{% twice %}abc{% endtwice %}");
        String rendered = String.valueOf(template.render());

        assertThat(rendered, is("abc abc"));
    }

    @Test
    public void breakTest() throws RecognitionException {

        final String context = "{\"array\":[11,22,33,44,55]}";

        final String markup = "{% for item in array %}" +
                "{% if item > 35 %}{% break %}{% endif %}" +
                "{{ item }}" +
                "{% endfor %}";

        assertThat(TemplateParser.DEFAULT.parse(markup).render(context), is("112233"));
    }

    /*
     * def test_break_with_no_block
     *   assigns = {'i' => 1}
     *   markup = '{% break %}'
     *   expected = ''
     *
     *   assert_template_result(expected, markup, assigns)
     * end
     */
    @Test
    public void breakWithNoBlockTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% break %}").render(), is(""));
    }

    @Test
    public void continueTest() throws RecognitionException {

        final String context = "{\"array\":[11,22,33,44,55]}";

        final String markup = "{% for item in array %}" +
                "{% if item < 35 %}{% continue %}{% endif %}" +
                "{{ item }}" +
                "{% endfor %}";

        assertThat(TemplateParser.DEFAULT.parse(markup).render(context), is("4455"));
    }

    /*
     * def test_continue_with_no_block
     *   assigns = {'i' => 1}
     *   markup = '{% continue %}'
     *   expected = ''
     *
     *   assert_template_result(expected, markup, assigns)
     * end
     */
    @Test
    public void continueWithNoBlockTest() throws RecognitionException {

        assertThat(TemplateParser.DEFAULT.parse("{% continue %}").render(), is(""));
    }

    /*
     * def test_no_transform
     *   assert_template_result('this text should come out of the template without change...',
     *                          'this text should come out of the template without change...')
     *
     *   assert_template_result('blah','blah')
     *   assert_template_result('<blah>','<blah>')
     *   assert_template_result('|,.:','|,.:')
     *   assert_template_result('','')
     *
     *   text = %|this shouldnt see any transformation either but has multiple lines
     *             as you can clearly see here ...|
     *   assert_template_result(text,text)
     * end
     */
    @Test
    public void no_transformTest() throws RecognitionException {
        assertThat(TemplateParser.DEFAULT.parse(
                "this text should come out of the template without change...").render(), is(
                        "this text should come out of the template without change..."));

        assertThat(TemplateParser.DEFAULT.parse("blah").render(), is("blah"));
        assertThat(TemplateParser.DEFAULT.parse("<blah>").render(), is("<blah>"));
        assertThat(TemplateParser.DEFAULT.parse("|,.:").render(), is("|,.:"));
        assertThat(TemplateParser.DEFAULT.parse("").render(), is(""));

        String text =
                "this shouldnt see any transformation either but has multiple lines\n as you can clearly see here ...";
        assertThat(TemplateParser.DEFAULT.parse(text).render(), is(text));
    }

    @Test
    public void testCustomTagRegistration() {
        TemplateParser parser = new TemplateParser.Builder().withParseSettings(new ParseSettings.Builder()
                .with(new Tag("custom_tag") {
                    @Override
                    public Object render(TemplateContext context, LNode... nodes) {
                        return "xxx";
                    }
                })
                .build()).build();
        
        Template template = parser.parse("{% custom_tag %}");
        assertEquals("xxx", template.render());
    }
}
