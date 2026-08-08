package tetris.controller;

import tetris.model.Board;
import tetris.model.GameState;
import tetris.model.Tetromino;
import tetris.model.TetrominoFactory;
import tetris.ui.GameScreen;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/*
 * Owns the gameplay state and rules. GameScreen only draws the values it receives.
 */
public class GameController {

    private static final long GRAVITY_INTERVAL_NANOS = 333_000_000L;
    private static final long MAX_FRAME_DELTA_NANOS = 50_000_000L;

    private final Board board = new Board();
    private final GameScreen gameScreen;
    private final Color[][] lockedColors = new Color[Board.HEIGHT][Board.WIDTH];
    private final AnimationTimer gravityTimer;

    private Tetromino currentPiece;
    private GameState gameState = GameState.RUNNING;
    private int score;
    private long lastFrameTimeNanos;
    private long accumulatedFallNanos;

    public GameController(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        gravityTimer = new AnimationTimer() {
            @Override
            public void handle(long currentTimeNanos) {
                updateGravity(currentTimeNanos);
            }
        };
    }

    public void startGame() {
        gameScreen.setKeyHandler(this::handleKeyPress);
        spawnPiece();
        render();
        if (gameState == GameState.RUNNING) {
            gravityTimer.start();
        }
        gameScreen.requestKeyboardFocus();
    }

    /**
     * Moves only the active JavaFX node between logical board rows. The Board
     * model changes once each 333 ms, while keyboard input remains available
     * on every frame.
     */
    private void updateGravity(long currentTimeNanos) {
        if (gameState != GameState.RUNNING || currentPiece == null) {
            return;
        }

        if (lastFrameTimeNanos == 0) {
            lastFrameTimeNanos = currentTimeNanos;
            return;
        }

        long frameDelta = Math.min(
                currentTimeNanos - lastFrameTimeNanos,
                MAX_FRAME_DELTA_NANOS
        );
        lastFrameTimeNanos = currentTimeNanos;

        if (!canCurrentPieceFall()) {
            lockClearAndSpawn();
            render();
            return;
        }

        accumulatedFallNanos += frameDelta;
        if (accumulatedFallNanos >= GRAVITY_INTERVAL_NANOS) {
            currentPiece.moveDown();
            accumulatedFallNanos -= GRAVITY_INTERVAL_NANOS;

            if (!canCurrentPieceFall()) {
                accumulatedFallNanos = 0;
                lockClearAndSpawn();
                render();
                return;
            }

            // Re-anchor the JavaFX piece at its new logical row. This happens
            // only three times per second; per-frame updates remain one translate.
            render();
        }

        double fallProgress = accumulatedFallNanos / (double) GRAVITY_INTERVAL_NANOS;
        gameScreen.setActivePieceVerticalOffset(fallProgress * GameScreen.CELL_SIZE);
    }

    private boolean canCurrentPieceFall() {
        return board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY() + 1);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.P && gameState != GameState.GAME_OVER) {
            togglePause();
            return;
        }

        if (gameState != GameState.RUNNING) {
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

        resetFallProgressIfBlocked();
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
        if (canCurrentPieceFall()) {
            currentPiece.moveDown();
            accumulatedFallNanos = 0;
        } else {
            lockClearAndSpawn();
        }
    }

    private void hardDrop() {
        while (board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
            currentPiece.moveDown();
        }
        accumulatedFallNanos = 0;
        lockClearAndSpawn();
    }

    private void resetFallProgressIfBlocked() {
        if (!canCurrentPieceFall()) {
            accumulatedFallNanos = 0;
        }
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
        accumulatedFallNanos = 0;
        lastFrameTimeNanos = 0;
        if (!board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            gameState = GameState.GAME_OVER;
            gravityTimer.stop();
            gameScreen.showStatus("GAME OVER");
        }
    }

    private void togglePause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSED;
            gravityTimer.stop();
            lastFrameTimeNanos = 0;
            gameScreen.showPauseOverlay(true);
            gameScreen.showStatus("Game paused");
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING;
            gravityTimer.start();
            gameScreen.showPauseOverlay(false);
            gameScreen.showStatus("");
        }
    }

    private void render() {
        gameScreen.render(board, lockedColors, currentPiece);
        double fallProgress = accumulatedFallNanos / (double) GRAVITY_INTERVAL_NANOS;
        gameScreen.setActivePieceVerticalOffset(fallProgress * GameScreen.CELL_SIZE);
    }
}
