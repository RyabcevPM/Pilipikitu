package by.rpm.external.api;

import by.rpm.PilipikituBot.Application;
import by.rpm.PilipikituBot.Brain;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.TelegramConfiguration;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.OutgoingMessage;

public class GetChartIDRoute extends RouteBuilder {

    private Brain brain = new Brain();
    public String s1;

    @Override
    public void configure() {
        String telega = String.format("telegram:bots/?authorizationToken=%s&chatId=%s", Application.AUTHORIZATION_TOKEN, Application.CHAT_ID);
//        from("direct:start").to(telega);
//        from(telega)
//                .bean(brain, "chatBotProcess1")
//                .bean(brain, "chatBotProcess2")
//                .to(telega);
//        from("direct:start").to(telega);
        from("direct:start")
                .toF("http://api.telegram.org/bot%s/getUpdates", Application.AUTHORIZATION_TOKEN)
                .toString();

//        from("direct:start").to("stream::out");
////        from(String.format("telegram:bots/?authorizationToken=%s", Application.AUTHORIZATION_TOKEN))
//        from("direct:start")
//                .to("file:src/main/resources/order/")
//                .to("stream::out")
        ;

    }
}

