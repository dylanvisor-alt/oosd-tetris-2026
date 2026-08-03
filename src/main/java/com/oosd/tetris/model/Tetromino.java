package com.oosd.tetris.model;

public abstract class Tetromino implements Movable {

    protected int x;
    protected int y;

    public Tetromino(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void moveDown() {
        y++;
    }

    // Each concrete shape defines its own rotation logic
    @Override
    public abstract void rotate();
}