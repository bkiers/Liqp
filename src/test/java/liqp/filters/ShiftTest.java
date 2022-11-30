package liqp.filters;

import static liqp.TestUtils.assertPatternInvalid;
import static liqp.TestUtils.assertPatternResultEquals;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.TemplateParser;

public class ShiftTest {
    @Test
    public void testSimple() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "-bar1",
            "{% assign items = 'foo,bar' | split: ',' | shift %}" +
                "{% for item in items %}-{{ item }}{% endfor %}{{ items.size }}");
    }

    @Test
    public void testEmpty() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "0",
            "{% assign items = '' | split: ',' | shift %}{% for item in items %}-{{ item }}{% endfor %}{{ items.size }}");
    }

    @Test
    public void testString() throws RecognitionException {
        assertPatternResultEquals("shift does not return an array when the original item isn't one",
            TemplateParser.DEFAULT_JEKYLL, "-Hello World11",
            "{% assign item = 'Hello World' | shift %}-{{ item }}{{ item.size }}");

        assertPatternResultEquals("shift does not return an array when the original item isn't one",
            TemplateParser.DEFAULT_JEKYLL, "-Hello World11",
            "{% assign item = 'Hello World' | shift: 0 %}-{{ item }}{{ item.size }}");
        assertPatternResultEquals("shift does not return an array when the original item isn't one",
            TemplateParser.DEFAULT_JEKYLL, "-Hello World11",
            "{% assign item = 'Hello World' | shift: 1 %}-{{ item }}{{ item.size }}");
        assertPatternResultEquals("shift does not return an array when the original item isn't one",
            TemplateParser.DEFAULT_JEKYLL, "-Hello World11",
            "{% assign item = 'Hello World' | shift: 2 %}-{{ item }}{{ item.size }}");
    }

    @Test
    public void testHelloWorldSplitSpace() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "-World1",
            "{% assign item = 'Hello World' | split: ' ' | shift %}-{{ item }}{{ item.size }}");
    }

    @Test
    public void testShiftValues() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "HelloWorld2",
            "{% assign item = 'Hello World' | split: ' ' | shift: 0 %}{{ item }}{{ item.size }}");

        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "World1",
            "{% assign item = 'Hello World' | split: ' ' | shift: 1 %}{{ item }}{{ item.size }}");

        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "0",
            "{% assign item = 'Hello World' | split: ' ' | shift: 2 %}{{ item }}{{ item.size }}");

        assertPatternResultEquals("shift values larger than the array result in an empty one",
            TemplateParser.DEFAULT_JEKYLL, "0",
            "{% assign item = 'Hello World' | split: ' ' | shift: 3 %}{{ item }}{{ item.size }}");

        assertPatternInvalid("negative shift values are not allowed", TemplateParser.DEFAULT_JEKYLL,
            "{% assign item = 'Hello World' | split: ' ' | shift: -1 %}{{ item }}{{ item.size }}");
    }

    @Test
    public void testHelloWorldSplitZeroLengthString() throws RecognitionException {
        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "ello World10",
            "{% assign item = 'Hello World' | split: '' | shift %}{{ item }}{{ item.size }}");

        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "Hello World11",
            "{% assign item = 'Hello World' | split: '' | shift: 0 %}{{ item }}{{ item.size }}");

        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "ello World10",
            "{% assign item = 'Hello World' | split: '' | shift: 1 %}{{ item }}{{ item.size }}");

        assertPatternResultEquals(TemplateParser.DEFAULT_JEKYLL, "llo World9",
            "{% assign item = 'Hello World' | split: '' | shift: 2 %}{{ item }}{{ item.size }}");
    }
}
