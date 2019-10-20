package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.parser.Flavor;

public class ParseSettings {

    public final Flavor flavor;
    public final boolean stripSpacesAroundTags;
    public final boolean stripSingleLine;
    public final boolean showInternalError;
    public final ObjectMapper mapper;

    public static class Builder {

        Flavor flavor;
        boolean stripSpacesAroundTags;
        boolean stripSingleLine;
        boolean showInternalError;
        ObjectMapper mapper;

        public Builder() {
            this.flavor = Flavor.LIQUID;
            this.stripSpacesAroundTags = false;
            this.showInternalError = false;
            this.mapper = new ObjectMapper();
        }

        public Builder withFlavor(Flavor flavor) {
            this.flavor = flavor;
            return this;
        }

        public Builder withStripSpaceAroundTags(boolean stripSpacesAroundTags) {
            return this.withStripSpaceAroundTags(stripSpacesAroundTags, false);
        }

        public Builder withStripSpaceAroundTags(boolean stripSpacesAroundTags, boolean stripSingleLine) {

            if (stripSingleLine && !stripSpacesAroundTags) {
                throw new IllegalStateException("stripSpacesAroundTags must be true if stripSingleLine is true");
            }

            this.stripSpacesAroundTags = stripSpacesAroundTags;
            this.stripSingleLine = stripSingleLine;
            return this;
        }

        public Builder withShowInternalError(boolean showInternalError) {
            this.showInternalError = showInternalError;
            return this;
        }

        public Builder withMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public ParseSettings build() {
            return new ParseSettings(this.flavor, this.stripSpacesAroundTags, this.stripSingleLine, this.showInternalError, this.mapper);
        }
    }

    private ParseSettings(Flavor flavor, boolean stripSpacesAroundTags, boolean stripSingleLine, boolean showInternalError, ObjectMapper mapper) {
        this.flavor = flavor;
        this.stripSpacesAroundTags = stripSpacesAroundTags;
        this.stripSingleLine = stripSingleLine;
        this.showInternalError = showInternalError;
        this.mapper = mapper;
    }
}
