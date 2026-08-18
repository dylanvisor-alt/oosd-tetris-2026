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

    private static final double NANO_TO_MS = 1_000_000.0;

    private static final double DROP_SPEED = 300.0;
    private static final double MAX_FRAME_GAP = 50.0;

    private static final Color LOCKED_COLOUR = Color.web("#007aff");

    /* -------------------------------------------------------------------- */
    /*  game state - changes constantly while playing                       */
    /* -------------------------------------------------------------------- */
    private final Board board = new Board();
    private final GameScreen game_screen;
    private final Color[][] LOCKED_COLOURS = new Color[Board.HEIGHT][Board.WIDTH];
    private final AnimationTimer GRAVITY_TIMER;

    private Tetromino current_piece;
    private GameState game_state = GameState.RUNNING;
    private int score;
    private double lastFrameTimeMs;
    private double accumulatedFallMs;

    public GameController(GameScreen game_screen) {
        this.game_screen = game_screen;
        GRAVITY_TIMER = new AnimationTimer() {
            @Override
            public void handle(long currentTimeNanos) {
                // convert straight away so every other method deals in ms only
                double currentTimeMs = currentTimeNanos / NANO_TO_MS;
                updateGravity(currentTimeMs);
            }
        };
    }

    /* -------------------------------------------------------------------- */
    /*  starting the game                                                    */
    /* -------------------------------------------------------------------- */

    public void startGame() {
        game_screen.setKeyHandler(this::handleKeyPress);
        spawnPiece();
        render();
        if (game_state == GameState.RUNNING) {
            GRAVITY_TIMER.start();
        }
        game_screen.requestKeyboardFocus();
    }

    /* -------------------------------------------------------------------- */
    /*  gravity - moves the piece down automatically over time              */
    /* -------------------------------------------------------------------- */

    // moves only the active javafx node between logical board rows. the
    // board model changes once each gravity interval, while keyboard input
    // remains available on every frame
    private void updateGravity(double currentTimeMs) {
        if (game_state != GameState.RUNNING || current_piece == null) {
            return;
        }

        if (lastFrameTimeMs == 0) {
            lastFrameTimeMs = currentTimeMs;
            return;
        }

        double frameDelta = Math.min(currentTimeMs - lastFrameTimeMs, MAX_FRAME_GAP);
        lastFrameTimeMs = currentTimeMs;

        if (!canCurrentPieceFall()) {
            lockClearAndSpawn();
            render();
            return;
        }

        accumulatedFallMs += frameDelta;
        if (accumulatedFallMs >= DROP_SPEED) {
            current_piece.moveDown();
            accumulatedFallMs -= DROP_SPEED;

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

        double fallProgress = accumulatedFallMs / DROP_SPEED;
        game_screen.setActivePieceVerticalOffset(fallProgress * GameScreen.CELL_SIZE);
    }

    private boolean canCurrentPieceFall() {
        return board.canPlace(current_piece, current_piece.getX(), current_piece.getY() + 1);
    }

    /* -------------------------------------------------------------------- */
    /*  keyboard controls                                                   */
    /* -------------------------------------------------------------------- */

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.P && game_state != GameState.GAME_OVER) {
            togglePause();
            return;
        }

        if (game_state != GameState.RUNNING) {
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
        if (board.canPlace(current_piece, current_piece.getX() + direction, current_piece.getY())) {
            if (direction < 0) {
                current_piece.moveLeft();
            } else {
                current_piece.moveRight();
            }
        }
    }

    private void softDrop() {
        if (canCurrentPieceFall()) {
            current_piece.moveDown();
            accumulatedFallMs = 0;
        } else {
            lockClearAndSpawn();
        }
    }

    private void hardDrop() {
        while (board.canPlace(current_piece, current_piece.getX(), current_piece.getY() + 1)) {
            current_piece.moveDown();
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
        current_piece.rotate();
        if (!board.canPlace(current_piece, current_piece.getX(), current_piece.getY())) {
            // every supplied shape uses four rotations, so three more undo an invalid turn
            current_piece.rotate();
            current_piece.rotate();
            current_piece.rotate();
        }
    }

    /* -------------------------------------------------------------------- */
    /*  locking pieces and clearing rows                                    */
    /* -------------------------------------------------------------------- */

    private void saveCurrentPieceColours() {
        int[][] shape = current_piece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int boardRow = current_piece.getY() + row;
                    int boardCol = current_piece.getX() + col;
                    if (boardRow >= 0 && boardRow < Board.HEIGHT && boardCol >= 0 && boardCol < Board.WIDTH) {
                        LOCKED_COLOURS[boardRow][boardCol] = LOCKED_COLOUR;
                    }
                }
            }
        }
    }

    // board is the single source of truth for which rows cleared - the
    // colour grid just follows along with whatever rows board reports,
    // instead of scanning and deciding this independently
    private void lockClearAndSpawn() {
        saveCurrentPieceColours();
        board.lockPiece(current_piece);

        List<Integer> clearedRows = board.clearFullRows();
        for (int row : clearedRows) {
            shiftColoursDown(row);
        }

        if (!clearedRows.isEmpty()) {
            score += scoreForLines(clearedRows.size());
            game_screen.updateScore(score);
        }
        spawnPiece();
    }

    private void shiftColoursDown(int clearedRow) {
        for (int row = clearedRow; row > 0; row--) {
            LOCKED_COLOURS[row] = LOCKED_COLOURS[row - 1].clone();
        }
        LOCKED_COLOURS[0] = new Color[Board.WIDTH];
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
        current_piece = TetrominoFactory.createRandomPiece();
        accumulatedFallMs = 0;
        lastFrameTimeMs = 0;
        if (!board.canPlace(current_piece, current_piece.getX(), current_piece.getY())) {
            game_state = GameState.GAME_OVER;
            GRAVITY_TIMER.stop();
            game_screen.showGameOver(score);
        }
    }

    private void togglePause() {
        if (game_state == GameState.RUNNING) {
            game_state = GameState.PAUSED;
            GRAVITY_TIMER.stop();
            lastFrameTimeMs = 0;
            game_screen.showPauseOverlay(true);
            game_screen.showStatus("Game paused");
        } else if (game_state == GameState.PAUSED) {
            game_state = GameState.RUNNING;
            GRAVITY_TIMER.start();
            game_screen.showPauseOverlay(false);
            game_screen.showStatus("");
        }
    }

    /* -------------------------------------------------------------------- */
    /*  drawing the board and piece on screen                               */
    /* -------------------------------------------------------------------- */

    private void render() {
        game_screen.render(board, LOCKED_COLOURS, current_piece);
        double fallProgress = accumulatedFallMs / DROP_SPEED;
        game_screen.setActivePieceVerticalOffset(fallProgress * GameScreen.CELL_SIZE);
    }
}
