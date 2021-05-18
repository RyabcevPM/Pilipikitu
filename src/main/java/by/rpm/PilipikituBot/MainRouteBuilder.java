package by.rpm.PilipikituBot;

import org.apache.camel.builder.RouteBuilder;

public class MainRouteBuilder extends RouteBuilder {

    public Brain brain = new Brain();

    @Override
    public void configure() {
        String telega = String.format("telegram:bots/?authorizationToken=%s&chatId=%s", Application.AUTHORIZATION_TOKEN, Application.CHAT_ID);
        from("direct:start").to(telega);
//        from("http://api.openweathermap.org/data/2.5/weather?id=630197&appid=ca87861547ac3573d0c119eea4076e39&lang=ru&mode=html")
//                .convertBodyTo(String.class).log(telega);
        from(telega)
                .bean(brain, "TelegramInputTextProcess")
                .bean(brain, "goNextStep")
                .to(telega);

//        from(telega)
//                .to("file:D:\\Java\\mvn_project\\Pilipikitu\\src\\main\\resources\\files\\1.txt");



    }
}