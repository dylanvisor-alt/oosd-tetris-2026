package com.oosd.tetris.model;

import javafx.scene.paint.Color;

public abstract class Tetromino implements Movable {

    protected int x;
    protected int y;
    protected Color color;

    public Tetromino(int startX, int startY, Color color) {
        this.x = startX;
        this.y = startY;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void moveDown() {
        y++;
    }

    @Override
    public abstract void rotate();

    public abstract int[][] getShape();
}