import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsJson implements Settings{
    public static final String DEFAULT_FILENAME = "settings.json";
    private final String portField = "port";
    private int port;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void readSettings(String filename) {
        File file = new File(filename);
        if (!file.exists())
            createSettings(filename);

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(filename));
            JSONObject jsonObject = (JSONObject) obj;
            port = Math.toIntExact((Long)jsonObject.get("port"));
            System.out.println(jsonObject);
        } catch (IOException e) {
            System.out.println("Error reading settings file");
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Error parsing settings file");
            e.printStackTrace();
        }
    }

    @Override
    public void createSettings(String filename, int port) {
        try (FileWriter file = new FileWriter(filename)) {
            JSONObject obj = new JSONObject();
            obj.put(portField, port);
            file.write(obj.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createSettings(String filename) {
        createSettings(filename, DEFAULT_PORT);
    }
}
