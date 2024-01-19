package hr.algebra.javatwo.controller;

import hr.algebra.javatwo.GameApplication;
import hr.algebra.javatwo.model.ClankColor;
import hr.algebra.javatwo.model.GameState;
import hr.algebra.javatwo.model.GridCell;
import hr.algebra.javatwo.model.RoleName;
import hr.algebra.javatwo.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hr.algebra.javatwo.model.Constants.*;
import static hr.algebra.javatwo.utils.DocumentationUtils.appendModifier;
import static hr.algebra.javatwo.utils.DocumentationUtils.getFullyQualifiedName;

public class GameController {


    @FXML
    private Button stepButton;
    @FXML
    private Button useButton;
    @FXML
    private Button skipButton;
    @FXML
    private Button newGameButton;

    @FXML
    private Label redLivesLabel;

    @FXML
    private Label blueLivesLabel;

    @FXML
    private ImageView redPlayerImage;
    @FXML
    private ImageView bluePlayerImage;

    @FXML
    private ImageView dragonStepImage;

    @FXML
    private ImageView goldImage;
    @FXML
    private GridPane boardGridPane;

    @FXML
    private GridPane dragonStepGridPane;

    @FXML
    private Pane clankPane;
    @FXML
    private Pane bagPane;

    @FXML
    private Label timerLabel;
    @FXML
    private Label playerLabel;

    @FXML
    private TextField chatMessageTextField;

    @FXML
    private TextFlow chatMessagesTextFlow;


    private boolean redPlayerTurn = true;
    private List<ClankColor> bag = new ArrayList<>();
    private List<ClankColor> clank = new ArrayList<>();
    private int dragonPosition = 0;

    private int timeInSeconds;
    private String winner;
    private Timeline timeline;

    public void initialize() {

        skipButton.setDisable(true);
        useButton.setDisable(true);
        newGameButton.setVisible(false);

        stepButton.setText("0");
        timeInSeconds = SHOW_GOLD_AFTER_SEC;
        blueLivesLabel.setText(String.valueOf(NUM_LIVES));
        redLivesLabel.setText(String.valueOf(NUM_LIVES));
        clankPane.getChildren().clear();
        bagPane.getChildren().clear();

        boardGridPane.getChildren().clear();
        dragonStepGridPane.getChildren().clear();

        boardGridPane.add(bluePlayerImage, 0, 0);
        boardGridPane.add(redPlayerImage, 0, 0);
        dragonStepGridPane.add(dragonStepImage, 0, 0);
        goldImage.setVisible(false);

        final Timeline timelineChat = ChatUtils.CheckMessages(chatMessagesTextFlow);
        timelineChat.play();


        if (GameApplication.loggedInRoleName.equals(RoleName.SERVER)) {
            stepButton.setDisable(true);
            ChatUtils.StartRmiRemoteChatServer();

        } else {
            stepButton.setDisable(false);
            ChatUtils.StartRmiRemoteChatClient();
            for (int i = 0; i < NUM_DRAGONS; i++) {
                placeDragons();
            }
        }


        for (int i = 0; i < NUM_DRAGONS_IN_BAG; i++) {
            bag.add(ClankColor.D);
        }
        StartTimer();

        chatMessageTextFieldAddEventListener();

    }


