package liqp;


import liqp.parser.LiquidSupport;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class RenderSettings {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public enum EvaluateMode {
        LAZY,
        EAGER
    }


    /**
     * If template context is not available yet - it's ok to create new.
     * This function don't need access to local context variables,
     * as it operates with parameters.
     * @param context - value object with RenderSettings, ParseSettings and ProtectionSettings in it.
     */
    public Map<String, Object> evaluate(final TemplateContext context, Map<String, Object> variables) {
        if (evaluateMode == EvaluateMode.EAGER) {
            return LiquidSupport.LiquidSupportFromInspectable.objectToMap(context, variables);
        }
        return variables;
    }

    /**
     * If template context is not available yet - it's ok to create new.
     * This function don't need access to local context variables,
     * as it operates with parameters.
     * @param context - holder for RenderSettings, ParseSettings and ProtectionSettings in it.
     */
    public LiquidSupport evaluate(final TemplateContext context, final Object variable) {
        if (variable instanceof LiquidSupport) {
            return ((LiquidSupport) variable);
        }
        return new LiquidSupport.LiquidSupportFromInspectable(context, variable);
    }

    public final boolean strictVariables;
    public final boolean showExceptionsFromInclude;
    public final boolean raiseExceptionsInStrictMode;
    public final EvaluateMode evaluateMode;
    public final Locale locale;
    public final TimeZone defaultTimeZone;

    public static class Builder {

        boolean strictVariables;
        boolean showExceptionsFromInclude;
        boolean raiseExceptionsInStrictMode;
        EvaluateMode evaluateMode;
        Locale locale;
        TimeZone defaultTimeZone;


        public Builder() {
            this.strictVariables = false;
            this.raiseExceptionsInStrictMode = true;
            this.evaluateMode = EvaluateMode.LAZY;
            this.locale = DEFAULT_LOCALE;
        }

        public Builder withStrictVariables(boolean strictVariables) {
            this.strictVariables = strictVariables;
            this.showExceptionsFromInclude = false;
            return this;
        }

        public Builder withRaiseExceptionsInStrictMode(boolean raiseExceptionsInStrictMode) {
            this.raiseExceptionsInStrictMode = raiseExceptionsInStrictMode;
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
        public Builder withDefaultTimeZone(TimeZone defaultTimeZone) {
            this.defaultTimeZone = defaultTimeZone;
            return this;
        }

        public RenderSettings build() {
            return new RenderSettings(this.strictVariables, this.showExceptionsFromInclude, this.raiseExceptionsInStrictMode, this.evaluateMode, this.locale, this.defaultTimeZone);
        }
    }

    private RenderSettings(boolean strictVariables, boolean showExceptionsFromInclude, boolean raiseExceptionsInStrictMode, EvaluateMode evaluateMode, Locale locale, TimeZone defaultTimeZone) {
        this.strictVariables = strictVariables;
        this.showExceptionsFromInclude = showExceptionsFromInclude;
        this.raiseExceptionsInStrictMode = raiseExceptionsInStrictMode;
        this.evaluateMode = evaluateMode;
        this.locale = locale;
        this.defaultTimeZone = defaultTimeZone;
    }
}
