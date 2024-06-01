package liqp.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.TemplateContext;
import liqp.TemplateParser;

public class Json extends Filter {
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        ObjectMapper mapper = context.getParser().mapper;

        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            context.addError(e);
            if (context.getErrorMode() == TemplateParser.ErrorMode.STRICT) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return value;
        }
    }
}
