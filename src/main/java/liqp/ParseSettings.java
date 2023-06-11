package liqp;

import java.util.*;

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
    public final boolean evaluateInOutputTag;
    public final TemplateParser.ErrorMode errorMode;
    public final boolean liquidStyleInclude;

    public static class Builder {
        Flavor flavor;
        boolean stripSpacesAroundTags;
        boolean stripSingleLine;
        ObjectMapper mapper;
        List<Insertion> insertions = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        Boolean evaluateInOutputTag;
        TemplateParser.ErrorMode errorMode;
        Boolean liquidStyleInclude;

        public Builder() {
            this.flavor = null;
            this.stripSpacesAroundTags = false;
            this.mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        }

        public Builder(ParseSettings other) {
            this.flavor = other.flavor;
            this.stripSpacesAroundTags = other.stripSpacesAroundTags;
            this.stripSingleLine = other.stripSingleLine;
            this.mapper = other.mapper;
            this.insertions = new ArrayList<>(other.insertions.values());
            this.filters = new ArrayList<>(other.filters.values());
            this.evaluateInOutputTag = other.evaluateInOutputTag;
            this.errorMode = other.errorMode;
            this.liquidStyleInclude = other.liquidStyleInclude;
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
                    .withErrorMode(settings.errorMode)
                    .withLiquidStyleInclude(settings.liquidStyleInclude)
                    .with(settings.filters.values());
        }

        public Builder with(Insertion insertion) {
            this.insertions.add(insertion);
            return this;
        }

        public Builder with(Filter filter) {
            this.filters.add(filter);
            return this;
        }

        Builder withInsertions(Collection<Insertion> insertions) {
            this.insertions.addAll(insertions);
            return this;
        }

        public Builder with(Collection<Filter> filters) {
            this.filters.addAll(filters);
            return this;
        }

        public Builder withEvaluateInOutputTag(boolean evaluateInOutputTag) {
            this.evaluateInOutputTag = evaluateInOutputTag;
            return this;
        }

        public Builder withErrorMode(TemplateParser.ErrorMode errorMode) {
            this.errorMode = errorMode;
            return this;
        }

        public Builder withLiquidStyleInclude(boolean liquidStyleInclude) {
            this.liquidStyleInclude = liquidStyleInclude;
            return this;
        }

        public ParseSettings build() {
            Flavor fl = this.flavor;
            if (fl == null) {
                fl = DEFAULT_FLAVOR;
            }

            Boolean evaluateInOutputTag = this.evaluateInOutputTag;
            if (evaluateInOutputTag == null) {
                evaluateInOutputTag = fl.isEvaluateInOutputTag();
            }

            TemplateParser.ErrorMode errorMode = this.errorMode;
            if (errorMode == null) {
                errorMode = fl.getErrorMode();
            }

            Boolean liquidStyleInclude = this.liquidStyleInclude;
            if (liquidStyleInclude == null) {
                liquidStyleInclude = fl.isLiquidStyleInclude();
            }

            Insertions allInsertions = fl.getInsertions().mergeWith(Insertions.of(this.insertions));

            Filters finalFilters = fl.getFilters().mergeWith(filters);

            return new ParseSettings(fl, this.stripSpacesAroundTags, this.stripSingleLine, this.mapper,
                    allInsertions, finalFilters, evaluateInOutputTag, errorMode, liquidStyleInclude);
        }

    }

    private ParseSettings(Flavor flavor, boolean stripSpacesAroundTags, boolean stripSingleLine,
            ObjectMapper mapper, Insertions insertions, Filters filters, boolean evaluateInOutputTag, TemplateParser.ErrorMode errorMode, boolean liquidStyleInclude) {
        this.flavor = flavor;
        this.stripSpacesAroundTags = stripSpacesAroundTags;
        this.stripSingleLine = stripSingleLine;
        this.mapper = mapper;
        this.insertions = insertions;
        this.filters = filters;
        this.evaluateInOutputTag = evaluateInOutputTag;
        this.errorMode = errorMode;
        this.liquidStyleInclude = liquidStyleInclude;
    }
}
