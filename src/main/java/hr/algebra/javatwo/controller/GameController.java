package hr.algebra.javatwo.controller;

import hr.algebra.javatwo.GameApplication;
import hr.algebra.javatwo.Threads.GetLastMoveThread;
import hr.algebra.javatwo.Threads.SaveGameMoveThread;
import hr.algebra.javatwo.model.*;
import hr.algebra.javatwo.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static hr.algebra.javatwo.model.Constants.*;
import static hr.algebra.javatwo.utils.DocumentationUtils.*;

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
    private Button sendButton;

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
    private Label nextPlayerLabel;
    @FXML
    private Label lastMoveLabel;
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


        RoleName loggedInRole = GameApplication.loggedInRoleName;
        switch (loggedInRole) {
            case SERVER:
                CheckMessages();
                stepButton.setDisable(true);
                ChatUtils.StartRmiRemoteChatServer();
                break;
            case CLIENT:
                CheckMessages();
                stepButton.setDisable(false);
                for (int i = 0; i < NUM_DRAGONS; i++) {
                   GameUtils.placeDragons(boardGridPane);
                }
                ChatUtils.StartRmiRemoteChatClient();
                break;
            case SINGLE_PLAYER:
                stepButton.setDisable(false);
                for (int i = 0; i < NUM_DRAGONS; i++) {
                    GameUtils.placeDragons(boardGridPane);
                }
                sendButton.setDisable(true);
                chatMessageTextField.setDisable(true);
                GetLastMoveThread getLastMoveThread = new GetLastMoveThread(lastMoveLabel);
                Thread threadStarter = new Thread(getLastMoveThread);
                threadStarter.start();
                break;
        }


        for (int i = 0; i < NUM_DRAGONS_IN_BAG; i++) {
            bag.add(ClankColor.D);
        }
        StartTimer();

        chatMessageTextFieldAddEventListener();

    }
    private void CheckMessages() {
        final Timeline timelineChat = ChatUtils.CheckMessages(chatMessagesTextFlow);
        timelineChat.play();
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
               GameUtils.ShowImage(boardGridPane,goldImage);
            }

        }
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    @FXML
    protected void onStepButtonClick() {
        rollDice();
        SendGameState(new GameState(stepButton.getText()));

    }
    @FXML
    protected void generateDocumentationClick() {
        generateDocumentation();
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
        } else if (GameApplication.loggedInRoleName.equals(RoleName.SERVER)) {
            NetworkingUtils.sendGameStateToClient(gameStateToSend);
        }

    }

    @FXML
    protected void onUseButtonClick() {
        useSteps();
        List<GridCell> gameBoardState = GameStateUtils.createGameBoardState(boardGridPane);
        SendGameState(new GameState(gameBoardState, timeInSeconds, bag, clank, redPlayerTurn, redLivesLabel.getText(), blueLivesLabel.getText(), dragonPosition, stepButton.getText(), winner));
        if (!GameApplication.loggedInRoleName.equals(RoleName.SINGLE_PLAYER)) {
            stepButton.setDisable(true);
            skipButton.setDisable(true);
            useButton.setDisable(true);
        }
        GameMove gameMove = new GameMove(redPlayerTurn, Integer.parseInt(stepButton.getText()), LocalDateTime.now());
        SaveGameMoveThread saveGameMoveThread = new SaveGameMoveThread(gameMove);
        Thread threadStarter = new Thread(saveGameMoveThread);
        threadStarter.start();
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
        nextPlayerLabel.setText(redPlayerTurn ? "Red" : "Blue");
        nextPlayerLabel.setTextFill(redPlayerTurn ? Color.RED : Color.BLUE);
    }

    private boolean CheckWinner() {
        int redLives = Integer.parseInt(redLivesLabel.getText());
        int blueLives = Integer.parseInt(blueLivesLabel.getText());

        if (redLives < 1) {
            winner = "Blue";
            AlertWinner();
            timeline.stop();
            return true;
        }
        if (blueLives < 1) {
            winner = "Red";
            AlertWinner();
            timeline.stop();
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

        if (GameUtils.stepOnGold(currentCol, currentRow, boardGridPane)) {
            winner = redPlayerTurn ? "Red" : "Blue";
            AlertWinner();
        }

        if (GameUtils.stepOnDragon(currentCol, currentRow, boardGridPane)) {
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
                GameUtils.addDragon(cell.getCol(), cell.getRow(),boardGridPane);
            }
        });
    }


}