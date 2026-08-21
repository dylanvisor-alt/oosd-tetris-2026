package tetris.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PauseOverlay extends StackPane {

    public PauseOverlay() {
        Label title = new Label("PAUSED");
        title.getStyleClass().add("pause-title");

        Label pauseInstruction = new Label("Press P to Continue");
        pauseInstruction.getStyleClass().add("pause-instruction");

        Label menuInstruction = new Label("Press E for Main Menu");
        menuInstruction.getStyleClass().add("pause-menu-instruction");

        VBox message = new VBox(8, title, pauseInstruction, menuInstruction);
        message.setAlignment(Pos.CENTER);
        message.getStyleClass().add("pause-card");
        setAlignment(Pos.CENTER);
        getChildren().add(message);
        getStyleClass().add("pause-overlay");
        setPaused(false);
    }

    public void setPaused(boolean paused) {
        setVisible(paused);
        setManaged(paused);
    }
}
