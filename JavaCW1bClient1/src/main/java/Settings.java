public interface Settings {
    int DEFAULT_PORT = 12321;

    int getPort();

    void readSettings(String filename);
    void createSettings(String filename, int port);
}
