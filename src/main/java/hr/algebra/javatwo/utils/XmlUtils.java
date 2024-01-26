package hr.algebra.javatwo.utils;

import hr.algebra.javatwo.model.GameMove;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {

    private static final String FILENAME = "files/xml/game_moves.xml";

    public static void saveNewGameMove(GameMove gameMove) {

        List<GameMove> gameMoveList = new ArrayList<>();

        if (Files.exists(Path.of(FILENAME))) {
            gameMoveList.addAll(getAllGameMove());
        }

        gameMoveList.add(gameMove);

        try {
            Document document = createDocument("gameMoves");

            for (GameMove move : gameMoveList) {
                Element gameMoveRootElement = document.createElement("gameMove");
                document.getDocumentElement().appendChild(gameMoveRootElement);
                gameMoveRootElement.appendChild(createElement(document, "isRedTurn", String.valueOf(move.isRedTurn())));
                gameMoveRootElement.appendChild(createElement(document, "step", String.valueOf(move.getStep())));
                gameMoveRootElement.appendChild(createElement(document, "dateTime", move.getDateTime().toString()));
            }
            saveDocument(document, FILENAME);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }


    private static void saveDocument(Document document, String fileName) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(new File(FILENAME)));
    }


    private static Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        return domImplementation.createDocument(null, element, null);
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }
    public static List<GameMove> getAllGameMove() {

        List<GameMove> gameMoves = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(FILENAME));

            Element rootElement = document.getDocumentElement();
            NodeList gameMovesNodeList = rootElement.getChildNodes();

            for (int i = 0; i < gameMovesNodeList.getLength(); i++) {
                Node gameMoveNode = gameMovesNodeList.item(i);
                if (gameMoveNode.getNodeType() == Node.ELEMENT_NODE) {
                    boolean isRedTurn = false;
                    int step = 0;
                    LocalDateTime dateTime = LocalDateTime.now();
                    Element gameMoveRootElement = (Element) gameMoveNode;

                    NodeList gameMoveRootElementChildList = gameMoveRootElement.getChildNodes();
                    for (int j = 0; j < gameMoveRootElementChildList.getLength(); j++) {

                        Node gameMoveRootElementChildNode = gameMoveRootElementChildList.item(j);
                        if (gameMoveRootElementChildNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element gameMoveChildElement = (Element) gameMoveRootElementChildNode;
                            switch (gameMoveChildElement.getTagName()) {
                                case "isRedTurn" ->
                                        isRedTurn = Boolean.parseBoolean(gameMoveChildElement.getTextContent());
                                case "step" -> step = Integer.parseInt(gameMoveChildElement.getTextContent());
                                case "dateTime" ->
                                        dateTime = LocalDateTime.parse(gameMoveChildElement.getTextContent());
                            }
                        }
                    }
                    GameMove newGameMove = new GameMove(isRedTurn, step, dateTime);
                    System.out.println(newGameMove.toString());
                    gameMoves.add(newGameMove);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return gameMoves;
    }

    public static void deleteGameMovesFile() {
        File file = new File(FILENAME);
        if (file.exists()) {
            file.delete();
        }
    }


}
