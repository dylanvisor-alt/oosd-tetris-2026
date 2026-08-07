package com.oosd.tetris.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/** A reusable overlay shown above the board while gameplay is paused. */
public class PauseOverlay extends StackPane {

    public PauseOverlay() {
        Label title = new Label("PAUSED");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label instruction = new Label("Press P to continue");
        instruction.setTextFill(Color.WHITE);

        VBox message = new VBox(8, title, instruction);
        message.setAlignment(Pos.CENTER);
        setAlignment(Pos.CENTER);
        getChildren().add(message);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.68);");
        setPaused(false);
    }

    public void setPaused(boolean paused) {
        setVisible(paused);
        setManaged(paused);
    }
}
