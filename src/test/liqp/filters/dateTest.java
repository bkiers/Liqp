package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class dateTest {

    @Test
    public void applyTest() throws RecognitionException {

        final int seconds = 946702800;
        final Date date = new Date(seconds * 1000L);

        String[][] tests = {
                { "{{" + seconds + " | date: 'mu'}}", "mu" },
                { "{{" + seconds + " | date: '%'}}", "%" },
                { "{{" + seconds + " | date: '%? %%'}}", "%? %" },
                { "{{" + seconds + " | date: '%a'}}", new SimpleDateFormat("EEE").format(date) },
                { "{{" + seconds + " | date: '%A'}}", new SimpleDateFormat("EEEE").format(date) },
                { "{{" + seconds + " | date: '%b'}}", new SimpleDateFormat("MMM").format(date) },
                { "{{" + seconds + " | date: '%B'}}", new SimpleDateFormat("MMMM").format(date) },
                { "{{" + seconds + " | date: '%c'}}", new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy").format(date) },
                { "{{" + seconds + " | date: '%d'}}", new SimpleDateFormat("dd").format(date) },
                { "{{" + seconds + " | date: '%H'}}", new SimpleDateFormat("HH").format(date) },
                { "{{" + seconds + " | date: '%I'}}", new SimpleDateFormat("hh").format(date) },
                { "{{" + seconds + " | date: '%j'}}", new SimpleDateFormat("DDD").format(date) },
                { "{{" + seconds + " | date: '%m'}}", new SimpleDateFormat("MM").format(date) },
                { "{{" + seconds + " | date: '%M'}}", new SimpleDateFormat("mm").format(date) },
                { "{{" + seconds + " | date: '%p'}}", new SimpleDateFormat("a").format(date) },
                { "{{" + seconds + " | date: '%S'}}", new SimpleDateFormat("ss").format(date) },
                { "{{" + seconds + " | date: '%U'}}", new SimpleDateFormat("ww").format(date) },
                { "{{" + seconds + " | date: '%W'}}", new SimpleDateFormat("ww").format(date) },
                { "{{" + seconds + " | date: '%w'}}", new SimpleDateFormat("F").format(date) },
                { "{{" + seconds + " | date: '%x'}}", new SimpleDateFormat("MM/dd/yy").format(date) },
                { "{{" + seconds + " | date: '%X'}}", new SimpleDateFormat("HH:mm:ss").format(date) },
                { "{{" + seconds + " | date: 'x=%y'}}", "x=" + new SimpleDateFormat("yy").format(date) },
                { "{{" + seconds + " | date: '%Y'}}", new SimpleDateFormat("yyyy").format(date) },
                { "{{" + seconds + " | date: '%Z'}}", new SimpleDateFormat("z").format(date) }
        };

        for(String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
