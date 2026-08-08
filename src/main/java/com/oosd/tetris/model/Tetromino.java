package com.oosd.tetris.model;

import javafx.scene.paint.Color;

public abstract class Tetromino implements Movable {

    // abstract base class for every piece type. Holds the shared position/colour
    // data and the moveDown() behaviour that's identical for all shapes.
    // subclasses (TPiece, IPiece, etc.) must supply their own rotate() and getShape().

    protected int x;
    protected int y;
    protected Color colour;

    public Tetromino(int startX, int startY, Color colour) {
        this.x = startX;
        this.y = startY;
        this.colour = colour;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return colour;
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