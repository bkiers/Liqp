package liqp.filters;

import liqp.TemplateContext;
import liqp.filters.where.JekyllWhereImpl;
import liqp.filters.where.LiquidWhereImpl;
import liqp.filters.where.PropertyResolverHelper;
import liqp.filters.where.WhereImpl;
import liqp.parser.Flavor;

/**
 * There are two different implementations of this filter in ruby.
 *
 * One is from shopify/liquid package:
 * https://github.com/Shopify/liquid/blob/master/lib/liquid/standardfilters.rb
 * https://github.com/Shopify/liquid/blob/master/test/integration/standard_filter_test.rb
 *
 * And the other is from jekyll/jekyll package:
 * https://github.com/jekyll/jekyll/blob/master/lib/jekyll/filters.rb
 * https://github.com/jekyll/jekyll/blob/master/test/test_filters.rb
 *
 * The differ between them are in how they work with objects and arrays as second argument. And this is rare usage case,
 * and even more, java is not a ruby so we cannot implement exact behavior for these edge cases.
 *
 */
public class Where extends Filter {


    public Where(){
        super("where");
    }

    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        WhereImpl delegate;
        if (context.getParseSettings().flavor == Flavor.JEKYLL) {
            checkParams(params, 2);
            delegate = new JekyllWhereImpl(context, PropertyResolverHelper.INSTANCE);
        } else {
            checkParams(params, 1, 2);
            delegate = new LiquidWhereImpl(context, PropertyResolverHelper.INSTANCE);
        }
        return delegate.apply(value, params);
    }

}
