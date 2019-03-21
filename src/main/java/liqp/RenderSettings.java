package liqp;


public class RenderSettings {

    public final boolean strictVariables;
    public final boolean showExceptionsFromInclude;

    public static class Builder {

        boolean strictVariables;
        boolean showExceptionsFromInclude;

        public Builder() {
            this.strictVariables = false;
            this.showExceptionsFromInclude = false;
        }

        public Builder withStrictVariables(boolean strictVariables) {
            this.strictVariables = strictVariables;
            return this;
        }

        public Builder withShowExceptionsFromInclude(boolean showExceptionsFromInclude) {
            this.showExceptionsFromInclude = showExceptionsFromInclude;
            return this;
        }

        public RenderSettings build() {
            return new RenderSettings(this.strictVariables, this.showExceptionsFromInclude);
        }
    }

    private RenderSettings(boolean strictVariables, boolean showExceptionsFromInclude) {
        this.strictVariables = strictVariables;
        this.showExceptionsFromInclude = showExceptionsFromInclude;
    }
}
