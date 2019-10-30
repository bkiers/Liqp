package liqp.filters;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;

/**
 * Created by vasyl.khrystiuk on 10/30/2019.
 */
public class Normalize_WhitespaceTest {

    /*
    context "normalize_whitespace filter" do
      should "replace newlines with a space" do
        assert_equal "a b", @filter.normalize_whitespace("a\nb")
        assert_equal "a b", @filter.normalize_whitespace("a\n\nb")
      end

      should "replace tabs with a space" do
        assert_equal "a b", @filter.normalize_whitespace("a\tb")
        assert_equal "a b", @filter.normalize_whitespace("a\t\tb")
      end

      should "replace multiple spaces with a single space" do
        assert_equal "a b", @filter.normalize_whitespace("a  b")
        assert_equal "a b", @filter.normalize_whitespace("a\t\nb")
        assert_equal "a b", @filter.normalize_whitespace("a \t \n\nb")
      end

      should "strip whitespace from beginning and end of string" do
        assert_equal "a", @filter.normalize_whitespace("a ")
        assert_equal "a", @filter.normalize_whitespace(" a")
        assert_equal "a", @filter.normalize_whitespace(" a ")
      end
    end
     */
   @Test
   public void testNormalizeWhitespace() {
       Normalize_Whitespace filter = new Normalize_Whitespace();
        String[][] cases = {
                {"a b", "a\nb", "replace newlines with a space"},
                {"a b", "a\n\nb", "replace newlines with a space"},
                {"a b", "a\t\tb", "replace tabs with a space"},
                {"a b", "a\t\tb", "replace tabs with a space"},
                {"a b", "a  b", "replace multiple spaces with a single space"},
                {"a b", "a\t\nb", "replace multiple spaces with a single space"},
                {"a b", "a \t \n\nb", "replace multiple spaces with a single space"},
                {"a", "a ", "strip whitespace from beginning and end of string"},
                {"a", " a", "strip whitespace from beginning and end of string"},
                {"a", " a ", "strip whitespace from beginning and end of string"}
        };
        for (String[] caze: cases) {
            Object actual = filter.apply(caze[1]);
            assertEquals(caze[2], caze[0], actual);
        }
   }

}
