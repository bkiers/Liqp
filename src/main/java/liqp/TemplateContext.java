package liqp;

import liqp.parser.Flavor;

import java.util.LinkedHashMap;
import java.util.Map;

public class TemplateContext {

    protected TemplateContext parent;
    public final ProtectionSettings protectionSettings;
    public final Flavor flavor;
    private Map<String, Object> variables;

    public TemplateContext() {
        this(new ProtectionSettings.Builder().build(), Flavor.LIQUID, new LinkedHashMap<String, Object>());
    }

    public TemplateContext(ProtectionSettings protectionSettings, Flavor flavor, Map<String, Object> variables) {
        this.parent = null;
        this.protectionSettings = protectionSettings;
        this.flavor = flavor;
        this.variables = variables;
    }

    public TemplateContext(TemplateContext parent) {
        this.parent = parent;
        this.protectionSettings = parent.protectionSettings;
        this.flavor = parent.flavor;
        this.variables = new LinkedHashMap<String, Object>();
    }

    public void incrementIterations() {
        this.protectionSettings.incrementIterations();
    }

    public boolean containsKey(String key) {

        if (this.containsKey(key)) {
            return true;
        }

        if (parent != null) {
            return parent.containsKey(key);
        }

        return false;
    }

    public Object get(String key) {

        // First try to retrieve the key from the local context
        Object value = this.variables.get(key);

        if (value != null) {
            return value;
        }

        if (parent != null) {
            // Not available locally, try the parent context
            return parent.get(key);
        }

        // Not available
        return null;
    }

    public Object put(String key, Object value) {
        return this.put(key, value, false);
    }

    public Object put(String key, Object value, boolean putInRootContext) {

        if (!putInRootContext || parent == null) {
            // Either store it in the local context, or this context is the root context
            return this.variables.put(key, value);
        }

        // Else put in the parent context
        return parent.put(key, value, putInRootContext);
    }

    public Object remove(String key) {

        if (this.variables.containsKey(key)) {
            // Remove the key from the local context
            return this.variables.remove(key);
        }

        if (parent != null) {
            // Not available in the local context, try the parent
            return parent.remove(key);
        }

        // Key was not present
        return null;
    }

    public Map<String,Object> getVariables() {
        return new LinkedHashMap<String, Object>(this.variables);
    }
}
