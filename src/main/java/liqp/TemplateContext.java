package liqp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import liqp.RenderTransformer.ObjectAppender;
import liqp.exceptions.ExceededMaxIterationsException;

public class TemplateContext {
    private static final AtomicBoolean FOUND_DUMMY = new AtomicBoolean(false);

    public static final String REGISTRY_CYCLE = "cycle";
    public static final String REGISTRY_IFCHANGED = "ifchanged";
    public static final String REGISTRY_FOR = "for";
    public static final String REGISTRY_FOR_STACK = "for_stack";
    public static final String REGISTRY_ITERATION_PROTECTOR = "iteration_protector";
    public static final String REGISTRY_ROOT_FOLDER = "registry_root_folder";


    protected TemplateContext parent;

    private final TemplateParser parser;

    private Map<String, Object> variables;
    private Map<String, Object> environmentMap;
    private Map<String, Object> registry;

    private final List<Exception> errors;
    private final Template template;

    public TemplateContext() {
        this(TemplateParser.DEFAULT, new LinkedHashMap<>());
    }


    public TemplateContext(Map<String, Object> variables) {
        this(new TemplateParser.Builder().build(), variables);
    }

    public TemplateContext(TemplateParser parser, Map<String, Object> variables) {
      this(null, parser, variables);
    }

    public TemplateContext(Template template, TemplateParser parser, Map<String, Object> variables) {
        this.template = template;
        this.parent = null;
        this.parser = parser;
        this.variables = new LinkedHashMap<>(variables);
        this.errors = new ArrayList<>();
    }

    public TemplateContext(TemplateContext parent) {
        this(parent.template, parent.getParser(), new LinkedHashMap<String, Object>());
        this.parent = parent;
    }

    protected TemplateContext(Map<String, Object> variables, TemplateContext parent) {
        this(parent);
        this.variables = variables;
    }

    public TemplateParser getParser() {
        return parser;
    }

    public void addError(Exception exception) {
        this.errors.add(exception);
    }

    public List<Exception> errors() {
        return new ArrayList<>(this.errors);
    }

    public void incrementIterations() {
        Map<String, Integer> iteratorProtector = getRegistry(REGISTRY_ITERATION_PROTECTOR);
        if (!iteratorProtector.containsKey(REGISTRY_ITERATION_PROTECTOR)) {
            iteratorProtector.put(REGISTRY_ITERATION_PROTECTOR, 0);
        }
        int value = iteratorProtector.get(REGISTRY_ITERATION_PROTECTOR) + 1;
        iteratorProtector.put(REGISTRY_ITERATION_PROTECTOR, value);
        this.checkForMaxIterations(value);
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
        return get(key, FOUND_DUMMY);
    }

    public Object get(String key, AtomicBoolean found) {
        // First try to retrieve the key from the local context
        if (this.variables.containsKey(key)) {
            found.set(true);
            return this.variables.get(key);
        }

        if (parent != null) {
            // Not available locally, try the parent context
            return parent.get(key, found);
        }

        // Not available
        found.set(false);
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

    public Map<String, Object> getVariables() {
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

    @SuppressWarnings("unchecked")
    public <T extends Map<String, ?>> T getRegistry(String registryName) {
        if (parent != null) {
            return parent.getRegistry(registryName);
        }

        if (!Arrays.asList(REGISTRY_CYCLE, REGISTRY_IFCHANGED, REGISTRY_FOR, REGISTRY_FOR_STACK, REGISTRY_ITERATION_PROTECTOR, REGISTRY_ROOT_FOLDER)
                .contains(registryName)) {
            // this checking exists for safety of library, any listed type is expected, not more
            throw new RuntimeException("unknown registry type: " + registryName);
        }
        if (registry == null) {
            registry = new HashMap<>();
        }

        if (!registry.containsKey(registryName)) {
            registry.put(registryName, new HashMap<String, T>());
        }
        //noinspection unchecked
        return (T) registry.get(registryName);
    }


    public ObjectAppender.Controller newObjectAppender(int estimatedNumberOfAppends) {
        return parser.getRenderTransformer().newObjectAppender(this,
                estimatedNumberOfAppends);
    }

    public TemplateContext newChildContext(Map<String, Object> variablesForChild) {
        return new TemplateContext(variablesForChild, this);
    }


    public void checkForMaxIterations(int iterations) {
        int maxIterations = parser.getLimitMaxIterations();
        if (iterations > maxIterations) {
            throw new ExceededMaxIterationsException(maxIterations);
        }
    }

    public TemplateParser.ErrorMode getErrorMode() {
        return parser.getErrorMode();
    }

    public TemplateContext newChildContext() {
        return newChildContext(new HashMap<>());
    }

    public Path getRootFolder() {
        Map<String, Object> registry = getRegistry(TemplateContext.REGISTRY_ROOT_FOLDER);
        return (Path) registry.get(TemplateContext.REGISTRY_ROOT_FOLDER);
    }

}
