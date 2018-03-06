package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UniqTest {

    /*
        def test_uniq
          assert_equal ["foo"], @filters.uniq("foo")
          assert_equal [1, 3, 2, 4], @filters.uniq([1, 1, 3, 2, 3, 1, 4, 3, 2, 1])
          assert_equal [{ "a" => 1 }, { "a" => 3 }, { "a" => 2 }], @filters.uniq([{ "a" => 1 }, { "a" => 3 }, { "a" => 1 }, { "a" => 2 }], "a")
          testdrop = TestDrop.new
          assert_equal [testdrop], @filters.uniq([testdrop, TestDrop.new], 'test')
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"", ""},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
