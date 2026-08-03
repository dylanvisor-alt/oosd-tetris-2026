package com.oosd.tetris;

import com.oosd.tetris.model.Board;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int CELL_SIZE = 25;

    @Override
    public void start(Stage primaryStage) {
        Board board = new Board();

        GridPane grid = new GridPane();

        for (int row = 0; row < Board.HEIGHT; row++) {
            for (int col = 0; col < Board.WIDTH; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.web("#1e1e1e"));
                cell.setStroke(Color.web("#333333"));
                grid.add(cell, col, row);
            }
        }

        Scene scene = new Scene(grid, Board.WIDTH * CELL_SIZE, Board.HEIGHT * CELL_SIZE);

        primaryStage.setTitle("Tetris - Group PG15");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}