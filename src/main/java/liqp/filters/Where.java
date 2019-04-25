package liqp.filters;

import liqp.TemplateContext;
import liqp.filters.where.JekyllWhereImpl;
import liqp.filters.where.LiquidWhereImpl;
import liqp.filters.where.PropertyResolverAdapter;
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

    public static final PropertyResolverAdapter.Helper HELPER = new PropertyResolverAdapter.Helper();

    private WhereImpl delegate;


    public Where(){
        super("where");
    }

    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (context.parseSettings.flavor == Flavor.JEKYLL) {
            checkParams(params, 2);
            this.delegate = new JekyllWhereImpl(context.parseSettings.mapper, HELPER);
        } else {
            checkParams(params, 1, 2);
            this.delegate = new LiquidWhereImpl(context.parseSettings.mapper, HELPER);
        }
        return delegate.apply(value, params);
    }

}
