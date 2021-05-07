package by.rpm.PilipikituBot;

import by.rpm.external.api.Telegram;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Application {
    public static final String AUTHORIZATION_TOKEN;
    public static final String CHAT_ID;

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
            Telegram.SendText(context, "Hello World! \n/Pilipikitu in action.");
            Brain brain = new Brain();
            brain.InitButtonsForAction(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
