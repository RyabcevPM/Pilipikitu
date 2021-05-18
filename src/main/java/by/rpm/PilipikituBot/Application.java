package by.rpm.PilipikituBot;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Application {
    public static final String AUTHORIZATION_TOKEN;
    public static final String CHAT_ID;
    public static final File RECIPES_LIKE_FILE;
    public static final File RECIPES_NEW_FILE;
    public static final File PRISE_LIST_FILE;
    public static final File SCOLD_LIST_FILE;
    public static final File DRINK_FUN_FILE;
    public static final String TEST_PHOTO_FILE_NAME;
    public static final String TEST_MP3_FILE_NAME;


    /*RECIPES_LIKE_FILE=resources/files/RecipesLike.xml
RECIPES_NEW_FILE=resources/files/RecipesNew.xml
PRISE_LIST_FILE=resources/files/prise.txt
SCOLD_LIST_FILE=resources/files/scold.txt
DRINK_FUN_FILE=resources/files/drinkFun.txt
*/

    //public static Brain brain = new Brain();

    static {
        Properties properties = new Properties();
        ClassLoader loader = Application.class.getClassLoader();
        try (InputStream resourceStream = loader.getResourceAsStream("application.properties")) {
            properties.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AUTHORIZATION_TOKEN = properties.getProperty("authorizationToken");
        CHAT_ID = properties.getProperty("chatId"); //https://t.me/joinchat/kcrbtd21T-I5MTI6

        String prefix = "src/main/webapp/WEB-INF/classes/";
        PRISE_LIST_FILE = new File( prefix + properties.getProperty("PRISE_LIST_FILE"));
        RECIPES_LIKE_FILE = new File( prefix + properties.getProperty("RECIPES_LIKE_FILE"));
        RECIPES_NEW_FILE = new File( prefix + properties.getProperty("RECIPES_NEW_FILE"));
        SCOLD_LIST_FILE = new File( prefix + properties.getProperty("SCOLD_LIST_FILE"));
        DRINK_FUN_FILE = new File(prefix + properties.getProperty("DRINK_FUN_FILE"));
        TEST_PHOTO_FILE_NAME = new File(prefix + properties.getProperty("TEST_PHOTO_FILE")).getAbsolutePath();
        TEST_MP3_FILE_NAME = new File(prefix + properties.getProperty("TEST_MP3_FILE")).getAbsolutePath();

        // fow WEBAPPP
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        PRISE_LIST_FILE = new File( classLoader.getResource(properties.getProperty("PRISE_LIST_FILE")).getFile());
//        RECIPES_LIKE_FILE = new File( classLoader.getResource(properties.getProperty("RECIPES_LIKE_FILE")).getFile());
//        RECIPES_NEW_FILE = new File( classLoader.getResource(properties.getProperty("RECIPES_NEW_FILE")).getFile());
//        SCOLD_LIST_FILE = new File( classLoader.getResource(properties.getProperty("SCOLD_LIST_FILE")).getFile());
//        DRINK_FUN_FILE = new File( classLoader.getResource(properties.getProperty("DRINK_FUN_FILE")).getFile());
//        TEST_PHOTO_FILE_NAME = new File(classLoader.getResource(properties.getProperty("TEST_PHOTO_FILE")).getFile()).getAbsolutePath();
//        TEST_MP3_FILE_NAME = new File(classLoader.getResource(properties.getProperty("TEST_MP3_FILE")).getFile()).getAbsolutePath();
    }

    public static void main(String[] args) {
        System.out.println("---------------------> Pilipikitu starting");
        CamelContext context;
//        context = new DefaultCamelContext();
//        try {
//            context.addRoutes(new GetChartIDRoute());
//            context.start();
////            System.out.println(Telegram.getChartID(context));
////            Telegram.SendText(context, "Hello World!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            context.stop();
//        }

        context = new DefaultCamelContext();
        try {
            context.addRoutes(new MainRouteBuilder());
            context.start();
//            Thread.sleep(100);
//            Brain brain = new Brain();
//      brain.InitButtonsForAction(context);

//            brain.InitButtonsForAction(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
