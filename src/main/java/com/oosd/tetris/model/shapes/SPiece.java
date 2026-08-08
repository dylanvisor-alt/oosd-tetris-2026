package com.oosd.tetris.model.shapes;

import com.oosd.tetris.model.Tetromino;
import javafx.scene.paint.Color;

public class SPiece extends Tetromino {

    private int rotationState = 0;

    public SPiece(int startX, int startY, Color colour) {
        super(startX, startY, colour);
    }

    @Override
    public void moveLeft() { x--; }

    @Override
    public void moveRight() { x++; }

    @Override
    public void rotate() {
        rotationState = (rotationState + 1) % 2; // S-piece has 2 distinct states
    }

    @Override
    public int[][] getShape() {
        return switch (rotationState) {
            case 0 -> new int[][] {{0, 1, 1}, {1, 1, 0}};
            case 1 -> new int[][] {{1, 0}, {1, 1}, {0, 1}};
            default -> new int[][] {{0, 1, 1}, {1, 1, 0}};
        };
    }
}