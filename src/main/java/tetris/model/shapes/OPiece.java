package tetris.model.shapes;

import tetris.model.Tetromino;
import javafx.scene.paint.Color;

public class OPiece extends Tetromino {

    public OPiece(int startX, int startY, Color colour) {
        super(startX, startY, colour);
    }

    @Override
    public void moveLeft() { x--; }

    @Override
    public void moveRight() { x++; }

    @Override
    public void rotate() {
        // O-piece looks identical in every rotation, so nothing changes
    }

    @Override
    public int[][] getShape() {
        return new int[][] {{1, 1}, {1, 1}};
    }
}