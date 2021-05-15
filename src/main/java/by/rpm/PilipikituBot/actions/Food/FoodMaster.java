package by.rpm.PilipikituBot.actions.Food;

import by.rpm.PilipikituBot.actions.ActionChain;
import by.rpm.PilipikituBot.actions.FinalMessage;

import java.util.ArrayList;
import java.util.Optional;

public class FoodMaster extends ActionChain {
    private static String INIT_MESSAGE = "Как будем выбирать?";
    private static String FIRST_LEVEL_LIKE = "Любимые";
    private static String FIRST_LEVEL_BREAKFAST = "Придумать завтрак";
    private static String FIRST_LEVEL_DINNER = "Придумать обед";
    private static String FIRST_LEVEL_SUPPER = "Придумать ужин";
    private static String FIRST_LEVEL_SNACK = "Придумать перекус";
    private static String FIRST_LEVEL_BIG_MENU = "Выбрать новое";
    private static String SELECTION_RANDOM = "Случайный из топчика";
    private static String SELECTION_SMART = "По продуктам";
    private static String SELECTION_HEAVY = "Питательный";
    private static String SELECTION_EASY = "Быстрый и простой";
    private static String SELECTION_USEFUL = "Полезный";
    private static String NO = "НЕТ";
    private static String APPLY_NO_MESSAGE = "Хорошо, не буду. И вообще забыл.";
    private static ArrayList<ActionChain> likeMenu;


    public FoodMaster() {
        super("Еда", INIT_MESSAGE);
        ArrayList<ActionChain> menu2 = new ArrayList<>();

        menu2.add(new FinalMessage(SELECTION_RANDOM, NOT_IMPLEMENTATION));
        menu2.add(new FinalMessage(SELECTION_SMART, NOT_IMPLEMENTATION));
        menu2.add(new FinalMessage(SELECTION_HEAVY, NOT_IMPLEMENTATION));
        menu2.add(new FinalMessage(SELECTION_EASY, NOT_IMPLEMENTATION));
        menu2.add(new FinalMessage(SELECTION_USEFUL, NOT_IMPLEMENTATION));
        menu2.add(BACK_TO_MAIN_MENU);


        getButtons().add(new ActionChain(FIRST_LEVEL_BREAKFAST, menu2));
        getButtons().add(new ActionChain(FIRST_LEVEL_DINNER, menu2));
        getButtons().add(new ActionChain(FIRST_LEVEL_SUPPER, menu2));
        getButtons().add(new ActionChain(FIRST_LEVEL_SNACK, menu2));
        getButtons().add(new ActionChain(FIRST_LEVEL_BIG_MENU, RecipeKeeper.loadMenu(RecipeKeeper.NEW_RECIPES)));
        getButtons().add(BACK_TO_MAIN_MENU);
        likeMenu = RecipeKeeper.loadMenu(RecipeKeeper.LIKE_RECIPES);
        getButtons().add(new ActionChain(FIRST_LEVEL_LIKE, likeMenu));
    }

    public static boolean isFoodLink(String message) {
        return message.contains("food");
    }

    public static ActionChain addToLikeAction(String url) {
        ActionChain result = new ActionChain(FoodMaster.class.getName(), "Добавить в любиме блюда?");
        result.getButtons().add(new FinalMessage(NO, APPLY_NO_MESSAGE));
        for (ActionChain segment : likeMenu) {
            result.getButtons().add(new RecipeKeeper(segment.getName(), url, segment.getName(), likeMenu));
        }
        return result;
    }


    @Override
    public Optional<ActionChain> getNext(String buttonName) {
        Optional<ActionChain> next = super.getNext(buttonName);
        if (buttonName.equals(FIRST_LEVEL_LIKE)) {
            likeMenu = RecipeKeeper.loadMenu(RecipeKeeper.LIKE_RECIPES);
            next.get().setButtons(likeMenu);
        }


        return next;
    }

}
