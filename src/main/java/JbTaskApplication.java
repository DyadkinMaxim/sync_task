import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

public class JbTaskApplication {

    private static final String loadDirectory = "C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\remote\\";
    private static final String sftpPath = "/test";
    private static final String sshHost = "34.71.206.155";
    private static final String sftpPort = "22";
    private static final String sshUser = "dyadkinm";
    private static final String sshPrivateKey = "C:\\Users\\Dyadkin Maxim\\.ssh\\id_ed25519";

    //ssh -i id_ed25519.pub dyadkinm@34.71.206.155

    public static void main(String[] args) {
//        File source = new File("C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\source");
//        TargetFile target = new LocalTargetFile(new File("C:\\Users\\Dyadkin Maxim\\Desktop\\jb_task\\target"));
//        LocalFileManager lfm = new LocalFileManager();
//        var localScheduler = new LocalScheduler();
//        localScheduler.localSchedule(source, target, lfm);
        try {
            sshConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sshConnect() throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        String username = sshUser;
        File privateKey = new File(sshPrivateKey);
        KeyProvider keys = client.loadKeys(privateKey.getPath());
        client.connect(sshHost);
        try {
            client.authPublickey(username, keys);
            final Session session = client.startSession();
            try {
                final Session.Command cmd = session.exec("true");
                cmd.join(1, TimeUnit.SECONDS);
                System.out.println("Ok");
            } finally {
                session.close();
            }
        } finally {
            client.disconnect();
        }
    }
}
