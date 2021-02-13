package liqp.nodes;

import liqp.TemplateContext;

public class AtomNode implements LNode {

    public static final AtomNode EMPTY = new AtomNode(new Object() {
        @Override
        public String toString() {
            return "";
        }
    });
    public static final AtomNode BLANK = new AtomNode(new Object() {
        @Override
        public String toString() {
            return "";
        }
    });

    private Object value;

    public AtomNode(Object value) {
        this.value = value;
    }

    public static boolean isEmpty(Object o) {
        return o == EMPTY.value;
    }

    public static boolean isBlank(Object o) {
        return o == BLANK.value;
    }

    @Override
    public Object render(TemplateContext context) {

        return value;
    }
}
