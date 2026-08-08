package com.oosd.tetris.model;

public class Board {

    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    // owns the actual 10x20 grid of locked blocks, plus collision checking, locking pieces in, and clearing full rows.

    private final int[][] grid;

    // 0 = empty, 1 = occupied

    public Board() {
        grid = new int[HEIGHT][WIDTH];
    }

    // treats out-of-bounds as "occupied" too, so walls/floor act like collisions without needing separate boundary checks everywhere else.
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

    // Checks whether a piece could legally sit at (newX, newY) without overlapping a wall, the floor, or an existing locked block.
    public boolean canPlace(Tetromino piece, int newX, int newY) {
        int[][] shape = piece.getShape();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int boardRow = newY + row;
                    int boardCol = newX + col;

                    if (isCellOccupied(boardRow, boardCol)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Writes a piece's blocks permanently into the grid once it can't fall any further - this is what turns a falling piece into part of the stack.
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

    // scans from the bottom up, removing any fully filled rows.
    // returns how many rows were cleared, used for scoring.

    public int clearFullRows() {
        int clearedCount = 0;

        for (int row = HEIGHT - 1; row >= 0; row--) {
            if (isRowFull(row)) {
                removeRow(row);
                clearedCount++;
                row++; // re-check this same index, since rows above just shifted down
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

    // shifts every row above the cleared one down by one, then clears the top row
    private void removeRow(int rowToRemove) {
        for (int row = rowToRemove; row > 0; row--) {
            grid[row] = grid[row - 1].clone();
        }
        grid[0] = new int[WIDTH];
    }
}