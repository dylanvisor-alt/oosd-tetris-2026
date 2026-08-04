package com.oosd.tetris.model;

import javafx.scene.paint.Color;

public abstract class Tetromino implements Movable {

    // abstract base class for every piece type. Holds the shared position/colour
    // data and the moveDown() behaviour that's identical for all shapes.
    // subclasses (TPiece, IPiece, etc.) must supply their own rotate() and getShape().

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

    // no implementation here on purpose - each shape rotates differently
    @Override
    public void moveDown() {
        y++;
    }

    // each shape returns its own grid of 1s/0s describing its blocks
    @Override
    public abstract void rotate();

    public abstract int[][] getShape();
}