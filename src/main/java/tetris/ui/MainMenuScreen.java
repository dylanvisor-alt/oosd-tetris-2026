package tetris.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tetris.util.Constants;

public class MainMenuScreen {

    private static final String BUTTON_STYLE =
            "-fx-background-color: #1e1e1e;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 14px;"
                    + "-fx-padding: 10 0 10 0;"
                    + "-fx-background-radius: 6;"
                    + "-fx-border-color: #333333;"
                    + "-fx-border-radius: 6;"
                    + "-fx-border-width: 1;";

    private static final String BUTTON_HOVER_STYLE =
            "-fx-background-color: #007aff;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 14px;"
                    + "-fx-padding: 10 0 10 0;"
                    + "-fx-background-radius: 6;"
                    + "-fx-border-color: #007aff;"
                    + "-fx-border-radius: 6;"
                    + "-fx-border-width: 1;";

    private final StackPane root = new StackPane();

    public MainMenuScreen(
            Runnable onPlay,
            Runnable onConfiguration,
            Runnable onHighScores,
            Runnable onExit
    ) {
        Label title = new Label("TETRIS");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");

        Label subtitle = new Label("2006ICT - Milestone 1");
        subtitle.setTextFill(Color.web("#cccccc"));
        subtitle.setStyle("-fx-font-size: 15px;");

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

        root.getChildren().add(menu);
        root.setAlignment(Pos.CENTER);
        root.setMinSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
        root.setPrefSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
        root.setStyle("-fx-background-color: #121212;");
    }

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(BUTTON_STYLE);
        button.setOnMouseEntered(event -> button.setStyle(BUTTON_HOVER_STYLE));
        button.setOnMouseExited(event -> button.setStyle(BUTTON_STYLE));

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