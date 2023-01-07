package core;

import datasource.base.IFile;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

/** Represents progress tracking
 */
@Slf4j
public class Progress {
    private IFile source;
    private IFile target;
    private long totalFileNumber;
    private volatile AtomicLong processedFileNumber;
    private volatile int progressValue;

    public Progress(IFile source, IFile target) {
        this.source = source;
        this.target = target;
        resetProgress();
    }

    /**
     * Returns current progress value
     *
     * @return      String with progress value
     */
    public String countProgress() {
        if (totalFileNumber == 0) {
            return "Empty source and target directories";
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
        if(progressValue == 100) {
            resetProgress();
        }
    }

    private   void resetProgress() {
        processedFileNumber = new AtomicLong(-1); // to account source root directory
        totalFileNumber = source.countAll() + target.countAll();
        log.info("Progress reset is done");
    }
}
