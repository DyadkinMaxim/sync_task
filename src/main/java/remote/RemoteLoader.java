package remote;

public interface RemoteLoader {

    void execute() throws Exception;

    void download() throws Exception;

    void upload() throws Exception;
}
