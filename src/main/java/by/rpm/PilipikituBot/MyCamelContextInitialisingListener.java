package by.rpm.PilipikituBot;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;

public class MyCamelContextInitialisingListener extends ContextLoaderListener {
    private CamelContext camelContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        camelContext = new DefaultCamelContext();
        MainRouteBuilder routeBuilder;
        System.out.println("I'm init!");
        try {
            routeBuilder = new MainRouteBuilder();
            camelContext.addRoutes(routeBuilder);
            camelContext.start();
            routeBuilder.brain.InitButtonsForAction(camelContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        camelContext.stop();
    }
}
