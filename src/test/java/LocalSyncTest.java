import client.PauseResume;
import core.FileUtils;
import core.Progress;
import datasource.base.IFile;
import datasource.local.LocalFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class LocalSyncTest {

    @Test
    public void testCopyAllFiles() {
        TestUtils.clearDirectory(TestUtils.SOURCE_DIR);
        TestUtils.clearDirectory(TestUtils.TARGET_DIR);
        TestUtils.copyDirectory(TestUtils.SAMPLE_DIR, TestUtils.SOURCE_DIR);

        Assertions.assertTrue(syncronzieDirs());
    }

    @Test
    public void testDeleteAllFiles() {
        TestUtils.clearDirectory(TestUtils.SOURCE_DIR);
        TestUtils.clearDirectory(TestUtils.TARGET_DIR);
        TestUtils.copyDirectory(TestUtils.SAMPLE_DIR, TestUtils.TARGET_DIR);

        Assertions.assertTrue(syncronzieDirs());
    }

    @Test
    public void testEmptyDirs() {
        TestUtils.clearDirectory(TestUtils.SOURCE_DIR);
        TestUtils.clearDirectory(TestUtils.TARGET_DIR);

        Assertions.assertTrue(syncronzieDirs());
    }

    /**
     * Sync for delete files and subsequent copy
     */
    @Test
    public void testComplexSync() {
        TestUtils.clearDirectory(TestUtils.SOURCE_DIR);
        TestUtils.clearDirectory(TestUtils.TARGET_DIR);
        TestUtils.copyDirectory(TestUtils.SAMPLE_DIR, TestUtils.SOURCE_DIR); //should be copied
        TestUtils.copyDirectory(TestUtils.EXCESS_DIR, TestUtils.TARGET_DIR); //should be deleted

        Assertions.assertTrue(syncronzieDirs());
    }

    private boolean syncronzieDirs() {
        var pause = new PauseResume();
        var progress = new Progress(pause);
        IFile source = new LocalFile(new File(TestUtils.SOURCE_DIR), progress);
        IFile target = new LocalFile(new File(TestUtils.TARGET_DIR), progress);
        progress.setSource(source);
        progress.setTarget(target);

        pause.changePause();
        FileUtils.doSync(source, target, progress, pause, TestUtils.SYNC_TYPE);
        pause.changePause();
        boolean isEqual = true;
        try {
            isEqual = DirectoryComparator.directoryContentEquals(
                    Path.of(TestUtils.SOURCE_DIR), Path.of(TestUtils.TARGET_DIR));
        } catch(IOException e) {
            log.error(e.getMessage());
        }
        return isEqual;
    }
}