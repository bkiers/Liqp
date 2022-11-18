package liqp;


import liqp.exceptions.VariableNotExistException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RenderSettingsTest {
    
    protected TemplateParser parserWithStrictVariables() {
        return new TemplateParser.Builder().withRenderSettings(new RenderSettings.Builder()
                .withStrictVariables(true).build()).build();
    }
    
    @Test
    public void renderWithStrictVariables1() {
        try {
            parserWithStrictVariables().parse("{{mu}}").render();
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("mu"));
        }
    }

    @Test
    public void renderWithStrictVariables2() {
        try {
            parserWithStrictVariables().parse("{{mu}} {{qwe.asd.zxc}}").render("mu", "muValue");
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("qwe.asd.zxc"));
        }
    }

    @Test
    public void renderWithStrictVariablesInCondition1() {
        parserWithStrictVariables().parse("{% if mu == \"somethingElse\" %}{{ badVariableName }}{% endif %}")
                .render("mu", "muValue");
    }

    @Test
    public void renderWithStrictVariablesInCondition2() {
        try {
            parserWithStrictVariables().parse("{% if mu == \"muValue\" %}{{ badVariableName }}{% endif %}")
                    .render("mu", "muValue");
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("badVariableName"));
        }
    }

    @Test
    public void renderWithStrictVariablesInAnd1() {
        try {
            parserWithStrictVariables().parse("{% if mu == \"muValue\" and checkThis %}{{ badVariableName }}{% endif %}")
                    .render("mu", "muValue");
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("checkThis"));
        }
    }

    @Test
    public void renderWithStrictVariablesInAnd2() {
        parserWithStrictVariables().parse("{% if mu == \"somethingElse\" and doNotCheckThis %}{{ badVariableName }}{% endif %}")
                .render("mu", "muValue");
    }

    @Test
    public void renderWithStrictVariablesInOr1() {
        try {
            parserWithStrictVariables().parse("{% if mu == \"muValue\" or doNotCheckThis %}{{ badVariableName }}{% endif %}")
                    .render("mu", "muValue");
        } catch (RuntimeException ex) {
            VariableNotExistException e = (VariableNotExistException) TestUtils.getExceptionRootCause(ex);
            assertThat(e.getVariableName(), is("badVariableName"));
        }
    }

    @Test
    public void renderWithStrictVariablesInOr2() {
        try {
            parserWithStrictVariables().parse("{% if mu == \"somethingElse\" or checkThis %}{{ badVariableName }}{% endif %}")
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
        
        TemplateParser parser = new TemplateParser.Builder().withRenderSettings(renderSettings).build();

        Template template = parser.parse("{{a}}{{b}}{{c}}");

        assertThat(template.errors().size(), is(0));

        String rendered = template.render("b", "FOO");

        // There should be 2 exceptions recorded for non-existing variables `a` and `c`
        assertThat(template.errors().size(), is(2));

        // Rendering should not terminate
        assertThat(rendered, is("FOO"));
    }
}
