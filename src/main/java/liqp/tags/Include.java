package liqp.tags;

import liqp.Template;
import liqp.nodes.LNode;

import java.io.File;
import java.util.Map;

class Include extends Tag {

    public static String INCLUDES_DIRECTORY_KEY = "liqp@includes_directory";
    public static File DEFAULT_INCLUDES_DIRECTORY = new File("snippets");
    public static String DEFAULT_EXTENSION = ".liquid";

    @Override
    public Object render(Map<String, Object> context, LNode... nodes) {
        File includesDirectory = (File)context.get(INCLUDES_DIRECTORY_KEY);
        if(includesDirectory == null) {
            includesDirectory = DEFAULT_INCLUDES_DIRECTORY;
        }
        try {
            String includeResource = super.asString(nodes[0].render(context));
            String extension = DEFAULT_EXTENSION;
            if(includeResource.indexOf('.') > 0) {
                extension = "";
            }
            File includeResourceFile = new File(includesDirectory, includeResource + extension);
            Template include = Template.parse(includeResourceFile);

            // check if there's a optional "with expression"
            if(nodes.length > 1) {
                Object value = nodes[1].render(context);
                context.put(includeResource, value);
            }

            return include.render(context);

        } catch(Exception e) {
            return "";
        }
    }
}
