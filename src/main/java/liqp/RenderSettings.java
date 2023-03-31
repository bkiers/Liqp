package liqp;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

import liqp.parser.LiquidSupport;

public class RenderSettings {
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    
    public static final RenderSettings DEFAULT = new RenderSettings.Builder().build();

    public enum EvaluateMode {
        LAZY,
        EAGER
    }

    /**
     * If template context is not available yet - it's ok to create new.
     * This function don't need access to local context variables,
     * as it operates with parameters.
     */
    public Map<String, Object> evaluate(final ObjectMapper mapper, Map<String, Object> variables) {
        if (evaluateMode == EvaluateMode.EAGER) {
            return LiquidSupport.LiquidSupportFromInspectable.objectToMap(mapper, variables);
        }
        return variables;
    }

    /**
     * If template context is not available yet - it's ok to create new.
     * This function don't need access to local context variables,
     * as it operates with parameters.
     */
    public LiquidSupport evaluate(final ObjectMapper mapper, final Object variable) {
        if (variable instanceof LiquidSupport) {
            return ((LiquidSupport) variable);
        }
        return new LiquidSupport.LiquidSupportFromInspectable(mapper, variable);
    }

    /**
     * The same as <code>template.render!({}, strict_variables: true)</code> in ruby
     */
    public final boolean strictVariables;
    /**
     * This field doesn't have equivalent in ruby.
     */
    public final boolean showExceptionsFromInclude;
    public final EvaluateMode evaluateMode;
    public final Locale locale;
    public final ZoneId defaultTimeZone;
    private final RenderTransformer renderTransformer;
    private final Consumer<Map<String, Object>> environmentMapConfigurator;

    public static class Builder {

        boolean strictVariables;
        boolean showExceptionsFromInclude;
        EvaluateMode evaluateMode;
        Locale locale;
        ZoneId defaultTimeZone;
        RenderTransformer renderTransformer;
        Consumer<Map<String, Object>> environmentMapConfigurator;

        public Builder() {
            this.strictVariables = false;
            this.evaluateMode = EvaluateMode.LAZY;
            this.locale = DEFAULT_LOCALE;
            this.renderTransformer = null;
            this.environmentMapConfigurator = null;
            this.showExceptionsFromInclude = true;
        }

        public Builder withStrictVariables(boolean strictVariables) {
            this.strictVariables = strictVariables;
            return this;
        }

        public Builder withShowExceptionsFromInclude(boolean showExceptionsFromInclude) {
            this.showExceptionsFromInclude = showExceptionsFromInclude;
            return this;
        }

        public Builder withEvaluateMode(EvaluateMode evaluateMode) {
            this.evaluateMode = evaluateMode;
            return this;
        }

        /**
         * Sets the {@link RenderTransformer}.
         * 
         * @param renderTransformer The transformer, or {@code null} to use the default.
         * @return This builder.
         */
        public Builder withRenderTransformer(RenderTransformer renderTransformer) {
            this.renderTransformer = renderTransformer;
            return this;
        }

        public Builder withLocale(Locale locale){
            Objects.requireNonNull(locale);
            this.locale = locale;
            return this;
        }

        /**
         * Set default timezone for showing timezone of date/time types
         * that does not have own timezone information.
         * May be null, so the timezone pattern will be omitted in formatted strings.
         * @param defaultTimeZone - value or <code>null<code/>
         * @return
         */
        public Builder withDefaultTimeZone(ZoneId defaultTimeZone) {
            this.defaultTimeZone = defaultTimeZone;
            return this;
        }

        /**
         * Sets the configurator of the {@link TemplateContext}'s environment map
         * ({@link TemplateContext#getEnvironmentMap()}) instance.
         * 
         * The configurator is called upon the creation of a new root context. Typically, this allows the
         * addition of certain parameters to the context environment.
         * 
         * @param configurator The configurator, or {@code null}.
         * @return This builder.
         */
        public Builder withEnvironmentMapConfigurator(Consumer<Map<String, Object>> configurator) {
            this.environmentMapConfigurator = configurator;
            return this;
        }

        public RenderSettings build() {
            if (this.defaultTimeZone == null) {
                this.defaultTimeZone = ZoneId.systemDefault();
            }
            return new RenderSettings(this.strictVariables, this.showExceptionsFromInclude, this.evaluateMode, this.renderTransformer, this.locale, this.defaultTimeZone, this.environmentMapConfigurator);
        }
    }

    private RenderSettings(boolean strictVariables, boolean showExceptionsFromInclude, EvaluateMode evaluateMode,
        RenderTransformer renderTransformer, Locale locale, ZoneId defaultTimeZone,
        Consumer<Map<String, Object>> environmentMapConfigurator) {
        this.strictVariables = strictVariables;
        this.showExceptionsFromInclude = showExceptionsFromInclude;
        this.evaluateMode = evaluateMode;
        this.renderTransformer = renderTransformer == null ? RenderTransformerDefaultImpl.INSTANCE
            : renderTransformer;
        this.locale = locale;
        this.defaultTimeZone = defaultTimeZone;
        this.environmentMapConfigurator = environmentMapConfigurator;
    }

    public RenderTransformer getRenderTransformer() {
        return renderTransformer;
    }

    public Consumer<Map<String, Object>> getEnvironmentMapConfigurator() {
        return environmentMapConfigurator;
    }
}
