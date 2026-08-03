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

    public int clearFullRows() {
        int clearedCount = 0;

        for (int row = HEIGHT - 1; row >= 0; row--) {
            if (isRowFull(row)) {
                removeRow(row);
                clearedCount++;
                row++; // recheck same index, since rows shifted down
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