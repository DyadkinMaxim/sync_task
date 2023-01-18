import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestUtils {
    public static final String SOURCE_DIR = "src/test/resources/source";
    public static final String TARGET_DIR = "src/test/resources/targetdir";
    public static final String SAMPLE_DIR = "src/test/resources/sample";
    public static final String EXCESS_DIR = "src/test/resources/sample/subdir1/subdir2";
    public static final String SYNC_TYPE = "TEST";

    public static void clearDirectory(String dirPath) {
        try {
            org.apache.commons.io.FileUtils.cleanDirectory(new File(dirPath));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void copyDirectory(String sourcePath, String targetPath) {
        try {
            org.apache.commons.io.FileUtils.copyDirectory(
                    new File(sourcePath), new File(targetPath));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
