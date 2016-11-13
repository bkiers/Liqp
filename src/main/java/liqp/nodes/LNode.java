package liqp.nodes;

import liqp.TemplateContext;

/**
 * Denotes a node in the AST the parse creates from the
 * input source.
 */
public interface LNode {

    /**
     * Renders this AST.
     *
     * @param context
     *         the context (variables) with which this
     *         node should be rendered.
     *
     * @return an Object denoting the rendered AST.
     */
    Object render(TemplateContext context);
}
