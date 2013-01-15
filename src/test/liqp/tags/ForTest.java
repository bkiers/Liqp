package liqp.tags;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ForTest {

    @Test
    public void renderTest() throws RecognitionException {

        String json = "{\"array\" : [1,2,3,4,5,6,7,8,9,10], \"item\" : {\"quantity\" : 5} }";

        String[][] tests = {
                { "{% for item in array %}{{ item }}{% endfor %}", "12345678910" },
                { "{% for item in array limit:8.5 %}{{ item }}{% endfor %}", "12345678" },
                { "{% for item in array limit:8.5 offset:6 %}{{ item }}{% endfor %}", "78910" },
                { "{% for item in array limit:2 offset:6 %}{{ item }}{% endfor %}", "78" },
                { "{% for i in (1..item.quantity) %}{{ i }}{% endfor %}", "12345" },
                { "{% for i in (1..3) %}{{ i }}{% endfor %}", "123" },
                { "{% for i in (1..nil) %}{{ i }}{% endfor %}", "" },
                { "{% for i in (XYZ .. 7) %}{{ i }}{% endfor %}", "" },
                { "{% for i in (1 .. item.quantity) offset:2 %}{{ i }}{% endfor %}", "345" },
                { "{% for i in (1.. item.quantity) offset:nil %}{{ i }}{% endfor %}", "12345" },
                { "{% for i in (1 ..item.quantity) limit:4 OFFSET:2 %}{{ i }}{% endfor %}", "1234" },
                { "{% for i in (1..item.quantity) limit:4 offset:20 %}{{ i }}{% endfor %}", "" },
                { "{% for i in (1..item.quantity) limit:0 offset:2 %}{{ i }}{% endfor %}", "" },
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }
}
