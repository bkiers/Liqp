package liqp;

import liqp.parser.LiquidLexer;
import liqp.parser.LiquidParser;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class Main {

    private static void walk(CommonTree tree, String[] tokenNames, int indent) {

        if(tree == null) {
            return;
        }

        for(int i = 0; i < indent; i++) {
            System.out.print("  ");
        }

        boolean leaf = tree.getChildCount() == 0;

        if(tree.getType() == LiquidLexer.EOF) {
            return;
        }

        System.out.println(tokenNames[tree.getType()] + (leaf ? "='" + tree.getText() + "'" : ""));

        for(int i = 0; i < tree.getChildCount(); i++) {
            walk((CommonTree)tree.getChild(i), tokenNames, indent + 1);
        }
    }

    public static void main(String[] args) throws Exception {
        String test = "{% cycle 'o', 't' %}\n" +
                "{% cycle 33: 'one', 'two', 'three' %}\n" +
                "{% cycle 33: 'one', 'two', 'three' %}\n" +
                "{% cycle 3: '1', '2' %}\n" +
                "{% cycle 33: 'one', 'two' %}\n" +
                "{% cycle 33: 'one', 'two' %}\n" +
                "{% cycle 3: '1', '2' %}\n" +
                "{% cycle 3: '1', '2' %}\n" +
                "{% cycle 'o', 't' %}\n" +
                "{% cycle 'o', 't' %}";
        LiquidLexer lexer = new LiquidLexer(new ANTLRStringStream(test));
        LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));
        CommonTree ast = (CommonTree)parser.parse().getTree();
        walk(ast, parser.getTokenNames(), 0);
    }
}
