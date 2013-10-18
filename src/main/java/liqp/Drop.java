package liqp;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Drops: https://github.com/Shopify/liquid/wiki/Introduction-to-Drops
 */
public abstract class Drop {

    // The (optional) variable context this Drop lives in.
    protected Map<String, Object> context = null;

    /**
     * Called once for every invocation, even if the destination
     * method doesn't exist.
     *
     * @param method the method to invoke.
     *
     * @return a possible return value.
     */
    public Object before_method(String method) {
        return null;
    }

    /**
     * Called by liquid to invoke the target drop method.
     *
     * @param method the method to invoke.
     *
     * @return a possible return value.
     */
    public final Object invoke_drop(String method) {

        Object beforeReturn = this.before_method(method);

        try {
            Method instanceMethod = this.getClass().getMethod(method);
            return instanceMethod.invoke(this); // TODO check void
        }
        catch(Exception e) {
            return beforeReturn;
        }
    }

    /**
     * Currently returns true regardless.
     *
     * @return currently returns true regardless.
     */
    public boolean has_key() {
        return true;
    }

    /**
     * Returns the drop instance.
     *
     * @return the drop instance.
     */
    public final Drop to_liquid() {
        return this;
    }

    /**
     * Set the (optional) variable context.
     *
     * @param context the variable context.
     */
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
