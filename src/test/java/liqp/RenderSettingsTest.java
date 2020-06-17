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

    @Test
    public void renderWithStrictVariablesInCondition1() {
        Template.parse("{% if mu == \"somethingElse\" %}{{ badVariableName }}{% endif %}")
                .withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build())
                .render("mu", "muValue");
    }

    @Test
    public void renderWithStrictVariablesInCondition2() {
        try {
            Template.parse("{% if mu == \"muValue\" %}{{ badVariableName }}{% endif %}")
                    .withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build())
                    .render("mu", "muValue");
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("badVariableName"));
        }
    }

    @Test
    public void renderWithStrictVariablesInAnd1() {
        try {
            Template.parse("{% if mu == \"muValue\" and checkThis %}{{ badVariableName }}{% endif %}")
                    .withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build())
                    .render("mu", "muValue");
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("checkThis"));
        }
    }

    @Test
    public void renderWithStrictVariablesInAnd2() {
        Template.parse("{% if mu == \"somethingElse\" and doNotCheckThis %}{{ badVariableName }}{% endif %}")
                .withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build())
                .render("mu", "muValue");
    }

    @Test
    public void renderWithStrictVariablesInOr1() {
        try {
            Template.parse("{% if mu == \"muValue\" or doNotCheckThis %}{{ badVariableName }}{% endif %}")
                    .withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build())
                    .render("mu", "muValue");
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("badVariableName"));
        }
    }

    @Test
    public void renderWithStrictVariablesInOr2() {
        try {
            Template.parse("{% if mu == \"somethingElse\" or checkThis %}{{ badVariableName }}{% endif %}")
                    .withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build())
                    .render("mu", "muValue");
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("checkThis"));
        }
    }

    @Test
    public void raiseExceptionsInStrictModeFalseTest() {
        RenderSettings renderSettings = new RenderSettings.Builder()
                .withStrictVariables(true)
                .withRaiseExceptionsInStrictMode(false)
                .build();

        Template template = Template.parse("{{a}}{{b}}{{c}}").withRenderSettings(renderSettings);

        assertThat(template.errors().size(), is(0));

        String rendered = template.render("b", "FOO");

        // There should be 2 exceptions recorded for non-existing variables `a` and `c`
        assertThat(template.errors().size(), is(2));

        // Rendering should not terminate
        assertThat(rendered, is("FOO"));
    }
}
