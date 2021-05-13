package by.rpm.PilipikituBot;


import by.rpm.PilipikituBot.actions.*;
import by.rpm.external.api.Telegram;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.telegram.TelegramConfiguration;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.model.MessageResult;
import org.apache.camel.component.telegram.model.OutgoingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * This class contains the chat-bot logic: use your fantasy to implement your own Bot.
 */

public class Brain {
    public static final String HELLO_MESSAGE = "Hello World! \nPilipikitu in action.\n";
    private Logger log = LoggerFactory.getLogger(getClass());
    private static ActionChain mainMenu = getMainMenu();

    private ActionChain currentAction;

    public Brain() {
        System.out.println("init Brain");
    }

    private static ActionChain getMainMenu() {
        ArrayList<ActionChain> menu = new ArrayList<>();
        menu.add(new SelectOneLine("Похвали меня", new File("D:\\Java\\mvn_project\\Pilipikitu\\src\\main\\resources\\files\\prise.txt")));
        menu.add(new SelectOneLine("Поругай меня", new File("D:\\Java\\mvn_project\\Pilipikitu\\src\\main\\resources\\files\\scold.txt")));
        menu.add(new TestDynamicMenu("Button test",3));
        {   // submenu1
            ArrayList<ActionChain> subMenuButtons = new ArrayList<>();
            subMenuButtons.add(new FinalMessage("location", Telegram.getLocation(53.947, 27.70743, null, null)));
            subMenuButtons.add(new FinalMessage("photo", Telegram.getPhoto("d:\\pas\\logo.jpg", "")));
            subMenuButtons.add(new FinalMessage("audio", Telegram.getAudio("C:\\Users\\rpm\\Downloads\\griby_ringon_min.mp3", "")));
            {   // submenu2
                ArrayList<ActionChain> subMenuButtons2 = new ArrayList<>();
                subMenuButtons2.add(new FinalMessage("location2", Telegram.getLocation(60, 28, null, null)));
                subMenuButtons2.add(new FinalMessage("show dev picture", Telegram.getPhoto("d:\\117420.png", "")));
                ActionChain subMenu2 = new ActionChain("Еще одна вложенность!", subMenuButtons2);
                subMenuButtons.add(subMenu2);
            }
            ActionChain subMenu = new ActionChain("Что я еще умею?", subMenuButtons, "Выбирай ");
            menu.add(subMenu);
        }
        System.out.println("init main menu");

        return new ActionChain("Главное меню", menu);
    }


    public void InitButtonsForAction(CamelContext context) {
        ProducerTemplate template = context.createProducerTemplate();
        currentAction = mainMenu;
        MessageResult result = template.requestBody("direct:start",
                currentAction.showCaseButtons(), MessageResult.class);
    }

    public void goNextStep(Exchange exchange) {
        if (currentAction == null) InitButtonsForAction(exchange.getContext());

    }

    public OutgoingMessage TelegramInputTextProcess(Exchange exchange) {
        String inputText = exchange.getIn().getBody(String.class);
        System.out.println("you say: " + inputText);
        CamelContext context = exchange.getContext();

        Optional<ActionChain> newAction = currentAction.getNext(inputText);
        if (newAction.isPresent()) {
            OutgoingMessage msg;
            if (newAction.get() instanceof IsFinalAction) {
                IsFinalAction finalAction = (IsFinalAction) newAction.get();
                msg = finalAction.getResult();
                currentAction = null;
                return msg;
            } else {
                currentAction = newAction.get();
                return currentAction.showCaseButtons();
            }
        } else {
            return Telegram.getText("you say: " + inputText);
        }



//        Optional<ActionChain> newAction = currentAction.getButtons().stream().filter(o -> o.getName().equals(inputText)).findFirst();
//        if (newAction.isPresent()) {
//            System.out.println("run - " + inputText);
//
//            currentAction = newAction.get();
//            OutgoingMessage msg = currentAction.getResult();
//            System.out.println("is final - " + currentAction.isFinal());
//            if (currentAction.isFinal()) currentAction = null;
//            return msg;
//
//        } else {
//            return Telegram.getText("you say: " + inputText);
//        }
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