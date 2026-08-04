package com.oosd.tetris.model.shapes;

import com.oosd.tetris.model.Tetromino;
import javafx.scene.paint.Color;

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
            case 0 -> new int[][] {{1, 1, 1, 1}};
            case 1 -> new int[][] {{1}, {1}, {1}, {1}};
            default -> new int[][] {{1, 1, 1, 1}};
        };
    }
}