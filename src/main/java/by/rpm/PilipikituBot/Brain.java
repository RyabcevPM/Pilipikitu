package by.rpm.PilipikituBot;


import by.rpm.PilipikituBot.actions.BotAction;
import by.rpm.PilipikituBot.actions.BotPraise;
import by.rpm.PilipikituBot.actions.BotScold;
import by.rpm.PilipikituBot.actions.SelectOneLine;
import by.rpm.external.api.Telegram;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.telegram.TelegramConfiguration;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the chat-bot logic: use your fantasy to implement your own Bot.
 */

public class Brain {
    public static final String INIT_MESSAGE = "\uD83D\uDC47";
    private Logger log = LoggerFactory.getLogger(getClass());
    private int msgCnt = 0;

    private Map<String, BotAction> botActions;

    public Brain() {
        System.out.println("init Brain");
        BotAction action;
        botActions = new HashMap<>();
        action = new SelectOneLine("Похвали меня", new File("D:\\Java\\mvn_project\\Pilipikitu\\src\\main\\resources\\files\\prise.txt"));
        botActions.put(action.getName(),action);
        action = new SelectOneLine("Поругай меня", new File("D:\\Java\\mvn_project\\Pilipikitu\\src\\main\\resources\\files\\scold.txt"));
        botActions.put(action.getName(),action);
        botActions.put("location", null);
        botActions.put("photo", null);
        botActions.put("audio", null);
    }

    public void chatBotProcess1(Exchange exchange) {
        String str1 = exchange.getIn().getBody(String.class);
        if (str1==null) return;
        String msg = new StringBuffer(str1).deleteCharAt(0).toString();
        CamelContext context = exchange.getContext();
        log.info(msg);
        Telegram.SendToDirect(context, "You are say: " + msg);
    }

    public String chatBotProcess2(String message) {
        return "answer # " + ++msgCnt;
    }



    public void InitButtonsForAction(CamelContext context){
        ProducerTemplate template = context.createProducerTemplate();
        ArrayList<String> buttons = new ArrayList<>();
        buttons.addAll(botActions.keySet());
        MessageResult result = template.requestBody("direct:start", Telegram.getButtons(buttons, INIT_MESSAGE), MessageResult.class);
    }

    public void initNewAction(Exchange exchange){
        InitButtonsForAction(exchange.getContext());
    }

    public OutgoingMessage TelegramInputTextProcess(Exchange exchange) {
        String inputText = exchange.getIn().getBody(String.class);
        System.out.println("you say: " + inputText);
        CamelContext context = exchange.getContext();
        if (inputText.equals("location")) return Telegram.getLocation(53.947, 27.70743, null, null);
        if (inputText.equals("photo")) return Telegram.getPhoto( "d:\\pas\\logo.jpg","");
        if (inputText.equals("audio")) return Telegram.getAudio( "C:\\Users\\rpm\\Downloads\\griby_ringon_min.mp3", "");
        if (botActions.containsKey(inputText)) return Telegram.getText(botActions.get(inputText).getInfo());

        return Telegram.getText("you say: " + inputText);
    }


    private String resolveChatId(TelegramConfiguration config, OutgoingMessage message, Exchange exchange) {
        String chatId;
        // Try to get the chat id from the message body
        chatId = message.getChatId();
        // Get the chat id from headers
        if (chatId == null) {
            chatId = (String) exchange.getIn().getHeader(TelegramConstants.TELEGRAM_CHAT_ID);
        }
        // If not present in the headers, use the configured value for chat id
        if (chatId == null) {
            chatId = config.getChatId();
        }
        // Chat id is mandatory
        if (chatId == null) {
            throw new IllegalStateException("Chat id is not set in message headers or route configuration");
        }
        return chatId;
    }
}