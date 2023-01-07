package core;

import fileManagement.IFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SyncImpl implements Sync {

    private static boolean globalPause = false;

    @Override
    public synchronized void synchronize(IFile source, IFile target, Progress progress) throws IOException {
        if (source.isDirectory()) {
            validateTarget(source, target, progress);
            String[] sources = source.list();
            Set<String> srcNames = new HashSet<>(Arrays.asList(sources));
            String[] targets = target.list();

            //delete files not present in source
            for (String fileName : targets) {
                if (!srcNames.contains(fileName)) {
                    target.getChild(fileName).delete();
                } else {
                    progress.incrementProgress();
                }
            }
            log.debug("All files not present is source are deleted");
            //copy each file from source
            for (String fileName : sources) {
                IFile sourceFile = source.getChild(fileName);
                IFile targetFile = target.getChild(fileName);
                log.info("Sync  called for target file " + targetFile.getCanonicalPath());
                synchronize(sourceFile, targetFile, progress);
            }
        } else {
            if (target.exists() && target.isDirectory()) {
                target.delete();
            }
            if (target.exists()) {
                long sts = source.lastModified() / 1000;
                long dts = target.lastModified();
                //do not copy if same timestamp and same length
                if (sts == 0 || sts != dts || source.length() != target.length()) {
                    target.copyFile(source);
                } else {
                    progress.incrementProgress();
                }
            } else {
                target.copyFile(source);
            }
        }
        log.info(String.format("Sync successfully finished for source: %s, target: %s",
                source.getCanonicalPath(), target.getCanonicalPath()));
    }

    private void validateTarget(IFile source, IFile target, Progress progress) throws IOException {
        if (!target.exists()) {
            if (!target.mkdirs()) {
                throw new IOException("Could not create path " + target);
            }
        } else if (!target.isDirectory()) {
            throw new IOException(
                    "Source and Destination not of the same type:"
                            + source.getCanonicalPath() + " , " + target.getCanonicalPath()
            );
        }
            progress.incrementProgress();
    }

    @Override
    public void setPause(boolean isPaused) {
        globalPause = isPaused;
    }
}