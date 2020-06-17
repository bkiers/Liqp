package liqp.parser;

import liqp.RenderSettings;
import liqp.Template;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class LiquidSupportTest {

    private static final RenderSettings EAGER_RENDERING = new RenderSettings.Builder().withEvaluateMode(RenderSettings.EvaluateMode.EAGER).build();

    // test plan:
    // 1 feature
    // 3 cases:
    //   1) default rendering
    //     a) POJO
    //     b) POJO with Inspectable
    //     c) POJO with LiquidSupport
    //   2) eager rendering
    //     a) POJO
    //     b) POJO with Inspectable
    //     c) POJO with LiquidSupport

    public static class PojoChild {
        private String val = "childOK";
        public String getVal() { return val; }
        public void setVal(String val) { this.val = val; }
    }
    public static class Pojo {
        private String val = "OK";
        private PojoChild child = new PojoChild();
        public String getVal() { return val; }
        public void setVal(String val) { this.val = val; }
        public PojoChild getChild() { return child; }
        public void setChild(PojoChild child) { this.child = child; }
    }
    public static class InsPojo extends Pojo implements Inspectable {}
    public static class SuppPojo extends Pojo implements LiquidSupport {
        @Override
        public Map<String, Object> toLiquid() {
            Map<String, Object> map = new HashMap<>();
            Map<String, String > child = new HashMap<>();
            child.put("val", "SuppChild");
            map.put("child", child);
            return map;
        }
    }

    private Map<String, Object> getDataAsFoo(Object foo) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", foo);
        return data;
    }
    private void assertOldRender(String template, Map<String, Object> data, String expected) {
        String fooA = Template.parse(template).render(data);
        assertThat(fooA, is(expected));
    }

    private void assertEagerRender(String template, Map<String, Object> data, String expected) {
        String fooA = Template.parse(template).withRenderSettings(EAGER_RENDERING).render(data);
        assertThat(fooA, is(expected));
    }
    // LookupNode
    @Test
    public void testLookupNode1a() {
        assertOldRender("{{foo.child.val}}", getDataAsFoo(new Pojo()), "");
    }
    @Test
    public void testLookupNode1b() {
        assertOldRender("{{foo.child.val}}", getDataAsFoo(new InsPojo()), "childOK");
    }
    @Test
    public void testLookupNode1c() {
        assertOldRender("{{foo.child.val}}", getDataAsFoo(new SuppPojo()), "SuppChild");
    }
    @Test
    public void testLookupNode2a() {
        assertEagerRender("{{foo.child.val}}", getDataAsFoo(new Pojo()), "childOK");
    }
    @Test
    public void testLookupNode2b() {
        assertEagerRender("{{foo.child.val}}", getDataAsFoo(new InsPojo()), "childOK");
    }
    @Test
    public void testLookupNode2c() {
        assertEagerRender("{{foo.child.val}}", getDataAsFoo(new SuppPojo()), "SuppChild");
    }
    // map filter
    @Test(expected = Exception.class)
    public void testMapFilter1a() {
        assertOldRender("{{ foo | map: 'child' | map: 'val' }}", getDataAsFoo(new Pojo()), null);
    }
    @Test
    public void testMapFilter1b() {
        assertOldRender("{{ foo | map: 'child' | map: 'val' }}", getDataAsFoo(new InsPojo()), "childOK");
    }
    @Test
    public void testMapFilter1c() {
        assertOldRender("{{ foo | map: 'child' | map: 'val' }}", getDataAsFoo(new SuppPojo()), "SuppChild");
    }
    @Test
    public void testMapFilter2a() {
        assertEagerRender("{{ foo | map: 'child' | map: 'val' }}", getDataAsFoo(new Pojo()), "childOK");
    }
    @Test
    public void testMapFilter2b() {
        assertEagerRender("{{ foo | map: 'child' | map: 'val' }}", getDataAsFoo(new InsPojo()), "childOK");
    }
    @Test
    public void testMapFilter2c() {
        assertEagerRender("{{ foo | map: 'child' | map: 'val' }}", getDataAsFoo(new SuppPojo()), "SuppChild");
    }

    // LookupNode size
    @Test
    public void testLookupNodeSize1a() {
        assertOldRender("{{foo.child.size}}", getDataAsFoo(new Pojo()), "");
    }

    @Test
    public void testLookupNodeSize1b() {
        assertOldRender("{{foo.child.size}}", getDataAsFoo(new InsPojo()), "1");
    }

    @Test
    public void testLookupNodeSize1c() {
        assertOldRender("{{foo.child.size}}", getDataAsFoo(new SuppPojo()), "1");
    }

    @Test
    public void testLookupNodeSize2a() {
        assertEagerRender("{{foo.child.size}}", getDataAsFoo(new Pojo()), "1");
    }

    @Test
    public void testLookupNodeSize2b() {
        assertEagerRender("{{foo.child.size}}", getDataAsFoo(new InsPojo()), "1");
    }

    @Test
    public void testLookupNodeSize2c() {
        assertEagerRender("{{foo.child.size}}", getDataAsFoo(new SuppPojo()), "1");
    }


    public static class Target implements LiquidSupport {

        public Target() {
        }

        private String val;
        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        @Override
        public Map<String, Object> toLiquid() {
            HashMap<String, Object> data = new HashMap<>();
            data.put("val", "OK");
            return data;
        }
    }

    static class Foo {
        public String a = "A";
    }

    @Test
    public void verifyOldBehaviorWorks() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", new Foo());
        String fooA = Template.parse("{{foo.a}}").render(data);

        assertThat(fooA, is(""));
    }

    @Test
    public void renderMapWithPojosWithNewRenderingSettings() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", new Foo());
        String fooA = Template.parse("{{foo.a}}").withRenderSettings(EAGER_RENDERING).render(data);

        assertThat(fooA, is("A"));
    }

    @Test
    public void renderMapWithPojosWithMarkingInspectable() {
        Map<String, Object> data = new HashMap<String, Object>();
        class FooWrapper extends Foo implements Inspectable {}
        data.put("foo", new FooWrapper());
        String fooA = Template.parse("{{foo.a}}").render(data);

        assertThat(fooA, is("A"));
    }

    @Test
    public void testLiquidSupport() {
        // given
        Target inspect = new Target();
        inspect.setVal("not this");
        Map<String, Object> in = new HashMap<>();
        in.put("a", inspect);

        // when
        String res = Template.parse("{{a.val}}").render(in);

        // then
        assertEquals("OK", res);
    }

    @Test
    public void renderLiquidSupportWithNewRenderingSettings() {
        Target inspect = new Target();
        inspect.setVal("not this");
        Map<String, Object> in = new HashMap<>();
        in.put("a", inspect);

        String fooA = Template.parse("{{a.val}}").withRenderSettings(new RenderSettings.Builder().withEvaluateMode(RenderSettings.EvaluateMode.EAGER).build()).render(in);

        assertThat(fooA, is("OK"));
    }
}
