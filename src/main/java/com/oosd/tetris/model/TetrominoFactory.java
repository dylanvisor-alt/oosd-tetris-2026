package com.oosd.tetris.model;

import com.oosd.tetris.model.shapes.IPiece;
import com.oosd.tetris.model.shapes.JPiece;
import com.oosd.tetris.model.shapes.LPiece;
import com.oosd.tetris.model.shapes.OPiece;
import com.oosd.tetris.model.shapes.SPiece;
import com.oosd.tetris.model.shapes.TPiece;
import com.oosd.tetris.model.shapes.ZPiece;
import javafx.scene.paint.Color;

import java.util.Random;

public class TetrominoFactory {

    private static final Random random = new Random();
    private static final int SPAWN_X = 4;
    private static final int SPAWN_Y = 0;

    private static final Color[] COLORS = {
            Color.web("#00bcd4"), // cyan
            Color.web("#ffeb3b"), // yellow
            Color.web("#9c27b0"), // purple
            Color.web("#4caf50"), // green
            Color.web("#f44336"), // red
            Color.web("#3f51b5"), // blue
            Color.web("#ff9800")  // orange
    };

    public static Tetromino createRandomPiece() {
        int choice = random.nextInt(7); // 0 to 6, one per shape
        Color randomColor = COLORS[random.nextInt(COLORS.length)];
        return switch (choice) {
            case 0 -> new IPiece(SPAWN_X, SPAWN_Y, randomColor);
            case 1 -> new OPiece(SPAWN_X, SPAWN_Y, randomColor);
            case 2 -> new TPiece(SPAWN_X, SPAWN_Y, randomColor);
            case 3 -> new SPiece(SPAWN_X, SPAWN_Y, randomColor);
            case 4 -> new ZPiece(SPAWN_X, SPAWN_Y, randomColor);
            case 5 -> new JPiece(SPAWN_X, SPAWN_Y, randomColor);
            case 6 -> new LPiece(SPAWN_X, SPAWN_Y, randomColor);
            default -> new TPiece(SPAWN_X, SPAWN_Y, randomColor);
        };
    }
}