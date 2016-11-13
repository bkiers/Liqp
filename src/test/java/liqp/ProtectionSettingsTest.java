package liqp;

import org.junit.Test;

public class ProtectionSettingsTest {

    @Test
    public void testWithinMaxRenderTimeMillis() {
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
}
