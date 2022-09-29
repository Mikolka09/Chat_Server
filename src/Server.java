import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    static List<MyChannel> channels = new ArrayList<>();

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(8888);
        while (true){
            Socket client = server.accept();
            MyChannel chanel = new MyChannel(client);
            channels.add(chanel);
            new Thread(chanel).start();
        }
    }
}
