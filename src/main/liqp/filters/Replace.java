package liqp.filters;

class Replace extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        String original = String.valueOf(value);

        String needle = String.valueOf(super.get(0, params));
        String replacement = String.valueOf(super.get(1, params));

        return original.replace(needle, replacement);
    }
}
