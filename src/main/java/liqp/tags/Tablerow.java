package liqp.tags;

import liqp.TemplateContext;
import liqp.nodes.LNode;
import liqp.parser.LiquidSupport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Tablerow extends Block {

    private static final String COLS = "cols";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";

    /*
     * tablerowloop.length       # => length of the entire for loop
     * tablerowloop.index        # => index of the current iteration
     * tablerowloop.index0       # => index of the current iteration (zero based)
     * tablerowloop.rindex       # => how many items are still left?
     * tablerowloop.rindex0      # => how many items are still left? (zero based)
     * tablerowloop.first        # => is this the first iteration?
     * tablerowloop.last         # => is this the last iteration?
     * tablerowloop.col          # => index of column in the current row
     * tablerowloop.col0         # => index of column in the current row (zero based)
     * tablerowloop.col_first    # => is this the first column in the row?
     * tablerowloop.col_last     # => is this the last column in the row?
     */
    private static final String TABLEROWLOOP = "tablerowloop";
    private static final String LENGTH = "length";
    private static final String INDEX = "index";
    private static final String INDEX0 = "index0";
    private static final String RINDEX = "rindex";
    private static final String RINDEX0 = "rindex0";
    private static final String FIRST = "first";
    private static final String LAST = "last";
    private static final String COL = "col";
    private static final String COL0 = "col0";
    private static final String COL_FIRST = "col_first";
    private static final String COL_LAST = "col_last";
    private static final String ROW = "row";


    /*
     * Tables
     */
    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        String valueName = super.asString(nodes[0].render(context), context);
        Object[] collection = super.asArray(nodes[1].render(context), context);
        LNode block = nodes[2];
        Map<String, Integer> attributes = getAttributes(collection, 3, context, nodes);

        int cols = attributes.get(COLS);
        int limit = attributes.get(LIMIT);
        int offset = attributes.get(OFFSET);

        if(offset != 0) {
            if ((collection.length > 0) && (offset < collection.length)) {
                collection = Arrays.copyOfRange(collection, offset, collection.length);
            } else {
                collection = new Object[]{};
            }
        }

        TemplateContext nestedContext = new TemplateContext(context);
        int total = Math.min(collection.length, limit);
        TablerowloopDrop tablerowloopDrop = new TablerowloopDrop(total, cols);
        nestedContext.put(TABLEROWLOOP, tablerowloopDrop);


        StringBuilder builder = new StringBuilder();



        if(total == 0) {

            builder.append("<tr class=\"row1\">\n</tr>\n");
        }
        else {

            for(int i = 0, c = 1, r = 0; i < total; i++, c++) {

                context.incrementIterations();

                nestedContext.put(valueName, collection[i]);
                if(c == 1) {
                    r++;
                    builder.append("<tr class=\"row").append(r).append("\">").append(r == 1 ? "\n" : "");
                }

                builder.append("<td class=\"col").append(c).append("\">");
                builder.append(super.asString(block.render(nestedContext), context));
                builder.append("</td>");

                if(c == cols || i == total - 1) {
                    builder.append("</tr>\n");
                    c = 0;
                }
                tablerowloopDrop.increment();
            }
        }

        nestedContext.remove(TABLEROWLOOP);
        nestedContext.remove(valueName);

        return builder.toString();
    }

    private Map<String, Integer> getAttributes(Object[] collection, int fromIndex, TemplateContext context, LNode... tokens) {

        Map<String, Integer> attributes = new HashMap<String, Integer>();

        attributes.put(COLS, collection.length);
        attributes.put(LIMIT, Integer.MAX_VALUE);
        attributes.put(OFFSET, 0);

        for (int i = fromIndex; i < tokens.length; i++) {

            Object[] attribute = super.asArray(tokens[i].render(context), context);

            try {
                attributes.put(super.asString(attribute[0], context), super.asNumber(attribute[1]).intValue());
            }
            catch (Exception e) {
                /* just ignore incorrect attributes */
            }
        }

        return attributes;
    }
    public static class TablerowloopDrop implements LiquidSupport {
        private final long length;
        private final long cols;
        private long row;
        private long col;
        private long index;
        private Map<String, Object> tablerowloopContext = new HashMap<>();

        TablerowloopDrop(long length, long cols) {
            this.length = length;
            this.cols = cols;
            this.row = 1;
            this.col = 1;
            this.index = 0;
        }

        @Override
        public Map<String, Object> toLiquid() {
            tablerowloopContext.put(LENGTH, length);
            tablerowloopContext.put(INDEX0, index);
            tablerowloopContext.put(INDEX, index + 1);
            tablerowloopContext.put(RINDEX0, length - index - 1);
            tablerowloopContext.put(RINDEX, length - index);
            tablerowloopContext.put(FIRST, index == 0);
            tablerowloopContext.put(LAST, index == length - 1);
            tablerowloopContext.put(COL0, col - 1);
            tablerowloopContext.put(COL, col);
            tablerowloopContext.put(COL_FIRST, col == 1);
            tablerowloopContext.put(COL_LAST, col == cols);
            tablerowloopContext.put(ROW, row); // <-- add tests
            return tablerowloopContext;
        }

        public void increment() {
            index++;
            if (col == cols) {
                col = 1;
                row ++;
            } else {
                col++;
            }
        }
    }
}
