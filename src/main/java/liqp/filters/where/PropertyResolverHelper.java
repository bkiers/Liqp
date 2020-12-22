package liqp.filters.where;

import liqp.LValue;
import liqp.TemplateContext;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

import java.util.ArrayList;
import java.util.List;

public class PropertyResolverHelper {
    private final List<PropertyResolverAdapter> propertyResolverAdapters;

    public static PropertyResolverHelper INSTANCE = new PropertyResolverHelper();
    static {
        // default resolver for Inspectable type
        // allow Inspectable items to be inspected via "where" filter
        INSTANCE.add(new PropertyResolverAdapter() {
            // dummy LValue for accessing helper method #asString
            private final LValue lValue = new LValue() {};
            @Override
            public Object getItemProperty(TemplateContext context, Object input, Object property) {
                LiquidSupport evaluated = context.renderSettings.evaluate(context.parseSettings.mapper, (Inspectable) input);
                return evaluated.toLiquid().get(lValue.asString(property));
            }

            @Override
            public boolean support(Object target) {
                return target instanceof Inspectable;
            }
        });
    }

    private PropertyResolverHelper() {
        this.propertyResolverAdapters = new ArrayList<>();
    }

    public void add(PropertyResolverAdapter one) {
        this.propertyResolverAdapters.add(one);
    }

    public PropertyResolverAdapter findFor(Object target) {
        for (PropertyResolverAdapter e : propertyResolverAdapters) {
            if (e.support(target)) {
                return e;
            }
        }
        return null;
    }
}