    private void StartTimer() {

        timeline = new Timeline(new KeyFrame(
                Duration.seconds(1), actionEvent -> {
            timeInSeconds--;

            int remainingSeconds = timeInSeconds % 60;
            int minutes = timeInSeconds / 60;
            String formattedTime = String.format("%02d:%02d", minutes, remainingSeconds);
            timerLabel.setText(formattedTime);

            if (timeInSeconds <= 0) {
                timeline.stop();
                ShowGold();
            }

        }
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();


    }


    private void ShowGold() {
        Random random = new Random();
        int randomRow = random.nextInt(5);
        int randomCol = random.nextInt(5);

        goldImage.setVisible(true);

        boardGridPane.add(goldImage, randomCol, randomRow);


    }

    private void placeDragons() {
        Random random = new Random();
        int randomRow = random.nextInt(5);
        int randomCol = random.nextInt(5);

        if (randomRow == 0 && randomCol == 0) {
            randomRow = 2;
            randomCol = 2;
        }

        addDragon(randomCol, randomRow);
    }

    private void addDragon(int col, int row) {
        Label dragonLabel = new Label("D");
        dragonLabel.setTextFill(Color.RED);
        dragonLabel.setAlignment(Pos.BOTTOM_RIGHT);
        dragonLabel.setPrefSize(150, 150);
        boardGridPane.add(dragonLabel, col, row);
    }

    @FXML
    protected void onStepButtonClick() {
        rollDice();
        SendGameState(new GameState(stepButton.getText()));
    }

    @FXML
    public void onSendButtonClick() {
        ChatUtils.sendMessage(chatMessageTextField.getText());
        chatMessageTextField.clear();
    }

    private void chatMessageTextFieldAddEventListener() {
        chatMessageTextField.setOnKeyPressed(event -> {
            if (event.getCode().getName().equals("Enter")) {
                onSendButtonClick();
            }
        });
    }

    private void SendGameState(GameState gameStateToSend) {
        if (GameApplication.loggedInRoleName.equals(RoleName.CLIENT)) {
            NetworkingUtils.sendGameStateToServer(gameStateToSend);
        } else {
            NetworkingUtils.sendGameStateToClient(gameStateToSend);
        }

    }

    @FXML
    protected void onUseButtonClick() {
        useSteps();
        List<GridCell> gameBoardState = GameStateUtils.createGameBoardState(boardGridPane);
        SendGameState(new GameState(gameBoardState, timeInSeconds, bag, clank, redPlayerTurn, redLivesLabel.getText(), blueLivesLabel.getText(), dragonPosition, stepButton.getText(), winner));
        stepButton.setDisable(true);
        skipButton.setDisable(true);
        useButton.setDisable(true);
    }

    @FXML
    protected void onSkipButtonClick() {
        switchPlayer();
        List<GridCell> gameBoardState = GameStateUtils.createGameBoardState(boardGridPane);
        SendGameState(new GameState(gameBoardState, timeInSeconds, bag, clank, redPlayerTurn, redLivesLabel.getText(), blueLivesLabel.getText(), dragonPosition, stepButton.getText(), winner));
    }

    private void rollDice() {
        stepButton.setDisable(true);
        skipButton.setDisable(false);
        useButton.setDisable(false);
        Random random = new Random();
        int step = random.nextInt(6) + 1;
        stepButton.setText(Integer.toString(step));
    }
    private void useSteps() {
        stepButton.setDisable(false);
        skipButton.setDisable(true);

        ImageView currentPlayer = redPlayerTurn ? redPlayerImage : bluePlayerImage;
        int step = Integer.parseInt(stepButton.getText());
        movePlayer(currentPlayer, step);
        if (GridPane.getColumnIndex(dragonStepImage) < 5) MoveDragon();
        switchPlayer();
    }

    private void switchPlayer() {
        stepButton.setDisable(false);
        useButton.setDisable(true);
        skipButton.setDisable(true);
        redPlayerTurn = !redPlayerTurn;
        playerLabel.setText(redPlayerTurn ? "Red" : "Blue");
        playerLabel.setTextFill(redPlayerTurn ? Color.RED : Color.BLUE);
    }

    private boolean CheckWinner() {
        int redLives = Integer.parseInt(redLivesLabel.getText());
        int blueLives = Integer.parseInt(blueLivesLabel.getText());

        if (redLives < 1) {
            winner = "Blue";
            AlertWinner();
            return true;
        }
        if (blueLives < 1) {
            winner = "Blue";
            AlertWinner();

            return true;
        }
        return false;
    }

    private void AlertWinner() {
        DialogUtils.showDialog(Alert.AlertType.INFORMATION,
                "Game Finished!", "Winner is " + winner + " player");
        newGameButton.setVisible(true);
        timeline.stop();

    }

    private void MoveDragon() {
        dragonPosition = GridPane.getColumnIndex(dragonStepImage) + 1;
        GridPane.setColumnIndex(dragonStepImage, dragonPosition);
    }


    private void movePlayer(ImageView player, int step) {

        int currentRow = GridPane.getRowIndex(player);
        int currentCol = GridPane.getColumnIndex(player);

        currentCol += step;

        if (currentCol > 4) {
            int dif = currentCol - 5;
            currentRow++;
            currentCol = dif;
        }

        currentRow = Math.min(currentRow, 4);
        currentCol = Math.min(currentCol, 4);


        if (currentCol == 4 && currentRow == 4) {
            currentRow = 0;
            currentCol = 0;
        }


        if (currentCol * currentRow > 24) {
            return;
        }

        GridPane.setRowIndex(player, currentRow);
        GridPane.setColumnIndex(player, currentCol);


        int rectangleAdd = (int) Math.ceil((double) step / 2);

        addPaneRectangle(redPlayerTurn ? Color.RED : Color.BLUE, rectangleAdd, clankPane, false);

        if (stepOnGold(currentCol, currentRow)) {
            winner = redPlayerTurn ? "Red" : "Blue";
            AlertWinner();
        }

        if (stepOnDragon(currentCol, currentRow)) {
            bag.addAll(clank);
            Collections.shuffle(bag);

            List<ClankColor> selectedFromBag = new ArrayList<>(bag.subList(0, dragonPosition + 1));

            bagPane.getChildren().clear();
            for (ClankColor element : selectedFromBag) {

                Color color = null;
                switch (element) {
                    case D -> {
                        color = Color.BLACK;
                        if (dragonPosition > 0) dragonPosition--;
                        GridPane.setColumnIndex(dragonStepImage, dragonPosition);
                    }
                    case R -> {
                        color = Color.RED;
                        int redLives = Integer.parseInt(redLivesLabel.getText()) - 1;
                        redLivesLabel.setText(Integer.toString(redLives));
                        if (CheckWinner()) return;
                    }
                    case B -> {
                        color = Color.BLUE;
                        int blueLives = Integer.parseInt(blueLivesLabel.getText()) - 1;
                        blueLivesLabel.setText(Integer.toString(blueLives));
                        if (CheckWinner()) return;
                    }
                }


                addPaneRectangle(color, 1, bagPane, false);
            }

            clankPane.getChildren().clear();
            clank.clear();
            bag.subList(0, dragonPosition + 1).clear();
            bag = bag.stream().filter(el -> el.equals(ClankColor.D)).collect(Collectors.toList());


        }
    }

    private void addPaneRectangle(Color color, int rectangleAdd, Pane pane, boolean skipAdding) {
        for (int i = 0; i < rectangleAdd; i++) {
            Rectangle playerRectangle = new Rectangle(20, 20, color);
            int clankSize = pane.getChildren().size();
            playerRectangle.setTranslateX(clankSize * 25);
            pane.getChildren().add(playerRectangle);
            if (!skipAdding) clank.add(redPlayerTurn ? ClankColor.R : ClankColor.B);
        }

    }

    private boolean stepOnDragon(int col, int row) {
        Node node = getNodeByRowColIndex(col, row, boardGridPane);
        return node instanceof Label;
    }


    private boolean stepOnGold(int col, int row) {
        Node node = getNodeByRowColIndex(col, row, boardGridPane);
        return node instanceof ImageView;

    }

    private Node getNodeByRowColIndex(int col, int row, GridPane gridPane) {
        Node res = null;
        ObservableList<Node> children = gridPane.getChildren();

        for (Node n : children) {
            if (GridPane.getColumnIndex(n) != null && GridPane.getColumnIndex(n) == col && GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) == row) {
                if (!(n instanceof ImageView image)) {
                    res = n;
                    break;
                } else {
                    String url = image.getImage().getUrl();
                    if (url.contains("gold.png")) {
                        res = n;
                    }
                }

            }

        }
        return res;
    }


    public void saveGame() {
        List<GridCell> gameBoardState = GameStateUtils.createGameBoardState(boardGridPane);

        FileUtils.saveGame(gameBoardState, timeInSeconds, bag, clank, redPlayerTurn, redLivesLabel.getText(),
                blueLivesLabel.getText(), dragonPosition, stepButton.getText());
    }


    public void RefreshGameBoard(GameState gameState) {

        if (gameState.getWinner() != null) {
            System.out.println(gameState.getWinner());
            winner = gameState.getWinner();
            AlertWinner();
        }

        redPlayerTurn = !gameState.isRedPlayerTurn();
        switchPlayer();

        timeInSeconds = gameState.getTimeInSeconds();
        timeline.stop();
        StartTimer();


        SetBoardGameElements(gameState);

        bag.clear();
        bag = gameState.getBag();
        clank.clear();
        clank = gameState.getClank();
        clankPane.getChildren().clear();
        clank.forEach(clankColor -> addPaneRectangle(clankColor.equals(ClankColor.R) ? Color.RED : Color.BLUE, 1, clankPane, true));

        redLivesLabel.setText(gameState.getRedLives());
        blueLivesLabel.setText(gameState.getBlueLives());
        dragonPosition = gameState.getDragonPosition();
        GridPane.setColumnIndex(dragonStepImage, dragonPosition);
    }

    public void RefreshStepButton(GameState gameState) {
        stepButton.setText(gameState.getLastStep());

    }

    public void loadGame() {

        GameState recoveredGameState = FileUtils.loadGame();

        if (recoveredGameState != null) {

            SetBoardGameElements(recoveredGameState);

            timeInSeconds = recoveredGameState.getTimeInSeconds();
            timeline.stop();
            StartTimer();

            bag.clear();
            bag = recoveredGameState.getBag();

            clank.clear();
            clank = recoveredGameState.getClank();
            clankPane.getChildren().clear();
            clank.forEach(clankColor -> addPaneRectangle(clankColor.equals(ClankColor.R) ? Color.RED : Color.BLUE, 1, clankPane, true));

            redPlayerTurn = recoveredGameState.isRedPlayerTurn();
            redLivesLabel.setText(recoveredGameState.getRedLives());
            blueLivesLabel.setText(recoveredGameState.getBlueLives());
            dragonPosition = recoveredGameState.getDragonPosition();
            GridPane.setColumnIndex(dragonStepImage, dragonPosition);
            stepButton.setText(recoveredGameState.getLastStep());

            DialogUtils.showDialog(Alert.AlertType.INFORMATION,
                    "Game loaded!", "Your game has been successfully loaded!");
        }
    }

    private void SetBoardGameElements(GameState recoveredGameState) {
        List<GridCell> gameBoardState = recoveredGameState.getGameBoardState();
        boardGridPane.getChildren().clear();
        gameBoardState.forEach(cell -> {
            String node = cell.getNode();
            int startIndex = node.indexOf("id=");
            if (startIndex != -1) {
                int endIndex = node.indexOf(",");
                String id = node.substring(startIndex + 3, endIndex);
                if (id.contains("red")) {
                    boardGridPane.add(redPlayerImage, cell.getCol(), cell.getRow());
                } else if (id.contains("blue")) {
                    boardGridPane.add(bluePlayerImage, cell.getCol(), cell.getRow());
                } else if (id.contains("gold")) {
                    boardGridPane.add(goldImage, cell.getCol(), cell.getRow());
                }
            } else {
                addDragon(cell.getCol(), cell.getRow());
            }
        });
    }

    public void generateDocumentation() {

        String projectPath = System.getProperty("user.dir");
        Path targetPath = Path.of(projectPath, "target");

        String headerHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                <title>Clank! game documentation</title>
                </head>
                <body>
                                
                                
                """;

        try (Stream<Path> paths = Files.walk(targetPath)) {
            List<String> classFiles = paths
                    .map(Path::toString)
                    .filter(file -> file.endsWith(".class"))
                    .filter(file -> !file.endsWith("module-info.class"))
                    .toList();

            for (String classFile : classFiles) {
                String fullyQualifiedName = getFullyQualifiedName(classFile);

                Class<?> deserializedClass = Class.forName(fullyQualifiedName);

                headerHtml += "<h2>" + fullyQualifiedName + "</h2>";
                headerHtml += "<ul>";


                Field[] classFields = deserializedClass.getDeclaredFields();
                for (Field field : classFields) {
                    headerHtml += ("<li>");
                    appendModifier(field.getModifiers());
                    headerHtml += field.getType().getTypeName() + '\n';
                    headerHtml += field.getName() + '\n';
                    headerHtml += "</li>";
                }

                headerHtml += "</ul>";
            }

            String footerHtml = """
                    </body>
                    </html>
                    """;

            String generatedHtml = headerHtml + footerHtml;


            Path documentationFilePath = Path.of("files/documentation.html");
            DialogUtils.showDialog(Alert.AlertType.INFORMATION,
                    "Documentation  generated!", "Documentation has been successfully generated!");


            Files.write(documentationFilePath, generatedHtml.getBytes());
        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showDialog(Alert.AlertType.INFORMATION,
                    "Error!", "An error occurred during documentation generation!");


            throw new RuntimeException(e);
        }
    }


}