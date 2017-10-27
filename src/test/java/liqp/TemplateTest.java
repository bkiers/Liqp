package liqp;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TemplateTest {

    static class Foo {

        public String a = "A";
        private String b = "B";
        private String c = "C";

        public String getB() {
            return b;
        }
    }

    @Test
    public void renderObjectTest() throws RecognitionException {

        // `a` is public
        assertThat(Template.parse("{{foo.a}}").render(true, "foo", new Foo()), is("A"));

        // there is a public `getB()` method that exposes `b`
        assertThat(Template.parse("{{foo.b}}").render(true, "foo", new Foo()), is("B"));

        // `c` is not accessible
        assertThat(Template.parse("{{foo.c}}").render(true, "foo", new Foo()), is(""));
    }

    @Test
    public void renderJSONStringTest() throws RecognitionException {

        final String expected = "Hey";

        String rendered = Template.parse("{{mu}}").render("{\"mu\" : \"" + expected + "\"}");
        assertThat(rendered, is(expected));
    }

    @Test(expected = RuntimeException.class)
    public void renderJSONStringTestInvalidJSON() throws RecognitionException {
        Template.parse("mu").render("{\"key : \"value\"}"); // missing quote after `key`
    }

    @Test
    public void renderVarArgsTest() throws RecognitionException {

        final String expected = "Hey";

        String rendered = Template.parse("{{mu}}").render("mu", expected);
        assertThat(rendered, is(expected));

        rendered = Template.parse("{{a}}{{b}}{{c}}").render(
                "a", expected,
                "b", expected,
                "c", null
        );
        assertThat(rendered, is(expected + expected));

        rendered = Template.parse("{{a}}{{b}}{{c}}").render(
                "a", expected,
                "b", expected,
                "c" /* no value */
        );
        assertThat(rendered, is(expected + expected));

        rendered = Template.parse("{{a}}{{b}}{{c}}").render(
                "a", "A",
                "b", "B",
                "c", "C"
        );
        assertThat(rendered, is("ABC"));
    }

    @Test(expected = RuntimeException.class)
    public void renderVarArgsTestInvalidKey2() throws RecognitionException {
        Template.parse("mu").render(null, 456);
    }
}
