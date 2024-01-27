package hr.algebra.javatwo.controller;

import hr.algebra.javatwo.GameApplication;
import hr.algebra.javatwo.Threads.GetLastMoveThread;
import hr.algebra.javatwo.Threads.SaveGameMoveThread;
import hr.algebra.javatwo.model.*;
import hr.algebra.javatwo.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static hr.algebra.javatwo.model.Constants.*;
import static hr.algebra.javatwo.utils.DocumentationUtils.*;

public class GameController {
    @FXML
    private Button stepButton, useButton, skipButton, newGameButton, sendButton, replayButton;
    @FXML
    private Label redLivesLabel, blueLivesLabel, stepsLabel, lastMoveLabel, nextPlayerLabel;
    @FXML
    private ImageView redPlayerImage, bluePlayerImage, dragonStepImage, goldImage;
    @FXML
    private GridPane dragonStepGridPane, boardGridPane;
    @FXML
    private Pane bagPane, clankPane;
    @FXML
    private TextField chatMessageTextField;
    @FXML
    private TextFlow chatMessagesTextFlow;
    private boolean redPlayerTurn = true, goldShowed = false;
    private List<ClankColor> bag = new ArrayList<>();
    private List<ClankColor> clank = new ArrayList<>();
    private int dragonPosition = 0;
    private String winner;

    public void initialize() {
        GameUtils.ButtonsDisable(stepButton, false, skipButton, true, useButton, true);
        List<Node> elementsToHide = Arrays.asList(newGameButton, replayButton, goldImage, lastMoveLabel);
        GameUtils.hideElements(elementsToHide);
        resetGame();
        List<Pane> elementsToClear = Arrays.asList(clankPane, bagPane, boardGridPane, dragonStepGridPane);
        GameUtils.clearChildren(elementsToClear);
        boardGridPane.add(bluePlayerImage, 0, 0);
        boardGridPane.add(redPlayerImage, 0, 0);
        dragonStepGridPane.add(dragonStepImage, 0, 0);
        RoleName loggedInRole = GameApplication.loggedInRoleName;
        switch (loggedInRole) {
            case BLUE:
                CheckMessages();
                stepButton.setDisable(true);
                ChatUtils.StartRmiRemoteChatServer();
                break;
            case RED:
                CheckMessages();
                stepButton.setDisable(false);
                for (int i = 0; i < NUM_DRAGONS; i++) {
                    GameUtils.placeDragons(boardGridPane);
                }
                ChatUtils.StartRmiRemoteChatClient();
                break;
            case SINGLE_PLAYER:
                lastMoveLabel.setVisible(true);
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
        stepsLabel.setText(String.valueOf(SHOW_GOLD_AFTER_STEPS));
        goldShowed=false;
        winner = null;
        chatMessageTextFieldAddEventListener();
        XmlUtils.deleteGameMovesFile();
    }

    private void resetGame() {
        stepButton.setText("0");
        blueLivesLabel.setText(String.valueOf(NUM_LIVES));
        redLivesLabel.setText(String.valueOf(NUM_LIVES));
    }

    private void CheckMessages() {
        final Timeline timelineChat = ChatUtils.CheckMessages(chatMessagesTextFlow);
        timelineChat.play();
    }
    @FXML
    protected void onStepButtonClick() {
        GameUtils.rollDice(stepButton);
        int currentSteps = Integer.parseInt(stepsLabel.getText()) - Integer.parseInt(stepButton.getText());
        if (!goldShowed){
            if (currentSteps<0){
                currentSteps=0;
            }
            stepsLabel.setText(String.valueOf(currentSteps));
            if (Integer.parseInt(stepsLabel.getText()) <=0) {
                goldShowed=true;
                GameUtils.ShowImage(boardGridPane, goldImage);
            }
        }
        GameUtils.ButtonsDisable(stepButton, true, skipButton, false, useButton, false);
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
        if (GameApplication.loggedInRoleName.equals(RoleName.RED)) {
            NetworkingUtils.sendGameStateToServer(gameStateToSend);
        } else if (GameApplication.loggedInRoleName.equals(RoleName.BLUE)) {
            NetworkingUtils.sendGameStateToClient(gameStateToSend);
        }
    }

    @FXML
    protected void onUseButtonClick() {
        GameMove gameMove = new GameMove(redPlayerTurn, Integer.parseInt(stepButton.getText()), LocalDateTime.now());
        XmlUtils.saveNewGameMove(gameMove);
        SaveGameMoveThread saveGameMoveThread = new SaveGameMoveThread(gameMove);
        Thread threadStarter = new Thread(saveGameMoveThread);
        threadStarter.start();
        useSteps();
        List<GridCell> gameBoardState = GameStateUtils.createGameBoardState(boardGridPane);
        SendGameState(new GameState(gameBoardState, Integer.parseInt(stepsLabel.getText()), bag, clank, redPlayerTurn,goldShowed, redLivesLabel.getText(),
                blueLivesLabel.getText(), dragonPosition, stepButton.getText(), winner));
        if (!GameApplication.loggedInRoleName.equals(RoleName.SINGLE_PLAYER)) {
            GameUtils.ButtonsDisable(stepButton, true, skipButton, true, useButton, true);
        }
    }

    @FXML
    protected void onSkipButtonClick() {
        switchPlayer();
        List<GridCell> gameBoardState = GameStateUtils.createGameBoardState(boardGridPane);
        SendGameState(new GameState(gameBoardState, Integer.parseInt(stepsLabel.getText()), bag, clank, redPlayerTurn,goldShowed, redLivesLabel.getText(),
                blueLivesLabel.getText(), dragonPosition, stepButton.getText(), winner));
    }


    private void useSteps() {
        GameUtils.ButtonsDisable(stepButton, false, skipButton, true, useButton, true);
        ImageView currentPlayer = redPlayerTurn ? redPlayerImage : bluePlayerImage;
        int step = Integer.parseInt(stepButton.getText());
        movePlayer(currentPlayer, step);
        if (GridPane.getColumnIndex(dragonStepImage) < 5) MoveDragon();
        switchPlayer();
    }

    private void switchPlayer() {
        GameUtils.ButtonsDisable(stepButton, false, skipButton, true, useButton, true);
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
            return true;
        }
        if (blueLives < 1) {
            winner = "Red";
            AlertWinner();
            return true;
        }
        return false;
    }

