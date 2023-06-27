package liqp.tags;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.ParseSettings;
import liqp.Template;
import liqp.TemplateParser;

// All output in this test class is tested against Ruby 2.3.1 and Liquid 4.0.0
public class WhitespaceControlTest {

    @Test
    public void noStrip() throws RecognitionException {

        String source = "a  \n  {% assign letter = 'b' %}  \n{{ letter }}\n  c";
        Template template = TemplateParser.DEFAULT.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("a..\n....\nb\n..c"));
    }

    @Test
    public void oneLhsStrip() throws RecognitionException {

        String source = "a  \n  {%- assign letter = 'b' %}  \n{{ letter }}\n  c";
        Template template = TemplateParser.DEFAULT.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("a..\nb\n..c"));
    }

    @Test
    public void oneRhsStrip() throws RecognitionException {

        String source = "a  \n  {% assign letter = 'b' -%}  \n{{ letter }}\n  c";
        Template template = TemplateParser.DEFAULT.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("a..\n..b\n..c"));
    }

    @Test
    public void oneBothStrip() throws RecognitionException {

        String source = "a  \n  {%- assign letter = 'b' -%}  \n{{ letter }}\n  c";
        Template template = TemplateParser.DEFAULT.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("ab\n..c"));
    }

    @Test
    public void twoLhsStrip() throws RecognitionException {

        String source = "a  \n  {%- assign letter = 'b' %}  \n{{- letter }}\n  c";
        Template template = TemplateParser.DEFAULT.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("ab\n..c"));
    }

    @Test
    public void twoRhsStrip() throws RecognitionException {

        String source = "a  \n  {% assign letter = 'b' -%}  \n{{ letter -}}\n  c";
        Template template = TemplateParser.DEFAULT.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("a..\n..bc"));
    }

    @Test
    public void allStrip() throws RecognitionException {

        String source = "a  \n  {%- assign letter = 'b' -%}  \n{{- letter -}}\n  c";
        Template template = TemplateParser.DEFAULT.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("abc"));
    }

    @Test
    public void defaultStrip() throws RecognitionException {

        String source = "a  \n  {% assign letter = 'b' %}  \n{{ letter }}\n  c";
        ParseSettings settings = new ParseSettings.Builder().withStripSpaceAroundTags(true).build();
        TemplateParser parser = new TemplateParser.Builder().withParseSettings(settings).build();
        Template template = parser.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("abc"));
    }
}
