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

    private static final double NANO_TO_MS = 1_000_000.0;

    private static final double DROP_SPEED = 300.0;
    private static final double MAX_FRAME_GAP = 50.0;

    private static final Color LOCKED_COLOUR = Color.web("#007aff");

    /* -------------------------------------------------------------------- */
    /*  game state - changes constantly while playing                       */
    /* -------------------------------------------------------------------- */
    private final Board board = new Board();
    private final GameScreen gamesScreen;
    private final Color[][] LOCKED_COLOURS = new Color[Board.HEIGHT][Board.WIDTH];
    private final AnimationTimer gravityTimer;

    private Tetromino current_piece;
    private GameState game_state = GameState.RUNNING;
    private int score;
    private double lastFrameTimeMs;
    private double accumulatedFallMs;

    /* -------------------------------------------------------------------- */
    /*  starting the game                                                    */
    /* -------------------------------------------------------------------- */

    public void startGame() {
        gamesScreen.setKeyHandler(this::handleKeyPress);
        spawnPiece();
        render();
        if (game_state == GameState.RUNNING) {
            gravityTimer.start();
        }
        gamesScreen.requestKeyboardFocus();
    }

    /* -------------------------------------------------------------------- */
    /*  gravity - moves the piece down automatically over time              */
    /* -------------------------------------------------------------------- */

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
        gamesScreen.setActivePieceVerticalOffset(fallProgress * GameScreen.CELL_SIZE);
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

        if (event.getCode() == KeyCode.E && game_state == GameState.PAUSED) {
            backToMenu.run();
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

    private Runnable backToMenu;

    public GameController(GameScreen gameScreen, Runnable backToMenu) {
        this.gamesScreen = gameScreen;
        this.backToMenu = backToMenu;
        gravityTimer = new AnimationTimer() {
            @Override
            public void handle(long currentTimeNanos) {
                double currentTimeMs = currentTimeNanos / NANO_TO_MS;
                updateGravity(currentTimeMs);
            }
        };
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

    private void lockClearAndSpawn() {
        saveCurrentPieceColours();
        board.lockPiece(current_piece);

        List<Integer> clearedRows = board.clearFullRows();
        for (int row : clearedRows) {
            shiftColoursDown(row);
        }

        if (!clearedRows.isEmpty()) {
            score += scoreForLines(clearedRows.size());
            gamesScreen.updateScore(score);
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
            gravityTimer.stop();
            gamesScreen.showGameOver(score);
        }
    }

    private void togglePause() {
        if (game_state == GameState.RUNNING) {
            game_state = GameState.PAUSED;
            gravityTimer.stop();
            lastFrameTimeMs = 0;
            gamesScreen.showPauseOverlay(true);
            gamesScreen.showStatus("Game paused");
        } else if (game_state == GameState.PAUSED) {
            game_state = GameState.RUNNING;
            gravityTimer.start();
            gamesScreen.showPauseOverlay(false);
            gamesScreen.showStatus("");
        }
    }

    /* -------------------------------------------------------------------- */
    /*  drawing the board and piece on screen                               */
    /* -------------------------------------------------------------------- */

    private void render() {
        gamesScreen.render(board, LOCKED_COLOURS, current_piece);
        double fallProgress = accumulatedFallMs / DROP_SPEED;
        gamesScreen.setActivePieceVerticalOffset(fallProgress * GameScreen.CELL_SIZE);
    }
}
