package liqp;

import org.junit.Test;

public class ProtectionSettingsTest {

    @Test
    public void testWithinMaxRenderTimeMillis() {
        TemplateParser.DEFAULT.parse("{% for i in (1..100) %}{{ i }}{% endfor %}")
                .render();
        
        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxRenderTimeMillis(1000L).build()).build();

        parser.parse("{% for i in (1..100) %}{{ i }}{% endfor %}").render();
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxRenderTimeMillis() {
        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxRenderTimeMillis(1).build()).build();

        parser.parse("{% for i in (1..100000) %}{{ i }}{% endfor %}").render();
    }

    @Test
    public void testWithinMaxIterationsRange() {
        TemplateParser.DEFAULT.parse("{% for i in (1..100) %}{{ i }}{% endfor %}")
                .render();
        
        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxIterations(1000).build()).build();

        parser.parse("{% for i in (1..100) %}{{ i }}{% endfor %}")
                .render();
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxIterationsRange() {
        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxIterations(99).build()).build();

        parser.parse("{% for i in (1..100) %}{{ i }}{% endfor %}")
                .render();
    }

    @Test
    public void testWithinMaxIterationsArray() {
        TemplateParser.DEFAULT.parse("{% for i in array %}{{ i }}{% endfor %}")
                .render("{\"array\": [1, 2, 3, 4, 5, 6, 7, 8, 9]}");

        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxIterations(20).build()).build();

        parser.parse("{% for i in array %}{{ i }}{% endfor %}")
                .render("{\"array\": [1, 2, 3, 4, 5, 6, 7, 8, 9]}");
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxIterationsArray() {
        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxIterations(5).build()).build();
        
        parser.parse("{% for i in array %}{{ i }}{% endfor %}")
                .render("{\"array\": [1, 2, 3, 4, 5, 6, 7, 8, 9]}");
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxIterationsArray2D() {
        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxIterations(10).build()).build();

        parser.parse("{% for a in array %}{% for i in a %}{{ i }}{% endfor %}{% endfor %}")
                .render("{\"array\": [[1,2,3,4,5], [11,12,13,14,15], [21,22,23,24,25]]}");
    }

    @Test
    public void testWithinMaxIterationsTablerow() {
        TemplateParser.DEFAULT.parse("{% tablerow n in collections.frontpage cols:3%} {{n}} {% endtablerow %}")
                .render("{ \"collections\" : { \"frontpage\" : [1,2,3,4,5,6] } }");
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxIterationsTablerow() {
        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxIterations(5).build()).build();

        parser.parse("{% tablerow n in collections.frontpage cols:3%} {{n}} {% endtablerow %}")
                .render("{ \"collections\" : { \"frontpage\" : [1,2,3,4,5,6] } }");
    }

    @Test
    public void testWithinMaxTemplateSizeBytes() {
        TemplateParser.DEFAULT.parse("{% tablerow n in collections.frontpage cols:3%} {{n}} {% endtablerow %}")
                .render("{ \"collections\" : { \"frontpage\" : [1,2,3,4,5,6] } }");

        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxTemplateSizeBytes(3000).build()).build();
        parser.parse("{% tablerow n in collections.frontpage cols:3%} {{n}} {% endtablerow %}").render(
                "{ \"collections\" : { \"frontpage\" : [1,2,3,4,5,6] } }");
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxTemplateSizeBytes() {
        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxTemplateSizeBytes(30).build()).build();

        parser.parse("{% tablerow n in collections.frontpage cols:3%} {{n}} {% endtablerow %}")
                .render("{ \"collections\" : { \"frontpage\" : [1,2,3,4,5,6] } }");
    }

    @Test
    public void testWithinMaxSizeRenderedString() {
        TemplateParser.DEFAULT.parse("{% for i in (1..100) %}{{ abc }}{% endfor %}")
                .render("{\"abc\": \"abcdefghijklmnopqrstuvwxyz\"}");

        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxSizeRenderedString(2700).build()).build();

        parser.parse("{% for i in (1..100) %}{{ abc }}{% endfor %}").render(
                "{\"abc\": \"abcdefghijklmnopqrstuvwxyz\"}");
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxSizeRenderedString() {
        TemplateParser parser = new TemplateParser.Builder().withProtectionSettings(
                new ProtectionSettings.Builder().withMaxSizeRenderedString(2500).build()).build();
        
        parser.parse("{% for i in (1..1000) %}{{ abc }}{% endfor %}")
                .render("{\"abc\": \"abcdefghijklmnopqrstuvwxyz\"}");
    }
}
