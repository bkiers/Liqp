package liqp.filters.date.fuzzy;

import java.util.List;

class LookupResult {

    final List<Part> parts;
    final boolean found;

    LookupResult(List<Part> parts, boolean found) {
        this.parts = parts;
        this.found = found;
    }

}
