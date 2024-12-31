package liqp.filters.date.fuzzy;

import java.util.List;

class LookupResult {

    private final String name;
    final List<Part> parts;
    final boolean found;

    LookupResult(String name, List<Part> parts, boolean found) {
        this.name = name;
        this.parts = parts;
        this.found = found;
    }

    public String getName() {
        return name;
    }
}
