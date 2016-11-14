package liqp;

import liqp.exceptions.ExceededMaxIterationsException;
import liqp.parser.Flavor;

import java.util.LinkedHashMap;
import java.util.Map;

public class TemplateContext {

    public final ProtectionSettings protectionSettings;
    public final Flavor flavor;
    private final Map<String, Object> variables;
    private int iterations;

    public TemplateContext() {
        this(new ProtectionSettings.Builder().build(), Flavor.LIQUID, new LinkedHashMap<String, Object>());
    }

    public TemplateContext(ProtectionSettings protectionSettings, Flavor flavor, Map<String, Object> variables) {
        this.protectionSettings = protectionSettings;
        this.flavor = flavor;
        this.variables = variables;
        this.iterations = 0;
    }

    public void incrementIterations() {

        this.iterations++;

        if (this.iterations > this.protectionSettings.maxIterations) {
            throw new ExceededMaxIterationsException(this.protectionSettings.maxIterations);
        }
    }

    public boolean containsKey(String key) {
        return this.variables.containsKey(key);
    }

    public Object get(String key) {
        return this.variables.get(key);
    }

    public Object put(String key, Object value) {
        return this.variables.put(key, value);
    }

    public Object remove(String key) {
        return this.variables.remove(key);
    }

    public Map<String,Object> getVariables() {
        return new LinkedHashMap<String, Object>(this.variables);
    }
}
