import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server {

    private final static String LOCALHOST = "localhost";
    private Set<String> clientNames = new HashSet<>();
    private ServerSocketChannel serverChannel;
    private List<ClientHandler> clients = new ArrayList<>();



    public static void main(String[] args) {
        new Server().start();
    }

    private void start() {
        int port = getPort();
        openChannel(LOCALHOST, port);
        while (true) {
            try {
                SocketChannel sc = serverChannel.accept();
                ClientHandler clientHandler = new ClientHandler(sc, clients);
                clientHandler.start();
                clients.add(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println( e.getMessage());
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
            System.out.println(e.getMessage());
        }
    }

}
