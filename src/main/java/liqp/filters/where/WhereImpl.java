package liqp.filters.where;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.LValue;
import liqp.parser.Flavor;

/**
 * Created by vasyl.khrystiuk on 10/09/2019.
 */
public abstract class WhereImpl extends LValue {

    protected final ObjectMapper mapper;
    protected final Flavor flavor;
    protected final PropertyResolverAdapter.Helper resolverHelper;

    protected WhereImpl(ObjectMapper mapper, Flavor flavor, PropertyResolverAdapter.Helper helper) {
        this.mapper = mapper;
        this.flavor = flavor;
        resolverHelper = helper;
    }

    public abstract Object apply(Object value, Object... params);


}
