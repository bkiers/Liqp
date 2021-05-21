package liqp.tags;

import liqp.*;
import liqp.exceptions.LiquidException;
import liqp.exceptions.VariableNotExistException;
import liqp.filters.Filter;
import liqp.parser.Flavor;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
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
    public void testIncludeVariableSyntaxTag() {
        Template template = Template.parse("{% include {{ tmpl }} %}", jekyll());
        String res = template.render("{ \"var\" : \"TEST\", \"tmpl\" : \"include_read_var\"}");
        assertEquals("TEST", res);
    }

    @Test(expected = LiquidException.class)
    public void renderWithShouldThrowExceptionInJekyll() throws RecognitionException {

        Template template = Template.parse("{% include 'color' with 'red' %}",
                new ParseSettings.Builder()
                        .withFlavor(Flavor.JEKYLL)
                        .build(),
                new RenderSettings
                        .Builder()
                        .withRaiseExceptionsInStrictMode(true)
                        .withShowExceptionsFromInclude(true)
                        .build()
        );

        template.render();

        fail();
    }

    @Test
    public void renderWithShouldWorkInLiquid() throws RecognitionException {

        Template template = Template.parse("{% include 'color' with 'red' %}",
                new ParseSettings.Builder()
                        .withFlavor(Flavor.LIQUID)
                        .build(),
                new RenderSettings
                        .Builder()
                        .withRaiseExceptionsInStrictMode(true)
                        .withShowExceptionsFromInclude(true)
                        .build()
        );

        String render = template.render();

        assertEquals("color: 'red'\nshape: ''", render);
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
        // given
        File jekyll = new File(new File("").getAbsolutePath(), "src/test/jekyll");
        File index = new File(jekyll, "index_without_quotes.html");
        Template template = Template.parse(index, new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build());
        Map<String, Object> data = new HashMap<String, Object>();
        String alternativePath = new File(new File("").getAbsolutePath(), "src/test/jekyll/alternative_includes").getAbsolutePath();
        data.put(Include.INCLUDES_DIRECTORY_KEY, alternativePath);

        // when
        String result = template.render(data);

        // then
        assertTrue(result.contains("ALTERNATIVE"));
    }
    @Test
    public void includeDirectoryKeyPathInInputShouldChangeIncludeDirectory() throws IOException {
        // given
        File jekyll = new File(new File("").getAbsolutePath(), "src/test/jekyll");
        File index = new File(jekyll, "index_without_quotes.html");
        Template template = Template.parse(index, new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build());
        Map<String, Object> data = new HashMap<String, Object>();
        String alternativePath = new File(new File("").getAbsolutePath(), "src/test/jekyll/alternative_includes").getAbsolutePath();
        data.put(Include.INCLUDES_DIRECTORY_KEY, Paths.get(alternativePath));

        // when
        String result = template.render(data);

        // then
        assertTrue(result.contains("ALTERNATIVE"));
    }

    @Test
    public void errorInIncludeCauseIgnoreErrorWhenNoExceptionsFromInclude() throws IOException {
        //given
        File jekyll = new File(new File("").getAbsolutePath(), "src/test/jekyll");
        File index = new File(jekyll, "index_with_errored_include.html");
        ParseSettings parseSettings = new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
        RenderSettings renderSettings = new RenderSettings.Builder().withShowExceptionsFromInclude(false).build();
        Template template = Template.parse(index, parseSettings).withRenderSettings(renderSettings);


        // when
        String result = template.render();

        // then
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
        Template template = Template.parse(index, parseSettings);

        Filter.registerFilter(new Filter("unknown_and_for_sure_enexist_filter") {
        });

        // when
        String result = template.render();

        // then
        assertTrue(result.contains("THE_ERROR"));
    }

    @Test
    public void testIncludeMustSeeVariablesFromOuterScopeInLiquid() throws IOException {
        // liquid

        String templateText = "{% assign var = 'variable' %}{% include 'include_read_var' %}";

        Template template = Template.parse(templateText, liquid())
                .withRenderSettings(new RenderSettings.Builder().withShowExceptionsFromInclude(true).build());

        assertEquals("variable", template.render());

    }


    @Test
    public void testIncludeMustCreateVariablesInOuterScopeInLiquid() throws IOException {
        // liquid
        String templateText = "{% include 'include_create_new_var' %}{{ incl_var }}";

        Template template = Template.parse(templateText, liquid())
                .withRenderSettings(new RenderSettings.Builder().withShowExceptionsFromInclude(true).build());

        assertEquals("incl_var", template.render());
    }

    @Test
    public void testIncludeWithDecrementShouldNotInterfereOuterVar() throws IOException {
        // liquid

        String templateText = "{% assign var = 4 %}{% include 'include_decrement_var_not_interfere' %} ! {{ var }}";

        Template template = Template.parse(templateText, liquid())
                .withRenderSettings(new RenderSettings.Builder().withShowExceptionsFromInclude(true).build());

        assertEquals("-1 ! 4", template.render());
    }


    @Test
    public void testIncludeMustSeeVariablesFromOuterScopeInJekyll() throws IOException {
        // jekyll

        Template template = Template.parse("" +
                "{% assign var = 'variable' %}" +
                "{% include include_read_var.liquid %}", jekyll())
                .withRenderSettings(new RenderSettings.Builder().withShowExceptionsFromInclude(true).build());

        assertEquals("variable", template.render());

    }


    // https://github.com/bkiers/Liqp/issues/179
    @Test
    public void testIncludesMissingValues() throws IOException {
        // given
        // when
        String rendered = Template.parse( ""
                + "{% assign list = \"1,2\" | split: \",\" %}"
                + "{% for n in list %}"
                +     "{% assign inner = n %}"
                +     "{% include include_iterations_variables.liquid %}"
                + "{% endfor %}", jekyll())
                .withRenderSettings(new RenderSettings.Builder().withShowExceptionsFromInclude(true).build())
                .render();


        // then
        assertEquals(""
                + "list: 12\n"
                + "inner: 1\n"
                + "n: 1\n"
                + "list: 12\n"
                + "inner: 2\n"
                + "n: 2\n", rendered);
    }

    @Test
    public void testRewriteValuesFromInclude() {
        // given
        // when
        String rendered = Template.parse("{% assign val = 'OUTER'%}{% include 'include_var' %}{{val}}").render();

        // then
        assertEquals("INNER", rendered);
    }

    @Test
    public void testDecrementIncrementMustContinueThoughInclude() {
        String rendered = Template.parse(""
                + "[{% decrement var1 %},{% increment var2 %}]"
                + "[{% include 'include_decrement_var' %}]"
                + "[{% decrement var1 %},{% increment var2 %}]"
                + "[{{ var1 }}, {{ var2 }}]"
                + "").withRenderSettings(new RenderSettings.Builder().withShowExceptionsFromInclude(true).build())
                .render();

        assertEquals("[-1,0][-2,1][-3,2][-3, 3]", rendered);
    }

    @Test
    public void testCycleMustContinueThoughInclude() throws IOException {
        String rendered = Template.parse(""
                + "{% cycle 1,2,3,4 %}"
                + "{% assign list = \"1\" | split: \",\" %}{% for n in list %}{% cycle 1,2,3,4 %}{% endfor %}"
                + "{% cycle 1,2,3,4 %}"
                + "{% include 'include_cycle' %}"
                + "")
                .withRenderSettings(new RenderSettings.Builder().withShowExceptionsFromInclude(true).build())
                .render();
        assertEquals("1234", rendered);
    }

    @Test
    public void testIfchangedThoughInclude() throws IOException {
        String rendered = Template.parse(""
                + "{% ifchanged %}1{% endifchanged %}"
                + "{% ifchanged %}2{% endifchanged %}"
                + "{% include 'include_ifchanged' %}"
                + "{% ifchanged %}3{% endifchanged %}"
                + "").render();
        assertEquals("12><3", rendered);
    }

    @Test
    public void testOwnScopeInInclude() throws IOException {

        // when
        String rendered = Template.parse("{% for item in (1..2) %}{% include 'include_iteration' %}{% endfor %}{{ item }}").render();

        // then
        assertEquals("1212", rendered);
    }

    public ParseSettings jekyll() {
        return new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
    }

    public ParseSettings liquid() {
        return new ParseSettings.Builder().withFlavor(Flavor.LIQUID).build();
    }

}
