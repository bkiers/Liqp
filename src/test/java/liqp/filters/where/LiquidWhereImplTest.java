package liqp.filters.where;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.ParseSettings;
import liqp.Template;
import liqp.parser.Flavor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LiquidWhereImplTest {


    private Template parse(String input) {
        return Template.parse(input, new ParseSettings.Builder().withFlavor(Flavor.LIQUID).build());
    }

    @Before
    public void setUp() throws Exception {

    }

    /*
    *
    def test_where
    input = [
        {"handle" => "alpha", "ok" => true},
        {"handle" => "beta", "ok" => false},
        {"handle" => "gamma", "ok" => false},
        {"handle" => "delta", "ok" => true},
    ]

    expectation = [
        {"handle" => "alpha", "ok" => true},
        {"handle" => "delta", "ok" => true},
    ]

    assert_equal(expectation, filters.where(input, "ok", true))
    assert_equal(expectation, filters.where(input, "ok"))
    *
    * */

    @Test
    public void testWhere() {

        // given
        String input = "{ \"x\" : [" +
                "        {\"handle\" : \"alpha\", \"ok\" : true}, " +
                "        {\"handle\" : \"beta\", \"ok\" : false}, " +
                "        {\"handle\" : \"gamma\", \"ok\" : false}, " +
                "        {\"handle\" : \"delta\", \"ok\" : true} " +
                "    ]}";

        // when
        String rendered1 = parse("{{ x | where: 'ok', true | size }}" +
                "{% assign items = x | where: 'ok', true %}" +
                "{% for item in items %}" +
                "{{ item.handle }}" +
                "{% endfor %}")
                .render(input);
        String rendered2 = parse("{{ x | where: 'ok' | size }}" +
                "{% assign items = x | where: 'ok' %}" +
                "{% for item in items %}" +
                "{{ item.handle }}" +
                "{% endfor %}")
                .render(input);

        // then
        assertEquals("2alphadelta", rendered1);
        assertEquals("2alphadelta", rendered2);
    }

    /*
    def test_where_no_key_set
        input = [
        {"handle" => "alpha", "ok" => true},
        {"handle" => "beta"},
        {"handle" => "gamma"},
        {"handle" => "delta", "ok" => true},
    ]

    expectation = [
        {"handle" => "alpha", "ok" => true},
        {"handle" => "delta", "ok" => true},
    ]

    assert_equal(expectation, filters.where(input, "ok", true))
    assert_equal(expectation, filters.where(input, "ok"))
     */
    @Test
    public void testWhereNoKeySet() {
        // given
        String input = "{ \"x\" : [\n" +
                "        {\"handle\" : \"alpha\", \"ok\" : true},\n" +
                "        {\"handle\" : \"beta\"},\n" +
                "        {\"handle\" : \"gamma\"},\n" +
                "        {\"handle\" : \"delta\", \"ok\" : true}\n" +
                "    ]}";

        // when
        String rendered1 = parse("{{ x | where: 'ok', true | size }}" +
                "{% assign items = x | where: 'ok', true %}" +
                "{% for item in items %}" +
                "{{ item.handle }}" +
                "{% endfor %}")
                .render(input);
        String rendered2 = parse("{{ x | where: 'ok' | size }}" +
                "{% assign items = x | where: 'ok' %}" +
                "{% for item in items %}" +
                "{{ item.handle }}" +
                "{% endfor %}")
                .render(input);

        // then
        assertEquals("2alphadelta", rendered1);
        assertEquals("2alphadelta", rendered2);
    }

    /*
    def test_where_non_array_map_input
    assert_equal([{"a" => "ok"}], filters.where({"a" => "ok"}, "a", "ok"))
    assert_equal([], filters.where({"a" => "not ok"}, "a", "ok"))
     */
    @Test
    public void testWhereNonArrayInput() {
        assertEquals("1ok", parse("" +
                "{{ x | where: 'a', 'ok' | size }}" +
                "{{ x | where: 'a', 'ok' | map: 'a' }}")
                .render("{ \"x\" : {\"a\": \"ok\"} }"));

        assertEquals("0", parse("" +
                "{{ x | where: 'a', 'ok' | size }}" +
                "{{ x | where: 'a', 'ok' | map: 'a' }}")
                .render("{ \"x\" : {\"a\": \"not ok\"} }"));
    }

    /*
     def test_where_indexable_but_non_map_value
        assert_raises(Liquid::ArgumentError) { filters.where(1, "ok", true) }
        assert_raises(Liquid::ArgumentError) { filters.where(1, "ok") }

     */
    @Test
    public void testWhereIndexableButNotMapValue(){
        try {
            parse("{{ x | where: 'ok' }}").render("{\"x\" : 1 }");
            fail();
        } catch (Exception e) {

        }

        try {
            parse("{{ x | where: 'ok', true }}").render("{\"x\" : 1 }");
            fail();
        } catch (Exception e) {

        }
    }

    /*
    input = [
        {"message" => "Bonjour!", "language" => "French"},
        {"message" => "Hello!", "language" => "English"},
        {"message" => "Hallo!", "language" => "German"},
    ]

    assert_equal([{"message" => "Bonjour!", "language" => "French"}], filters.where(input, "language", "French"))
    assert_equal([{"message" => "Hallo!", "language" => "German"}], filters.where(input, "language", "German"))
    assert_equal([{"message" => "Hello!", "language" => "English"}], filters.where(input, "language", "English"))
     */

    @Test
    public void testWhereNonBooleanValue() {
        // given
        String input = "{ \"x\" : [\n" +
                "        {\"message\" : \"Bonjour!\", \"language\" : \"French\"},\n" +
                "        {\"message\" : \"Hello!\", \"language\" : \"English\"},\n" +
                "        {\"message\" : \"Hallo!\", \"language\" : \"German\"}\n" +
                "    ]}";

        // then
        assertEquals("Bonjour!", parse("{{ x | where: 'language', 'French' | map: 'message' }}").render(input));
        assertEquals("Hello!", parse("{{ x | where: 'language', 'English' | map: 'message' }}").render(input));
        assertEquals("Hallo!", parse("{{ x | where: 'language', 'German' | map: 'message' }}").render(input));
    }

    /*
    def test_where_array_of_only_unindexable_values
    assert_nil(filters.where([nil], "ok", true))
    assert_nil(filters.where([nil], "ok"))
     */
    @Test
    public void testWhereArrayOfOnlyUnindexableValues() {
        assertEquals("", parse("{{ x | where: 'ok', true }}").render("{ \"x\": [null]}"));
        assertEquals("", parse("{{ x | where: 'ok'}}").render("{ \"x\": [null]}"));
    }


    /*
    def test_where_no_target_value
    input = [
        {"foo" => false},
        {"foo" => true},
        {"foo" => "for sure"},
        {"bar" => true},
    ]

    assert_equal([{"foo" => true}, {"foo" => "for sure"}], filters.where(input, "foo"))
     */

    // NOTE:
    // We cannot sort the array in java and in ruby:
    // [{"foo" => true }, {"foo" => 1}].sort { |a, b| a["foo"] <=> b["foo"] }
    // ArgumentError (comparison of Hash with Hash failed)
    // but existent order is ok
    @Test
    public void testWhereNoTargetValue() {
        // given
        String input = "{ \"x\" : [\n" +
                "        {\"foo\" : false },\n" +
                "        {\"foo\" : true  },\n" +
                "        {\"foo\" : \"for sure\"   },\n" +
                "        {\"bar\" : true  }\n" +
                "    ]}";

        // when
        assertEquals("2 true for sure ", parse("" +
                "{{ x | where: 'foo' | size }} " +
                "{% assign items = x | where: 'foo' %}" +
                "{% for item in items %}" +
                "{{ item.foo }} " +
                "{% endfor %}"
                ).render(input));

        // then
    }



}
