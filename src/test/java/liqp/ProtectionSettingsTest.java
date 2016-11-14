package liqp;

import org.junit.Test;

public class ProtectionSettingsTest {

    @Test
    public void testWithinMaxRenderTimeMillis() {
        Template.parse("{% for i in (1..100) %}{{ i }}{% endfor %}")
                .render();

        Template.parse("{% for i in (1..100) %}{{ i }}{% endfor %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxRenderTimeMillis(1000L).build())
                .render();
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxRenderTimeMillis() {
        Template.parse("{% for i in (1..10000) %}{{ i }}{% endfor %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxRenderTimeMillis(1).build())
                .render();
    }

    @Test
    public void testWithinMaxIterationsRange() {
        Template.parse("{% for i in (1..100) %}{{ i }}{% endfor %}")
                .render();

        Template.parse("{% for i in (1..100) %}{{ i }}{% endfor %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxIterations(1000).build())
                .render();
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxIterationsRange() {
        Template.parse("{% for i in (1..100) %}{{ i }}{% endfor %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxIterations(99).build())
                .render();
    }

    @Test
    public void testWithinMaxIterationsArray() {
        Template.parse("{% for i in array %}{{ i }}{% endfor %}")
                .render("{\"array\": [1, 2, 3, 4, 5, 6, 7, 8, 9]}");

        Template.parse("{% for i in array %}{{ i }}{% endfor %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxIterations(20).build())
                .render("{\"array\": [1, 2, 3, 4, 5, 6, 7, 8, 9]}");
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxIterationsArray() {
        Template.parse("{% for i in array %}{{ i }}{% endfor %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxIterations(5).build())
                .render("{\"array\": [1, 2, 3, 4, 5, 6, 7, 8, 9]}");
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxIterationsArray2D() {
        Template.parse("{% for a in array %}{% for i in a %}{{ i }}{% endfor %}{% endfor %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxIterations(10).build())
                .render("{\"array\": [[1,2,3,4,5], [11,12,13,14,15], [21,22,23,24,25]]}");
    }

    @Test
    public void testWithinMaxIterationsTablerow() {
        Template.parse("{% tablerow n in collections.frontpage cols:3%} {{n}} {% endtablerow %}")
                .render("{ \"collections\" : { \"frontpage\" : [1,2,3,4,5,6] } }");
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxIterationsTablerow() {
        Template.parse("{% tablerow n in collections.frontpage cols:3%} {{n}} {% endtablerow %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxIterations(5).build())
                .render("{ \"collections\" : { \"frontpage\" : [1,2,3,4,5,6] } }");
    }

    @Test
    public void testWithinMaxTemplateSizeBytes() {
        Template.parse("{% tablerow n in collections.frontpage cols:3%} {{n}} {% endtablerow %}")
                .render("{ \"collections\" : { \"frontpage\" : [1,2,3,4,5,6] } }");

        Template.parse("{% tablerow n in collections.frontpage cols:3%} {{n}} {% endtablerow %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxTemplateSizeBytes(3000).build())
                .render("{ \"collections\" : { \"frontpage\" : [1,2,3,4,5,6] } }");
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxTemplateSizeBytes() {
        Template.parse("{% tablerow n in collections.frontpage cols:3%} {{n}} {% endtablerow %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxTemplateSizeBytes(30).build())
                .render("{ \"collections\" : { \"frontpage\" : [1,2,3,4,5,6] } }");
    }

    @Test
    public void testWithinMaxSizeRenderedString() {
        Template.parse("{% for i in (1..100) %}{{ abc }}{% endfor %}")
                .render("{\"abc\": \"abcdefghijklmnopqrstuvwxyz\"}");

        Template.parse("{% for i in (1..100) %}{{ abc }}{% endfor %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxSizeRenderedString(2700).build())
                .render("{\"abc\": \"abcdefghijklmnopqrstuvwxyz\"}");
    }

    @Test(expected = RuntimeException.class)
    public void testExceedMaxSizeRenderedString() {
        Template.parse("{% for i in (1..1000) %}{{ abc }}{% endfor %}")
                .withProtectionSettings(new ProtectionSettings.Builder().withMaxSizeRenderedString(2500).build())
                .render("{\"abc\": \"abcdefghijklmnopqrstuvwxyz\"}");
    }
}
