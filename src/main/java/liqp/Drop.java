package liqp;

import java.lang.reflect.Method;

/**
 * Drops: https://github.com/Shopify/liquid/wiki/Introduction-to-Drops
 */
public abstract class Drop {

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
}
