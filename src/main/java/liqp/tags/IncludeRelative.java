package liqp.tags;

import liqp.TemplateContext;
import liqp.antlr.CharStreamWithLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <pre>
 *   class IncludeRelativeTag < IncludeTag
 *       def tag_includes_dirs(context)
 *         Array(page_path(context)).freeze
 *       end
 *
 *       def page_path(context)
 *         page, site = context.registers.values_at(:page, :site)
 *         return site.source unless page
 *
 *         site.in_source_dir File.dirname(resource_path(page, site))
 *       end
 *
 *       private
 *
 *       def resource_path(page, site)
 *         path = page["path"]
 *         path = File.join(site.config["collections_dir"], path) if page["collection"]
 *         path.delete_suffix("/#excerpt")
 *       end
 *     end
 * </pre>
 */
public class IncludeRelative extends Include {

    public IncludeRelative() {
        super("include_relative");
    }

    @Override
    protected CharStreamWithLocation detectSource(TemplateContext context, String includeResource) throws IOException {
        Path rootPath = context.getRootFolder();
        if (rootPath == null) {
            rootPath = Paths.get(".").toAbsolutePath();
        }
        Path includePath = rootPath.resolve(includeResource);
        return new CharStreamWithLocation(includePath);
    }
}
