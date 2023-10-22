package liqp.tags;

import liqp.Template;
import liqp.TemplateParser;
import liqp.exceptions.LiquidException;
import liqp.parser.Flavor;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

import static liqp.TemplateParser.pwd;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IncludeRelativeTest {
    @Test
    public void testSimpleCase() throws IOException {
        Path root = pwd().resolve("src/test/jekyll/relative_include");
        Path index = root.resolve("index.liquid");

        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.JEKYLL)
                .withShowExceptionsFromInclude(false)
                .build();

        Template template = parser.parse(index);
        String res = template.render();
        assertEquals("Hello World!", res);
    }

    @Test
    public void testLiquid() throws IOException {
        Path root = pwd().resolve("src/test/jekyll/relative_include");
        Path index = root.resolve("index.liquid");

        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.LIQUID)
                .withShowExceptionsFromInclude(false)
                .build();

        try {
            parser.parse(index);
            fail("include_relative is not supported in liquid");
        } catch (LiquidException ignored) {
        }
    }
}