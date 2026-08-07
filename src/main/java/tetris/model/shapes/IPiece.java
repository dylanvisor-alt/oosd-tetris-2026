package tetris.model.shapes;

import tetris.model.Tetromino;
import javafx.scene.paint.Color;

// The straight/long piece - only has 2 visually distinct rotations (horizontal and vertical), unlike most other shapes which have 4.
public class IPiece extends Tetromino {

    private int rotationState = 0;

    public IPiece(int startX, int startY, Color color) {
        super(startX, startY, color);
    }

    @Override
    public void moveLeft() { x--; }

    @Override
    public void moveRight() { x++; }

    @Override
    public void rotate() {
        rotationState = (rotationState + 1) % 2; // I-piece only has 2 real states
    }

    @Override
    public int[][] getShape() {
        return switch (rotationState) {
            case 0 -> new int[][] {{1, 1, 1, 1}}; // horizontal
            case 1 -> new int[][] {{1}, {1}, {1}, {1}}; //vertical
            default -> new int[][] {{1, 1, 1, 1}};
        };
    }
}