package by.rpm.PilipikituBot.actions;

import java.util.Optional;

public class TestDynamicMenu extends ActionChain {
    private static final String FINAL_BUTTON = "Back to main menu";
    private static final String FINAL_MESSAGE = "Test complete";
    private int cnt;

    public TestDynamicMenu(String name, int cnt) {
        super(name, String.valueOf(cnt));
        this.cnt = cnt;
        for (int i = -cnt + 1; i < cnt; i++) {
            buttons.add(new ActionChain(i == 0 ? FINAL_BUTTON : String.valueOf(i)));
        }
    }

    @Override
    public Optional<ActionChain> getNext(String buttonName) {
        if (buttonName.equals(FINAL_BUTTON)) return Optional.of(new FinalMessage(FINAL_MESSAGE));
        Optional<ActionChain> res = super.FindButton(buttonName);
        if (!res.isPresent()) return Optional.of(this);

        return Optional.of(new TestDynamicMenu(getName(), cnt + Integer.valueOf(buttonName)));
    }


}
