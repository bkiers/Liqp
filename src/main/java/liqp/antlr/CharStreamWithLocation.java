package liqp.antlr;

import liqp.TemplateParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;
import java.nio.file.Path;

public class CharStreamWithLocation extends FilterCharStream {
    public final Path path;

    public CharStreamWithLocation(CharStream in) throws IOException {
        super(in);
        this.path = NameResolver.getLocationFromCharStream(in);
    }

    public CharStreamWithLocation(CharStream in, Path path) throws IOException {
        super(in);
        this.path = path;
    }

    public CharStreamWithLocation(Path path) throws IOException {
        super(CharStreams.fromPath(path));
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
