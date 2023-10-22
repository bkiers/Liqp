package liqp.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.IntStream;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@FunctionalInterface
public interface NameResolver {
    CharStreamWithLocation resolve(String name) throws IOException;

    static Path getLocationFromCharStream(CharStream input) {
        String path = IntStream.UNKNOWN_SOURCE_NAME.equals(input.getSourceName()) ? null : input.getSourceName();
        if (path != null) {
            try {
                return Paths.get(path).toAbsolutePath();
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
