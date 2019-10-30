package liqp.tags;

import liqp.*;
import liqp.exceptions.VariableNotExistException;
import liqp.filters.Filter;
import liqp.parser.Flavor;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.hamcrest.Matcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.*;

public class IncludeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Filter.class.getDeclaredMethod("resetFilters");
        method.setAccessible(true);
        method.invoke(null);
    }

    @Test
    public void renderTest() throws RecognitionException {

        String source =
                "{% assign shape = 'circle' %}\n" +
                "{% include 'color' %}\n" +
                "{% include 'color' with 'red' %}\n" +
                "{% include 'color' with 'blue' %}\n" +
                "{% assign shape = 'square' %}\n" +
                "{% include 'color' with 'red' %}";

        Template template = Template.parse(source);

        String rendered = template.render();

        assertThat(rendered, is("\n" +
                "color: ''\n" +
                "shape: 'circle'\n" +
                "color: 'red'\n" +
                "shape: 'circle'\n" +
                "color: 'blue'\n" +
                "shape: 'circle'\n" +
                "\n" +
                "color: 'red'\n" +
                "shape: 'square'"));
    }

    @Test
    public void renderTestWithIncludeDirectorySpecifiedInContextLiquidFlavor() throws Exception {
        File jekyll = new File(new File("").getAbsolutePath(), "src/test/jekyll");
        File index = new File(jekyll, "index_with_quotes.html");
        Template template = Template.parse(index);
        String result = template.render();
        assertTrue(result.contains("HEADER"));
    }

    @Test
    public void renderTestWithIncludeDirectorySpecifiedInContextJekyllFlavor() throws Exception {
        File jekyll = new File(new File("").getAbsolutePath(), "src/test/jekyll");
        File index = new File(jekyll, "index_without_quotes.html");
        Template template = Template.parse(index, Flavor.JEKYLL);
        String result = template.render();
        assertTrue(result.contains("HEADER"));
    }

    // https://github.com/bkiers/Liqp/issues/95
    @Test
    public void renderTestWithIncludeSubdirectorySpecifiedInContextJekyllFlavor() throws Exception {
        File jekyll = new File(new File("").getAbsolutePath(), "src/test/jekyll");
        File index = new File(jekyll, "index_without_quotes_subdirectory.html");
        Template template = Template.parse(index, Flavor.JEKYLL);
        String result = template.render();
        assertTrue(result.contains("FOOTER"));
    }

    @Test
    public void renderTestWithIncludeDirectorySpecifiedInJekyllFlavor() throws Exception {
        File index = new File("src/test/jekyll/index_without_quotes.html");
        Template template = Template.parse(index, Flavor.JEKYLL);
        String result = template.render();
        assertTrue(result.contains("HEADER"));
    }

    @Test
    public void renderTestWithIncludeDirectorySpecifiedInLiquidFlavor() throws Exception {
        File index = new File("src/test/jekyll/index_with_quotes.html");
        Template template = Template.parse(index, Flavor.LIQUID);
        String result = template.render();
        assertTrue(result.contains("HEADER"));
    }

    // https://github.com/bkiers/Liqp/issues/95
    @Test
    public void renderTestWithIncludeSubdirectorySpecifiedInJekyllFlavor() throws Exception {
        File index = new File("src/test/jekyll/index_without_quotes_subdirectory.html");
        Template template = Template.parse(index, Flavor.JEKYLL);
        String result = template.render();
        assertTrue(result.contains("FOOTER"));
    }

    @Test
    public void renderTestWithIncludeSubdirectorySpecifiedInLiquidFlavorWithStrictVariables() throws Exception {

        String expected = "Sample Footer";
        File index = new File("src/test/jekyll/index_with_variables.html");
        Template template = Template.parse(
            index,
            new ParseSettings.Builder().withFlavor(Flavor.LIQUID).build(),
            new RenderSettings.Builder().withStrictVariables(true).withShowExceptionsFromInclude(true).build());
        Map<String, Object> variables = new HashMap<>();
        variables.put("FOOTERTEXT", expected);
        String result = template.render(variables);
        assertTrue(result.contains(expected));
    }


    @Test
    public void renderTestWithIncludeSubdirectorySpecifiedInLiquidFlavorWithStrictVariablesException() throws Exception {

        thrown.expectCause(isA(VariableNotExistException.class));

        File index = new File("src/test/jekyll/index_with_variables.html");
        Template template = Template.parse(
            index,
            new ParseSettings.Builder().withFlavor(Flavor.LIQUID).build(),
            new RenderSettings.Builder().withStrictVariables(true).withShowExceptionsFromInclude(true).build());
        template.render();
    }

    // https://github.com/bkiers/Liqp/issues/95
    @Test
    public void renderTestWithIncludeSubdirectorySpecifiedInLiquidFlavor() throws Exception {
        File index = new File("src/test/jekyll/index_with_quotes_subdirectory.html");
        Template template = Template.parse(index, Flavor.LIQUID);
        String result = template.render();
        assertTrue(result.contains("FOOTER"));
    }

    // https://github.com/bkiers/Liqp/issues/75
    @Test
    public void expressionInIncludeTagJekyll() {

        String source = "{% assign variable = 'header.html' %}{% include {{variable}} %}";

        ParseSettings settings = new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
        String rendered = Template.parse(source, settings).render();

        assertTrue(rendered.contains("HEADER"));
    }

    // https://github.com/bkiers/Liqp/issues/75
    @Test(expected = RuntimeException.class)
    public void expressionInIncludeTagLiquidThrowsException() {

        String source = "{% assign variable = 'header.html' %}{% include {{variable}} %}";

        ParseSettings settings = new ParseSettings.Builder().withFlavor(Flavor.LIQUID).build();
        Template.parse(source, settings).render();
    }

    // https://github.com/bkiers/Liqp/issues/75
    @Test(expected = RuntimeException.class)
    public void expressionInIncludeTagDefaultFlavorThrowsException() {

        String source = "{% assign variable = 'header.html' %}{% include {{variable}} %}";

        Template.parse(source).render();
    }

    @Test
    public void includeDirectoryKeyInInputShouldChangeIncludeDirectory() throws IOException {
        // given
        File jekyll = new File(new File("").getAbsolutePath(), "src/test/jekyll");
        File index = new File(jekyll, "index_without_quotes.html");
        Template template = Template.parse(index, new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(Include.INCLUDES_DIRECTORY_KEY, new File(new File("").getAbsolutePath(), "src/test/jekyll/alternative_includes"));

        // when
        String result = template.render(data);

        // then
        assertTrue(result.contains("ALTERNATIVE"));
    }

    @Test
    public void includeDirectoryKeyStringInInputShouldChangeIncludeDirectory() throws IOException {
        //given
        File jekyll = new File(new File("").getAbsolutePath(), "src/test/jekyll");
        File index = new File(jekyll, "index_with_errored_include.html");
        Template template = Template.parse(index, Flavor.JEKYLL);

        // when
        String result = template.render();

        // them
        assertFalse(result.contains("THE_ERROR"));
    }

    @Test(expected = RuntimeException.class)
    public void errorInIncludeCauseMissingIncludeWithCustomRendering() throws IOException {
        //given
        File jekyll = new File(new File("").getAbsolutePath(), "src/test/jekyll");
        File index = new File(jekyll, "index_with_errored_include.html");
        ParseSettings parseSettings = new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
        RenderSettings renderSettings = new RenderSettings.Builder().withShowExceptionsFromInclude(true).build();
        Template template = Template.parse(index, parseSettings).withRenderSettings(renderSettings);


        // when
        template.render();

        // them
        fail();
    }

    @Test
    public void errorInIncludeCauseMissingIncludeWithCustomRenderingAndFixedError() throws IOException {
        //given
        File jekyll = new File(new File("").getAbsolutePath(), "src/test/jekyll");
        File index = new File(jekyll, "index_with_errored_include.html");
        ParseSettings parseSettings = new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
        RenderSettings renderSettings = new RenderSettings.Builder().withShowExceptionsFromInclude(true).build();
        Template template = Template.parse(index, parseSettings).withRenderSettings(renderSettings);

        Filter.registerFilter(new Filter("unknown_and_for_sure_enexist_filter") {
        });

        // when
        String result = template.render();

        // them
        assertTrue(result.contains("THE_ERROR"));
    }

    private static class NestedCauseMatcher<T extends Throwable> extends ThrowableCauseMatcher<T> {

        private Matcher<? extends Throwable> causeMatcher;

        public NestedCauseMatcher(Matcher<T> causeMatcher) {

            super(causeMatcher);
            this.causeMatcher = causeMatcher;
        }

        @Override
        protected boolean matchesSafely(T item) {

            boolean match = false;
            Throwable parent = item;
            Throwable cause = null;

            while (((cause = parent.getCause()) != null) && (cause != parent) && !(match = causeMatcher.matches(cause))) {

                parent = cause;
            }

            return match;
        }

    }
}
