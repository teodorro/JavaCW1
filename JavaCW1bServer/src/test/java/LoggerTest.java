import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class LoggerTest {
    private final String TEST_FILENAME = "testLog.log";
    private final String TEST_STR = "testString";


    @Before
    @After
    public void deleteSettingsFile() {
        File file = new File(TEST_FILENAME);
        if (file.exists())
            file.delete();
    }


    @Test
    public void testLog_CreateFileIfNotExists() {
        Logger logger = new Logger(TEST_FILENAME);

        logger.log(TEST_STR);

        String s = "";
        try (BufferedReader br = new BufferedReader(new FileReader(TEST_FILENAME))) {
            s = br.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        Assert.assertEquals(TEST_STR, s);
    }

    @Test
    public void testLog_AppendIfFileExists(){
        Logger logger = new Logger(TEST_FILENAME);

        logger.log(TEST_STR);
        logger.log(TEST_STR);

        String s = "";
        try (BufferedReader br = new BufferedReader(new FileReader(TEST_FILENAME))) {
            s = br.readLine();
            s = br.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        Assert.assertEquals(TEST_STR, s);
    }

//    @Test
//    public void testLog_TryCreateLoggerWithSameName(){
//        deleteSettingsFile();
//        Logger logger1 = new Logger(TEST_FILENAME);
//        Logger logger2 = new Logger(TEST_FILENAME);
//        ExecutorService es = Executors.newFixedThreadPool(2);
//        es.submit(() -> {logger1.log(TEST_STR);});
//        es.submit(() -> {logger2.log(TEST_STR);});
//
//        String s = "";
//        try (BufferedReader br = new BufferedReader(new FileReader(TEST_FILENAME))) {
//            s = br.readLine();
//            s = br.readLine();
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//        }
//
//        Assert.assertEquals(TEST_STR, s);
//        deleteSettingsFile();
//    }

}
