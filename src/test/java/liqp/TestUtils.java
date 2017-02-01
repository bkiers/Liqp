package liqp;

import java.lang.reflect.Method;
import liqp.nodes.LNode;
import liqp.nodes.LiquidWalker;
import liqp.parser.LiquidLexer;
import liqp.parser.LiquidParser;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

public final class TestUtils {

    private TestUtils() {
        // no need to instantiate this class
    }

    /**
     * Parses the input `source` and invokes the `rule` and returns this LNode.
     *
     * @param source
     *              the input source to be parsed.
     * @param rule
     *              the rule name (method name) to be invoked
     * @return
     * @throws Exception
     */
    public static LNode getNode(String source, String rule) throws Exception {

        LiquidLexer lexer = new LiquidLexer(new ANTLRStringStream("{{ " + source + " }}"));
        LiquidParser parser =  new LiquidParser(new CommonTokenStream(lexer));

        CommonTree root = (CommonTree)parser.parse().getTree();
        CommonTree child = (CommonTree)root.getChild(0).getChild(0);

        LiquidWalker walker = new LiquidWalker(new CommonTreeNodeStream(child));

        Method method = walker.getClass().getMethod(rule);

        return (LNode)method.invoke(walker);
    }

    public static void dumpTokens(String source) {

        LiquidLexer lexer = new LiquidLexer(new ANTLRStringStream(source));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();

        for (Token t : tokenStream.getTokens()) {
            System.out.printf("%-20s '%s'\n",
                    t.getType() == -1 ? "EOF" : LiquidParser.tokenNames[t.getType()],
                    t.getText().replace("\n", "\\n"));
        }
    }

    public static void main(String[] args) {
        dumpTokens("a  \n  {%-");
    }
}
