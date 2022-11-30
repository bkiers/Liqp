package liqp.filters;

import static liqp.TestUtils.assertPatternResultEquals;

import org.junit.Test;

import liqp.TemplateParser;

public class PushTest {
    @Test
    public void testSimple() {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "-foo-bar",
            "{% assign items = \"foo\" | split: \",\" | push: \"bar\" %}" +
                "{% for item in items %}-{{ item }}{% endfor %}");
    }

    @Test
    public void testImmutability() {
        assertPatternResultEquals("push should not affect the original array",
            TemplateParser.DEFAULT_JEKYLL, "-foo",
            "{% assign items = \"foo\" | split: \",\" %}{% assign itemsCopy = items | push: \"bar\" %}" +
                "{% for item in items %}-{{ item }}{% endfor %}");

        assertPatternResultEquals("push should not affect the original list",
            TemplateParser.DEFAULT_JEKYLL, "-foo-bar",
            "{% assign items = \"foo\" | split: \",\" | push: \"bar\" %}{% assign itemsCopy = items | push: \"baz\" %}" +
                "{% for item in items %}-{{ item }}{% endfor %}");
    }
}
