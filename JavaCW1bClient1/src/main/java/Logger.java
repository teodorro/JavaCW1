import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    private String filename;


    public Logger(String filename) {
        this.filename = filename;
    }

    public void log(String message){
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.append(message + "\n");
            writer.flush();
        } catch (IOException ex) {
            System.out.println("error logging");
            System.out.println(ex.getMessage());
        }
    }

}