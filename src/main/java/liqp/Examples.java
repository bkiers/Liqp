package liqp;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import liqp.blocks.Block;
import liqp.filters.Filter;
import liqp.nodes.LNode;
import liqp.tags.Tag;

/**
 * A class holding some examples of how to use Liqp.
 */
public class Examples {

    private static void demoGuards() {

        TemplateParser parser = new TemplateParser.Builder() //
                .withMaxSizeRenderedString(300) //
                .withMaxIterations(15) //
                .withMaxRenderTimeMillis(100L) //
                .withMaxTemplateSizeBytes(100) //
                .build();

        String rendered = parser.parse("{% for i in (1..10) %}{{ text }}{% endfor %}").render(
                "{\"text\": \"abcdefghijklmnopqrstuvwxyz\"}");

        System.out.println(rendered);
    }

    private static void demoSimple() {

        Template template = TemplateParser.DEFAULT.parse("hi {{name}}");
        String rendered = template.render("name", "tobi");
        System.out.println(rendered);

        template = TemplateParser.DEFAULT.parse("hi {{name}}");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "tobi");
        rendered = template.render(map);
        System.out.println(rendered);

        template = TemplateParser.DEFAULT.parse("hi {{name}}");
        rendered = template.render("{\"name\" : \"tobi\"}");
        System.out.println(rendered);
    }

    private static void demoCustomStrongFilter() {

        // first register your custom filter
        TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("b") {
            @Override
            public Object apply(TemplateContext context, Object value, Object... params) {
                // create a string from the value
                String text = super.asString(value, context);

                // replace and return *...* with <strong>...</strong>
                return text.replaceAll("\\*(\\w(.*?\\w)?)\\*", "<strong>$1</strong>");
            }
        }).build();

        // use your filter
        Template template = parser.parse("{{ wiki | b }}");
        String rendered = template.render("{\"wiki\" : \"Some *bold* text *in here*.\"}");
        System.out.println(rendered);
    }

    private static void demoCustomRepeatFilter() {
        TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("repeat") {
            @Override
            public Object apply(TemplateContext context, Object value, Object... params) {

                // check if an optional parameter is provided
                int times = params.length == 0 ? 1 : super.asNumber(params[0]).intValue();

                // get the text of the value
                String text = super.asString(value, context);

                StringBuilder builder = new StringBuilder();

                while (times-- > 0) {
                    builder.append(text);
                }

                return builder.toString();
            }
        }).build();

        // use your filter
        Template template = parser.parse("{{ 'a' | repeat }}\n{{ 'b' | repeat:5 }}");
        String rendered = template.render();
        System.out.println(rendered);
    }

    private static void demoCustomSumFilter() {
        TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("sum") {
            @Override
            public Object apply(TemplateContext context, Object value, Object... params) {

                Object[] numbers = super.asArray(value, context);

                double sum = 0;

                for (Object obj : numbers) {
                    sum += super.asNumber(obj).doubleValue();
                }

                return sum;
            }
        }).build();

        Template template = parser.parse("{{ numbers | sum }}");
        String rendered = template.render("{\"numbers\" : [1, 2, 3, 4, 5]}");
        System.out.println(rendered);
    }

    private static void customLoopBlock() {

        TemplateParser parser = new TemplateParser.Builder().withBlock(new Block("loop") {
            @Override
            public Object render(TemplateContext context, LNode... nodes) {

                int n = super.asNumber(nodes[0].render(context)).intValue();
                LNode block = nodes[1];

                StringBuilder builder = new StringBuilder();

                while (n-- > 0) {
                    builder.append(super.asString(block.render(context), context));
                }

                return builder.toString();
            }
        }).build();

        String source = "{% loop 5 %}looping!\n{% endloop %}";

        Template template = parser.parse(source);

        String rendered = template.render();

        System.out.println(rendered);
    }

    public static void instanceFilter() {

        TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("sum") {
            @Override
            public Object apply(TemplateContext context, Object value, Object... params) {

                Object[] numbers = super.asArray(value, context);

                double sum = 0;

                for (Object obj : numbers) {
                    sum += super.asNumber(obj).doubleValue();
                }

                return sum;
            }
        }).build();

        Template template = parser.parse("{{ numbers | sum }}");

        String rendered = template.render("{\"numbers\" : [1, 2, 3, 4, 5]}");
        System.out.println(rendered);
    }

    public static void demoStrictVariables() {
        try {
            TemplateParser parser = new TemplateParser.Builder() //
                    .withStrictVariables(true)
                    .withErrorMode(TemplateParser.ErrorMode.STRICT)
                    .build();

            parser.parse("{{mu}}").render();
        } catch (RuntimeException ex) {
            System.out.println("Caught an exception for strict variables");
        }
    }

    public static void customRandomTag() {
        TemplateParser parser = new TemplateParser.Builder().withTag(new Tag("rand") {
            private final Random rand = new Random();

            @Override
            public Object render(TemplateContext context, LNode... nodes) {
                return rand.nextInt(10) + 1;
            }
        }).build();

        Template template = parser.parse("{% rand %}");
        String rendered = template.render();
        System.out.println(rendered);
    }

    public static void customFilter() {

        TemplateParser parser = new TemplateParser.Builder().withFilter(new Filter("sum") {
            @Override
            public Object apply(TemplateContext context, Object value, Object... params) {

                Object[] numbers = super.asArray(value, context);
                double sum = 0;

                for (Object obj : numbers) {
                    sum += super.asNumber(obj).doubleValue();
                }

                return sum;
            }
        }).build();

        Template template = parser.parse("{{ numbers | sum }}");

        String rendered = template.render("{\"numbers\" : [1, 2, 3, 4, 5]}");
        System.out.println(rendered);
    }

    public static void main(String[] args) throws Exception {

        System.out.println("running liqp.Examples");

        System.out.println("\n=== demoSimple() ===");
        demoSimple();

        System.out.println("\n=== demoCustomStrongFilter() ===");
        demoCustomStrongFilter();

        System.out.println("\n=== demoCustomRepeatFilter() ===");
        demoCustomRepeatFilter();

        System.out.println("\n=== demoCustomSumFilter() ===");
        demoCustomSumFilter();

        System.out.println("\n=== demoGuards() ===");
        demoGuards();

        System.out.println("\n=== customLoopBlock() ===");
        customLoopBlock();

        System.out.println("\n=== instanceFilter() ===");
        instanceFilter();

        System.out.println("\n=== demoStrictVariables() ===");
        demoStrictVariables();

        System.out.println("\n=== customRandomTag() ===");
        customRandomTag();

        System.out.println("\n=== customFilter() ===");
        customFilter();

        System.out.println("Done!");
    }
}
