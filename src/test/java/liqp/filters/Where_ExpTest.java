package liqp.filters;

import liqp.ParseSettings;
import liqp.Template;
import liqp.parser.Flavor;
import org.junit.Test;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;

public class Where_ExpTest {

    private final String array_of_objects = " { \"var\" : [" +
            "    { \"color\" : \"teal\", \"size\" : \"large\"  },\n" +
            "    { \"color\" : \"red\",  \"size\" : \"large\"  },\n" +
            "    { \"color\" : \"red\",  \"size\" : \"medium\" },\n" +
            "    { \"color\" : \"blue\", \"size\" : \"medium\" }\n" +
            "            ] }";
    /*
     *       should "return any input that is not an array" do
     *         assert_equal "some string", @filter.where_exp("some string", "la", "le")
     *       end
     */
    @Test
    public void testReturnAnyInputThatIsNotAnArray() {
        // given
        Map<String, Object> data = singletonMap("var", (Object) "some string");

        // when
        String res = Template.parse("{{ var | where_exp: \"la\", \"le\" }}", jekyll())
                .render(data);

        // then
        assertEquals("some string", res);
    }

    /*
     *       should "filter objects in a hash appropriately" do
     *         hash = { "a" => { "color"=>"red" }, "b" => { "color"=>"blue" } }
     *         assert_equal 1, @filter.where_exp(hash, "item", "item.color == 'red'").length
     *         assert_equal(
     *           [{ "color"=>"red" }],
     *           @filter.where_exp(hash, "item", "item.color == 'red'")
     *         )
     *       end
     */
    @Test
    public void testFilterObjectsInMapAppropriately() {
        // given
        String data = "{ \"var\" : { \"a\" : { \"color\" : \"red\" }, \"b\" : { \"color\" : \"blue\" } } }";

        // when
        String res = parse("{{ var | where_exp: \"item\", \"item.color == 'red'\" | map: 'color' }}")
                .render(data);

        // then
        assertEquals("red", res);
    }

    /*
     *       should "filter objects appropriately" do
     *         assert_equal(
     *           2,
     *           @filter.where_exp(@array_of_objects, "item", "item.color == 'red'").length
     *         )
     *       end
     */
    @Test
    public void testfFilterObjectsAppropriately() {
        // given

        // when
        String res = parse("{{ var | where_exp: \"item\", \"item.color == 'red'\" | size}}").render(array_of_objects);

        // then
        assertEquals("2", res);
    }

    /*
     *
     *
     *       should "filter objects appropriately with 'or', 'and' operators" do
     *         assert_equal(
     *           [
     *             { "color" => "teal", "size" => "large"  },
     *             { "color" => "red",  "size" => "large"  },
     *             { "color" => "red",  "size" => "medium" },
     *           ],
     *           @filter.where_exp(
     *             @array_of_objects, "item", "item.color == 'red' or item.size == 'large'"
     *           )
     *         )
     *
     *         assert_equal(
     *           [
     *             { "color" => "red", "size" => "large" },
     *           ],
     *           @filter.where_exp(
     *             @array_of_objects, "item", "item.color == 'red' and item.size == 'large'"
     *           )
     *         )
     *       end
     */
    @Test
    public void testFilterObjectsAppropriatelyWithOrAndOperators() {
        // given

        // when
        String res = parse("{% assign a = var | where_exp: \"item\", \"item.color == 'red' or item.size == 'large'\" %}" +
                "{% for i in a %}{{i.color}} - {{i.size}}\n{% endfor %}").render(array_of_objects);
        assertEquals("teal - large\nred - large\nred - medium\n", res);

        // then
        String res2 = parse("{% assign a = var | where_exp: \"item\", \"item.color == 'red' and item.size == 'large'\" %}" +
                "{% for i in a %}{{i.color}} - {{i.size}}\n{% endfor %}").render(array_of_objects);
        assertEquals("red - large\n", res2);

    }

