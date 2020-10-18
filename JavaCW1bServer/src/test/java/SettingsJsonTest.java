import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsJsonTest {
    private static final String TEST_FILENAME = "testSettings.json";

    @Before
    @After
    public void deleteSettingsFile() {
        File file = new File(TEST_FILENAME);
        if (file.exists())
            file.delete();
    }

    @Test
    public void testCreateSettings_Ok(){
        SettingsJson settings = new SettingsJson();
        File file = new File(TEST_FILENAME);
        if (file.exists())
            file.delete();

        settings.createSettings(TEST_FILENAME);

        Assert.assertTrue(file.exists());
    }

    @Test
    public void testCreateSettings_WhenAlreadyExists(){
        SettingsJson settings = new SettingsJson();
        File file = new File(TEST_FILENAME);

        settings.createSettings(TEST_FILENAME);

        Assert.assertTrue(file.exists());
    }

    @Test
    public void testReadSettings_OkFileExists(){
        SettingsJson settings = new SettingsJson();
        settings.createSettings(TEST_FILENAME);

        settings.readSettings(TEST_FILENAME);

        Assert.assertEquals(Settings.DEFAULT_PORT, settings.getPort());
    }

    @Test
    public void testReadSettings_NoFile(){
        SettingsJson settings = new SettingsJson();

        settings.readSettings(TEST_FILENAME);

        Assert.assertEquals(Settings.DEFAULT_PORT, settings.getPort());
    }

    @Test(expected = NullPointerException.class)
    public void testReadSettings_NoInfoAboutPort(){
        try (FileWriter writer = new FileWriter(TEST_FILENAME, false)) {
            writer.write("{\"smthWrong\":666}");
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        SettingsJson settings = new SettingsJson();

        settings.readSettings(TEST_FILENAME);
    }
}
