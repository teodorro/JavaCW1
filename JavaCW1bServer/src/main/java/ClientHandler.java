import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class ClientHandler extends Thread {
    private final String NAME_ACCEPTED = "Name accepted";
    private SocketChannel socketChannel;
    private ByteBuffer inputBuffer;
    private List<ClientHandler> clientHandlers;
    public ExecutorService es = Executors.newFixedThreadPool(3);
    private PriorityBlockingQueue<String> messages = new PriorityBlockingQueue<>();
    private String clientName = "";
    private Logger logger;


    public ClientHandler(SocketChannel socketChannel, List<ClientHandler> clientHandlers, Logger logger) {
        this.socketChannel = socketChannel;
        this.clientHandlers = clientHandlers;
        this.logger = logger;

        inputBuffer = ByteBuffer.allocate(2 << 10);
        initOutStream();
        initInStream();
    }


    private void initInStream(){
        es.submit(() -> {
            while(socketChannel.isConnected()){
                boolean res = readMessage();
                if (!res)
                    break;
            }
            System.out.println("handler end in");
        });
    }

    private void initOutStream(){
        es.submit(() -> {
            while(socketChannel.isConnected()){
                boolean res = sendMessages();
                if (!res)
                    break;
            }
            System.out.println("handler end out");
        });
    }

    private boolean readMessage(){
        int bytesCount = 0;
        if (!socketChannel.isOpen())
            return false;
        try {
            bytesCount = socketChannel.read(inputBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            inputBuffer.clear();
            return false;
        }
        String msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8).trim();
        if (clientName.isBlank()){
            if (clientHandlers.stream().noneMatch(x -> x.clientName.equals(msg))){
                clientName = msg;
                sendMessage(NAME_ACCEPTED);
                logger.log(clientName + " logged in");
            } else {
                sendMessage("This name is busy");
            }
        } else {
            messages.add(msg);
            logger.log(msg);
        }

        inputBuffer.clear();
        return true;
    }

    private boolean sendMessages() {
        try {
            String msg = messages.take();
            clientHandlers.stream().filter(x -> x != this).forEach(handler -> handler.sendMessage(msg));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void sendMessage(String message){
        if (message == null || message.isBlank())
            return;
        if (!socketChannel.isOpen())
            return;
        try {
            socketChannel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
