package by.rpm.PilipikituBot;

import by.rpm.PilipikituBot.Application;
import by.rpm.PilipikituBot.Brain;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.bean.Bean;
import sun.reflect.generics.scope.Scope;

import java.io.File;

public class MainRouteBuilder extends RouteBuilder {


    @Override
    public void configure() {
        String telega = String.format("telegram:bots/?authorizationToken=%s&chatId=%s", Application.AUTHORIZATION_TOKEN, Application.CHAT_ID);
        from("direct:start").to(telega);
//        from("http://api.openweathermap.org/data/2.5/weather?id=630197&appid=ca87861547ac3573d0c119eea4076e39&lang=ru&mode=html")
//                .convertBodyTo(String.class).log(telega);
        from(telega)
                .bean(Application.brain, "TelegramInputTextProcess")
                .bean(Application.brain, "goNextStep")
                .to(telega);

//        from(telega)
//                .to("file:D:\\Java\\mvn_project\\Pilipikitu\\src\\main\\resources\\files\\1.txt");



    }
}