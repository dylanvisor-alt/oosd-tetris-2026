package tetris.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Main navigation view. Actions are supplied as callbacks so this screen does
 * not need to know about Stage, SceneManager, or the game model.
 */
public class MainMenuScreen {

    private static final double SCREEN_WIDTH = 520;
    private static final double SCREEN_HEIGHT = 600;

    private final StackPane root = new StackPane();

    public MainMenuScreen(
            Runnable onPlay,
            Runnable onConfiguration,
            Runnable onHighScores,
            Runnable onExit
    ) {
        Label title = new Label("TETRIS");
        title.getStyleClass().add("game-title");

        Label subtitle = new Label("2006ICT • Milestone 1");
        subtitle.getStyleClass().add("subtitle");

        Button playButton = createMenuButton("Play", onPlay);
        playButton.setDefaultButton(true);
        Button configurationButton = createMenuButton("Configuration", onConfiguration);
        Button highScoresButton = createMenuButton("High Scores", onHighScores);
        Button exitButton = createMenuButton("Exit", onExit);

        VBox menu = new VBox(
                14,
                title,
                subtitle,
                playButton,
                configurationButton,
                highScoresButton,
                exitButton
        );
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(48));
        menu.setMaxWidth(360);
        menu.getStyleClass().add("menu-panel");

        root.getChildren().add(menu);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        root.getStyleClass().add("app-background");
    }

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("menu-button");

        if (action == null) {
            button.setDisable(true);
        } else {
            button.setOnAction(event -> action.run());
        }
        return button;
    }

    public Parent getRoot() {
        return root;
    }
}
