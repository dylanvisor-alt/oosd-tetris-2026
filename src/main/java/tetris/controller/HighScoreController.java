package tetris.controller;

import tetris.model.ScoreEntry;
import tetris.persistence.ScoreRepository;
import tetris.persistence.SQLiteScoreRepository;

import java.util.List;
import java.util.Objects;

/** Coordinates high-score storage without coupling JavaFX screens to SQLite. */
public class HighScoreController {

    private static final int HIGH_SCORE_LIMIT = 10;

    private final ScoreRepository scoreRepository;

    /** Uses the application's local SQLite database. */
    public HighScoreController() {
        this(new SQLiteScoreRepository(SQLiteScoreRepository.defaultDatabasePath()));
    }

    public HighScoreController(ScoreRepository scoreRepository) {
        this.scoreRepository = Objects.requireNonNull(scoreRepository, "scoreRepository");
    }

    public List<ScoreEntry> getTopScores() {
        return scoreRepository.findTopScores(HIGH_SCORE_LIMIT);
    }

    public void saveScore(String playerName, int score) {
        Objects.requireNonNull(playerName, "playerName");
        scoreRepository.save(new ScoreEntry(playerName.trim(), score));
    }
}
