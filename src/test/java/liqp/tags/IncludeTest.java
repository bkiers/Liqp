package liqp.tags;

import liqp.Template;
import liqp.parser.Flavor;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class IncludeTest {

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
}
