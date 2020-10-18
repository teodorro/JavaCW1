import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private String hostname = "localhost";

    public static void main(String[] args) {
        new Client().start();
    }

    public void start(){
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
            InetSocketAddress socketAddress = new InetSocketAddress("localhost", port);
            final SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(socketAddress);
            try (Scanner scanner = new Scanner(System.in)) {
                final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                while (true) {
//                    String msg = scanner.nextLine();
//                    socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                    int bytesCount = socketChannel.read(inputBuffer);
                    System.out.println(new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8).trim());
                    inputBuffer.clear();
                }

            } finally {
                socketChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
