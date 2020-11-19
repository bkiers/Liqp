package liqp.tags;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import liqp.ParseSettings;
import liqp.RenderSettings;
import liqp.Template;
import liqp.temprunner.InTmpFolder;
import liqp.temprunner.IncludeContext;
import liqp.temprunner.TmpFolderRule;
import liqp.exceptions.VariableNotExistException;
import liqp.filters.Filter;
import liqp.parser.Flavor;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class IncludeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TmpFolderRule tmpFolderRule = new TmpFolderRule();

    @Before
    public void init() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        Method method = Filter.class.getDeclaredMethod("resetFilters");
        method.setAccessible(true);
        method.invoke(null);

    }

    @Test
    @InTmpFolder
    public void renderTest() throws RecognitionException, IOException {
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/color.liquid", "color: '{{ color }}'\n"
                + "shape: '{{ shape }}'");


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
    @InTmpFolder
    public void renderTestWithIncludeDirectorySpecifiedInContextLiquidFlavor() throws Exception {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/header.html", "HEADER");
        File index = context.writeFile("index_with_quotes.html",
                "{% include 'header.html' %}");

        // when
        Template template = Template.parse(index);
        String result = template.render();

        // then
        assertTrue(result.contains("HEADER"));
    }

    @Test
    @InTmpFolder
    public void renderTestWithIncludeDirectorySpecifiedInContextJekyllFlavor() throws Exception {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/header.html", "HEADER");
        File index = context.writeFile("index_without_quotes.html", "{% include header.html %}");

        // when
        Template template = Template.parse(index, Flavor.JEKYLL);
        String result = template.render();

        // then
        assertTrue(result.contains("HEADER"));
    }

    // https://github.com/bkiers/Liqp/issues/95
    @Test
    @InTmpFolder
    public void renderTestWithIncludeSubdirectorySpecifiedInContextJekyllFlavor() throws Exception {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/wmt/footer.html", "FOOTER");
        File index = context.writeFile("index_without_quotes_subdirectory.html",
                "{% include wmt/footer.html %}");

        // when
        Template template = Template.parse(index, Flavor.JEKYLL);
        String result = template.render();

        // then
        assertTrue(result.contains("FOOTER"));
    }

    @Test
    @InTmpFolder
    public void renderTestWithIncludeDirectorySpecifiedInJekyllFlavor() throws Exception {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/header.html", "HEADER");
        File index = context.writeFile("index_without_quotes.html",
                "{% include header.html %}");

        // when
        Template template = Template.parse(index, Flavor.JEKYLL);
        String result = template.render();

        // then
        assertTrue(result.contains("HEADER"));
    }

    @Test
    @InTmpFolder
    public void renderTestWithIncludeDirectorySpecifiedInLiquidFlavor() throws Exception {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/header.html", "HEADER");
        File index = context.writeFile("index_with_quotes.html",
                "{% include 'header.html' %}");

        // when
        Template template = Template.parse(index, Flavor.LIQUID);
        String result = template.render();

        // then
        assertTrue(result.contains("HEADER"));
    }

    // https://github.com/bkiers/Liqp/issues/95
    @Test
    @InTmpFolder
    public void renderTestWithIncludeSubdirectorySpecifiedInJekyllFlavor() throws Exception {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/wmt/footer.html", "FOOTER");
        File index = context.writeFile("index_without_quotes_subdirectory.html",
                "{% include wmt/footer.html %}");

        // when
        Template template = Template.parse(index, Flavor.JEKYLL);
        String result = template.render();

        // then
        assertTrue(result.contains("FOOTER"));
    }

    @Test
    @InTmpFolder
    public void renderTestWithIncludeSubdirectorySpecifiedInLiquidFlavorWithStrictVariables() throws Exception {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/wmt/footer-with-variables.html", "{{ FOOTERTEXT }}");
        File index = context.writeFile("index_with_variables.html", "{% include 'wmt/footer-with-variables.html' %}");
        String expected = "Sample Footer";

        // when
        Template template = Template.parse(
            index,
            new ParseSettings.Builder().withFlavor(Flavor.LIQUID).build(),
            new RenderSettings.Builder().withStrictVariables(true).withShowExceptionsFromInclude(true).build());
        Map<String, Object> variables = new HashMap<>();
        variables.put("FOOTERTEXT", expected);
        String result = template.render(variables);

        // then
        assertTrue(result.contains(expected));
    }


    @Test
    @InTmpFolder
    public void renderTestWithIncludeSubdirectorySpecifiedInLiquidFlavorWithStrictVariablesException() throws Exception {
        // given
        thrown.expectCause(isA(VariableNotExistException.class));
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/wmt/footer-with-variables.html", "{{ FOOTERTEXT }}");
        File index = context.writeFile("index_with_variables.html",
                "{% include 'wmt/footer-with-variables.html' %}");

        // when
        Template template = Template.parse(
            index,
            new ParseSettings.Builder().withFlavor(Flavor.LIQUID).build(),
            new RenderSettings.Builder().withStrictVariables(true).withShowExceptionsFromInclude(true).build());
        template.render();

        // then
        fail();
    }

    // https://github.com/bkiers/Liqp/issues/95
    @Test
    @InTmpFolder
    public void renderTestWithIncludeSubdirectorySpecifiedInLiquidFlavor() throws Exception {
        // given

        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/wmt/footer.html", "FOOTER");
        File index = context.writeFile("index_with_quotes_subdirectory.html",
                "{% include 'wmt/footer.html' %}");

        // when
        Template template = Template.parse(index, Flavor.LIQUID);
        String result = template.render();

        // then
        assertTrue(result.contains("FOOTER"));
    }

    // https://github.com/bkiers/Liqp/issues/75
    @Test
    @InTmpFolder
    public void expressionInIncludeTagJekyll() throws IOException {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/header.html", "HEADER");
        String source = "{% assign variable = 'header.html' %}{% include {{variable}} %}";

        // when
        ParseSettings settings = new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
        String rendered = Template.parse(source, settings).render();

        // then
        assertTrue(rendered.contains("HEADER"));
    }

    // https://github.com/bkiers/Liqp/issues/75
    @Test(expected = RuntimeException.class)
    @InTmpFolder
    public void expressionInIncludeTagLiquidThrowsException() throws IOException {
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/header.html", "HEADER");
        String source = "{% assign variable = 'header.html' %}{% include {{variable}} %}";

        ParseSettings settings = new ParseSettings.Builder().withFlavor(Flavor.LIQUID).build();
        Template.parse(source, settings).render();
    }

    // https://github.com/bkiers/Liqp/issues/75
    @Test(expected = RuntimeException.class)
    @InTmpFolder
    public void expressionInIncludeTagDefaultFlavorThrowsException() throws IOException {
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/header.html", "HEADER");
        String source = "{% assign variable = 'header.html' %}{% include {{variable}} %}";

        Template.parse(source).render();
    }

    @Test
    @InTmpFolder
    public void includeDirectoryKeyInInputShouldChangeIncludeDirectory() throws IOException {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        File index = context.writeFile("index_without_quotes.html", "{% include header.html %}");
        File alternativeFile = context.writeFile("alternative_includes/header.html", "ALTERNATIVE");
        String alternativePath = alternativeFile.getParent();

        Template template = Template.parse(index, new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(Include.INCLUDES_DIRECTORY_KEY, new File(alternativePath));

        // when
        String result = template.render(data);

        // then
        assertTrue(result.contains("ALTERNATIVE"));
    }

    @Test
    @InTmpFolder
    public void includeDirectoryKeyStringInInputShouldChangeIncludeDirectory() throws IOException {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        File index = context.writeFile("index_without_quotes.html", "{% include header.html %}");
        File alternativeFile = context.writeFile("alternative_includes/header.html", "ALTERNATIVE");
        String alternativePath = alternativeFile.getParent();

        Template template = Template.parse(index, new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(Include.INCLUDES_DIRECTORY_KEY, alternativePath);

        // when
        String result = template.render(data);

        // then
        assertTrue(result.contains("ALTERNATIVE"));
    }
    @Test
    @InTmpFolder
    public void includeDirectoryKeyPathInInputShouldChangeIncludeDirectory() throws IOException {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        File index = context.writeFile("index_without_quotes.html", "{% include header.html %}");
        File alternativeFile = context.writeFile("alternative_includes/header.html", "ALTERNATIVE");
        String alternativePath = alternativeFile.getParent();

        Template template = Template.parse(index, new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(Include.INCLUDES_DIRECTORY_KEY, Paths.get(alternativePath));

        // when
        String result = template.render(data);

        // then
        assertTrue(result.contains("ALTERNATIVE"));
    }

    @Test
    @InTmpFolder
    public void errorInIncludeCauseIgnoreErrorWhenNoExceptionsFromInclude() throws IOException {
        //given
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/broken.html", "THE_ERROR\n"
                + "this jekyll filter is missing in this library, so this cause error:\n"
                + "{{ \"a     a\" | unknown_and_for_sure_enexist_filter }}\n");

        File index = context.writeFile("index_with_errored_include.html",
                "{% include broken.html %}");


        // when
        Template template = Template.parse(index, Flavor.JEKYLL);
        String result = template.render();

        // them
        assertFalse(result.contains("THE_ERROR"));
    }

    @Test(expected = RuntimeException.class)
    @InTmpFolder
    public void errorInIncludeCauseMissingIncludeWithCustomRendering() throws IOException {
        //given
        IncludeContext context = tmpFolderRule.getContext();

        context.writeFile("_includes/broken.html", "THE_ERROR\n"
                + "this jekyll filter is missing in this library, so this cause error:\n"
                + "{{ \"a     a\" | unknown_and_for_sure_enexist_filter }}\n");
        File index = context.writeFile("index_with_errored_include.html",
                "{% include broken.html %}");

        ParseSettings parseSettings = new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
        RenderSettings renderSettings = new RenderSettings.Builder().withShowExceptionsFromInclude(true).build();

        // when
        Template template = Template.parse(index, parseSettings).withRenderSettings(renderSettings);
        template.render();

        // then
        fail();
    }

    @Test
    @InTmpFolder
    public void errorInIncludeCauseMissingIncludeWithCustomRenderingAndFixedError() throws IOException {
        //given
        IncludeContext context = tmpFolderRule.getContext();

        context.writeFile("_includes/broken.html", "THE_ERROR\n"
                + "this jekyll filter is missing in this library, so this cause error:\n"
                + "{{ \"a     a\" | unknown_and_for_sure_enexist_filter }}\n");
        File index = context.writeFile("index_with_errored_include.html",
                "{% include broken.html %}");

        Template template = Template.parse(index, jekyll()).withRenderSettings(RenderSettings.EXCEPTIONS_FROM_INCLUDE);

        Filter.registerFilter(new Filter("unknown_and_for_sure_enexist_filter") {
        });

        // when
        String result = template.render();

        // them
        assertTrue(result.contains("THE_ERROR"));
    }

    @InTmpFolder
    @Test
    public void testIncludeMustSeeVariablesFromOuterScopeInLiquid() throws IOException {
        IncludeContext context = tmpFolderRule.getContext();

        // liquid
        context.writeFile("snippets/include_read_var.liquid", "{{ var }}");

        String templateText = "{% assign var = 'variable' %}{% include 'include_read_var' %}";

        Template template = Template.parse(templateText, liquid())
                .withRenderSettings(RenderSettings.EXCEPTIONS_FROM_INCLUDE);

        assertEquals("variable", template.render());

    }


    @Test
    @InTmpFolder
    public void testIncludeMustCreateVariablesInOuterScopeInLiquid() throws IOException {
        // liquid
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/include_create_new_var.liquid", "{% assign incl_var = 'incl_var' %}");

        String templateText = "{% include 'include_create_new_var' %}{{ incl_var }}";

        Template template = Template.parse(templateText, liquid())
                .withRenderSettings(RenderSettings.EXCEPTIONS_FROM_INCLUDE);

        assertEquals("incl_var", template.render());
    }

    @Test
    @InTmpFolder
    public void testIncludeWithDecrementShouldNotInterfereOuterVar() throws IOException {
        // liquid
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/include_decrement_var.liquid", "{% decrement var %}");

        String templateText = "{% assign var = 4 %}{% include 'include_decrement_var' %} ! {{ var }}";

        Template template = Template.parse(templateText, liquid())
                .withRenderSettings(RenderSettings.EXCEPTIONS_FROM_INCLUDE);

        assertEquals("-1 ! 4", template.render());
    }


    @Test
    @InTmpFolder
    public void testIncludeMustSeeVariablesFromOuterScopeInJekyll() throws IOException {
        // jekyll
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/include_read_var.liquid", "{{ var }}");

        String templateText = "{% assign var = 'variable' %}{% include include_read_var.liquid %}";

        Template template = Template.parse(templateText, jekyll())
                .withRenderSettings(RenderSettings.EXCEPTIONS_FROM_INCLUDE);

        assertEquals("variable", template.render());

    }

    @Test
    @InTmpFolder
    public void testDecrementIncrementMustContinueThoughInclude() throws IOException {
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/include_decrement_var.liquid", "{% decrement var1 %},{% increment var2 %}");

        String templateText = "[{% decrement var1 %},{% increment var2 %}][{% include include_decrement_var.liquid %}][{% decrement var1 %},{% increment var2 %}]";

        Template template = Template.parse(templateText, jekyll())
                .withRenderSettings(RenderSettings.EXCEPTIONS_FROM_INCLUDE);

        assertEquals("[-1,0][-2,1][-3,2]", template.render());
    }

    @Test
    @InTmpFolder
    public void testCycleMustContinueThoughInclude() throws IOException {
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("_includes/cycle_case.liquid", "{% cycle 1,2,3 %}");

        String templateText = "{% cycle 1,2,3 %}{% include cycle_case.liquid %}{% cycle 1,2,3 %}";

        Template template = Template.parse(templateText, jekyll())
                .withRenderSettings(RenderSettings.EXCEPTIONS_FROM_INCLUDE);

        assertEquals("123", template.render());
    }

    // https://github.com/bkiers/Liqp/issues/179
    @Test
    @InTmpFolder
    public void testIncludesMissingValues() throws IOException {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        File index = context.writeFile("index.html", ""
                + "{% assign list = \"1,2\" | split: \",\" %}"
                + "{% for n in list %}"
                +     "{% assign inner = n %}"
                +     "{% include include.liquid %}"
                + "{% endfor %}");

        context.writeFile("_includes/include.liquid", ""
                + "list: {{ list }}\n"
                + "inner: {{ inner }}\n"
                + "n: {{ n }}\n");

        // when
        String rendered = Template.parse(index, Flavor.JEKYLL).render();


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
    @InTmpFolder
    public void testRewriteValuesFromInclude() throws IOException {
        // given
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/include.liquid", "{% assign val = 'INNER'%}");

        // when
        String rendered = Template.parse("{% assign val = 'OUTER'%}{% include 'include' %}{{val}}").render();

        // then
        assertEquals("INNER", rendered);
    }

    @Test
    @InTmpFolder
    public void testDecrementThroughInclude() throws IOException {
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/include_decrement_var.liquid", "{% decrement var1 %},{% increment var2 %}");

        String rendered = Template.parse(""
                + "[{% decrement var1 %},{% increment var2 %}]"
                + "[{% include 'include_decrement_var' %}]"
                + "[{% decrement var1 %},{% increment var2 %}]"
                + "[{{ var1 }}, {{ var2 }}]"
                + "").render();

        assertEquals("[-1,0][-2,1][-3,2][-3, 3]", rendered);
    }

    @Test
    @InTmpFolder
    public void testCycleThoughInclude() throws IOException {
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/include_cycle.liquid", "{% cycle 1,2,3,4 %}");

        String rendered = Template.parse(""
                + "{% cycle 1,2,3,4 %}"
                + "{% assign list = \"1\" | split: \",\" %}{% for n in list %}{% cycle 1,2,3,4 %}{% endfor %}"
                + "{% cycle 1,2,3,4 %}"
                + "{% include 'include_cycle' %}"
                + "").render();
        assertEquals("1234", rendered);
    }

    @Test
    @InTmpFolder
    public void testIfchangedThoughInclude() throws IOException {
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/include_ifchanged.liquid", ">{% ifchanged %}2{% endifchanged %}<");

        String rendered = Template.parse(""
                + "{% ifchanged %}1{% endifchanged %}"
                + "{% ifchanged %}2{% endifchanged %}"
                + "{% include 'include_ifchanged' %}"
                + "{% ifchanged %}3{% endifchanged %}"
                + "").render();
        assertEquals("12><3", rendered);
    }

    @Test
    @InTmpFolder
    public void testOwnScopeInInclude() throws IOException {
        IncludeContext context = tmpFolderRule.getContext();
        context.writeFile("snippets/include.liquid",
                "{% for item in (1..2) %}{{ item }}{% endfor %}");

        // when
        String rendered = Template.parse("{% for item in (1..2) %}{% include 'include' %}{% endfor %}{{ item }}").render();

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
