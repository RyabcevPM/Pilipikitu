package by.rpm.PilipikituBot.actions;

import by.rpm.external.api.Telegram;
import org.apache.camel.component.telegram.model.OutgoingMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class SelectOneLine extends ActionChain implements IsFinalAction {
    private int resultInd;
    private ArrayList<String> lines;
    private Telegram.TextType textType;

    public SelectOneLine(String name, File file) {
        this(name, file, Telegram.TextType.NORMAL);
    }

    public SelectOneLine(String name, File file, Telegram.TextType textType) {
        super(name);
        lines = new ArrayList<>();
        Scanner sc = null;
        resultInd = 0;
        this.textType = textType;
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
        if (++resultInd >= lines.size()) resultInd = 0;
        return Telegram.getText(lines.get(resultInd), textType);
    }


}
