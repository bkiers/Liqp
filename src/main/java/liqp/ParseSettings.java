package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.parser.Flavor;

public class ParseSettings {

    public final Flavor flavor;
    public final boolean stripSpacesAroundTags;
    public final ObjectMapper mapper;

    public static class Builder {

        Flavor flavor;
        boolean stripSpacesAroundTags;
        ObjectMapper mapper;

        public Builder() {
            this.flavor = Flavor.LIQUID;
            this.stripSpacesAroundTags = false;
            this.mapper = new ObjectMapper();
        }

        public Builder withFlavor(Flavor flavor) {
            this.flavor = flavor;
            return this;
        }

        public Builder withStripSpaceAroundTags(boolean stripSpacesAroundTags) {
            this.stripSpacesAroundTags = stripSpacesAroundTags;
            return this;
        }

        public Builder withMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public ParseSettings build() {
            return new ParseSettings(this.flavor, this.stripSpacesAroundTags, this.mapper);
        }
    }

    private ParseSettings(Flavor flavor, boolean stripSpacesAroundTags, ObjectMapper mapper) {
        this.flavor = flavor;
        this.stripSpacesAroundTags = stripSpacesAroundTags;
        this.mapper = mapper;
    }
}
