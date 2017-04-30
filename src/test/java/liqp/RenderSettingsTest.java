package liqp;


import liqp.exceptions.VariableNotExistException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RenderSettingsTest {
    @Test
    public void renderWithStrictVariables1() {
        try {
            Template.parse("{{mu}}")
                    .withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build())
                    .render();
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("mu"));
        }
    }

    @Test
    public void renderWithStrictVariables2() {
        try {
            Template.parse("{{mu}} {{qwe.asd.zxc}}")
                    .withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build())
                    .render("mu", "muValue");
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("qwe.asd.zxc"));
        }
    }
}
