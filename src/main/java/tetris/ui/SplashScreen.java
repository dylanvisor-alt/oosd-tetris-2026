package tetris.ui;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.util.Constants;

import java.util.Objects;

/* Displays a short loading screen before the main menu. */

public class SplashScreen {

    private static final Duration DISPLAY_TIME = Duration.seconds(3);

    private final StackPane root = new StackPane();
    private final PauseTransition displayDelay = new PauseTransition(DISPLAY_TIME);

    public SplashScreen() {
        Label title = new Label("TETRIS");
        title.getStyleClass().add("splash-title");

        Label course = new Label("Object Oriented Software Development");
        course.getStyleClass().add("subtitle");

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(34, 34);

        Label loading = new Label("Loading game...");
        loading.getStyleClass().add("splash-loading");

        VBox content = new VBox(12, title, course, progress, loading);
        content.setAlignment(Pos.CENTER);
        content.setMaxSize(360, 300);
        content.getStyleClass().add("splash-panel");

        root.getChildren().add(content);
        root.setMinSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
        root.setPrefSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
        root.getStyleClass().add("app-background");
    }

    public Parent getRoot() {
        return root;
    }

    /* Starts the non-blocking delay, then asks Main to navigate to the menu. */
    public void start(Runnable onFinished) {
        Objects.requireNonNull(onFinished, "onFinished");
        displayDelay.stop();
        displayDelay.setOnFinished(event -> onFinished.run());
        displayDelay.playFromStart();
    }

    public void stop() {
        displayDelay.stop();
    }
}
