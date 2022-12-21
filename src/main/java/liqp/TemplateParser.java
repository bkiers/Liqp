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
    /**
     * Returns a {@link TemplateParser} configured with all default settings for "Liquid" flavor.
     */
    public static final TemplateParser DEFAULT = Flavor.LIQUID.defaultParser();
    public static final TemplateParser DEFAULT_JEKYLL = Flavor.JEKYLL.defaultParser();

    private final ParseSettings parseSettings;
    private final RenderSettings renderSettings;
    private final ProtectionSettings protectionSettings;

    public static class Builder {
        private ParseSettings parseSettings = ParseSettings.DEFAULT;
        private RenderSettings renderSettings = RenderSettings.DEFAULT;
        private ProtectionSettings protectionSettings = ProtectionSettings.DEFAULT;

        public Builder() {
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

        public TemplateParser build() {
            return new TemplateParser(this.parseSettings, this.renderSettings,
                    this.protectionSettings);
        }
    }

    TemplateParser(ParseSettings parseSettings, RenderSettings renderSettings,
            ProtectionSettings protectionSettings) {
        this.parseSettings = parseSettings;
        this.renderSettings = renderSettings;
        this.protectionSettings = protectionSettings;
    }

    public Template parse(File file) throws IOException {
        return new Template.BuiltTemplate(this, CharStreams.fromPath(file.toPath()));
    }

    public Template parse(String input) {
        return new Template.BuiltTemplate(this, CharStreams.fromString(input));
    }

    public Template parse(InputStream input) throws IOException {
        return new Template.BuiltTemplate(this, CharStreams.fromStream(input));
    }

    public Template parse(Reader reader) throws IOException {
        return new Template.BuiltTemplate(this, CharStreams.fromReader(reader));
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

}
