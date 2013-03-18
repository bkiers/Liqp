package liqp.tags;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AssignTest {

    @Test
    public void renderTest() throws RecognitionException {

        String[][] tests = {
                {"{% assign name = 'freestyle' %}{{ name }}", "freestyle"},
                {"{% assign age = 42 %}{{ age }}", "42"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }

        String json = "{\"values\":[\"A\", [\"B1\", \"B2\"], \"C\"]}";

        assertThat(Template.parse("{% assign foo = values %}.{{ foo[1][1] }}.").render(json), is(".B2."));

        json = "{\"values\":[\"A\", {\"bar\":{\"xyz\":[\"B1\", \"ok\"]}}, \"C\"]}";

        assertThat(Template.parse("{% assign foo = values %}.{{ foo[1].bar.xyz[1] }}.").render(json), is(".ok."));
    }

    /*
     * def test_assigned_variable
     *   assert_template_result('.foo.',
     *                          '{% assign foo = values %}.{{ foo[0] }}.',
     *                          'values' => %w{foo bar baz})
     *
     *   assert_template_result('.bar.',
     *                          '{% assign foo = values %}.{{ foo[1] }}.',
     *                          'values' => %w{foo bar baz})
     * end
     *
     * def test_assign_with_filter
     *   assert_template_result('.bar.',
     *                          '{% assign foo = values | split: "," %}.{{ foo[1] }}.',
     *                          'values' => "foo,bar,baz")
     * end
     */
    @Test
    public void applyOriginalTest() {

        final String[] values = {"foo", "bar", "baz"};

        assertThat(Template.parse("{% assign foo = values %}.{{ foo[0] }}.").render("values", values), is(".foo."));
        assertThat(Template.parse("{% assign foo = values %}.{{ foo[1] }}.").render("values", values), is(".bar."));

        assertThat(Template.parse("{% assign foo = values | split: \",\" %}.{{ foo[1] }}.").render("values", "foo,bar,baz"), is(".bar."));
    }
}
