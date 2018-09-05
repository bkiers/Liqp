package liqp.filters;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StripTest {

    /*
        def test_strip
          assert_template_result 'ab c', "{{ source | strip }}", 'source' => " ab c  "
          assert_template_result 'ab c', "{{ source | strip }}", 'source' => " \tab c  \n \t"
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ source | strip }}", "", "{ \"source\": null }"},
                {"{{ source | strip }}", "ab c", "{ \"source\": \" ab c  \" }"},
                {"{{ source | strip }}", "ab c", "{ \"source\": \" \\tab c  \\n \\t\" }"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }
}
