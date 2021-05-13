package by.rpm.PilipikituBot.actions;

import by.rpm.external.api.Telegram;
import org.apache.camel.component.telegram.model.OutgoingMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class SelectOneLine extends ActionChain implements IsFinalAction{
    private int resultInd;
    private ArrayList<String> lines;

    public SelectOneLine(String name, File file) {
        super(name);
        lines = new ArrayList<>();
        Scanner sc = null;
        resultInd = 0;
        try {
            sc = new Scanner(file);
            while (sc.hasNext()) lines.add(sc.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sc.close();
        Collections.shuffle(lines);
    }

    @Override
    public OutgoingMessage getResult() {
        return Telegram.getText(lines.get(resultInd++));
    }


}
