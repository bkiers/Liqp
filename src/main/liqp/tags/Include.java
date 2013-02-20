package liqp.tags;

import liqp.Template;
import liqp.nodes.LNode;

import java.io.File;
import java.util.Map;

class Include extends Tag {

    public static File snippetsFolder = new File("snippets");
    public static String extension = ".liquid";

    /*
     * temporarily disable tag processing to avoid syntax conflicts.
     */
    @Override
    public Object render(Map<String, Object> context, LNode... nodes) {

        try {
            String fileNameWithoutExt = String.valueOf(nodes[0].render(context));

            Template include = Template.parse(new File(snippetsFolder, fileNameWithoutExt + extension));

            // check if there's a optional "with expression"
            if(nodes.length > 1) {
                Object value = nodes[1].render(context);
                context.put(fileNameWithoutExt, value);
            }

            return include.render(context);

        } catch(Exception e) {
            return "";
        }
    }
}
