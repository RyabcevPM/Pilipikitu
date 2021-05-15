package by.rpm.PilipikituBot.actions.Food;

import by.rpm.PilipikituBot.actions.ActionChain;
import by.rpm.PilipikituBot.actions.FinalMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class RecipeKeeper extends ActionChain {
    private static String SAVE = "Сохарнить";
    private static String CANCEL = "Отмена";
    private static String CONFIRM_MESSAGE = "Отправте мне знавание и затем нажмите \"" + SAVE + "\"";
    private static String APPLY_NO_MESSAGE = "Хорошо, не буду. И вообще забыл.";
    private String url;
    private String segment;
    private static ArrayList<ActionChain> likeMenu;
    public static String LIKE_RECIPES = "D:\\Java\\mvn_project\\Pilipikitu\\src\\main\\resources\\files\\2.txt";
    public static String NEW_RECIPES = "D:\\Java\\mvn_project\\Pilipikitu\\src\\main\\resources\\files\\RecipesNew.xml";


    public RecipeKeeper(String name, String url, String segment, ArrayList<ActionChain> likeMenu) {
        super(name, CONFIRM_MESSAGE);
        System.out.println(name);
        getButtons().add(new ActionChain(SAVE));
        getButtons().add(new FinalMessage(CANCEL, APPLY_NO_MESSAGE));
        this.url = url;
        this.segment = segment;
        this.likeMenu = likeMenu;
    }

    @Override
    public void setInputText(String inputText) {
        if (super.getNext(inputText).isPresent()) return;
        super.setInputText(inputText);
        if (!inputText.isEmpty()) message = inputText + " - принято. Сохранить?";
    }

    @Override
    public Optional<ActionChain> getNext(String buttonName) {
        if (buttonName.equals(SAVE) && inputText != "") {
            SaveNewLike();
            String finalMessage = String.format("Рецепт \"%s\" добавлен в раздел: \"%s\"", inputText, segment);
            System.out.println(finalMessage);
            return Optional.of(new FinalMessage(finalMessage));
        }
        return Optional.of(this);
    }

    private void SaveNewLike() {
        try {
            XMLOutputFactory output = XMLOutputFactory.newInstance();
//            XMLStreamWriter writer = output.createXMLStreamWriter(new FileWriter(FoodMaster.LIKE_RECIPES));
            XMLStreamWriter writer = output.createXMLStreamWriter(new FileWriter("D:\\Java\\mvn_project\\Pilipikitu\\src\\main\\resources\\files\\2.txt"));

            writer.writeStartDocument("1.0");
            writer.writeStartElement("recipes");
            for (int i = 0; i < likeMenu.size(); i++) {
                writer.writeStartElement("segment");

                writer.writeStartElement("name");
                writer.writeCharacters(likeMenu.get(i).getName());
                writer.writeEndElement();

                writer.writeStartElement("list");
                if (likeMenu.get(i).getName().equals(segment)) {
                    writer.writeStartElement("a");
                    writer.writeAttribute("href", url);
                    writer.writeCharacters(inputText);
                    writer.writeEndElement();

                }
                for (ActionChain action: likeMenu.get(i).getButtons()) {
                    writer.writeStartElement("a");
                    writer.writeAttribute("href", ((FoodRecipe)action).getLink());
                    writer.writeCharacters(action.getName());
                    writer.writeEndElement();
                }
                writer.writeEndElement();
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
        } catch (XMLStreamException | IOException ex) {
            ex.printStackTrace();
        }
    }


    static ArrayList<ActionChain> loadMenu(String fileName) {
        ArrayList<ActionChain> menu = new ArrayList<>();
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(fileName);

            XPathFactory pathFactory = XPathFactory.newInstance();
            XPath xpath = pathFactory.newXPath();
            XPathExpression expr = xpath.compile("recipes/segment");
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            ArrayList<ActionChain> list;
            String url;
            String name;
            String sectionName = "default";
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                NodeList nl = n.getChildNodes();
                list = new ArrayList<>();
                for (int e = 0; e < nl.getLength(); e++) {
                    if (nl.item(e).getNodeName().equals("name")) sectionName = nl.item(e).getTextContent();
                    if (nl.item(e).getNodeName().equals("list")) {
                        NodeList links = nl.item(e).getChildNodes();

                        for (int j = 0; j < links.getLength(); j++) {
                            if (links.item(j).getNodeName().equals("a")) {
                                url = links.item(j).getAttributes().getNamedItem("href").getNodeValue();
                                name = links.item(j).getTextContent();
                                list.add(new FoodRecipe(name, url));
                            }
                        }

                    }
                }
                menu.add(new ActionChain(sectionName, list));

            }
        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }

        return menu;
    }
}
