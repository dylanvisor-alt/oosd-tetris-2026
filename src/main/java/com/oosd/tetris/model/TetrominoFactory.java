package com.oosd.tetris.model;

import com.oosd.tetris.model.shapes.IPiece;
import com.oosd.tetris.model.shapes.JPiece;
import com.oosd.tetris.model.shapes.LPiece;
import com.oosd.tetris.model.shapes.OPiece;
import com.oosd.tetris.model.shapes.SPiece;
import com.oosd.tetris.model.shapes.TPiece;
import com.oosd.tetris.model.shapes.ZPiece;

import java.util.Random;

public class TetrominoFactory {

    private static final Random random = new Random();
    private static final int SPAWN_X = 4;
    private static final int SPAWN_Y = 0;

    public static Tetromino createRandomPiece() {
        int choice = random.nextInt(7); // 0 to 6, one per shape

        return switch (choice) {
            case 0 -> new IPiece(SPAWN_X, SPAWN_Y);
            case 1 -> new OPiece(SPAWN_X, SPAWN_Y);
            case 2 -> new TPiece(SPAWN_X, SPAWN_Y);
            case 3 -> new SPiece(SPAWN_X, SPAWN_Y);
            case 4 -> new ZPiece(SPAWN_X, SPAWN_Y);
            case 5 -> new JPiece(SPAWN_X, SPAWN_Y);
            case 6 -> new LPiece(SPAWN_X, SPAWN_Y);
            default -> new TPiece(SPAWN_X, SPAWN_Y);
        };
    }
}