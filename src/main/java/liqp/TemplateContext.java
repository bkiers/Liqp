package liqp;

import liqp.parser.Flavor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TemplateContext {

    protected TemplateContext parent;
    public final ProtectionSettings protectionSettings;
    public final RenderSettings renderSettings;
    public final ParseSettings parseSettings;
    private Map<String, Object> variables;
    private Map<String, Object> environmentMap;

    private List<RuntimeException> errors;

    public TemplateContext() {
        this(new ProtectionSettings.Builder().build(),
                new RenderSettings.Builder().build(),
                new ParseSettings.Builder().withFlavor(Flavor.LIQUID).build(),
                new LinkedHashMap<String, Object>());
    }

    @Deprecated // Use `TemplateContext(protectionSettings, renderSettings, parseSettings, variables)` instead
    public TemplateContext(ProtectionSettings protectionSettings, RenderSettings renderSettings, Flavor flavor, Map<String, Object> variables) {
        this(protectionSettings, renderSettings, new ParseSettings.Builder().withFlavor(flavor).build(), variables);
    }

    public TemplateContext(ProtectionSettings protectionSettings, RenderSettings renderSettings, ParseSettings parseSettings,
                           Map<String, Object> variables) {
        this.parent = null;
        this.protectionSettings = protectionSettings;
        this.renderSettings = renderSettings;
        this.parseSettings = parseSettings;
        this.variables = new LinkedHashMap<>(variables);
        this.errors = new ArrayList<>();
    }

    public TemplateContext(TemplateContext parent) {
        this(parent.protectionSettings, parent.renderSettings, parent.parseSettings, new LinkedHashMap<String, Object>());
        this.parent = parent;
    }

    public void addError(RuntimeException exception) {
        this.errors.add(exception);
    }

    public List<RuntimeException> errors() {
        return new ArrayList<>(this.errors);
    }

    public void incrementIterations() {
        this.protectionSettings.incrementIterations();
    }

    public boolean containsKey(String key) {

        if (this.variables.containsKey(key)) {
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

    public Map<String, Object> getEnvironmentMap() {
        if (parent != null) {
            return parent.getEnvironmentMap();
        }
        if (environmentMap == null) {
            environmentMap = new HashMap<>();
        }
        return environmentMap;
    }
}
