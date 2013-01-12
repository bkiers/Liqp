package liqp.filters;

class Replace extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        String original = super.asString(value);

        Object needle = super.get(0, params);
        Object replacement = super.get(1, params);

        if(needle == null) {
            throw new RuntimeException("invalid pattern: " + needle);
        }

        if(replacement == null) {
            throw new RuntimeException("invalid replacement: " + needle);
        }

        return original.replace(String.valueOf(needle), String.valueOf(replacement));
    }
}
