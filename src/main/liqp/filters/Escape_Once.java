package liqp.filters;

class Escape_Once extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        String str = super.asString(value);

        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
