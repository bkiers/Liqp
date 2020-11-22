package liqp.tags;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DecrementTest {

    @Test
    public void testDec() {

        String[][] tests = {
                {"{%decrement port %}", "-1"},
                {"{%decrement port %} {%decrement port%}", "-1 -2"},
                {"{%decrement port %} {%decrement starboard%} {%decrement port %} {%decrement port%} {%decrement starboard %}", "-1 -1 -2 -3 -2"},
                {"{% assign x = 42 %}{{x}} {%decrement x %} {%decrement x %} {{x}}", "42 -1 -2 42"},
                {"{% decrement x %} {% decrement x %} {{x}}", "-1 -2 -2"},
                {"{% decrement var %}{% assign var=5 %}{% decrement var %}{{ var }}{% decrement var %}", "-1-25-3"},
                {"{% decrement var %}{{ var }}{% assign var=5 %}{% decrement var %}{{ var }}{% decrement var %}", "-1-1-25-3"},
                {"{% decrement var %}{% assign exp='var' %}{{ [exp] }}", "-1-1"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
