package liqp.filters;

import java.math.BigDecimal;

public class Abs extends Filter {

    /*
    Everything that is not a number - cause this filter to return 0:
    case obj
      when Float
        BigDecimal(obj.to_s)
      when Numeric
        obj
      when String
        (obj.strip =~ /\A-?\d+\.\d+\z/) ? BigDecimal(obj) : obj.to_i
      else
        if obj.respond_to?(:to_number)
          obj.to_number
        else
          0
        end
      end
     */
    @Override
    public Object apply(Object value, Object... params) {

        if (super.isInteger(value) || super.canBeInteger(value)) {
            return Math.abs(super.asNumber(value).longValue());
        }

        if (super.isNumber(value) || super.canBeDouble(value)) {
            return asFormattedNumber(new BigDecimal(super.asNumber(value).toString()).abs());
        }

        return 0;
    }
}
