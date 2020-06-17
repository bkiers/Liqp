package liqp.filters.where;

import liqp.ParseSettings;
import liqp.Template;
import liqp.parser.Flavor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JekyllWhereImplTest {


    private Template parse(String input) {
        return Template.parse(input, new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build());
    }

    public static class ObjectEntry {
        public String color;
        public String size;

        public ObjectEntry(String color, String size) {
            this.color = color;
            this.size = size;
        }
    }

    public static class Child {
        Child(String c, String s) {
            color = c;
            marker = s;
        }
        public String color;
        public String marker;
    }

    private Object[] array_of_objects1 = new Object[]{
            new HashMap<String, String>() {{
                put("color", "teal");
                put("size", "large");
            }}
            ,
            new HashMap<String, String>() {{
                put("color", "red");
                put("size", "large");
            }}
            ,
            new HashMap<String, String>() {{
                put("color", "red");
                put("size", "medium");
            }}
            ,
            new HashMap<String, String>() {{
                put("color", "blue");
                put("size", "medium");
            }}
    };

    private Object[] array_of_objects2 = new Object[]{
            new ObjectEntry("teal", "large"),
            new ObjectEntry("red", "large"),
            new ObjectEntry("red", "medium"),
            new ObjectEntry("blue", "medium")
    };

    private List array_of_objects3 = new ArrayList() {{
            add(new ObjectEntry("teal", "large"));
            add(new HashMap(){{
                put("color", "red");
                put("size", "large");
            }});
            add(new ObjectEntry("red", "medium"));
            add(new ObjectEntry("blue", "medium"));
        }
    };

    /*
      should "return any input that is not an array" do
          assert_equal "some string", @filter.where("some string", "la", "le")
      end
     */
    @Test
    public void shouldReturnAnyInputThatIsNotAnArray() {
        // given

        // when
        String rendered = parse("{{ x | where: 'la', 'le' }}")
                .render("x", "some string");

        // then
        assertEquals("some string", rendered);
    }

    /*
      should "filter objects in a hash appropriately" do
        hash = {"a" => {"color" => "red"}, "b" => {"color" => "blue"}}
        assert_equal 1, @filter.where(hash, "color", "red").length
        assert_equal [{"color" => "red"}], @filter.where(hash, "color", "red")
      end
     */

    @Test
    public void shouldFilterObjectsInHashAppropriatelyWhenInputIsMap() {
        // given
        Map<String, Object> data = new HashMap<>();
        Map<String, String> aData = new HashMap<>();
        aData.put("color", "red");
        data.put("a", aData);
        Map<String, String> bData = new HashMap<>();
        bData.put("color", "blue");
        data.put("b", bData);

        // when
        String rendered = parse("{{ x | where: 'color', 'red' | size }}")
                .render(true, "x", data);


        // then
        assertEquals("1", rendered);
    }

    /*
    should "filter objects appropriately" do
       assert_equal 2, @filter.where(@array_of_objects, "color", "red").length
    end
     */

    @Test
    public void shouldFilterObjectsAppropriately() {
        assertEquals("2", parse("{{ x | where: 'color', 'red' | size }}")
                .render(true, "x", array_of_objects1));
        assertEquals("2", parse("{{ x | where: 'color', 'red' | size }}")
                .render(true, "x", array_of_objects2));
        assertEquals("2", parse("{{ x | where: 'color', 'red' | size }}")
                .render(true, "x", array_of_objects3));
    }

    /*
    should "filter objects with null properties appropriately" do
       array = [{}, {"color" => nil}, {"color" => ""}, {"color" => "text"}]
       assert_equal 2, @filter.where(array, "color", nil).length
    end
     */

    @Test
    public void testFilterObjectsWithNullPropertiesAppropriately() {
        // given
        Map o1 = new HashMap<>();
        o1.put("marker", "=1=");
        Object o2 = new Child(null, "=2=");
        Object o3 = new HashMap<String, String>() {{
            put("color", "");
            put("marker", "=3=");
        }};
        Object o4 = new Child("text", "=4=");

        Object[] input1 = new Object[]{ o1, o2, o3, o4};

        // when

        String rendered = parse("{{ x | where: 'color', nil | size }}, " +
                "{% assign items = x | where: 'color', nil %}" +
                "{% for item in items %}" +
                "item marker:{{ item.marker }}," +
                "{% endfor %}")
                    .render(true, "x", input1);

        // then
        assertEquals("2, item marker:=1=,item marker:=2=,", rendered);
    }

    /*
      should "filter array properties appropriately" do
     hash = {
         "a" => {"tags" => %w(x y)},
         "b" => {"tags" => ["x"]},
         "c" => {"tags" => %w(y z)},
     }
     assert_equal 2, @filter.where(hash, "tags", "x").length
     */
    public static class TagsHolder {
        public Object tags;
        private String marker;

        public TagsHolder(Object t, String marker) {
            this.tags = t;
            this.marker = marker;
        }

        public String getMarker() {
            return marker;
        }
    }

    public static class Meta2 {
        public Object a;
        public Object b;
        public Object c;
        public Meta2(Object _a, Object _b, Object _c){
            a = _a;
            b = _b;
            c = _c;
        }
    }
    @Test
    public void shouldFilterArrayPropertiesAppropriately() {
        // given
        Map<String, Object> o1 = new HashMap<>();
        o1.put("tags", new String[]{"x", "y"});
        o1.put("marker", "o1");
        TagsHolder o2 = new TagsHolder(Collections.singletonList("x"), "o2");
        TagsHolder o3 = new TagsHolder(new String[]{"y", "z"}, "o3");

        Meta2 meta2 = new Meta2(o1, o2, o3);

        // when
        String rendered = parse("size:{{ x | where: 'tags', 'x' | size }}, " +
                "{% assign sorted = x | where: 'tags', 'x' | sort: 'marker' %}" +
                "{% for item in sorted %}" +
                "item content: {{ item.tags }}, " +
                "{% endfor %}")
                .render(true, "x", meta2);

        // then
        assertEquals("size:2, item content: xy, item content: x, ", rendered);
    }


    /*
    should "filter array properties alongside string properties" do
    hash = {
        "a" => {"tags" => %w(x y)},
        "b" => {"tags" => "x"},
        "c" => {"tags" => %w(y z)},
    }
    assertEqual 2, filter.where(hash, "tags", "x").length
     */

    @Test
    public void shouldFilterArrayPropertiesAlongsideStringProperties() {
        // given
        Map<String, Object> o1 = new HashMap<>();
        o1.put("tags", new String[]{"x", "y"});
        o1.put("marker", "o1");
        TagsHolder o2 = new TagsHolder("x", "o2");
        TagsHolder o3 = new TagsHolder(new String[]{"y", "z"}, "o3");

        Meta2 meta2 = new Meta2(o1, o2, o3);

        // when
        String rendered = parse("size:{{ x | where: 'tags', 'x' | size }}, " +
                "{% assign sorted = x | where: 'tags', 'x' | sort: 'marker' %}" +
                "{% for item in sorted %}" +
                "item content: {{ item.tags }}, " +
                "{% endfor %}")
                .render(true, "x", meta2);

        // then
        assertEquals("size:2, item content: xy, item content: x, ", rendered);
    }

    /*
    should "filter hash properties with null and empty values" do
     */
    @Test
    public void shouldFilterHashPropertiesWithNullAndEmptyValues() {
        /*
        hash = {
        "a" => {"tags" => {}},
        "b" => {"tags" => ""},
        "c" => {"tags" => nil},
        "d" => {"tags" => ["x", nil]},
        "e" => {"tags" => []},
        "f" => {"tags" => "xtra"},
    }
         */
        Map data = new HashMap();
        data.put("a", new TagsHolder(new Object(), "a"));
        data.put("b", new TagsHolder("", "b"));
        data.put("c", new TagsHolder(null, "c"));
        data.put("d", new TagsHolder(new Object[]{"x", null}, "d"));
        data.put("e", new TagsHolder(new ArrayList<>(), "e"));
        data.put("f", new TagsHolder("xtra", "f"));


        /*
        assertEqual [{"tags" => nil}], filter.where(hash, "tags", nil)
        */
        String rendered = parse("size:{{ x | where: 'tags', nil | size }}, " +
                "marker:{{ x | where: 'tags', nil | map: 'marker' }}")
                .render(true, "x", data);

        assertEquals("size:1, marker:c", rendered);

        /*
        assertEqual(
            [{"tags" => ""}, {"tags" => ["x", nil]}],
            filter.where(hash, "tags", "")
        )
         */
        rendered = parse("size:{{ x | where: 'tags', '' | size }}, " +
                "{% assign sorted = x | where: 'tags', '' | sort: 'marker' %}" +
                "{% for item in sorted %}" +
                "item marker:{{ item.marker }}," +
                "{% endfor %}")
                .render(true, "x", data);
        assertEquals("size:2, item marker:b,item marker:d,", rendered);

        /*
         assertEqual(
                [{"tags" => {}}, {"tags" => ""}, {"tags" => nil}, {"tags" => []}],
                filter.where(hash, "tags", Liquid::Expression::LITERALS["empty"])
            )
        */

        rendered = parse("size:{{ x | where: 'tags', empty | size }}, " +
                "{% assign sorted = x | where: 'tags', empty | sort: 'marker' %}" +
                "{% for item in sorted %}" +
                "item marker:{{ item.marker }}," +
                "{% endfor %}")
                .render(true, "x", data);
        // size:4, item marker:a,item marker:b,item marker:c,item marker:e,
        assertEquals("size:4, item marker:a,item marker:b,item marker:c,item marker:e,", rendered);

        /*
         assertEqual(
                [{"tags" => {}}, {"tags" => ""}, {"tags" => nil}, {"tags" => []}],
                filter.where(hash, "tags", Liquid::Expression::LITERALS["blank"])
            )
        */
        rendered = parse("size:{{ x | where: 'tags', blank | size }}, " +
                "{% assign sorted = x | where: 'tags', blank | sort: 'marker' %}" +
                "{% for item in sorted %}" +
                "item marker:{{ item.marker }}," +
                "{% endfor %}")
                .render(true, "x", data);
        assertEquals("size:4, item marker:a,item marker:b,item marker:c,item marker:e,", rendered);

    }

    /*
    should "stringify during comparison for compatibility with liquid parsing" do
        hash = {
            "The Words" => {"rating" => 1.2, "featured" => false},
            "Limitless" => {"rating" => 9.2, "featured" => true},
            "Hustle" => {"rating" => 4.7, "featured" => true},
        }

        results = @filter.where(hash, "featured", "true")
        assert_equal 2, results.length
        assert_equal 9.2, results[0]["rating"]
        assert_equal 4.7, results[1]["rating"]

        results = @filter.where(hash, "rating", 4.7)
        assert_equal 1, results.length
        assert_equal 4.7, results[0]["rating"]
      end
     */

    @Test
    public void shouldStringifyDuringComparisonForCompatibilityWithLiquidParsing() {
        // given
        Map data = new HashMap();
        Map o1 = new HashMap();
        o1.put("rating", 1.2);
        o1.put("featured", false);
        data.put("The Words", o1);

        Map o2 = new HashMap();
        o2.put("rating", 9.2);
        o2.put("featured", true);
        data.put("Limitless", o2);

        Map o3 = new HashMap();
        o3.put("rating", 4.7);
        o3.put("featured", true);
        data.put("Hustle", o3);

        // when
        String rendered = parse("" +
                "{% assign res1 = x | where: 'featured', 'true' | sort: 'rating' %}" +
                "{% assign res2 = x | where: 'rating', 4.7 %}" +
                "size res1:{{ res1 | size }}, " +
                "size res2:{{ res2 | size }}, " +
                "{% for item in res1 %}" +
                "item rating:{{ item.rating }}," +
                "{% endfor %}")
                .render(true, "x", data);
        assertEquals("size res1:2, size res2:1, item rating:4.7,item rating:9.2,", rendered);
    }

    @Test
    public void shouldProperlyUseMapAfterFirst() {
        // given
        Map data = new HashMap();
        Map o1 = new HashMap();
        o1.put("rating", 1.2);
        o1.put("featured", false);
        data.put("The Words", o1);

        Map o2 = new HashMap();
        o2.put("rating", 9.2);
        o2.put("featured", true);
        data.put("Limitless", o2);

        Map o3 = new HashMap();
        o3.put("rating", 4.7);
        o3.put("featured", true);
        data.put("Hustle", o3);

        String rendered = parse("{{ x | where: 'rating', 1.2 | first | map: 'rating' }}")
                .render(true, "x", data);
        assertEquals("1.2", rendered);
    }
}