    private void AlertWinner() {
        DialogUtils.showDialog(Alert.AlertType.INFORMATION,
                "Game Finished!", "Winner is " + winner + " player");
        newGameButton.setVisible(true);
        replayButton.setVisible(true);
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
        if (winner == null){
            addPaneRectangle(redPlayerTurn ? Color.RED : Color.BLUE, rectangleAdd, clankPane, false);

        }
        if (GameUtils.stepOnGold(currentCol, currentRow, boardGridPane)&&winner==null) {
            winner = redPlayerTurn ? "Red" : "Blue";
            AlertWinner();
        }
        if (GameUtils.stepOnDragon(currentCol, currentRow, boardGridPane) && winner == null) {
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
        FileUtils.saveGame(gameBoardState, Integer.parseInt(stepsLabel.getText()), bag, clank, redPlayerTurn,goldShowed, redLivesLabel.getText(),
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
        stepsLabel.setText(String.valueOf(gameState.gettepsToShowGold()));
        SetBoardGameElements(gameState);
        bag.clear();
        if (gameState.isGoldShowed()){
            goldShowed=true;
            goldImage.setVisible(true);
        }

        bag = gameState.getBag();
        clank.clear();
        clank = gameState.getClank();
        clankPane.getChildren().clear();
        clank.forEach(clankColor -> addPaneRectangle(clankColor.equals(ClankColor.R) ? Color.RED : Color.BLUE,
                1, clankPane, true));
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
            stepsLabel.setText(String.valueOf(recoveredGameState.gettepsToShowGold()));
            bag.clear();
            bag = recoveredGameState.getBag();
            clank.clear();
            if (recoveredGameState.isGoldShowed()){
                goldShowed=true;
                goldImage.setVisible(true);
            }
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
                GameUtils.addDragon(cell.getCol(), cell.getRow(), boardGridPane);
            }
        });
    }

    public void replayGame() {
        goldImage.setVisible(false);
        resetGame();
        List<ImageView> elementsToSetOnStart = Arrays.asList(dragonStepImage, redPlayerImage, bluePlayerImage);
        GameUtils.setToStart(elementsToSetOnStart);
        List<GameMove> gameMoveList = XmlUtils.getAllGameMove();
        Timeline replayTimeline = getTimeline(gameMoveList);
        replayTimeline.play();
    }

    private Timeline getTimeline(List<GameMove> gameMoveList) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Timeline replayTimeline = new Timeline(new KeyFrame(
                Duration.seconds(1), actionEvent -> {
            GameMove gameMove = gameMoveList.get(atomicInteger.get());
            boolean isRedTurn = gameMove.isRedTurn();
            int step = gameMove.getStep();
            System.out.println(gameMove.isRedTurn() + "-" + gameMove.getDateTime());
            ImageView currentPlayer = isRedTurn ? redPlayerImage : bluePlayerImage;
            redPlayerTurn = isRedTurn;
            movePlayer(currentPlayer, step);
            atomicInteger.set(atomicInteger.get() + 1);
        }));
        replayTimeline.setCycleCount(gameMoveList.size());
        return replayTimeline;
    }
}