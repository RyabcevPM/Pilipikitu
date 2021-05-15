package by.rpm.external.api;


import by.rpm.PilipikituBot.Application;
import org.apache.camel.*;
import org.apache.camel.component.telegram.TelegramConstants;
import org.apache.camel.component.telegram.TelegramMediaType;
import org.apache.camel.component.telegram.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Telegram {

    public static final String INIT_MESSAGE_BUTTON_CHANGE = "\uD83D\uDC47";
    public static enum TextType{
        NORMAL,
        HTML,
        Markdown,
        BigText
    }


    public static void SendText(CamelContext context, String text){
        ProducerTemplate template = context.createProducerTemplate();
        OutgoingTextMessage msg = new OutgoingTextMessage();
        msg.setText(text);
        MessageResult msgResult = template.requestBody("direct:start", msg, MessageResult.class);
    };

    public static void SendToDirect(CamelContext context, OutgoingMessage msg){
        ProducerTemplate template = context.createProducerTemplate();
        MessageResult msgResult = template.requestBody("direct:start", msg, MessageResult.class);

    };

    public static void SendToDirect(CamelContext context, String msg){
        ProducerTemplate template = context.createProducerTemplate();
        MessageResult msgResult = template.requestBody("direct:start", getText(msg), MessageResult.class);
    };

    public static OutgoingMessage getText(String text){
        return getText(text, TextType.NORMAL);
    }

    public static OutgoingMessage getText(String text, TextType textType){
        OutgoingTextMessage msg = new OutgoingTextMessage();
        if (textType.equals(TextType.NORMAL))  msg.setText(text);
        if (textType.equals(TextType.Markdown)) { msg.setText(text);
            msg.setText(text);
            msg.setParseMode(TextType.Markdown.name());
        }
        if (textType.equals(TextType.BigText)) { msg.setText(text);
            msg.setText("```\n" + text.replaceAll("\\n", "\n") + "\n```");
            msg.setParseMode(TextType.Markdown.name());
        }
        if (textType.equals(TextType.HTML)) {
            msg.setText(text);
            msg.setParseMode(TextType.HTML.name());
        }

        return msg;
    };


    public static OutgoingMessage getLocation(double latitude, double longitude, String title, String address) {
        SendVenueMessage msg2 = new SendVenueMessage(latitude, longitude, title, address);
        return msg2;
    }

    public static OutgoingMessage getPhoto(String fileName, String caption) {
        OutgoingPhotoMessage photo = new OutgoingPhotoMessage();
        photo.setFilenameWithExtension(fileName);
        if (!caption.isEmpty()) photo.setCaption(caption);
        try {
            photo.setPhoto(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        };
        return photo;
    }

    public static OutgoingMessage getAudio(String fileName, String title) {
        OutgoingAudioMessage audio = new OutgoingAudioMessage();
        audio.setFilenameWithExtension(fileName);
        if (!title.isEmpty()) audio.setTitle(title);
        try {
            audio.setAudio(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        };
        return audio;
    }


    public static OutgoingMessage getButtons(ArrayList<String> buttons, String testMessage) {
        OutgoingTextMessage msg = new OutgoingTextMessage();

        int columnCnt = Math.min(4, (int) Math.round(Math.sqrt (buttons.size())));
        if (columnCnt * columnCnt<buttons.size()) columnCnt+=1;
        msg.setText(testMessage + INIT_MESSAGE_BUTTON_CHANGE);

        ReplyKeyboardMarkup.Builder.KeyboardBuilder keyboardBuilder = ReplyKeyboardMarkup.builder().keyboard();
        int cnt = 0;
        ArrayList<InlineKeyboardButton> rowButtons;
        while (cnt<buttons.size()){
            rowButtons = new ArrayList<>();
            while (rowButtons.size() < columnCnt && cnt < buttons.size()) {
                rowButtons.add(InlineKeyboardButton.builder().text(buttons.get(cnt)).build());
                cnt += 1;
            }
            keyboardBuilder.addRow(rowButtons);
        }

        ReplyKeyboardMarkup replyMarkup = keyboardBuilder.close().oneTimeKeyboard(true).build();

        msg.setReplyMarkup(replyMarkup);

        return msg;
    }


    public static String getChartID(CamelContext context){
        ProducerTemplate template = context.createProducerTemplate();
        OutgoingTextMessage msg = new OutgoingTextMessage();
        MessageResult msgResult = template.requestBody("direct:start", msg, MessageResult.class);
        return msgResult.toString();
    }


    public static void GetUpdatesUsage(CamelContext context) {
        ConsumerTemplate template = context.createConsumerTemplate();
        Update message = template.receiveBodyNoWait(String.format("telegram:bots/?authorizationToken=%s&chatId=%s", Application.AUTHORIZATION_TOKEN, Application.CHAT_ID), Update.class);
        System.out.println(message);
    }


    public static OutgoingMessage toOutgoingMessage(String text){
        OutgoingTextMessage msg = new OutgoingTextMessage();
        msg.setText(text);
        return msg;
//        MessageResult msgResult = template.requestBody("direct:start", msg, MessageResult.class);
//        msgResult = template.requestBody("direct:log", msg, MessageResult.class);
//        System.out.println(msgResult);
    };




    @Converter
    public static OutgoingMessage toOutgoingMessage(byte[] message, Exchange exchange) {
        if (message == null) {
            // fail fast
            return null;
        }
        Object typeObj = exchange.getIn().getHeader(TelegramConstants.TELEGRAM_MEDIA_TYPE);
        TelegramMediaType type;
        if (typeObj instanceof String) {
            type = TelegramMediaType.valueOf((String) typeObj);
        } else {
            type = (TelegramMediaType) typeObj;
        }
        // If the message is a string, it will be converted to a OutgoingTextMessage
        if (type == null) {
            throw new IllegalStateException("Binary message require the header " + TelegramConstants.TELEGRAM_MEDIA_TYPE + " to be set with an appropriate org.apache.camel.component.telegram"
                    + ".TelegramMediaType object");
        }
        OutgoingMessage result;
        switch (type) {
            case PHOTO_JPG:
            case PHOTO_PNG: {
                OutgoingPhotoMessage img = new OutgoingPhotoMessage();
                String caption = (String) exchange.getIn().getHeader(TelegramConstants.TELEGRAM_MEDIA_TITLE_CAPTION);
                String fileName = "photo." + type.getFileExtension();
                img.setCaption(caption);
                img.setFilenameWithExtension(fileName);
                img.setPhoto(message);
                result = img;
                break;
            }
            case AUDIO: {
                OutgoingAudioMessage audio = new OutgoingAudioMessage();
                String title = (String) exchange.getIn().getHeader(TelegramConstants.TELEGRAM_MEDIA_TITLE_CAPTION);
                String fileName = "audio." + type.getFileExtension();
                audio.setTitle(title);
                audio.setFilenameWithExtension(fileName);
                audio.setAudio(message);
                result = audio;
                break;
            }
            case VIDEO: {
                OutgoingVideoMessage video = new OutgoingVideoMessage();
                String title = (String) exchange.getIn().getHeader(TelegramConstants.TELEGRAM_MEDIA_TITLE_CAPTION);
                String fileName = "video." + type.getFileExtension();
                video.setCaption(title);
                video.setFilenameWithExtension(fileName);
                video.setVideo(message);
                result = video;
                break;
            }
            case DOCUMENT:
            default: {
                // this can be any file
                OutgoingDocumentMessage document = new OutgoingDocumentMessage();
                String title = (String) exchange.getIn().getHeader(TelegramConstants.TELEGRAM_MEDIA_TITLE_CAPTION);
                document.setCaption(title);
                document.setFilenameWithExtension("file");
                document.setDocument(message);
                result = document;
                break;
            }
        }
        return result;
    }
}
