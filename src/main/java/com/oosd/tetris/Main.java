package com.oosd.tetris;

import com.oosd.tetris.model.Board;
import com.oosd.tetris.model.GameState;
import com.oosd.tetris.model.Tetromino;
import com.oosd.tetris.model.TetrominoFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private static final int CELL_SIZE = 25;
    private static final Color EMPTY_COLOR = Color.web("#1e1e1e");
    private static final Color LOCKED_COLOR = Color.web("#5c6bc0");
    private static final Color ACTIVE_COLOR = Color.web("#e91e63");
    private static final Duration TICK_RATE = Duration.millis(333);

    private final Board board = new Board();

    private Rectangle[][] cellViews;
    private Tetromino currentPiece;
    private Timeline timeline;
    private GameState state;
    private int score;
    private Label scoreLabel;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        cellViews = new Rectangle[Board.HEIGHT][Board.WIDTH];

        for (int row = 0; row < Board.HEIGHT; row++) {
            RowConstraints rowConstraints = new RowConstraints(CELL_SIZE);
            rowConstraints.setFillHeight(false);
            grid.getRowConstraints().add(rowConstraints);
        }
        for (int col = 0; col < Board.WIDTH; col++) {
            ColumnConstraints colConstraints = new ColumnConstraints(CELL_SIZE);
            colConstraints.setFillWidth(false);
            grid.getColumnConstraints().add(colConstraints);
        }

        for (int row = 0; row < Board.HEIGHT; row++) {
            for (int col = 0; col < Board.WIDTH; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(EMPTY_COLOR);
                cell.setStroke(Color.web("#333333"));
                cellViews[row][col] = cell;
                grid.add(cell, col, row);
            }
        }

        int gridWidth = Board.WIDTH * CELL_SIZE;
        int gridHeight = Board.HEIGHT * CELL_SIZE;
        grid.setMinSize(gridWidth, gridHeight);
        grid.setPrefSize(gridWidth, gridHeight);
        grid.setMaxSize(gridWidth, gridHeight);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setTextFill(Color.WHITE);
        statusLabel = new Label();
        statusLabel.setTextFill(Color.web("#e91e63"));

        VBox sidebar = new VBox(10, scoreLabel, statusLabel);
        sidebar.setPadding(new Insets(10));
        sidebar.setMinWidth(120);
        sidebar.setPrefWidth(120);

        BorderPane root = new BorderPane();
        root.setCenter(grid);
        root.setRight(sidebar);
        root.setStyle("-fx-background-color: #121212;");

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(this::handleKeyPress);

        primaryStage.setTitle("Tetris - 2006ICT");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();

        timeline = new Timeline(new KeyFrame(TICK_RATE, e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        state = GameState.RUNNING;
        spawnPiece();
        render();
        timeline.play();
    }

    private void spawnPiece() {
        currentPiece = TetrominoFactory.createRandomPiece();
        if (!board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            endGame();
        }
    }

    private void tick() {
        if (state != GameState.RUNNING) {
            return;
        }
        if (board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
            currentPiece.moveDown();
        } else {
            lockAndClear();
        }
        render();
    }

    private void lockAndClear() {
        board.lockPiece(currentPiece);
        int cleared = board.clearFullRows();
        if (cleared > 0) {
            score += scoreForLines(cleared);
            scoreLabel.setText("Score: " + score);
        }
        spawnPiece();
    }

    private int scoreForLines(int lines) {
        return switch (lines) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 500;
            case 4 -> 800;
            default -> 0;
        };
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.P && state != GameState.GAME_OVER) {
            togglePause();
            return;
        }
        if (state != GameState.RUNNING) {
            return;
        }

        switch (event.getCode()) {
            case LEFT -> {
                if (board.canPlace(currentPiece, currentPiece.getX() - 1, currentPiece.getY())) {
                    currentPiece.moveLeft();
                }
            }
            case RIGHT -> {
                if (board.canPlace(currentPiece, currentPiece.getX() + 1, currentPiece.getY())) {
                    currentPiece.moveRight();
                }
            }
            case DOWN -> {
                if (board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
                    currentPiece.moveDown();
                } else {
                    lockAndClear();
                }
            }
            case UP -> tryRotate();
            case SPACE -> hardDrop();
            default -> { }
        }
        render();
    }

    private void tryRotate() {
        currentPiece.rotate();
        if (!board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            currentPiece.rotate();
            currentPiece.rotate();
            currentPiece.rotate();
        }
    }

    private void hardDrop() {
        while (board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
            currentPiece.moveDown();
        }
        lockAndClear();
    }

    private void togglePause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            timeline.pause();
            statusLabel.setText("PAUSED");
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            timeline.play();
            statusLabel.setText("");
        }
    }

    private void endGame() {
        state = GameState.GAME_OVER;
        timeline.stop();
        statusLabel.setText("GAME OVER");
    }

    private void render() {
        for (int row = 0; row < Board.HEIGHT; row++) {
            for (int col = 0; col < Board.WIDTH; col++) {
                cellViews[row][col].setFill(board.isCellOccupied(row, col) ? LOCKED_COLOR : EMPTY_COLOR);
            }
        }
        drawPiece(currentPiece);
    }

    private void drawPiece(Tetromino piece) {
        int[][] shape = piece.getShape();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int boardRow = piece.getY() + row;
                    int boardCol = piece.getX() + col;

                    if (boardRow >= 0 && boardRow < Board.HEIGHT
                            && boardCol >= 0 && boardCol < Board.WIDTH) {
                        cellViews[boardRow][boardCol].setFill(ACTIVE_COLOR);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}