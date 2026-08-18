package tetris.ui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tetris.model.ScoreEntry;
import tetris.util.Constants;

import java.util.List;

public class HighScoreScreen {

    private static final String BUTTON_FIXED =
            "-fx-background-color: #1e1e1e;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 14px;"
                    + "-fx-padding: 10 0 10 0;"
                    + "-fx-background-radius: 6;"
                    + "-fx-border-color: #333333;"
                    + "-fx-border-radius: 6;"
                    + "-fx-border-width: 1;";

    private static final String BUTTON_HOVER =
            "-fx-background-color: #007aff;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 14px;"
                    + "-fx-padding: 10 0 10 0;"
                    + "-fx-background-radius: 6;"
                    + "-fx-border-color: #007aff;"
                    + "-fx-border-radius: 6;"
                    + "-fx-border-width: 1;";

    private final StackPane root = new StackPane();

    public HighScoreScreen(List<ScoreEntry> scores, Runnable onBack) {
        Label title = new Label("HIGH SCORES");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        GridPane scoreTable = createScoreTable(scores);

        Button backButton = new Button("Back");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle(BUTTON_FIXED);
        backButton.setOnMouseEntered(event -> backButton.setStyle(BUTTON_HOVER));
        backButton.setOnMouseExited(event -> backButton.setStyle(BUTTON_FIXED));
        backButton.setOnAction(event -> onBack.run());

        VBox content = new VBox(22, title, scoreTable, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(36));
        content.setMaxWidth(400);

        VBox.setVgrow(scoreTable, Priority.ALWAYS);

        root.getChildren().add(content);
        root.setAlignment(Pos.CENTER);
        root.setMinSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
        root.setPrefSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
        root.setStyle("-fx-background-color: #121212;");
    }

    private GridPane createScoreTable(List<ScoreEntry> scores) {
        GridPane table = new GridPane();
        table.setHgap(24);
        table.setVgap(8);
        table.setAlignment(Pos.CENTER);
        table.setPadding(new Insets(16));
        table.setStyle(
                "-fx-background-color: #1a1a1a;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-color: #333333;"
                        + "-fx-border-radius: 8;"
                        + "-fx-border-width: 1;"
        );

        addCell(table, "Rank", 0, 0, true);
        addCell(table, "Name", 1, 0, true);
        addCell(table, "Score", 2, 0, true);

        if (scores.isEmpty()) {
            Label emptyMessage = new Label("No scores saved yet");
            emptyMessage.setTextFill(Color.web("#999999"));
            emptyMessage.setStyle("-fx-font-size: 14px;");
            table.add(emptyMessage, 0, 1, 3, 1);
            GridPane.setHalignment(emptyMessage, HPos.CENTER);
            return table;
        }

        for (int index = 0; index < scores.size(); index++) {
            ScoreEntry entry = scores.get(index);
            int row = index + 1;
            addCell(table, Integer.toString(row), 0, row, false);
            addCell(table, entry.playerName(), 1, row, false);
            addCell(table, String.format("%,d", entry.score()), 2, row, false);
        }
        return table;
    }

    // isHeader controls colour/weight so header row stands out from the data rows
    private void addCell(GridPane table, String text, int column, int row, boolean isHeader) {
        Label label = new Label(text);
        if (isHeader) {
            label.setTextFill(Color.web("#007aff"));
            label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        } else {
            label.setTextFill(Color.WHITE);
            label.setStyle("-fx-font-size: 14px;");
        }
        label.setMaxWidth(Double.MAX_VALUE);
        table.add(label, column, row);
        GridPane.setHgrow(label, Priority.ALWAYS);
    }

    public Parent getRoot() {
        return root;
    }
}