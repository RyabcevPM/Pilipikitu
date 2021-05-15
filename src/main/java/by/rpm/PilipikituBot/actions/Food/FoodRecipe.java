package by.rpm.PilipikituBot.actions.Food;

import by.rpm.PilipikituBot.actions.ActionChain;
import by.rpm.PilipikituBot.actions.IsFinalAction;
import by.rpm.external.api.Telegram;
import org.apache.camel.component.telegram.model.OutgoingMessage;

public class FoodRecipe extends ActionChain implements IsFinalAction {
    private String link;

    public FoodRecipe(String name, String link) {
        super(name.replaceAll("||W", "").replaceAll("  ", " ").trim());
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    @Override
    public OutgoingMessage getResult() {
        return Telegram.getText(link);
    }
}
