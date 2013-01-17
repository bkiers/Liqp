package liqp.filters;

class Plus extends Filter {

    /*
     * plus(input, operand)
     *
     * addition
     */
    @Override
    public Object apply(Object value, Object... params) {

        Object rhsObj = params[0];

        if(value instanceof Long && rhsObj instanceof Long) {
            return ((Number)value).longValue() + ((Number)rhsObj).longValue();
        }

        return ((Number)value).doubleValue() + ((Number)rhsObj).doubleValue();
    }
}
