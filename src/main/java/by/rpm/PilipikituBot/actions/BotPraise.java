package by.rpm.PilipikituBot.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BotPraise implements BotAction {
    private static final ArrayList<String> prises;


    @Override
    public String getName() {
        return "Похвали меня";
    }

    static {
        File f = new File("files/prise.txt");
        prises = new ArrayList<>();
        Scanner sc = null;
        try {
            sc = new Scanner(f);
            while (sc.hasNext()) prises.add(sc.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getInfo() {
        Random rand = new Random();
        return prises.get(rand.nextInt(prises.size()-1));
    }
}
