package liqp.filters;

class Remove extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        String original = String.valueOf(value);

        String needle = String.valueOf(super.get(0, params));

        return original.replace(needle, "");
    }
}
