package liqp.filters.date.fuzzy.extractors;

import java.util.ArrayList;
import java.util.List;

public class PartExtractorResult {

    public final String extractorName;
    public boolean found;
    public int start;
    public int end;
    public List<String> formatterPatterns;
    public boolean isMonthName;
    public boolean yearWithoutEra;
    public boolean isWeekDay;


    public PartExtractorResult(String extractorName){
        this.extractorName = extractorName;
        this.formatterPatterns = new ArrayList<>();
    }
    public PartExtractorResult(String extractorName, String formatterPattern){
        this.extractorName = extractorName;
        this.formatterPatterns = new ArrayList<>();
        this.formatterPatterns.add(formatterPattern);
    }
}
