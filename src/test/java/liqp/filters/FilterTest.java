package liqp.filters;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.parser.Flavor;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FilterTest {

    @Test
    public void testCustomFilter() throws RecognitionException {

        TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("textilize") {
            @Override
            public Object apply(TemplateContext context, Object value, Object... params) {
                String s = super.asString(value, context).trim();
                return "<b>" + s.substring(1, s.length() - 1) + "</b>";
            }
        }).build();

        Template template = parser.parse("{{ '*hi*' | textilize }}");
        String rendered = String.valueOf(template.render());

        assertThat(rendered, is("<b>hi</b>"));
    }


    @Test
    public void testFlavoredFilters() {
        String templateText = "{{ ' a  b   c' | normalize_whitespace }}";

        Template template1 = new TemplateParser.Builder().withFlavor(Flavor.JEKYLL).build()
                .parse(templateText);

        String res = template1.render();
        assertEquals("a b c", res);

        Template template2 = new TemplateParser.Builder().withFlavor(Flavor.LIQUID).build()
                .parse(templateText);
        try {
            template2.render();
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("no filter available named: |normalize_whitespace"));
        }
    }
}
