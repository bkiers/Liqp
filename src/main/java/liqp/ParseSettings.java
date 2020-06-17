package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import liqp.parser.Flavor;

public class ParseSettings {

    public final static Flavor DEFAULT_FLAVOR = Flavor.LIQUID;

    public final Flavor flavor;
    public final boolean stripSpacesAroundTags;
    public final boolean stripSingleLine;
    public final ObjectMapper mapper;

    public static class Builder {

        Flavor flavor;
        boolean stripSpacesAroundTags;
        boolean stripSingleLine;
        ObjectMapper mapper;

        public Builder() {
            this.flavor = DEFAULT_FLAVOR;
            this.stripSpacesAroundTags = false;
            this.mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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

        public Builder withMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public ParseSettings build() {
            return new ParseSettings(this.flavor, this.stripSpacesAroundTags, this.stripSingleLine, this.mapper);
        }
    }

    private ParseSettings(Flavor flavor, boolean stripSpacesAroundTags, boolean stripSingleLine, ObjectMapper mapper) {
        this.flavor = flavor;
        this.stripSpacesAroundTags = stripSpacesAroundTags;
        this.stripSingleLine = stripSingleLine;
        this.mapper = mapper;
    }
}