    /*
     *       should "filter objects across multiple conditions" do
     *         sample = [
     *           { "color" => "teal", "size" => "large", "type" => "variable" },
     *           { "color" => "red",  "size" => "large", "type" => "fixed" },
     *           { "color" => "red",  "size" => "medium", "type" => "variable" },
     *           { "color" => "blue", "size" => "medium", "type" => "fixed" },
     *         ]
     *         assert_equal(
     *           [
     *             { "color" => "red", "size" => "large", "type" => "fixed" },
     *           ],
     *           @filter.where_exp(
     *             sample, "item", "item.type == 'fixed' and item.color == 'red' or item.color == 'teal'"
     *           )
     *         )
     *       end
     */
    @Test
    public void testFilterObjectsAcrossMultipleConditions() {
        // given
        String data = "{ \"var\" : [\n" +
                "{ \"color\" : \"teal\", \"size\" : \"large\", \"type\" : \"variable\" },\n" +
                "{ \"color\" : \"red\",  \"size\" : \"large\", \"type\" : \"fixed\" },\n" +
                "{ \"color\" : \"red\",  \"size\" : \"medium\", \"type\" : \"variable\" },\n" +
                "{ \"color\" : \"blue\", \"size\" : \"medium\", \"type\" : \"fixed\" }\n" +
                "] }";

        // when
        String rendered = parse("{% assign a = var | where_exp: \"item\", \"item.color == 'red' and item.size == 'large'\" %}" +
                "{% for i in a %}color - {{i.color}}, size - {{i.size}}, type - {{i.type}}{% endfor %}").render(data);

        // then
        assertEquals("color - red, size - large, type - fixed", rendered);
    }

    /*
     *       should "stringify during comparison for compatibility with liquid parsing" do
     *         hash = {
     *           "The Words" => { "rating" => 1.2, "featured" => false },
     *           "Limitless" => { "rating" => 9.2, "featured" => true },
     *           "Hustle"    => { "rating" => 4.7, "featured" => true },
     *         }
     *
     *         results = @filter.where_exp(hash, "item", "item.featured == true")
     *         assert_equal 2, results.length
     *         assert_equal 9.2, results[0]["rating"]
     *         assert_equal 4.7, results[1]["rating"]
     *
     *         results = @filter.where_exp(hash, "item", "item.rating == 4.7")
     *         assert_equal 1, results.length
     *         assert_equal 4.7, results[0]["rating"]
     *       end
     */
    @Test
    public void testStringifyDuringComparisonForCompatibilityWithLiquidParsing() {
        // given
        String data = "{ \"hash\" : {\n" +
                "\"The Words\" : { \"rating\" : 1.2, \"featured\" : false },\n" +
                "\"Limitless\" : { \"rating\" : 9.2, \"featured\" : true },\n" +
                "\"Hustle\"    : { \"rating\" : 4.7, \"featured\" : true }\n" +
                "} }";

        // when
        String rendered = parse("{% assign a = hash | where_exp: \"item\", \"item.featured == true\" %}" +
                "{% for i in a %}rating - {{i.rating}}\n{% endfor %}").render(data);
        assertEquals("rating - 9.2\nrating - 4.7\n", rendered);

        rendered = parse("{% assign a = hash | where_exp: \"item\", \"item.rating == 4.7\" %}" +
                "{% for i in a %}rating - {{i.rating}}\n{% endfor %}").render(data);
        assertEquals("rating - 4.7\n", rendered);
    }

    /*
     *       should "filter with other operators" do
     *         assert_equal [3, 4, 5], @filter.where_exp([1, 2, 3, 4, 5], "n", "n >= 3")
     *       end
     */
    @Test
    public void testFilterWithOtherOperators() {
        // given
        String data = "{ \"var\" : [ 1, 2, 3, 4, 5 ] }";

        // when
        String rendered = parse("{% assign a = var | where_exp: \"n\", \"n >= 3\" %}" +
                "{% for i in a %}{{i}}{% endfor %}").render(data);

        // then
        assertEquals("345", rendered);
    }

