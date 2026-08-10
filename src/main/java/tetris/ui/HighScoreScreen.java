package tetris.ui;

import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tetris.model.ScoreEntry;
import tetris.util.Constants;

import java.util.List;

/** Displays the top ten scores and provides navigation back to the main menu. */
public class HighScoreScreen {

    private final StackPane root = new StackPane();

    public HighScoreScreen(List<ScoreEntry> scores, Runnable onBack) {
        Label title = new Label("HIGH SCORES");
        title.getStyleClass().add("screen-title");

        GridPane scoreTable = createScoreTable(scores);

        Button backButton = new Button("Back");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.getStyleClass().add("menu-button");
        backButton.setOnAction(event -> onBack.run());

        VBox content = new VBox(22, title, scoreTable, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(36));
        content.setMaxWidth(400);
        content.getStyleClass().add("menu-panel");

        VBox.setVgrow(scoreTable, Priority.ALWAYS);

        root.getChildren().add(content);
        root.setAlignment(Pos.CENTER);
        root.setMinSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
        root.setPrefSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
        root.getStyleClass().add("app-background");
    }

    private GridPane createScoreTable(List<ScoreEntry> scores) {
        GridPane table = new GridPane();
        table.setHgap(24);
        table.setVgap(8);
        table.setAlignment(Pos.CENTER);
        table.setPadding(new Insets(16));
        table.getStyleClass().add("score-table");

        addCell(table, "Rank", 0, 0, "score-header");
        addCell(table, "Name", 1, 0, "score-header");
        addCell(table, "Score", 2, 0, "score-header");

        if (scores.isEmpty()) {
            Label emptyMessage = new Label("No scores saved yet");
            emptyMessage.getStyleClass().add("score-cell");
            table.add(emptyMessage, 0, 1, 3, 1);
            GridPane.setHalignment(emptyMessage, HPos.CENTER);
            return table;
        }

        for (int index = 0; index < scores.size(); index++) {
            ScoreEntry entry = scores.get(index);
            int row = index + 1;
            addCell(table, Integer.toString(row), 0, row, "score-cell");
            addCell(table, entry.playerName(), 1, row, "score-cell");
            addCell(table, String.format("%,d", entry.score()), 2, row, "score-cell");
        }
        return table;
    }

    private void addCell(GridPane table, String text, int column, int row, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setMaxWidth(Double.MAX_VALUE);
        table.add(label, column, row);
        GridPane.setHgrow(label, Priority.ALWAYS);
    }

    public Parent getRoot() {
        return root;
    }
}
