package liqp.filters.where;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.LValue;
import liqp.TemplateContext;
import liqp.parser.Flavor;

/**
 * Created by vasyl.khrystiuk on 10/09/2019.
 */
public abstract class WhereImpl extends LValue {

    protected final TemplateContext context;
    protected final PropertyResolverAdapter.Helper resolverHelper;

    protected WhereImpl(TemplateContext context, PropertyResolverAdapter.Helper helper) {
        this.context = context;
        this.resolverHelper = helper;
    }

    public abstract Object apply(Object value, Object... params);


}
