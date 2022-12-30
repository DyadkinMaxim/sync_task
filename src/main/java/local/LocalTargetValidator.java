package local;

import fileManagement.FileManager;
import fileManagement.TargetFile;
import java.io.File;
import java.time.Instant;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LocalTargetValidator implements Runnable {

    private final File source;
    private final TargetFile target;
    private final FileManager fm;

    @Override
    public void run() {
        System.out.println(Instant.now() + " " + target.lastModified());
        if (target.lastModified() > 30000) {
            var sync = new SyncImpl();
            try {
                sync.synchronize(source, target, fm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
