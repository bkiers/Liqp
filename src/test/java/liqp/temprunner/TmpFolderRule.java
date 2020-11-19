package liqp.temprunner;

import java.nio.file.Files;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * jUnit rule that provide tests running in liqp.temprunner.IncludeContext (in tmp folder).
 *
 */
public class TmpFolderRule implements TestRule {
    private final ThreadLocal<IncludeContext> localContext = new ThreadLocal<IncludeContext>() {
        @Override
        protected IncludeContext initialValue() {
            return new IncludeContext();
        }
    };
    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base, description);
    }

    public IncludeContext getContext() {
        return localContext.get();
    }

    private Statement statement(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (description.getAnnotation(InTmpFolder.class) == null) {
                    base.evaluate();
                    return;
                }

                IncludeContext context = localContext.get();
                try {
                    context.tmpDir = Files.createTempDirectory("liqp").toAbsolutePath();
                    context.oldUserDir = System.getProperty("user.dir");
                    System.setProperty("user.dir", context.getTmpDir().getAbsolutePath());
                    base.evaluate();
                } finally {
                    context.clean();
                }
            }
        };
    }

}