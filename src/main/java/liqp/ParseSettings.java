package liqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import liqp.filters.Filter;
import liqp.parser.Flavor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParseSettings {

    public final static Flavor DEFAULT_FLAVOR = Flavor.LIQUID;

    public final Flavor flavor;
    public final boolean stripSpacesAroundTags;
    public final boolean stripSingleLine;
    public final ObjectMapper mapper;
    public final List<Insertion> insertions;
    public final List<Filter> filters;

    public static class Builder {

        Flavor flavor;
        boolean stripSpacesAroundTags;
        boolean stripSingleLine;
        ObjectMapper mapper;
        List<Insertion> insertions = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        

        public Builder() {
            this.flavor = DEFAULT_FLAVOR;
            this.stripSpacesAroundTags = false;
            this.mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
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
        
        public Builder with(Insertion insertion) {
            this.insertions.add(insertion);
            return this;
        }
        
        public Builder with(Filter filter) {
            filters.add(filter);
            return this;
        }

        public ParseSettings build() {
            return new ParseSettings(this.flavor, this.stripSpacesAroundTags, this.stripSingleLine, this.mapper, this.insertions, this.filters);
        }
    }

    private ParseSettings(Flavor flavor, boolean stripSpacesAroundTags, boolean stripSingleLine, ObjectMapper mapper, List<Insertion> insertions, List<Filter> filters) {
        this.flavor = flavor;
        this.stripSpacesAroundTags = stripSpacesAroundTags;
        this.stripSingleLine = stripSingleLine;
        this.mapper = mapper;
        this.insertions = Collections.unmodifiableList(insertions);
        this.filters = Collections.unmodifiableList(filters);
    }
}
