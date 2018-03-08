package liqp.filters;

import java.util.Arrays;

public class Slice extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        super.checkParams(params,1, 2);

        if (!super.canBeInteger(params[0])) {
            throw new RuntimeException("Liquid error: invalid integer");
        }

        Object[] array = null;
        String string = null;
        int offset = super.asNumber(params[0]).intValue();
        int length = 1;
        int totalLength;

        if (super.isArray(value)) {
            array = super.asArray(value);
            totalLength = array.length;
        }
        else {
            string = super.asString(value);
            totalLength = string.length();
        }

        if (params.length > 1) {

            if(!super.canBeInteger(params[1])) {
                throw new RuntimeException("Liquid error: invalid integer");
            }

            length = super.asNumber(params[1]).intValue();
        }

        if (offset < 0) {
            offset = totalLength + offset;
        }

        if (offset + length > totalLength) {
            length = totalLength - offset;
        }

        if (offset > totalLength || offset < 0) {
            return "";
        }

        return array == null ?
                string.substring(offset, offset + length) :
                Arrays.copyOfRange(array, offset, offset + length);
    }
}
