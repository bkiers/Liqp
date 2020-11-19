package liqp.temprunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Small helper class the create tmp folder for includes, and create includes files in it.
 * Also change the "user.dir" property to that new folder, like the program running within that folder.
 * Calling {@link #clean()} method will delete tmp files and restore "user.dir".
 * Works only in case of calling it public methods, so its safe to use it in Before/After
 */
public class IncludeContext {

    Path tmpDir;
    String oldUserDir;

    IncludeContext() { }

    public File getTmpDir() {
        return tmpDir.toFile();
    }
    public File writeFile(String filename, String content) throws IOException {
        Path includePath = tmpDir.resolve(filename);
        if (!tmpDir.equals(includePath.getParent())) {
            Files.createDirectories(includePath.getParent());
        }
        Files.createFile(includePath);
        Files.write(includePath, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return includePath.toFile();
    }

    protected void clean() throws IOException {
        if (tmpDir == null) {
            return;
        }
        Files.walkFileTree(tmpDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        System.setProperty("user.dir", oldUserDir);
    }
}
