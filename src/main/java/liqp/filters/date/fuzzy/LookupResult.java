package liqp.filters.date.fuzzy;

import java.util.List;

class LookupResult {

    final List<Part> parts;
    final boolean found;
    final DatePatternRecognizingContext ctx;

    LookupResult(List<Part> parts, boolean found) {
        this.parts = parts;
        this.found = found;
        this.ctx = null;
    }

    LookupResult(List<Part> parts, boolean found, DatePatternRecognizingContext ctx) {
        this.parts = parts;
        this.found = found;
        this.ctx = ctx;
    }
}
