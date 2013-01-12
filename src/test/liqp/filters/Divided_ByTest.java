package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Divided_ByTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                { "{{ 8 | divided_by: 2 }}", "4" },
                { "{{ 8 | divided_by: 3 }}", "2" },
                { "{{ 8 | divided_by: 3. }}", String.valueOf(8 / 3.0) },
                { "{{ 8 | divided_by: 3.0 }}", String.valueOf(8 / 3.0) },
                { "{{ 8 | divided_by: 2.0 }}", "4.0" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
