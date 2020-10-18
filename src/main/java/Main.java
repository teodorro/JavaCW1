public class Main {
    public static void main(String[] args) {
        Settings ss = new SettingsJson();
        ss.readSettings(SettingsJson.DEFAULT_FILENAME);
        System.out.println(ss.getPort());
    }
}
