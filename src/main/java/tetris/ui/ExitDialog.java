package tetris.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Objects;

/** Shows a confirmation before the player exits the application. */
public final class ExitDialog {

    private final Stage owner;

    public ExitDialog(Stage owner) {
        this.owner = Objects.requireNonNull(owner, "owner");
    }

    public boolean showAndWait() {
        ButtonType exitButton = new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(owner);
        alert.setTitle("Exit Tetris");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Your current game will be closed.");
        alert.getButtonTypes().setAll(exitButton, cancelButton);

        return alert.showAndWait()
                .filter(exitButton::equals)
                .isPresent();
    }
}
