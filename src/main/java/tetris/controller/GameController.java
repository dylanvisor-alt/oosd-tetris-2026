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

import java.util.List;

/* -------------------------------------------------------------------- */
/*  owns the gameplay state and rules - GameScreen only draws the       */
/*  values it receives, it never decides anything itself                */
/* -------------------------------------------------------------------- */

public class GameController {

    /* -------------------------------------------------------------------- */
    /*  settings never changed while the game is running                    */
    /* -------------------------------------------------------------------- */

    // AnimationTimer only ever hands us nanoseconds, so this is the one spot
    // a nanosecond value has to exist - everything else works in milliseconds
    private static final double NANOS_PER_MS = 1_000_000.0;

    private static final double GRAVITY_INTERVAL_MS = 240.0;
    private static final double MAX_FRAME_DELTA_MS = 50.0;

    /* -------------------------------------------------------------------- */
    /*  game state - changes constantly while playing                       */
    /* -------------------------------------------------------------------- */
    private final Board board = new Board();
    private final GameScreen gameScreen;
    private final Color[][] lockedColors = new Color[Board.HEIGHT][Board.WIDTH];
    private final AnimationTimer gravityTimer;

    private Tetromino currentPiece;
    private GameState gameState = GameState.RUNNING;
    private int score;
    private double lastFrameTimeMs;
    private double accumulatedFallMs;

    public GameController(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        gravityTimer = new AnimationTimer() {
            @Override
            public void handle(long currentTimeNanos) {
                // convert straight away so every other method deals in ms only
                double currentTimeMs = currentTimeNanos / NANOS_PER_MS;
                updateGravity(currentTimeMs);
            }
        };
    }

    /* -------------------------------------------------------------------- */
    /*  starting the game                                                    */
    /* -------------------------------------------------------------------- */

    public void startGame() {
        gameScreen.setKeyHandler(this::handleKeyPress);
        spawnPiece();
        render();
        if (gameState == GameState.RUNNING) {
            gravityTimer.start();
        }
        gameScreen.requestKeyboardFocus();
    }

    /* -------------------------------------------------------------------- */
    /*  gravity - moves the piece down automatically over time              */
    /* -------------------------------------------------------------------- */

    // moves only the active javafx node between logical board rows. the
    // board model changes once each gravity interval, while keyboard input
    // remains available on every frame
    private void updateGravity(double currentTimeMs) {
        if (gameState != GameState.RUNNING || currentPiece == null) {
            return;
        }

        if (lastFrameTimeMs == 0) {
            lastFrameTimeMs = currentTimeMs;
            return;
        }

        double frameDelta = Math.min(currentTimeMs - lastFrameTimeMs, MAX_FRAME_DELTA_MS);
        lastFrameTimeMs = currentTimeMs;

        if (!canCurrentPieceFall()) {
            lockClearAndSpawn();
            render();
            return;
        }

        accumulatedFallMs += frameDelta;
        if (accumulatedFallMs >= GRAVITY_INTERVAL_MS) {
            currentPiece.moveDown();
            accumulatedFallMs -= GRAVITY_INTERVAL_MS;

            if (!canCurrentPieceFall()) {
                accumulatedFallMs = 0;
                lockClearAndSpawn();
                render();
                return;
            }

            // re-anchor the javafx piece at its new logical row - per-frame
            // updates remain a single translate operation
            render();
        }

        double fallProgress = accumulatedFallMs / GRAVITY_INTERVAL_MS;
        gameScreen.setActivePieceVerticalOffset(fallProgress * GameScreen.CELL_SIZE);
    }

    private boolean canCurrentPieceFall() {
        return board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY() + 1);
    }

    /* -------------------------------------------------------------------- */
    /*  keyboard controls                                                   */
    /* -------------------------------------------------------------------- */

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
            accumulatedFallMs = 0;
        } else {
            lockClearAndSpawn();
        }
    }

    private void hardDrop() {
        while (board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
            currentPiece.moveDown();
        }
        accumulatedFallMs = 0;
        lockClearAndSpawn();
    }

    private void resetFallProgressIfBlocked() {
        if (!canCurrentPieceFall()) {
            accumulatedFallMs = 0;
        }
    }

    private void tryRotate() {
        currentPiece.rotate();
        if (!board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            // every supplied shape uses four rotations, so three more undo an invalid turn
            currentPiece.rotate();
            currentPiece.rotate();
            currentPiece.rotate();
        }
    }

    /* -------------------------------------------------------------------- */
    /*  locking pieces and clearing rows                                    */
    /* -------------------------------------------------------------------- */

    // board is the single source of truth for which rows cleared - the
    // colour grid just follows along with whatever rows board reports,
    // instead of scanning and deciding this independently
    private void lockClearAndSpawn() {
        saveCurrentPieceColours();
        board.lockPiece(currentPiece);

        List<Integer> clearedRows = board.clearFullRows();
        for (int row : clearedRows) {
            shiftColoursDown(row);
        }

        if (!clearedRows.isEmpty()) {
            score += scoreForLines(clearedRows.size());
            gameScreen.updateScore(score);
        }
        spawnPiece();
    }

    private void shiftColoursDown(int clearedRow) {
        for (int row = clearedRow; row > 0; row--) {
            lockedColors[row] = lockedColors[row - 1].clone();
        }
        lockedColors[0] = new Color[Board.WIDTH];
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

    private int scoreForLines(int lineCount) {
        return switch (lineCount) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 500;
            case 4 -> 800;
            default -> 0;
        };
    }

    /* -------------------------------------------------------------------- */
    /*  spawning, pause, and game over                                      */
    /* -------------------------------------------------------------------- */

    private void spawnPiece() {
        currentPiece = TetrominoFactory.createRandomPiece();
        accumulatedFallMs = 0;
        lastFrameTimeMs = 0;
        if (!board.canPlace(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            gameState = GameState.GAME_OVER;
            gravityTimer.stop();
            gameScreen.showGameOver();
        }
    }

    private void togglePause() {
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSED;
            gravityTimer.stop();
            lastFrameTimeMs = 0;
            gameScreen.showPauseOverlay(true);
            gameScreen.showStatus("Game paused");
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING;
            gravityTimer.start();
            gameScreen.showPauseOverlay(false);
            gameScreen.showStatus("");
        }
    }

    /* -------------------------------------------------------------------- */
    /*  drawing the board and piece on screen                               */
    /* -------------------------------------------------------------------- */

    private void render() {
        gameScreen.render(board, lockedColors, currentPiece);
        double fallProgress = accumulatedFallMs / GRAVITY_INTERVAL_MS;
        gameScreen.setActivePieceVerticalOffset(fallProgress * GameScreen.CELL_SIZE);
    }
}
