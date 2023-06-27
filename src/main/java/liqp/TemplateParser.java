package liqp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.antlr.v4.runtime.CharStreams;

import liqp.parser.Flavor;

/**
 * The new main entrance point of this library.
 */
public class TemplateParser {

    public static final TemplateParser DEFAULT_JEKYLL = Flavor.JEKYLL.defaultParser();
    public static final TemplateParser DEFAULT_LIQUID = Flavor.LIQUID.defaultParser();
    public static final TemplateParser DEFAULT_LIQP = Flavor.LIQP.defaultParser();
    /**
     * Returns a {@link TemplateParser} configured with all default settings for "Liqp" flavor.
     */
    public static final TemplateParser DEFAULT = DEFAULT_LIQP;

    /**
     * Equivalent of
     * <code>
     * Liquid::Template.error_mode = :strict # Raises a SyntaxError when invalid syntax is used
     * Liquid::Template.error_mode = :warn # Adds strict errors to template.errors but continues as normal
     * Liquid::Template.error_mode = :lax # The default mode, accepts almost anything.
     * </code>
     * where usage is like:
     * <code>
     *     template = Liquid::Template.parse('', error_mode: :warn )
     * </code>
     */
    public enum ErrorMode {
        strict,
        warn,
        lax
    }

    private final ParseSettings parseSettings;
    private final RenderSettings renderSettings;
    private final ProtectionSettings protectionSettings;
    private final ErrorMode errorMode;

    public static class Builder {
        private ParseSettings parseSettings = ParseSettings.DEFAULT;
        private RenderSettings renderSettings = RenderSettings.DEFAULT;
        private ProtectionSettings protectionSettings = ProtectionSettings.DEFAULT;
        private ErrorMode errorMode;

        public Builder() {
        }

        public Builder(TemplateParser parser) {
            parseSettings = parser.parseSettings;
            renderSettings = parser.renderSettings;
            protectionSettings = parser.protectionSettings;
            errorMode = parser.errorMode;
        }

        public Builder withParseSettings(ParseSettings s) {
            this.parseSettings = s;
            return this;
        }

        public Builder withRenderSettings(RenderSettings s) {
            this.renderSettings = s;
            return this;
        }

        public Builder withProtectionSettings(ProtectionSettings s) {
            this.protectionSettings = s;
            return this;
        }

        @SuppressWarnings("hiding")
        public Builder withErrorMode(ErrorMode errorMode) {
            this.errorMode = errorMode;
            return this;
        }


        public TemplateParser build() {
            if (this.errorMode == null) {
                // fallback to Flavor-default
                this.errorMode = this.parseSettings.flavor.getErrorMode();
            }
            return new TemplateParser(this.parseSettings, this.renderSettings,
                    this.protectionSettings, this.errorMode);
        }
    }

    TemplateParser(ParseSettings parseSettings, RenderSettings renderSettings,
                   ProtectionSettings protectionSettings, ErrorMode errorMode) {
        this.parseSettings = parseSettings;
        this.renderSettings = renderSettings;
        this.protectionSettings = protectionSettings;
        this.errorMode = errorMode;
    }

    public Template parse(File file) throws IOException {
        return new Template(this, CharStreams.fromPath(file.toPath()));
    }

    public Template parse(String input) {
        return new Template(this, CharStreams.fromString(input));
    }

    public Template parse(InputStream input) throws IOException {
        return new Template(this, CharStreams.fromStream(input));
    }

    public Template parse(Reader reader) throws IOException {
        return new Template(this, CharStreams.fromReader(reader));
    }

    public ParseSettings getParseSettings() {
        return parseSettings;
    }

    public RenderSettings getRenderSettings() {
        return renderSettings;
    }

    public ProtectionSettings getProtectionSettings() {
        return protectionSettings;
    }

    public ErrorMode getErrorMode() {
        return errorMode;
    }
}
