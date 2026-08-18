package tetris.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tetris.controller.ConfigController;
import tetris.model.GameConfig;
import tetris.util.Constants;

import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigurationScreen {

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

    public ConfigurationScreen(ConfigController configController, Runnable onBack) {
        Objects.requireNonNull(configController, "configController");
        Objects.requireNonNull(onBack, "onBack");

        Label title = new Label("Configuration");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        GridPane settings = new GridPane();
        settings.setHgap(22);
        settings.setVgap(18);
        settings.setAlignment(Pos.CENTER);
        settings.setPadding(new Insets(16));
        settings.setStyle(
                "-fx-background-color: #1a1a1a;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-color: #333333;"
                        + "-fx-border-radius: 8;"
                        + "-fx-border-width: 1;"
        );

        ComboBox<String> fieldSize = new ComboBox<>();
        fieldSize.getItems().addAll(GameConfig.FIELD_SIZES);
        fieldSize.setValue(configController.getFieldSize());
        fieldSize.setMaxWidth(Double.MAX_VALUE);
        fieldSize.setOnAction(event -> configController.setFieldSize(fieldSize.getValue()));
        addSetting(settings, "Field size", fieldSize, 0);

        Label levelValue = new Label(Integer.toString(configController.getLevel()));
        levelValue.setTextFill(Color.web("#007aff"));
        levelValue.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        Slider level = new Slider(GameConfig.MIN_LEVEL, GameConfig.MAX_LEVEL, configController.getLevel());
        level.setMajorTickUnit(1);
        level.setMinorTickCount(0);
        level.setSnapToTicks(true);
        level.setShowTickMarks(true);
        level.setShowTickLabels(true);
        level.valueProperty().addListener((observable, oldValue, newValue) -> {
            int selectedLevel = newValue.intValue();
            levelValue.setText(Integer.toString(selectedLevel));
            configController.setLevel(selectedLevel);
        });

        HBox levelControl = new HBox(12, level, levelValue);
        levelControl.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(level, Priority.ALWAYS);
        addSetting(settings, "Level", levelControl, 1);

        addSetting(settings, "Music", createCheckBox(
                configController.isMusicEnabled(), configController::setMusicEnabled), 2);
        addSetting(settings, "Sound effects", createCheckBox(
                configController.isSoundEffectsEnabled(), configController::setSoundEffectsEnabled), 3);
        addSetting(settings, "AI play", createCheckBox(
                configController.isAiPlayEnabled(), configController::setAiPlayEnabled), 4);
        addSetting(settings, "Extended mode", createCheckBox(
                configController.isExtendedModeEnabled(), configController::setExtendedModeEnabled), 5);

        Label note = new Label("Settings are saved for this session.");
        note.setTextFill(Color.web("#999999"));
        note.setStyle("-fx-font-size: 12px;");

        Button backButton = new Button("Back");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle(BUTTON_STYLE);
        backButton.setOnMouseEntered(event -> backButton.setStyle(BUTTON_HOVER_STYLE));
        backButton.setOnMouseExited(event -> backButton.setStyle(BUTTON_STYLE));
        backButton.setOnAction(event -> onBack.run());

        VBox content = new VBox(24, title, settings, note, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(34));
        content.setMaxWidth(440);

        root.getChildren().add(content);
        root.setAlignment(Pos.CENTER);
        root.setMinSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
        root.setPrefSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
        root.setStyle("-fx-background-color: #121212;");
    }

    private void addSetting(GridPane grid, String text, Node control, int row) {
        Label label = new Label(text);
        label.setTextFill(Color.web("#cccccc"));
        label.setStyle("-fx-font-size: 14px;");
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private CheckBox createCheckBox(boolean selected, Consumer<Boolean> onChange) {
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(selected);
        checkBox.setOnAction(event -> onChange.accept(checkBox.isSelected()));
        return checkBox;
    }

    public Parent getRoot() {
        return root;
    }
}