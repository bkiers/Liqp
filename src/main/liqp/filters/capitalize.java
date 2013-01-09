package liqp.filters;

class capitalize extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        String original = String.valueOf(value);

        if(original.isEmpty()) {
            return original;
        }

        char first = original.charAt(0);

        return Character.toUpperCase(first) + original.substring(1);
    }
}
