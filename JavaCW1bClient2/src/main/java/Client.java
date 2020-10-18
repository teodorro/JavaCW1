import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private final String EXIT = "/exit";
    private final String LOCALHOST = "localhost";
    private final String NAME_ACCEPTED = "Name accepted";
    private final static String FILELOG = "file.log";

    private ExecutorService es = Executors.newFixedThreadPool(3);
    private SocketChannel socketChannel;
    private ByteBuffer inputBuffer;
    private Scanner scanner = new Scanner(System.in);
    private boolean isNameAccepted = false;
    private String name;
    private Logger logger = new Logger(FILELOG);


    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        int port = getPort();
        openChannel(LOCALHOST, port);
        initOutStream();
        initInStream();
    }

    private int getPort() {
        Settings settings = new SettingsJson();
        settings.readSettings(SettingsJson.DEFAULT_FILENAME);
        return settings.getPort();
    }

    private void initInStream() {
        es.submit(() -> {
            while (socketChannel.isConnected()) {
                boolean res = readMessage();
                if (!res)
                    break;
            }
            System.out.println("client end in");
        });
    }

    private void initOutStream() {
        es.submit(() -> {
            System.out.println("Enter your username:");
            while (socketChannel.isConnected()) {
                boolean res = sendMessage();
                if (!res)
                    break;
            }
            System.out.println("client end out");
        });
    }

    private void openChannel(String ip, int port) {
        InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(socketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputBuffer = ByteBuffer.allocate(2 << 10);
    }

    private boolean readMessage() {
        int bytesCount = 0;
        try {
            bytesCount = socketChannel.read(inputBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            inputBuffer.clear();
            return false;
        }
        String msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8).trim();
        System.out.println(msg);
        logger.log(msg);

        if (NAME_ACCEPTED.equals(msg)) {
            isNameAccepted = true;
        }
        inputBuffer.clear();
        return true;
    }

    private boolean sendMessage() {
        String msg = scanner.nextLine();
        if (msg.isBlank())
            return true;
        if (EXIT.equals(msg)) {
            stopConnection();
        }

        if (isNameAccepted)
            msg = "[" + LocalDateTime.now().toLocalDate() + " " + LocalDateTime.now().withNano(0).toLocalTime() + "] " + name + ": " + msg;
        else
            name = msg;
        logger.log(msg);

        if (!socketChannel.isOpen())
            return false;
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void stopConnection() {
        isNameAccepted = false;
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
