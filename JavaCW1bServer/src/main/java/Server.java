import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server {

    private final static String LOCALHOST = "localhost";
    private final static String FILELOG = "file.log";
    private Set<String> clientNames = new HashSet<>();
    private ServerSocketChannel serverChannel;
    private List<ClientHandler> clients = new ArrayList<>();
    private Logger logger = new Logger(FILELOG);



    public static void main(String[] args) {
        new Server().start();
    }

    private void start() {
        int port = getPort();
        openChannel(LOCALHOST, port);
        while (true) {
            try {
                SocketChannel socketChannel = serverChannel.accept();
                ClientHandler clientHandler = new ClientHandler(socketChannel, clients, logger);
                clientHandler.start();
                clients.add(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getPort(){
        Settings settings = new SettingsJson();
        settings.readSettings(SettingsJson.DEFAULT_FILENAME);
        return settings.getPort();
    }

    private void openChannel(String ip, int port)  {
        serverChannel = null;
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
