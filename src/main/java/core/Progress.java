package core;

import fileManagement.IFile;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Progress {
    private long totalFileNumber;
    private volatile AtomicLong processedFileNumber = new AtomicLong(-1); // to account source root directory

    private Progress(long sourceNumber, long targetNumber) {
        this.totalFileNumber = sourceNumber + targetNumber;
    }

    public static Progress initProgress(IFile source, IFile target) {
        var progress = new Progress(source.countAll(), target.countAll());
        return progress;
    }

    public String countProgress() {
        int progressValue;
        if (totalFileNumber == 0) {
            return "Empty source and target directories";
        } else {
            double doubleProgess = (double) processedFileNumber.longValue() / totalFileNumber;
            progressValue = (int) Math.round(doubleProgess * 100.0);

        }
        var message = "Progress is " + progressValue + "%";
        log.info(message);
        return message;
    }

    public void incrementProgress() {
        processedFileNumber = new AtomicLong(processedFileNumber.incrementAndGet());
        countProgress();
    }

}
