package by.rpm.PilipikituBot;


import by.rpm.PilipikituBot.actions.*;
import by.rpm.PilipikituBot.actions.Food.FoodMaster;
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



public class Brain {
    public static final OutgoingMessage HELLO_MESSAGE = Telegram.getText("Hello World! \nPilipikitu in action.\n");
    private static ActionChain mainMenu = getMainMenu();
    private Logger log = LoggerFactory.getLogger(getClass());
    private ActionChain currentAction;
//    private static String lastInputText = "";

//    public static String getLastInputText() {
//        return lastInputText;
//    }

    public Brain() {
        System.out.println("init Brain");
    }

    private static ActionChain getMainMenu() {
        ArrayList<ActionChain> menu = new ArrayList<>();
        menu.add(new SelectOneLine("Похвали меня", new File("src/main/resources/files/prise.txt")));
        menu.add(new SelectOneLine("Поругай меня", new File("src/main/resources/files/scold.txt")));
        menu.add(new SelectOneLine("Тост", new File("src/main/resources/files/drinkFun.txt"), Telegram.TextType.BigText));
        menu.add(new FoodMaster());
        {   // submenu1
            ArrayList<ActionChain> subMenuButtons = new ArrayList<>();
            subMenuButtons.add(new FinalMessage("location", Telegram.getLocation(53.947, 27.70743, null, null)));
            subMenuButtons.add(new FinalMessage("photo", Telegram.getPhoto("src/main/resources/image/logo.jpg", "")));
            subMenuButtons.add(new FinalMessage("audio", Telegram.getAudio("src/main/resources/mp3/griby_ringon_min.mp3", "")));
            String html =
                    "<b>bold</b>, <strong>bold</strong>\n" +
                            "<i>italic</i>, <em>italic</em>\n" +
                            "<u>underline</u>, <ins>underline</ins>\n" +
                            "<s>strikethrough</s>, <strike>strikethrough</strike>, <del>strikethrough</del>\n" +
                            "<b>bold <i>italic bold <s>italic bold strikethrough</s> <u>underline italic bold</u></i> bold</b>\n" +
                            "<a href=\"http://www.example.com/\">inline URL</a>\n" +
                            "<a href=\"tg://user?id=123456789\">inline mention of a user</a>\n" +
                            "<code>inline fixed-width code</code>\n" +
                            "<pre>pre-formatted fixed-width code block</pre>\n" +
                            "<pre><code class=\"language-python\">pre-formatted fixed-width code block written in the Python programming language</code></pre>";

            subMenuButtons.add(new FinalMessage("html", Telegram.getText(html,Telegram.TextType.HTML)));

            {   // submenu2
                ArrayList<ActionChain> subMenuButtons2 = new ArrayList<>();
                subMenuButtons2.add(new TestDynamicMenu("Button test", 3));
                subMenuButtons2.add(new FinalMessage("location2", Telegram.getLocation(60, 28, null, null)));
                subMenuButtons2.add(new FinalMessage("show dev picture", Telegram.getPhoto("src/main/resources/image/logo.jpg", "")));
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
//        CamelContext context = exchange.getContext();
        if (inputText.equals("/init")) {
            currentAction = null;
            return HELLO_MESSAGE;
        }

        currentAction.setInputText(inputText);

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
            if (FoodMaster.isFoodLink(inputText)) {
                currentAction = FoodMaster.addToLikeAction(inputText);
                return currentAction.showCaseButtons();
            }

            return Telegram.getText("you say: " + inputText);
        }


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