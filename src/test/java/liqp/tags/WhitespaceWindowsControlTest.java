package liqp.tags;

import liqp.ParseSettings;
import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WhitespaceWindowsControlTest {

    @Test
    public void noStrip() throws RecognitionException {

        String source = "a  \r\n  {% assign letter = 'b' %}  \r\n{{ letter }}\r\n  c";
        Template template = Template.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("a..\r\n....\r\nb\r\n..c"));
    }

    @Test
    public void oneLhsStrip() throws RecognitionException {

        String source = "a  \r\n  {%- assign letter = 'b' %}  \r\n{{ letter }}\r\n  c";
        Template template = Template.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("a..\r\nb\r\n..c"));
    }

    @Test
    public void oneRhsStrip() throws RecognitionException {

        String source = "a  \r\n  {% assign letter = 'b' -%}  \r\n{{ letter }}\r\n  c";
        Template template = Template.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("a..\r\n..b\r\n..c"));
    }

    @Test
    public void oneBothStrip() throws RecognitionException {

        String source = "a  \r\n  {%- assign letter = 'b' -%}  \r\n{{ letter }}\r\n  c";
        Template template = Template.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("ab\r\n..c"));
    }

    @Test
    public void twoLhsStrip() throws RecognitionException {

        String source = "a  \r\n  {%- assign letter = 'b' %}  \r\n{{- letter }}\r\n  c";
        Template template = Template.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("ab\r\n..c"));
    }

    @Test
    public void twoRhsStrip() throws RecognitionException {

        String source = "a  \r\n  {% assign letter = 'b' -%}  \r\n{{ letter -}}\r\n  c";
        Template template = Template.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("a..\r\n..bc"));
    }

    @Test
    public void allStrip() throws RecognitionException {

        String source = "a  \r\n  {%- assign letter = 'b' -%}  \r\n{{- letter -}}\r\n  c";
        Template template = Template.parse(source);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("abc"));
    }

    @Test
    public void defaultStrip() throws RecognitionException {

        String source = "a  \r\n  {% assign letter = 'b' %}  \r\n{{ letter }}\r\n  c";
        ParseSettings settings = new ParseSettings.Builder().withStripSpaceAroundTags(true).build();
        Template template = Template.parse(source, settings);
        String rendered = String.valueOf(template.render().replace(' ', '.'));

        assertThat(rendered, is("abc"));
    }
}
