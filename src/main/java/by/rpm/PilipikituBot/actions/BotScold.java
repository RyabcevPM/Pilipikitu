package by.rpm.PilipikituBot.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BotScold  implements BotAction {
    private static final ArrayList<String> scold;


    @Override
    public String getName() {
        return "Поругай меня";
    }

    static {
        File f = new File("files/scold.txt");
        scold = new ArrayList<>();
        Scanner sc = null;
        try {
            sc = new Scanner(f);
            while (sc.hasNext()) scold.add(sc.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getInfo() {
        Random rand = new Random();
        return scold.get(rand.nextInt(scold.size()-1));
    }
}
