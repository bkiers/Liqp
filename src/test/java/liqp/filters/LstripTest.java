package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;

public class LstripTest {

    /*
        def test_lstrip
          assert_template_result 'ab c  ', "{{ source | lstrip }}", 'source' => " ab c  "
          assert_template_result "ab c  \n \t", "{{ source | lstrip }}", 'source' => " \tab c  \n \t"
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ source | lstrip }}", "", "{ \"source\": null }"},
                {"{{ source | lstrip }}", "ab c  ", "{ \"source\": \" ab c  \" }"},
                {"{{ source | lstrip }}", "ab c  \n \t", "{ \"source\": \" \\tab c  \\n \\t\" }"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }
}
