package liqp.filters;

import liqp.TemplateContext;

public class DelegatingFilter extends Filter {
    
    public static Filter createFilter(DelegatingFilterDelegate delegate) {
        return new DelegatingFilter(delegate);
    }
    
    public interface DelegatingFilterDelegate {
        /**
         * Define name of this tag.
         */
        String getName();

        /**
         * @see Filter#apply(Object, TemplateContext, Object...) 
         */
        Object apply(Object value, TemplateContext context, Object... params);
    }
    private final DelegatingFilterDelegate delegate;

    public DelegatingFilter(DelegatingFilterDelegate delegate) {
        super(delegate.getName());
        this.delegate = delegate;
    }

    public Object apply(Object value, TemplateContext context, Object... params) {
        return delegate.apply(value, context, params);
    }
}
