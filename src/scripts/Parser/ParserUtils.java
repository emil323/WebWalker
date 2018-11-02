package scripts.Parser;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ParserUtils {

    public static Stream<Path> findFiles(String regex) throws IOException {
        Stream<Path> pathStream = Files.find(FileSystems.getDefault().getPath("data"), Integer.MAX_VALUE,
                (path, basicFileAttributes) -> path.toFile().getName().endsWith(regex));
        return pathStream;
    }
}
