package tetris.controller;

import tetris.model.Board;
import tetris.model.GameState;
import tetris.model.Tetromino;
import tetris.model.TetrominoFactory;
import tetris.ui.GameScreen;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/*
 * Owns the gameplay state and rules. GameScreen only draws the values it receives.
 */
public class GameController {

    private static final Duration GRAVITY_INTERVAL = Duration.millis(333);

    private final Board board = new Board();
    private final GameScreen gameScreen;
    private final Color[][] lockedColors = new Color[Board.HEIGHT][Board.WIDTH];
    private final Timeline gravityTimeline;

    private Tetromino currentPiece;
    private GameState gameState = GameState.RUNNING;
    private int score;
    private boolean dropAnimationRunning;

    public GameController(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        gravityTimeline = new Timeline(new KeyFrame(GRAVITY_INTERVAL, event -> startGravityStep()));
        gravityTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void startGame() {
        gameScreen.setKeyHandler(this::handleKeyPress);
        spawnPiece();
        render();
        gravityTimeline.play();
        gameScreen.requestKeyboardFocus();
    }

    private void startGravityStep() {
        if (gameState != GameState.RUNNING || dropAnimationRunning) {
            return;
        }

        if (board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
            animateGravityDrop();
        } else {
            lockClearAndSpawn();
        }
    }

    private void animateGravityDrop() {
        dropAnimationRunning = true;
        gameScreen.animateActivePieceDown(() -> {
            currentPiece.moveDown();
            dropAnimationRunning = false;
            render();
        });
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.P && gameState != GameState.GAME_OVER) {
            togglePause();
            return;
        }

        if (gameState != GameState.RUNNING || dropAnimationRunning) {
            return;
        }

        switch (event.getCode()) {
            case LEFT -> moveHorizontally(-1);
            case RIGHT -> moveHorizontally(1);
            case DOWN -> softDrop();
            case UP -> tryRotate();
            case SPACE -> hardDrop();
            default -> {
                return;
            }
        }
        render();
    }

    private void moveHorizontally(int direction) {
        if (board.canPlace(currentPiece, currentPiece.getX() + direction, currentPiece.getY())) {
            if (direction < 0) {
                currentPiece.moveLeft();
            } else {
                currentPiece.moveRight();
            }
        }
    }

    private void softDrop() {
        if (board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
            currentPiece.moveDown();
        } else {
            lockClearAndSpawn();
        }
    }

    private void hardDrop() {
        while (board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
            currentPiece.moveDown();
        }
        lockClearAndSpawn();
    }

    private void tryRotate() {
        currentPiece.rotate();
        if (!board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            // Every supplied shape uses four rotations, so three more undo an invalid turn.
            currentPiece.rotate();
            currentPiece.rotate();
            currentPiece.rotate();
        }
    }

    private void lockClearAndSpawn() {
        saveCurrentPieceColours();
        board.lockPiece(currentPiece);
        int clearedLines = clearMatchingColourRows();
        int boardClearedLines = board.clearFullRows();

        if (clearedLines != boardClearedLines) {
            throw new IllegalStateException("Board and colour grid are out of sync");
        }
        if (clearedLines > 0) {
            score += scoreForLines(clearedLines);
            gameScreen.updateScore(score);
        }
        spawnPiece();
    }

    private void saveCurrentPieceColours() {
        int[][] shape = currentPiece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int boardRow = currentPiece.getY() + row;
                    int boardCol = currentPiece.getX() + col;
                    if (boardRow >= 0 && boardRow < Board.HEIGHT && boardCol >= 0 && boardCol < Board.WIDTH) {
                        lockedColors[boardRow][boardCol] = currentPiece.getColor();
                    }
                }
            }
        }
    }

    /** Mirrors Board.clearFullRows so a cleared line keeps the remaining block colours aligned. */
    private int clearMatchingColourRows() {
        int cleared = 0;
        for (int row = Board.HEIGHT - 1; row >= 0; row--) {
            if (isBoardRowFull(row)) {
                for (int targetRow = row; targetRow > 0; targetRow--) {
                    System.arraycopy(lockedColors[targetRow - 1], 0, lockedColors[targetRow], 0, Board.WIDTH);
                }
                for (int col = 0; col < Board.WIDTH; col++) {
                    lockedColors[0][col] = null;
                }
                cleared++;
                row++;
            }
        }
        return cleared;
    }

    private boolean isBoardRowFull(int row) {
        for (int col = 0; col < Board.WIDTH; col++) {
            if (board.getCell(row, col) == 0) {
                return false;
            }
        }
        return true;
    }

    private int scoreForLines(int lineCount) {
        return switch (lineCount) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 500;
            case 4 -> 800;
            default -> 0;
        };
    }

    private void spawnPiece() {
        currentPiece = TetrominoFactory.createRandomPiece();
        if (!board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            gameState = GameState.GAME_OVER;
            gravityTimeline.stop();
            gameScreen.showStatus("GAME OVER");
        }
    }

    private void togglePause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSED;
            gravityTimeline.pause();
            gameScreen.pauseActiveAnimation();
            gameScreen.showPauseOverlay(true);
            gameScreen.showStatus("Game paused");
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING;
            gravityTimeline.play();
            gameScreen.resumeActiveAnimation();
            gameScreen.showPauseOverlay(false);
            gameScreen.showStatus("");
        }
    }

    private void render() {
        gameScreen.render(board, lockedColors, currentPiece);
    }
}
