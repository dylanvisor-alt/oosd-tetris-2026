package com.oosd.tetris.model.shapes;

import com.oosd.tetris.model.Tetromino;
import javafx.scene.paint.Color;

public class ZPiece extends Tetromino {

    private int rotationState = 0;

    public ZPiece(int startX, int startY, Color color) {
        super(startX, startY, color);
    }

    @Override
    public void moveLeft() { x--; }

    @Override
    public void moveRight() { x++; }

    @Override
    public void rotate() {
        rotationState = (rotationState + 1) % 2; // Z-piece has 2 distinct states
    }

    @Override
    public int[][] getShape() {
        return switch (rotationState) {
            case 0 -> new int[][] {{1, 1, 0}, {0, 1, 1}};
            case 1 -> new int[][] {{0, 1}, {1, 1}, {1, 0}};
            default -> new int[][] {{1, 1, 0}, {0, 1, 1}};
        };
    }
}