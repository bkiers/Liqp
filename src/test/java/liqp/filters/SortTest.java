package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

public class SortTest {

    static final TemplateContext context = new TemplateContext();

    @Test
    public void applyTest() throws RecognitionException {

        String json =
            "{" +
                " \"words\"   : [\"2\", \"13\", \"1\"], " +
                " \"numbers\" : [2, 13, 1] " +
            "}";

        String[][] tests = {
                {"{{ x | sort }}", ""},
                {"{{ words | sort }}", "1132"},
                {"{{ numbers | sort }}", "1213"},
                {"{{ numbers | sort | last }}", "13"},
                {"{{ numbers | sort | first }}", "1"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     * def test_sort
     *   assert_equal [1,2,3,4], @filters.sort([4,3,2,1])
     *   assert_equal [{"a" => 1}, {"a" => 2}, {"a" => 3}, {"a" => 4}], @filters.sort([{"a" => 4}, {"a" => 3}, {"a" => 1}, {"a" => 2}], "a")
     * end
     */
    @Test
    public void applyOriginalTest() {

        Filter filter = Filters.COMMON_FILTERS.get("sort");

        assertThat(filter.apply(new Integer[]{4,3,2,1}, context), is((Object)new Integer[]{1,2,3,4}));

        java.util.Map[] unsorted = new java.util.Map[]{
                new HashMap<String, Integer>(){{ put("a", 4); }},
                new HashMap<String, Integer>(){{ put("a", 3); }},
                new HashMap<String, Integer>(){{ put("a", 2); }},
                new HashMap<String, Integer>(){{ put("a", 1); }}
        };

        java.util.Map[] sorted = (Sort.SortableMap[])filter.apply(unsorted, context, "a");

        java.util.Map[] expected = new java.util.Map[]{
                new HashMap<String, Integer>(){{ put("a", 1); }},
                new HashMap<String, Integer>(){{ put("a", 2); }},
                new HashMap<String, Integer>(){{ put("a", 3); }},
                new HashMap<String, Integer>(){{ put("a", 4); }}
        };

        assertThat(sorted, is(expected));
    }

    public static class Pojo implements Inspectable {
        public Pojo(int a) {
            this.a = a;
        }

        private int a;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }

    @Test
    public void testInspectable() {
        Filter filter = Filters.COMMON_FILTERS.get("sort");

        Inspectable[] unsortedIns = new Inspectable[]{
                new Pojo(4), new Pojo(3), new Pojo(2), new Pojo(1)
        };

        java.util.Map[] sorted = (Sort.SortableMap[]) filter.apply(unsortedIns, context, "a");
        java.util.Map[] expected = new java.util.Map[]{
                new HashMap<String, Integer>() {{ put("a", 1); }},
                new HashMap<String, Integer>() {{ put("a", 2); }},
                new HashMap<String, Integer>() {{ put("a", 3); }},
                new HashMap<String, Integer>() {{ put("a", 4); }}
        };

        assertThat(sorted, is(expected));
    }

    public static class PojoWithSupport implements LiquidSupport {
        public PojoWithSupport(int a) {
            this.map = new HashMap<>();
            this.map.put("a", a);
        }
        private java.util.Map<String, Object> map;

        @Override
        public Map<String, Object> toLiquid() {
            return map;
        }
    }

    @Test
    public void testLiquidSupport() {
        Filter filter = Filters.COMMON_FILTERS.get("sort");

        Inspectable[] unsortedIns = new Inspectable[]{
                new PojoWithSupport(4), new PojoWithSupport(3), new PojoWithSupport(2), new PojoWithSupport(1)
        };

        java.util.Map[] sorted = (Sort.SortableMap[]) filter.apply(unsortedIns, context, "a");
        java.util.Map[] expected = new java.util.Map[]{
                new HashMap<String, Integer>() {{
                    put("a", 1);
                }},
                new HashMap<String, Integer>() {{
                    put("a", 2);
                }},
                new HashMap<String, Integer>() {{
                    put("a", 3);
                }},
                new HashMap<String, Integer>() {{
                    put("a", 4);
                }}
        };

        assertThat(sorted, is(expected));
    }
    
    @Test
    public void testSortMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("World", 2);
        map.put("Hello", 1);

        assertEquals("World2Hello1", TemplateParser.DEFAULT.parse(
            "{% assign sorted_data = data %}{% for e in sorted_data %}{{ e }}{% endfor %}")
            .render(Collections.singletonMap("data", map)));

        assertEquals("Hello1World2", TemplateParser.DEFAULT.parse(
            "{% assign sorted_data = data | sort %}{% for e in sorted_data %}{{ e }}{% endfor %}")
            .render(Collections.singletonMap("data", map)));
    }
}
