package liqp.filters;

import liqp.ParseSettings;
import liqp.Template;
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

        Filter.registerFilter(new Filter("textilize") {
            @Override
            public Object apply(Object value, Object... params) {
                String s = super.asString(value).trim();
                return "<b>" + s.substring(1, s.length() - 1) + "</b>";
            }
        });

        Template template = Template.parse("{{ '*hi*' | textilize }}");
        String rendered = String.valueOf(template.render());

        assertThat(rendered, is("<b>hi</b>"));
    }


    @Test
    public void testFlavoredFilters() {
        String templateText = "{{ ' a  b   c' | normalize_whitespace }}";

        ParseSettings jekyllSettings = new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
        ParseSettings defaultSettings = new ParseSettings.Builder().build();

        Template template1 = Template.parse(templateText, jekyllSettings);

        String res = template1.render();
        assertEquals("a b c", res);

        Template template2 = Template.parse(templateText, defaultSettings);
        try {
            template2.render();
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("no filter available named: |normalize_whitespace"));
        }
    }
}
