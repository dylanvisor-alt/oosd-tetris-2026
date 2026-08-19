package tetris.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PauseOverlay extends StackPane {

    public PauseOverlay() {
        Label title = new Label("PAUSED");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label pauseInstruction = new Label("Press P to Continue");
        pauseInstruction.setTextFill(Color.RED);

        Label menuInstruction = new Label("Press E for Main Menu");
        menuInstruction.setTextFill(Color.rgb(255, 0, 0, 0.6));

        VBox message = new VBox(8, title, pauseInstruction, menuInstruction);
        message.setAlignment(Pos.CENTER);
        setAlignment(Pos.CENTER);
        getChildren().add(message);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        setPaused(false);
    }

    public void setPaused(boolean paused) {
        setVisible(paused);
        setManaged(paused);
    }
}
