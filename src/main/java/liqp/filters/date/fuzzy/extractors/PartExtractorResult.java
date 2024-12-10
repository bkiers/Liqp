package liqp.filters.date.fuzzy.extractors;

public class PartExtractorResult {
    public PartExtractorResult(){}
    public PartExtractorResult(String formatterPattern){
        this.formatterPattern = formatterPattern;
    }
    public boolean found;
    public int start;
    public int end;
    public String formatterPattern;
}
