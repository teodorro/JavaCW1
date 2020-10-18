import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class Server {
    public static final int MAX_CLIENTS = 10;

    private String hostname = "localhost";
    private Set<String> clientNames = new HashSet<>();
    private PriorityBlockingQueue<String> clients = new PriorityBlockingQueue<>();
    private ExecutorService es = Executors.newFixedThreadPool(MAX_CLIENTS + 1);



    public static void main(String[] args) {
        new Server().start();
    }

    private void start() {
        int port = getPort();
        setUpChannel(port);
    }


    private int getPort(){
        Settings settings = new SettingsJson();
        settings.readSettings(SettingsJson.DEFAULT_FILENAME);
        return settings.getPort();
    }

    private void setUpChannel(int port){
        try {
            final ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(hostname, port));
            while (true) {
                try (SocketChannel socketChannel = serverChannel.accept()) {

                    continuousTransferMessages(socketChannel);

                } catch (IOException err) {
                    System.out.println(err.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void continuousTransferMessages(SocketChannel socketChannel) throws IOException {
        final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
        try (Scanner scanner = new Scanner(System.in)) {
            while (socketChannel.isConnected()) {
                System.out.println("write smth");
                String msg1 = scanner.nextLine();
                socketChannel.write(ByteBuffer.wrap(msg1.getBytes(StandardCharsets.UTF_8)));
//                int bytesCount = socketChannel.read(inputBuffer);
//                if (bytesCount == -1) break;
//                final String msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                inputBuffer.clear();
//
//                System.out.println(msg);
//                socketChannel.write(ByteBuffer.wrap(("Server: " + msg).getBytes(StandardCharsets.UTF_8)));
            }
        }
    }


}
