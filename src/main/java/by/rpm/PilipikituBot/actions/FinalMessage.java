package by.rpm.PilipikituBot.actions;

import by.rpm.external.api.Telegram;
import org.apache.camel.component.telegram.model.OutgoingMessage;

public class FinalMessage extends ActionChain implements IsFinalAction {
    private OutgoingMessage msg;

    public FinalMessage(String name, OutgoingMessage msg) {
        super(name);
        this.msg = msg;
    }

    public FinalMessage(String outputText) {
        this(outputText, Telegram.getText(outputText));
    }


    @Override
    public OutgoingMessage getResult() {
//        isFinal = true;
        return msg;
    }
}
