package com.oosd.tetris.model;

public class Board {

    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    private final int[][] grid;

    public Board() {
        grid = new int[HEIGHT][WIDTH];
    }

    public boolean isCellOccupied(int row, int col) {
        if (row < 0 || row >= HEIGHT || col < 0 || col >= WIDTH) {
            return true;
        }
        return grid[row][col] != 0;
    }

    public void setCell(int row, int col, int value) {
        if (row >= 0 && row < HEIGHT && col >= 0 && col < WIDTH) {
            grid[row][col] = value;
        }
    }

    public int getCell(int row, int col) {
        return grid[row][col];
    }

    // NEW: checks if a piece's shape can legally sit at a given position
    public boolean canPlace(Tetromino piece, int newX, int newY) {
        int[][] shape = piece.getShape();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int boardRow = newY + row;
                    int boardCol = newX + col;

                    if (isCellOccupied(boardRow, boardCol)) {
                        return false; // hits a wall, floor, or existing block
                    }
                }
            }
        }
        return true; // no collisions found, this position is legal
    }

    // NEW: permanently writes a piece's blocks into the grid once it lands
    public void lockPiece(Tetromino piece) {
        int[][] shape = piece.getShape();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int boardRow = piece.getY() + row;
                    int boardCol = piece.getX() + col;
                    setCell(boardRow, boardCol, 1);
                }
            }
        }
    }

    public int clearFullRows() {
        int clearedCount = 0;

        for (int row = HEIGHT - 1; row >= 0; row--) {
            if (isRowFull(row)) {
                removeRow(row);
                clearedCount++;
                row++;
            }
        }

        return clearedCount;
    }

    private boolean isRowFull(int row) {
        for (int col = 0; col < WIDTH; col++) {
            if (grid[row][col] == 0) {
                return false;
            }
        }
        return true;
    }

    private void removeRow(int rowToRemove) {
        for (int row = rowToRemove; row > 0; row--) {
            grid[row] = grid[row - 1].clone();
        }
        grid[0] = new int[WIDTH];
    }
}