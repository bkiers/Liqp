package liqp.filters.where;

import liqp.LValue;
import liqp.TemplateContext;

/**
 * Created by vasyl.khrystiuk on 10/09/2019.
 */
public abstract class WhereImpl extends LValue {

    protected final TemplateContext context;
    protected final PropertyResolverHelper resolverHelper;

    protected WhereImpl(TemplateContext context, PropertyResolverHelper helper) {
        this.context = context;
        this.resolverHelper = helper;
    }

    public abstract Object apply(Object value, Object... params);


}