    /*
     *       objects = [
     *         { "id" => "a", "groups" => [1, 2] },
     *         { "id" => "b", "groups" => [2, 3] },
     *         { "id" => "c" },
     *         { "id" => "d", "groups" => [1, 3] },
     *       ]
     */
    private final String objectWithGroups = "{ \"var\" : " +
            "[\n" +
            " { \"id\" : \"a\", \"groups\" : [1, 2] },\n" +
            " { \"id\" : \"b\", \"groups\" : [2, 3] },\n" +
            " { \"id\" : \"c\" },\n" +
            " { \"id\" : \"d\", \"groups\" : [1, 3] }\n" +
            " ] }";

    /*
     *       should "filter with the contains operator over arrays" do
     *         results = @filter.where_exp(objects, "obj", "obj.groups contains 1")
     *         assert_equal 2, results.length
     *         assert_equal "a", results[0]["id"]
     *         assert_equal "d", results[1]["id"]
     *       end
     */
    @Test
    public void testFilterWithTheContainsOperatorOverArrays() {
        // given

        // when
        String rendered = parse("{% assign a = var | where_exp: \"obj\", \"obj.groups contains 1\" %}" +
                "{% for i in a %}{{i.id}}{% endfor %}").render(objectWithGroups);

        // then
        assertEquals("ad", rendered);
    }

    /*
     *       should "filter with the contains operator over hash keys" do
     *         results = @filter.where_exp(objects, "obj", "obj contains 'groups'")
     *         assert_equal 3, results.length
     *         assert_equal "a", results[0]["id"]
     *         assert_equal "b", results[1]["id"]
     *         assert_equal "d", results[2]["id"]
     *       end
     */
    @Test
    public void testFilterWithTheContainsOperatorOverHashKeys() {
        // given

        // when
        String rendered = parse("{% assign a = var | where_exp: \"obj\", \"obj contains 'groups'\" %}" +
                "{% for i in a %}{{i.id}}{% endfor %}").render(objectWithGroups);

        // then
        assertEquals("abd", rendered);
    }

    @Test
    public void testShouldAccessGlobalVariables() {
        // given
        String data = "{ " +
                "\"var\" : [ " +
                "   {\"key\" : 1, \"marker\" : \"wrong\"}, " +
                "   {\"key\" : 12, \"marker\" : \"good\"}]" +
                "}";

        // when
        String res = parse("{% assign key = 12 %}" +
                "{{ var | where_exp: 'item', 'item.key == key' | map: 'marker'}}")
                .render(data);

        // then
        assertEquals("good", res);
    }

    @Test
    public void testShouldAccessLocalVariables() {
        // given
        String data = "{ " +
                "\"var\" : [ " +
                "   {\"key\" : 1, \"marker\" : \"wrong\"}, " +
                "   {\"key\" : 12, \"marker\" : \"good\"}]" +
                "}";

        // when
        String res = parse("{% for ii in (12..12) %}" +
                "{{ var | where_exp: 'item', 'item.key == ii' | map: 'marker'}}" +
                "{% endfor %}")
                .render(data);

        // then
        assertEquals("good", res);
    }

    @Test
    public void testShouldAccessComplexVariables() {
        // given
        String data = "{ " +
                "\"var\" : [ " +
                "   {\"key\" : 1, \"marker\" : \"wrong\"}, " +
                "   {\"key\" : 12, \"marker\" : \"good\"}" +
                "], \"groups\" : [ 11 , 12 , 13 ]" +
                "}";

        // when
        String res = parse("{{ var | where_exp: 'item', 'groups contains item.key' | map: 'marker'}}")
                .render(data);

        // then
        assertEquals("good", res);
    }

    public Template parse(String template) {
        return Template.parse(template, jekyll());
    }
    public ParseSettings jekyll() {
        return new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
    }

    public ParseSettings liquid() {
        return new ParseSettings.Builder().withFlavor(Flavor.LIQUID).build();
    }


}
