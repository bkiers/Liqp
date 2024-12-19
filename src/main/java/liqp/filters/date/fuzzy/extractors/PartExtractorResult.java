package liqp.filters.date.fuzzy.extractors;

import java.util.ArrayList;
import java.util.List;

public class PartExtractorResult {
    public PartExtractorResult(){}
    public PartExtractorResult(List<String> formatterPatterns){
        this.formatterPatterns = formatterPatterns;
    }
    public PartExtractorResult(String formatterPattern){
        this.formatterPatterns = new ArrayList<>();
        this.formatterPatterns.add(formatterPattern);
    }

    public boolean found;
    public int start;
    public int end;
    public List<String> formatterPatterns;
}
