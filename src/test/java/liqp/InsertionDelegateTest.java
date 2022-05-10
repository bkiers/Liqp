package liqp;

import liqp.nodes.LNode;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class InsertionDelegateTest {
    @Test
    public void testTagCreation() {
        // given
        Insertion tag = InsertionDelegate.createTag(new InsertionDelegate() {
            @Override
            public String getName() {
                return "current_date";
            }

            @Override
            public Object render(TemplateContext context, LNode... nodes) {
                return ZonedDateTime.of(2000, 1, 1, 15, 0, 0, 0, ZoneOffset.UTC);
            }
        });

        // when
        String res = Template.parse("today is: {% current_date %}", new ParseSettings.Builder()
                .with(tag)
                .build()).render();

        // then
        assertEquals("today is: 2000-01-01 15:00:00 Z", res);
    }
}