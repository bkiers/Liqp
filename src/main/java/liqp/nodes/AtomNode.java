package liqp.nodes;

import liqp.Context;

public class AtomNode implements LNode {

    public static final AtomNode EMPTY = new AtomNode(new Object());

    private Object value;

    public AtomNode(Object value) {
        this.value = value;
    }

    public static boolean isEmpty(Object o) {
        return o == EMPTY.value;
    }

    @Override
    public Object render(Context context) {

        return value;
    }
}
