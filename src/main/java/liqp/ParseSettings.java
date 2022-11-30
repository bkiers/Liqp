package liqp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import liqp.filters.Filter;
import liqp.filters.Filters;
import liqp.parser.Flavor;

public class ParseSettings {
    public static final Flavor DEFAULT_FLAVOR = Flavor.LIQUID;
    public static final ParseSettings DEFAULT = DEFAULT_FLAVOR.defaultParseSettings();

    public final Flavor flavor;
    public final boolean stripSpacesAroundTags;
    public final boolean stripSingleLine;
    public final ObjectMapper mapper;
    public final Insertions insertions;
    public final Filters filters;

    public static class Builder {
        Flavor flavor;
        boolean stripSpacesAroundTags;
        boolean stripSingleLine;
        ObjectMapper mapper;
        List<Insertion> insertions = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();

        public Builder() {
            this.flavor = null;
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
                throw new IllegalStateException(
                        "stripSpacesAroundTags must be true if stripSingleLine is true");
            }

            this.stripSpacesAroundTags = stripSpacesAroundTags;
            this.stripSingleLine = stripSingleLine;
            return this;
        }

        public Builder withMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public Builder with(ParseSettings settings) {
            return withFlavor(settings.flavor) //
                    .withStripSpaceAroundTags(stripSpacesAroundTags, stripSingleLine) //
                    .withMapper(settings.mapper) //
                    .withInsertions(settings.insertions.values()) //
                    .withFilters(settings.filters.values());
        }

        public Builder with(Insertion insertion) {
            this.insertions.add(insertion);
            return this;
        }

        @Deprecated
        Builder withInsertions(Collection<Insertion> insertions) {
            this.insertions.addAll(insertions);
            return this;
        }

        public Builder with(Filter filter) {
            filters.add(filter);
            return this;
        }

        @Deprecated
        Builder withFilters(Collection<Filter> filters) {
            this.filters.addAll(filters);
            return this;
        }

        public ParseSettings build() {
            Flavor fl = this.flavor;
            if (fl == null) {
                fl = Flavor.LIQUID;
            }

            return new ParseSettings(fl, this.stripSpacesAroundTags, this.stripSingleLine, this.mapper,
                    this.insertions, this.filters);
        }
    }

    private ParseSettings(Flavor flavor, boolean stripSpacesAroundTags, boolean stripSingleLine,
            ObjectMapper mapper, List<Insertion> insertions, List<Filter> filters) {
        this.flavor = flavor;
        this.stripSpacesAroundTags = stripSpacesAroundTags;
        this.stripSingleLine = stripSingleLine;
        this.mapper = mapper;
        this.insertions = Insertions.of(insertions);
        this.filters = Filters.of(filters);
    }
}
