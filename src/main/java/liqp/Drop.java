package liqp;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Drops: https://github.com/Shopify/liquid/wiki/Introduction-to-Drops
 */
public abstract class Drop {

    // The (optional) variable context this Drop lives in.
    protected Context context = null;

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

        Object beforeReturn = null;

        try {
            beforeReturn = this.before_method(method);

            List<Method> superMethods = Arrays.asList(Object.class.getDeclaredMethods());

            Method instanceMethod = this.getClass().getMethod(method);

            // `instanceMethod` is possibly an inherited method: check if it's not in the `superMethods`.
            if(!superMethods.contains(instanceMethod)) {
                return instanceMethod.invoke(this); // TODO check void?
            }
        }
        catch(Exception e) {
            // If a method is non-public or does not exist, an exception is thrown
            // and simply ignored (`beforeReturn` will be returned).
        }

        return beforeReturn;
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
    public void setContext(Context context) {
        this.context = context;
    }
}
