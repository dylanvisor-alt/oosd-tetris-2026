package tetris.model.shapes;

import tetris.model.Tetromino;
import javafx.scene.paint.Color;

/* -------------------------------------------------------------------- */
/*  the I-piece - the 4 grid straight one                              */
/* -------------------------------------------------------------------- */

// only has 2 visually distinct rotations (horizontal and vertical),
// unlike most other shapes which have 4
public class IPiece extends Tetromino {

    private int rotationState = 0;

    public IPiece(int startX, int startY, Color colour) {
        super(startX, startY, colour);
    }

    @Override
    public void moveLeft() { x--; }

    @Override
    public void moveRight() { x++; }

    @Override
    public void rotate() {
        rotationState = (rotationState + 1) % 2; // cycles 0 -> 1 -> 0 ...
    }

    @Override
    public int[][] getShape() {
        return switch (rotationState) {
            case 0 -> new int[][] {{1, 1, 1, 1}}; // horizontal
            case 1 -> new int[][] {{1}, {1}, {1}, {1}}; // vertical
            default -> new int[][] {{1, 1, 1, 1}};
        };
    }
}