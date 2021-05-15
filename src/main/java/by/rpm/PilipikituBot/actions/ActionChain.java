package by.rpm.PilipikituBot.actions;

import by.rpm.external.api.Telegram;
import org.apache.camel.component.telegram.model.OutgoingMessage;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionChain implements Comparable<ActionChain> {
    protected String message;

    public ArrayList<ActionChain> getButtons() {
        return buttons;
    }

    private ArrayList<ActionChain> buttons;
    protected String name;
    protected static FinalMessage BACK_TO_MAIN_MENU = new FinalMessage("В главное меню");
    protected static OutgoingMessage NOT_IMPLEMENTATION = Telegram.getText("Действие пока не реализовано");
    protected String inputText = "";

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public void setButtons(ArrayList<ActionChain> buttons) {
        this.buttons = buttons;
    }

    public ActionChain(String name, ArrayList<ActionChain> buttons, String message) {
        this.name = name;
        this.buttons = buttons;
        this.message = message;
    }
    public ActionChain(String name, String message) {
        this(name, new ArrayList<>(), message);
    }

    public ActionChain(String name, ArrayList<ActionChain> buttons) {
        this(name, buttons, "");
    }

    public ActionChain(String name) {
        this(name, new ArrayList<>(), "");
    }


    public ActionChain() {
        this("", new ArrayList<>(), "");
    }

    @Override
    public int compareTo(ActionChain o) {
        return getName().compareTo(o.getName());
    }

    public OutgoingMessage showCaseButtons() {
        return Telegram.getButtons(getButtonNames(),message);
    }


    public Optional<ActionChain> FindButton(String buttonName) {
        Optional<ActionChain> button = buttons.stream().filter(o -> o.getName().equals(buttonName)).findFirst();
        return button;
    }


    public Optional<ActionChain> getNext(String buttonName) {
//        Optional<ActionChain> newAction = buttons.stream().filter(o -> o.getName().equals(buttonName)).findFirst();
        return FindButton(buttonName);
    }

    public ArrayList<String> getButtonNames() {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(buttons.stream().map(o -> o.getName()).collect(Collectors.toList()));
        return list;
    }

    public String getName() {
        return name;
    }


}
