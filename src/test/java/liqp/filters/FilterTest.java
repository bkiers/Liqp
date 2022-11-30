package liqp.filters;

import liqp.ParseSettings;
import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.parser.Flavor;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FilterTest {

    @Test
    public void testCustomFilter() throws RecognitionException {
        ParseSettings parseSettings = new ParseSettings.Builder().with(new Filter("textilize") {
            @Override
            public Object apply(Object value, TemplateContext context, Object... params) {
                String s = super.asString(value, context).trim();
                return "<b>" + s.substring(1, s.length() - 1) + "</b>";
            }
        }).build();

        TemplateParser parser = new TemplateParser.Builder().withParseSettings(parseSettings).build();

        Template template = parser.parse("{{ '*hi*' | textilize }}");
        String rendered = String.valueOf(template.render());

        assertThat(rendered, is("<b>hi</b>"));
    }


    @Test
    public void testFlavoredFilters() {
        String templateText = "{{ ' a  b   c' | normalize_whitespace }}";

        ParseSettings jekyllSettings = new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
        ParseSettings defaultSettings = new ParseSettings.Builder().build();

        Template template1 = new TemplateParser.Builder().withParseSettings(jekyllSettings).build()
                .parse(templateText);

        String res = template1.render();
        assertEquals("a b c", res);

        Template template2 = new TemplateParser.Builder().withParseSettings(defaultSettings).build()
                .parse(templateText);
        try {
            template2.render();
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("no filter available named: |normalize_whitespace"));
        }
    }
}
