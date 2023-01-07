package datasource.base;

import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

    public static synchronized long countAll(IFile file) {
        var counter = new AtomicLong();
        getFilesCount(file, counter);
        log.debug(String.format("Counted %s files in %s", counter, file.getCanonicalPath()));
        return counter.longValue();
    }

    private static void getFilesCount(IFile file, AtomicLong counter) {
        String[] files = file.list();
        if (files != null) {
            for (var child : files) {
                IFile next = file.getChild(child);
                counter.incrementAndGet();
                if (next.isDirectory()) {
                    getFilesCount(next, counter);
                }
            }
        }
    }
}
