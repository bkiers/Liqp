package liqp.tags;

import java.io.File;

import liqp.Template;
import liqp.TemplateContext;
import liqp.nodes.LNode;

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
                includeResourceFile = new File(context.parseSettings.flavor.snippetsFolderName,
                        includeResource + extension);
            }

            Template template;
            if (context.getParser().isLegacyMode()) {
                template = Template.parse(includeResourceFile, context.getParseSettings(), context
                        .getRenderSettings());
            } else {
                template = context.getParser().parse(includeResourceFile);
            }

            // check if there's a optional "with expression"
            if (nodes.length > 1) {
                Object value = nodes[1].render(context);
                context.put(includeResource, value);
            }

            return template.renderUnguarded(context);

        } catch (Exception e) {
            if (context.renderSettings.showExceptionsFromInclude) {
                throw new RuntimeException("problem with evaluating include", e);
            } else {
                return "";
            }
        }
    }
}
