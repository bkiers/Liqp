package liqp.filters;

import static liqp.TestUtils.assertPatternResultEquals;

import org.junit.Test;

import liqp.TemplateParser;

public class UnshiftTest {
    @Test
    public void testSimple() {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "-foo-bar",
            "{% assign items = \"bar\" | split: \",\" | unshift: \"foo\" %}" +
                "{% for item in items %}-{{ item }}{% endfor %}");
    }

    @Test
    public void testImmutability() {
        assertPatternResultEquals("unshift should not affect the original array",
            TemplateParser.DEFAULT_JEKYLL, "-bar",
            "{% assign items = \"bar\" | split: \",\" %}{% assign itemsCopy = items | unshift: \"foo\" %}" +
                "{% for item in items %}-{{ item }}{% endfor %}");

        assertPatternResultEquals("unshift should not affect the original list",
            TemplateParser.DEFAULT_JEKYLL, "-foo-bar",
            "{% assign items = \"bar\" | split: \",\" | unshift: \"foo\" %}{% assign itemsCopy = items | unshift: \"baz\" %}" +
                "{% for item in items %}-{{ item }}{% endfor %}");
    }
}
