package liqp.filters;

import liqp.TemplateContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import liqp.PlainBigDecimal;

public class Round extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        if (!super.canBeDouble(value)) {
            return 0;
        }

        StringBuilder formatBuilder = new StringBuilder("0");
        Double number = super.asNumber(value).doubleValue();
        Long round = 0L;

        if (params.length > 0 && super.canBeDouble(params[0])) {
            round = super.asNumber(params[0]).longValue();
        }

        if (round > 0) {
            formatBuilder.append(".");

            for (int i = 0; i < round; i++) {
                formatBuilder.append("0");
            }
        }

        DecimalFormat formatter = new DecimalFormat(formatBuilder.toString());
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        return new PlainBigDecimal(formatter.format(number));
    }
}
