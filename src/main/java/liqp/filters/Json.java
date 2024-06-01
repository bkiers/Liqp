package liqp.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import liqp.TemplateContext;

public class Json extends Filter {
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        ObjectMapper mapper = context.parseSettings.mapper;

        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
