package liqp.tags;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import liqp.Template;
import liqp.TemplateContext;
import liqp.nodes.LNode;
import org.antlr.v4.runtime.CharStream;

public class Include extends Tag {

    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        try {
            String includeResource = super.asString(nodes[0].render(context), context);

            CharStream source = context.getParser().nameResolver.resolve(includeResource);

            Template template = context.getParser().parse(source);

            Map<String, Object> variables = new HashMap<String, Object>();

            if (nodes.length > 1) {
                if (context.getParser().liquidStyleInclude) {
                    // check if there's a optional "with expression"
                    Object value = nodes[1].render(context);
                    context.put(includeResource, value);
                } else {
                    // Jekyll-style variable assignments
                    Map<String, Object> includeMap = new HashMap<>();
                    variables.put("include", includeMap);
                    for (int i = 1, n = nodes.length; i < n; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> var = (Map<String, Object>) nodes[i].render(context);
                        includeMap.putAll(var);
                    }
                }
            }

            return template.renderToObjectUnguarded(variables, context, true);
        } catch (Exception e) {
            if (context.getParser().showExceptionsFromInclude) {
                throw new RuntimeException("problem with evaluating include", e);
            } else {
                return "";
            }
        }
    }
}
