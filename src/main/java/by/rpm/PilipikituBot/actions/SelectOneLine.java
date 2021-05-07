package by.rpm.PilipikituBot.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SelectOneLine implements BotAction{
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        Random rand = new Random();
        return lines.get(rand.nextInt(lines.size()-1));
    }

    private String name;
    private ArrayList<String> lines;

    public SelectOneLine(String name, File file) {
        this.name = name;
        lines = new ArrayList<>();
        Scanner sc = null;
        try (Scanner scanner = sc = new Scanner(file)) {
            while (sc.hasNext()) lines.add(sc.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


}
