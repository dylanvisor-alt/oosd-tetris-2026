package tetris.persistence;

import tetris.model.ScoreEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Stores high scores in a small local SQLite database. */
public final class SQLiteScoreRepository implements ScoreRepository {

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS high_scores (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_name TEXT NOT NULL,
                score INTEGER NOT NULL CHECK (score >= 0),
                achieved_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;

    private final String databaseUrl;

    public SQLiteScoreRepository(Path databasePath) {
        Objects.requireNonNull(databasePath, "databasePath");
        Path absolutePath = databasePath.toAbsolutePath();
        createParentDirectory(absolutePath);
        databaseUrl = "jdbc:sqlite:" + absolutePath;
        initialiseDatabase();
    }

    /** Keeps runtime data outside the Git repository and Maven build output. */
    public static Path defaultDatabasePath() {
        return Path.of(
                System.getProperty("user.home"),
                ".oosd-tetris",
                "highscores.db"
        );
    }

    @Override
    public void save(ScoreEntry scoreEntry) {
        Objects.requireNonNull(scoreEntry, "scoreEntry");
        String sql = "INSERT INTO high_scores (player_name, score) VALUES (?, ?)";

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, scoreEntry.playerName());
            statement.setInt(2, scoreEntry.score());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw databaseFailure("save a high score", exception);
        }
    }

    @Override
    public List<ScoreEntry> findTopScores(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Score limit must be positive");
        }

        String sql = """
                SELECT player_name, score
                FROM high_scores
                ORDER BY score DESC, id ASC
                LIMIT ?
                """;
        List<ScoreEntry> scores = new ArrayList<>();

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    scores.add(new ScoreEntry(
                            result.getString("player_name"),
                            result.getInt("score")
                    ));
                }
            }
        } catch (SQLException exception) {
            throw databaseFailure("load high scores", exception);
        }
        return List.copyOf(scores);
    }

    private void initialiseDatabase() {
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE_SQL);
        } catch (SQLException exception) {
            throw databaseFailure("initialise the high-score database", exception);
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    private void createParentDirectory(Path databasePath) {
        try {
            Files.createDirectories(databasePath.getParent());
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create the database directory", exception);
        }
    }

    private IllegalStateException databaseFailure(String action, SQLException exception) {
        return new IllegalStateException("Could not " + action, exception);
    }
}
