import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class ClientHandler extends Thread {
    private SocketChannel socketChannel;
    private ByteBuffer inputBuffer;
    private List<ClientHandler> clients;
    public ExecutorService es = Executors.newFixedThreadPool(3);
    private PriorityBlockingQueue<String> messages = new PriorityBlockingQueue<>();

    public ClientHandler(SocketChannel socketChannel, List<ClientHandler> clients) {
        this.socketChannel = socketChannel;
        this.clients = clients;
        inputBuffer = ByteBuffer.allocate(2 << 10);
        initOutStream();
        initInStream();
    }


    public void initInStream(){
        es.submit(() -> {
            while(socketChannel.isConnected()){
                readMessage();
            }
        });
    }

    public void initOutStream(){
        es.submit(() -> {
            while(socketChannel.isConnected()){
                sendMessages();
            }
        });
    }

    public void readMessage(){
        int bytesCount = 0;
        try {
            bytesCount = socketChannel.read(inputBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        String msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8).trim();
        messages.add(msg);

        inputBuffer.clear();
    }

    public void sendMessages() {
        try {
            String msg = messages.take();
            clients.stream().filter(x -> x != this).forEach(client -> client.sendMessage(msg));
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(String message){
        try {
            if (message == null || message.isBlank())
                return;
            socketChannel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
