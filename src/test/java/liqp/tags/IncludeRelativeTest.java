package liqp.tags;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.blocks.Block;
import liqp.exceptions.LiquidException;
import liqp.nodes.LNode;
import liqp.parser.Flavor;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IncludeRelativeTest {
    @Test
    public void testSimpleCase() throws IOException {
        Path root = Paths.get(".").toAbsolutePath().resolve("src/test/jekyll/relative_include");
        Path index = root.resolve("hello.liquid");

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
        Path root = Paths.get(".").toAbsolutePath().resolve("src/test/jekyll/relative_include");
        Path index = root.resolve("hello.liquid");

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

    @Test
    public void testLiquidWithCustomIncludeShouldAllowOverride() throws IOException {
        Path root = Paths.get(".").toAbsolutePath().resolve("src/test/jekyll/relative_include");
        Path index = root.resolve("hello.liquid");

        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.LIQUID)
                .withTag(new Tag("include_relative") {
                    @Override
                    public Object render(TemplateContext context, LNode... nodes) {
                        return "World";
                    }
                })
                .withShowExceptionsFromInclude(false)
                .build();

        Template template = parser.parse(index);
        String res = template.render();
        assertEquals("Hello World!", res);

    }

    // Scenario: Include a nested file relative to a post
    //    Given I have a _posts directory
    //    And I have a _posts/snippets directory
    //    And I have a _posts/snippets/welcome_para directory
    //    And I have the following post:
    //      | title     | date       | content                                         |
    //      | Star Wars | 2018-09-02 | {% include_relative snippets/welcome_para.md %} |
    //    And I have an "_posts/snippets/welcome_para.md" file that contains "{% include_relative snippets/welcome_para/greeting.md %} Dear Reader!"
    //    And I have an "_posts/snippets/welcome_para/greeting.md" file that contains "Welcome back"
    //    When I run jekyll build
    //    Then I should get a zero exit status
    //    And the _site directory should exist
    //    And I should see "Welcome back Dear Reader!" in "_site/2018/09/02/star-wars.html"
    @Test
    public void testIncludeANestedFileRelativeToAPost() throws IOException {
        Path root = Paths.get(".").toAbsolutePath().resolve("src/test/jekyll/relative_include");
        Path index = root.resolve("nested_include.liquid");

        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.JEKYLL)
                .withShowExceptionsFromInclude(false)
                .build();

        Template template = parser.parse(index);
        String res = template.render();
        assertEquals("Hello Nested and even more nested!!!", res);
    }

    @Test
    public void testCustomBlocksStackWithCustomBlockIncludeRelative() {
        TemplateParser parser = new TemplateParser.Builder()
                .withFlavor(Flavor.LIQUID)
                .withBlock(new Block("another") {
                    @Override
                    public Object render(TemplateContext context, LNode... nodes) {
                        LNode blockNode = nodes[nodes.length - 1];
                        return "[" + super.asString(blockNode.render(context), context) + "]";
                    }
                })
                .withBlock(new Block("include_relative") {
                    @Override
                    public Object render(TemplateContext context, LNode... nodes) {
                        return "World";
                    }
                })
                .withShowExceptionsFromInclude(false)
                .build();

        Template template = parser.parse("{% another %}{% include_relative snippets/welcome_para.md %}{% endinclude_relative %}{% endanother %}");

        String res = template.render();
        assertEquals("[World]", res);

    }
}