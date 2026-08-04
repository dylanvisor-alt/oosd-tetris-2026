package com.oosd.tetris.model.shapes;

import com.oosd.tetris.model.Tetromino;
import javafx.scene.paint.Color;

public class TPiece extends Tetromino {

    private int rotationState = 0;

    public TPiece(int startX, int startY, Color color) {
        super(startX, startY, color);
    }

    @Override
    public void moveLeft() {
        x--;
    }

    @Override
    public void moveRight() {
        x++;
    }

    @Override
    public void rotate() {
        rotationState = (rotationState + 1) % 4;
    }

    @Override
    public int[][] getShape() {
        return switch (rotationState) {
            case 0 -> new int[][] {{0, 1, 0}, {1, 1, 1}};
            case 1 -> new int[][] {{1, 0}, {1, 1}, {1, 0}};
            case 2 -> new int[][] {{1, 1, 1}, {0, 1, 0}};
            case 3 -> new int[][] {{0, 1}, {1, 1}, {0, 1}};
            default -> new int[][] {{0, 1, 0}, {1, 1, 1}};
        };
    }
}