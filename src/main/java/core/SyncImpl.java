package core;

import datasource.base.IFile;
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
        log.debug("Sync started for source "  + source.getCanonicalPath());
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
                log.debug(String.format("Sync called for: ",
                        sourceFile.getCanonicalPath(), target.getCanonicalPath()));
                synchronize(sourceFile, targetFile, progress);
            }
        } else {
            if (target.exists() && target.isDirectory()) {
                target.delete();
            }
            if (target.exists()) {
                long sourceLM = source.lastModified();
                long targetLM = target.lastModified();
                //do not copy if same timestamp and same length
                if (sourceLM != targetLM || source.length() != target.length()) {
                    target.copyFile(source);
                    source.setLastModified(target.lastModified());
                    // todo local DB for
                } else {
                    progress.incrementProgress();
                }
            } else {
                target.copyFile(source);
                source.setLastModified(target.lastModified());
            }
        }
        log.debug(String.format("Sync successfully finished for source: %s, target: %s",
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