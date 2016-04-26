package liqp.parser;

import liqp.parser.LiquidLexer;
import liqp.parser.LiquidParser;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.junit.Test;

public class LiquidLexerTest {

    @Test
    public void test() throws Exception {
        // {% include footer.html %}
        LiquidLexer lexer = new LiquidLexer(new ANTLRStringStream("{% include 'footer.html' %}"));
        LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));
        LiquidParser.include_tag_return context = parser.include_tag();
        System.out.println(context.getTree().toStringTree());

        lexer = new LiquidLexer(new ANTLRStringStream("{% include footer.html %}"));
        parser = new LiquidParser(Flavor.JEKYLL, new CommonTokenStream(lexer));
        context = parser.include_tag();
        System.out.println(context.getTree().toStringTree());
    }
}
