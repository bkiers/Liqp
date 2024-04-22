package liqp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import liqp.nodes.LNode;
import liqp.parser.v4.NodeVisitor;
import liquid.parser.v4.LiquidLexer;
import liquid.parser.v4.LiquidParser;

public final class TestUtils {

    private TestUtils() {
        // no need to instantiate this class
    }

    /**
     * Parses the input `source` and invokes the `rule` and returns this LNode.
     *
     * @param source
     *            the input source to be parsed.
     * @param rule
     *            the rule name (method name) to be invoked
     * @return The node.
     * @throws Exception
     */
    public static LNode getNode(String source, String rule) throws Exception {
        return getNode(source, rule, new TemplateParser.Builder().withEvaluateInOutputTag(true).build());
    }

    public static LNode getNode(String source, String rule, TemplateParser templateParser)
        throws Exception {

        LiquidLexer lexer = new LiquidLexer(CharStreams.fromString("{{ " + source + " }}"), templateParser.liquidStyleInclude, templateParser.stripSpacesAroundTags);
        LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer), templateParser.liquidStyleInclude, templateParser.evaluateInOutputTag, templateParser.errorMode);

        LiquidParser.OutputContext root = parser.output();
        NodeVisitor visitor = new NodeVisitor(templateParser.insertions, templateParser.filters, templateParser.liquidStyleInclude);

        return visitor.visitOutput(root);
    }

    public static Throwable getExceptionRootCause(Throwable e) {
        Throwable cause;
        Throwable result = e;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

    /**
     * Asserts that a certain pattern, evaluated using the given parser, results in the given expected
     * result.
     * 
     * @param parser
     *            The parser to use, e.g. {@link TemplateParser#DEFAULT}.
     * @param expectedResult
     *            The expected result.
     * @param pattern
     *            The liquid pattern.
     */
    public static void assertPatternResultEquals(TemplateParser parser, String expectedResult,
        String pattern) {
        assertPatternResultEquals(null, parser, expectedResult, pattern);
    }

    /**
     * Asserts that a certain pattern, evaluated using the given parser, results in the given expected
     * result.
     * 
     * @param message
     *            The identifying message for the {@link AssertionError}, or {@code null}.
     * @param parser
     *            The parser to use, e.g. {@link TemplateParser#DEFAULT}.
     * @param expectedResult
     *            The expected result.
     * @param pattern
     *            The liquid pattern.
     */
    public static void assertPatternResultEquals(String message, TemplateParser parser,
        String expectedResult, String pattern) {
        Template template = parser.parse(pattern);
        String rendered = String.valueOf(template.render());
        assertEquals(message, expectedResult, rendered);
    }

    /**
     * Asserts that a certain pattern, evaluated using the given parser, is invalid (results in a
     * {@link RuntimeException}).
     * 
     * @param parser
     *            The parser to use, e.g. {@link TemplateParser#DEFAULT}.
     * @param pattern
     *            The (invalid) liquid pattern.
     */
    public static void assertPatternInvalid(TemplateParser parser, String pattern) {
        assertPatternInvalid(null, parser, pattern);
    }

    /**
     * Asserts that a certain pattern, evaluated using the given parser, is invalid (results in a
     * {@link RuntimeException}).
     * 
     * @param message
     *            The identifying message for the {@link AssertionError}, or {@code null}.
     * @param parser
     *            The parser to use, e.g. {@link TemplateParser#DEFAULT}.
     * @param pattern
     *            The (invalid) liquid pattern.
     */
    public static void assertPatternInvalid(String message, TemplateParser parser, String pattern) {
        Template template = parser.parse(pattern);
        assertThrows(RuntimeException.class, () -> template.render());
    }
}
