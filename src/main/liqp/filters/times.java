package liqp.filters;

class times extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        Double lhs = Double.valueOf(String.valueOf(value));
        Double rhs = Double.valueOf(String.valueOf(super.get(0, params)));

        double product = lhs * rhs;

        if(product % 1 == 0) {
            return (long)product;
        }

        return product;
    }
}
