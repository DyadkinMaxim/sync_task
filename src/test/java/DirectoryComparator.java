import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents logic for directory comparison
 */
public class DirectoryComparator {
    public static boolean directoryContentEquals(Path dir1, Path dir2)
            throws IOException {
        boolean dir1Exists = Files.exists(dir1) && Files.isDirectory(dir1);
        boolean dir2Exists = Files.exists(dir2) && Files.isDirectory(dir2);

        if (dir1Exists && dir2Exists) {
            HashMap<Path, Path> dir1Paths = new HashMap<>();
            HashMap<Path, Path> dir2Paths = new HashMap<>();

            for (Path p : listPaths(dir1)) {
                dir1Paths.put(dir1.relativize(p), p);
            }

            for (Path p : listPaths(dir2)) {
                dir2Paths.put(dir2.relativize(p), p);
            }
            if (dir1Paths.size() != dir2Paths.size()) {
                return false;
            }

            for (Map.Entry<Path, Path> pathEntry : dir1Paths.entrySet()) {
                Path relativePath = pathEntry.getKey();
                Path absolutePath = pathEntry.getValue();
                if (!dir2Paths.containsKey(relativePath)) {
                    return false;
                } else {
                    if (!contentEquals(absolutePath,
                            dir2Paths.get(relativePath))) {
                        System.out.println(absolutePath);
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Recursively finds all files with given extensions in the given directory
     * and all of its sub-directories.
     */
    public static List<Path> listPaths(Path file, String... extensions)
            throws IOException {
        if (file == null) {
            return null;
        }

        List<Path> paths = new ArrayList<>();
        listPaths(file, paths, extensions);

        return paths;
    }

    /**
     * Recursively finds all paths with given extensions in the given directory
     * and all of its sub-directories.
     */
    protected static void listPaths(Path path, List<Path> result,
                                    String... extensions) throws IOException {
        if (path == null) {
            return;
        }

        if (Files.isReadable(path)) {
            if (Files.isDirectory(path)) {
                DirectoryStream<Path> directoryStream = Files
                        .newDirectoryStream(path);
                for (Path p : directoryStream) {
                    listPaths(p, result, extensions);
                }
                directoryStream.close();
            } else {
                String filename = path.getFileName().toString();
                if (extensions.length == 0) {
                    result.add(path);
                } else {
                    for (String extension : extensions) {
                        if (filename.toLowerCase().endsWith(extension)) {
                            result.add(path);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Compares the contents of the two given paths. If both paths don't exist,
     * the contents aren't equal and this method returns false.
     */
    public static boolean contentEquals(Path p1, Path p2)
            throws IOException {
        if (!Files.exists(p1) || !Files.exists(p2)) {
            return false;
        }

        if (Files.isDirectory(p1) && Files.isDirectory(p2)) {
            return directoryContentEquals(p1, p2);
        }

        if (p1.equals(p2)) {
            return true;
        }

        if (Files.size(p1) != Files.size(p2)) {
            return false;
        }

        InputStream in1 = null;
        InputStream in2 = null;
        try {
            in1 = Files.newInputStream(p1);
            in2 = Files.newInputStream(p2);

            int expectedByte = in1.read();
            while (expectedByte != -1) {
                if (expectedByte != in2.read()) {
                    return false;
                }
                expectedByte = in1.read();
            }
            return in2.read() == -1;
        } finally {
            if (in1 != null) {
                try {
                    in1.close();
                } catch (IOException e) {
                    return false;
                }
            }
            if (in2 != null) {
                try {
                    in2.close();
                } catch (IOException e) {
                    return false;
                }
            }
        }
    }
}