package liqp.filters;

import static liqp.TestUtils.assertPatternInvalid;
import static liqp.TestUtils.assertPatternResultEquals;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.TemplateParser;

public class PopTest {
    @Test
    public void testSimple() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "-foo1",
            "{% assign items = 'foo,bar' | split: ',' | pop %}" +
                "{% for item in items %}-{{ item }}{% endfor %}{{ items.size }}");
    }

    @Test
    public void testEmpty() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "0",
            "{% assign items = '' | split: ',' | pop %}{% for item in items %}-{{ item }}{% endfor %}{{ items.size }}");
    }

    @Test
    public void testString() throws RecognitionException {
        assertPatternResultEquals("pop does not return an array when the original item isn't one",
            TemplateParser.DEFAULT_JEKYLL, "-Hello World11",
            "{% assign item = 'Hello World' | pop %}-{{ item }}{{ item.size }}");

        assertPatternResultEquals("pop does not return an array when the original item isn't one",
            TemplateParser.DEFAULT_JEKYLL, "-Hello World11",
            "{% assign item = 'Hello World' | pop: 0 %}-{{ item }}{{ item.size }}");
        assertPatternResultEquals("pop does not return an array when the original item isn't one",
            TemplateParser.DEFAULT_JEKYLL, "-Hello World11",
            "{% assign item = 'Hello World' | pop: 1 %}-{{ item }}{{ item.size }}");
        assertPatternResultEquals("pop does not return an array when the original item isn't one",
            TemplateParser.DEFAULT_JEKYLL, "-Hello World11",
            "{% assign item = 'Hello World' | pop: 2 %}-{{ item }}{{ item.size }}");
    }

    @Test
    public void testHelloWorldSplitSpace() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "-Hello1",
            "{% assign item = 'Hello World' | split: ' ' | pop %}-{{ item }}{{ item.size }}");
    }

    @Test
    public void testPopValues() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "HelloWorld2",
            "{% assign item = 'Hello World' | split: ' ' | pop: 0 %}{{ item }}{{ item.size }}");

        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "Hello1",
            "{% assign item = 'Hello World' | split: ' ' | pop: 1 %}{{ item }}{{ item.size }}");

        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "0",
            "{% assign item = 'Hello World' | split: ' ' | pop: 2 %}{{ item }}{{ item.size }}");

        assertPatternResultEquals("pop values larger than the array result in an empty one",
            TemplateParser.DEFAULT_JEKYLL, "0",
            "{% assign item = 'Hello World' | split: ' ' | pop: 3 %}{{ item }}{{ item.size }}");

        assertPatternInvalid("negative pop values are not allowed", TemplateParser.DEFAULT_JEKYLL,
            "{% assign item = 'Hello World' | split: ' ' | pop: -1 %}{{ item }}{{ item.size }}");
    }

    @Test
    public void testHelloWorldSplitZeroLengthString() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "Hello Worl10",
            "{% assign item = 'Hello World' | split: '' | pop %}{{ item }}{{ item.size }}");

        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "Hello World11",
            "{% assign item = 'Hello World' | split: '' | pop: 0 %}{{ item }}{{ item.size }}");

        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "Hello Worl10",
            "{% assign item = 'Hello World' | split: '' | pop: 1 %}{{ item }}{{ item.size }}");

        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "Hello Wor9",
            "{% assign item = 'Hello World' | split: '' | pop: 2 %}{{ item }}{{ item.size }}");
    }
}
