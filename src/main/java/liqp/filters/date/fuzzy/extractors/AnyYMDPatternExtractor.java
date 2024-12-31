package liqp.filters.date.fuzzy.extractors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import liqp.filters.date.fuzzy.Part;

class AnyYMDPatternExtractor extends RegexPartExtractor {

    public enum RuleType {
        Y, M, D, PUNCTUATION;
    }
    public static class RulePart {
        private final RuleType type;
        private final Integer length;
        private final String content;
        private RulePart(RuleType type, String content) {
            this.type = type;
            this.content = content;
            this.length = null;
        }

        private RulePart(RuleType type, Integer length) {
            this.type = type;
            this.length = length;
            this.content = null;
        }
    }

    static RulePart pp(String content) {
        return new RulePart(RuleType.PUNCTUATION, content);
    }
    static RulePart pY4() {
        return new RulePart(RuleType.Y, 4);
    }
    static RulePart pY2() {
        return new RulePart(RuleType.Y, 2);
    }
    static RulePart pM() {
        return new RulePart(RuleType.M, (Integer)null);
    }
    static RulePart pD() {
        return new RulePart(RuleType.D, (Integer)null);
    }

    private final RulePart[] partsInOrder;
    protected AnyYMDPatternExtractor(String name, RulePart... partsInOrder) {
        super(name, reconstructPattern(partsInOrder), null);
        this.partsInOrder = partsInOrder;
    }

    private static String reconstructPattern(RulePart[] partsInOrder) {
        StringBuilder sb = new StringBuilder("(?:^|.*?\\D)");
        for (RulePart part : partsInOrder) {
            if (part.type == RuleType.PUNCTUATION) {
                if (".".equals(part.content)) {
                    sb.append("\\.");
                } else {
                    sb.append(part.content);
                }
            } else {
                if (part.type == RuleType.Y) {
                    if (part.length == null) {
                        throw new IllegalArgumentException("Year part must have length");
                    }
                    sb.append("(?<year>\\d{").append(part.length).append("})");
                } else if (part.type == RuleType.M) {
                    sb.append("(?<month>0?[1-9]|1[0-2])");
                } else if (part.type == RuleType.D) {
                    sb.append("(?<day>0?[1-9]|[12][0-9]|3[01])");
                }
            }
        }
        sb.append("(?:$|\\D.*?)");
        return sb.toString();
    }

    @Override
    public PartExtractorResult extract(String source, List<Part> parts, int i) {
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            PartExtractorResult result = new PartExtractorResult(name);
            result.found = true;
            result.start = matcher.start(findFirstGroupName());
            result.end = matcher.end(findLastGroupName());
            result.formatterPatterns = getPatterns(matcher);
            return result;
        }
        return new PartExtractorResult(name);
    }

    private String findLastGroupName() {
        List<RulePart> list = new ArrayList<>();
        Collections.addAll(list, partsInOrder);
        Collections.reverse(list);
        Optional<RulePart> first = list
                .stream()
                .filter(p -> p.type != RuleType.PUNCTUATION)
                .findFirst();
        return getNoGroupNameFound(first);
    }

    private String findFirstGroupName() {
        Optional<RulePart> first = Arrays.stream(partsInOrder)
                .filter(p -> p.type != RuleType.PUNCTUATION)
                .findFirst();
        return getNoGroupNameFound(first);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static String getNoGroupNameFound(Optional<RulePart> first) {
        return first.map(e -> {
                    switch (e.type) {
                        case Y:
                            return "year";
                        case M:
                            return "month";
                        case D:
                        default:
                            return "day";
                    }
                }).map(String::toLowerCase)
                .orElseThrow(() -> new IllegalArgumentException("No group name found"));
    }

    protected List<String> getPatterns(Matcher matcher) {
        List<String> start = new ArrayList<>();
        for (RulePart part : partsInOrder) {
            start = appendToExisting(start, () -> {
                if (part.type == RuleType.Y) {
                    if (matcher.group("year").length() == 2) {
                        return newList("yy");
                    } else {
                        return newList("yyyy");
                    }
                } else if (part.type == RuleType.M) {
                    if (matcher.group("month").length() == 1) {
                        return newList("M", "MM");
                    } else {
                        return newList("MM", "M");
                    }
                } else if (part.type == RuleType.D) {
                    if (matcher.group("day").length() == 1) {
                        return newList("d", "dd");
                    } else {
                        return newList("dd", "d");
                    }
                } else if (part.type == RuleType.PUNCTUATION) {
                    return Collections.singletonList(part.content);
                }
                return Collections.singletonList("");
            });
        }
        return start;
    }

}
