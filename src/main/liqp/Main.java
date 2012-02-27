package liqp;

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
        System.out.println(tokenNames[tree.getType()] + (leaf ? "='" + tree.getText() + "'" : ""));
        for(int i = 0; i < tree.getChildCount(); i++) {
            walk((CommonTree)tree.getChild(i), tokenNames, indent + 1);
        }
    }

    public static void main(String[] args) throws Exception {
        String test = "{% for image in product.images %}         \n" +
                      "  {{ image | product_img_url | img_tag }} \n" +
                      "{% endfor %}                              \n";
        LiquidLexer lexer = new LiquidLexer(new ANTLRStringStream(test));
        LiquidParser parser = new LiquidParser(new CommonTokenStream(lexer));
        CommonTree ast = parser.parse().tree;
        walk(ast, parser.getTokenNames(), 0);
    }
}
