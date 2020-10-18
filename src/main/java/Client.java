import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private String LOCALHOST = "localhost";
    public ExecutorService es = Executors.newFixedThreadPool(3);
    private SocketChannel socketChannel;
    private ByteBuffer inputBuffer;
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Client().start();
    }

    public void start(){
        int port = getPort();
        openChannel(LOCALHOST, port);
        initOutStream();
        initInStream();
    }

    private int getPort(){
        Settings settings = new SettingsJson();
        settings.readSettings(SettingsJson.DEFAULT_FILENAME);
        return settings.getPort();
    }

    private void initInStream(){
        es.submit(() -> {
            while(socketChannel.isConnected()){
                readMessage();
            }
        });
    }

    private void initOutStream(){
        es.submit(() -> {
            while(socketChannel.isConnected()){
                sendMessage();
            }
        });
    }

    private void openChannel(String ip, int port) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
            socketChannel = SocketChannel.open();
            socketChannel.connect(socketAddress);
            inputBuffer = ByteBuffer.allocate(2 << 10);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void readMessage(){
        int bytesCount = 0;
        try {
            bytesCount = socketChannel.read(inputBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        System.out.println(new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8).trim());
        inputBuffer.clear();
    }

    private void sendMessage() {
        String msg = scanner.nextLine();
        if ("exit".equals(msg)){
            stopConnection();
        }
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void stopConnection() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
