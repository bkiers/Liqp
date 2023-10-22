package liqp.antlr;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFSNameResolver implements NameResolver {
    public static String DEFAULT_EXTENSION = ".liquid";
    private final String root;

    public LocalFSNameResolver(String root) {
        this.root = root;
    }

    @Override
    public CharStreamWithLocation resolve(String name) throws IOException {
        Path directPath = Paths.get(name);
        if (directPath.isAbsolute()) {
            return new CharStreamWithLocation(directPath.toAbsolutePath());
        }
        String extension = DEFAULT_EXTENSION;
        if (name.indexOf('.') > 0) {
            extension = "";
        }
        name = name + extension;
        Path path = Paths.get(root, name);
        return new CharStreamWithLocation(path.toAbsolutePath());
    }
}
