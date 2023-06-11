package liqp.tags;

import liqp.Template;
import liqp.TemplateContext;
import liqp.nodes.LNode;
import liqp.parser.Flavor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Include extends Tag {

    public static final String INCLUDES_DIRECTORY_KEY = "liqp@includes_directory";
    public static String DEFAULT_EXTENSION = ".liquid";

    @SuppressWarnings("deprecation")
    @Override
    public Object render(TemplateContext context, LNode... nodes) {

        try {
            String includeResource = super.asString(nodes[0].render(context), context);
            String extension = DEFAULT_EXTENSION;
            if (includeResource.indexOf('.') > 0) {
                extension = "";
            }
            File includeResourceFile;
            String includesDirectory = (String) context.get(INCLUDES_DIRECTORY_KEY);

            if (includesDirectory != null) {
                includeResourceFile = new File(includesDirectory, includeResource + extension);
            } else {
                includeResourceFile = new File(context.getParseSettings().flavor.snippetsFolderName,
                        includeResource + extension);
            }

            Template template;
            template = context.getParser().parse(includeResourceFile);

            Map<String, Object> variables = new HashMap<String, Object>();

            if (nodes.length > 1) {
                if (context.getParseSettings().liquidStyleInclude) {
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
            if (context.getRenderSettings().showExceptionsFromInclude) {
                throw new RuntimeException("problem with evaluating include", e);
            } else {
                return "";
            }
        }
    }
}
