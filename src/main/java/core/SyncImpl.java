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

    /**
     * Recursive calls from root source and root target
     * Steps:
     * 1. Validates target directory structure: create directories if is not present
     * 2. delete target files, which is not present in source
     * 3. copy files from source to target if lastModified field is different
     * Progress tracks like: processed files / total files in source and target
     *
     * @param source root directory at first call and subfiles at subsequent calls
     * @param target root directory at first call and subfiles at subsequent calls
     */
    @Override
    public synchronized void synchronize(IFile source, IFile target, Progress progress) throws IOException {
        log.debug("Sync started for source " + source.getCanonicalPath());
        if (source.isDirectory()) {
            validateTarget(source, target, progress);
            String[] sources = source.list();
            Set<String> srcNames = new HashSet<>();
            if(Arrays.asList(sources) != null) {
                srcNames = new HashSet<>(Arrays.asList(sources));
            }
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
                log.debug(String.format("Sync called for: %s and %s",
                        sourceFile.getCanonicalPath(), target.getCanonicalPath()));
                synchronize(sourceFile, targetFile, progress);
            }
        } else {
            if (target.exists() && target.isDirectory()) {
                target.delete();
            }
            if (target.exists()) {
                long sourceLM = source.getLastModified();
                long targetLM = target.getLastModified();
                //do not copy if same timestamp and same length
                if (sourceLM != targetLM || source.length() != target.length()) {
                    target.copyFile(source);
                } else {
                    progress.incrementProgress();
                }
            } else {
                target.copyFile(source);
            }
        }
        log.debug(String.format("Sync successfully finished for source: %s, target: %s",
                source.getCanonicalPath(), target.getCanonicalPath()));
    }

    /**
     * Validates target directory structure: create directories if is not present
     */
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