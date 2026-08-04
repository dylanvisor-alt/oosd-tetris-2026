package com.oosd.tetris.model.shapes;

import com.oosd.tetris.model.Tetromino;

public class OPiece extends Tetromino {

    public OPiece(int startX, int startY) {
        super(startX, startY);
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