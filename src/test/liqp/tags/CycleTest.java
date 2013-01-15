package liqp.tags;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CycleTest {

    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {
                        "{% cycle 'o', 't' %}\n" +
                                "{% cycle 33: 'one', 'two', 'three' %}\n" +
                                "{% cycle 33: 'one', 'two', 'three' %}\n" +
                                "{% cycle 3: '1', '2' %}\n" +
                                "{% cycle 33: 'one', 'two' %}\n" +
                                "{% cycle 33: 'one', 'two' %}\n" +
                                "{% cycle 3: '1', '2' %}\n" +
                                "{% cycle 3: '1', '2' %}\n" +
                                "{% cycle 'o', 't' %}\n" +
                                "{% cycle 'o', 't' %}",
                        "o\n" +
                                "one\n" +
                                "two\n" +
                                "1\n" +
                                "\n" +
                                "one\n" +
                                "2\n" +
                                "1\n" +
                                "t\n" +
                                "o"
                },
                {
                        "{% cycle 'o', 'p' %}\n" +
                                "{% cycle 'o' %}\n" +
                                "{% cycle 'o' %}\n" +
                                "{% cycle 'o', 'p' %}\n" +
                                "{% cycle 'o', 'p' %}",
                        "o\n" +
                                "o\n" +
                                "o\n" +
                                "p\n" +
                                "o"
                },
                {
                        "{% cycle 'one', 'two', 'three' %}\n" +
                                "{% cycle 'one', 'two', 'three' %}\n" +
                                "{% cycle 'one', 'two' %}\n" +
                                "{% cycle 'one', 'two', 'three' %}\n" +
                                "{% cycle 'one' %}",
                        "one\n" +
                                "two\n" +
                                "one\n" +
                                "three\n" +
                                "one"
                }
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
