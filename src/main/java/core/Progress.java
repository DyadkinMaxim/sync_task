package core;

import datasource.base.IFile;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** Represents progress tracking
 */
@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
public class Progress {
    private final IFile source;
    private final IFile target;
    private long totalFileNumber;
    private volatile AtomicLong processedFileNumber;
    private volatile int progressValue;

    public void initProgress(IFile source, IFile target) {
        processedFileNumber = new AtomicLong(-1); // to account source root directory
        totalFileNumber = source.countAll() + target.countAll();
        log.info("Progress started");
    }

    /**
     * Returns current progress value
     *
     * @return      String with progress value
     */
    public String countProgress() {
        if (totalFileNumber == 0) {
            var emptyMsg = "Empty source and target directories";
            log.info(emptyMsg);
            return emptyMsg;
        } else {
            double doubleProgess = (double) this.processedFileNumber.longValue() / this.totalFileNumber;
            progressValue = (int) Math.round(doubleProgess * 100.0);
        }
        var message = "Progress is " + progressValue + "%";
        log.info(message);
        return message;
    }

    public synchronized void incrementProgress() {
        processedFileNumber.incrementAndGet();
        countProgress();
    }

    public void resetProgress() {
        //Recursive folders creation/deletion are complex to track -> fix here
        if(processedFileNumber.longValue() != totalFileNumber) {
            processedFileNumber.set(totalFileNumber);
            countProgress();
        }
        processedFileNumber = new AtomicLong(-1); // to account source root directory
        log.info("Sync finished :)");
        log.info("Progress reset is done");
    }
}
