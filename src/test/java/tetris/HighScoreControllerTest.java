package tetris;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tetris.controller.HighScoreController;
import tetris.model.ScoreEntry;
import tetris.persistence.SQLiteScoreRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighScoreControllerTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void persistsAndReturnsOnlyTenScoresInDescendingOrder() {
        Path databasePath = temporaryDirectory.resolve("test-highscores.db");
        HighScoreController controller = new HighScoreController(
                new SQLiteScoreRepository(databasePath)
        );

        for (int score = 0; score < 12; score++) {
            controller.saveScore("Player " + score, score * 100);
        }

        HighScoreController reopenedController = new HighScoreController(
                new SQLiteScoreRepository(databasePath)
        );
        List<ScoreEntry> topScores = reopenedController.getTopScores();

        assertTrue(Files.exists(databasePath));
        assertEquals(10, topScores.size());
        assertEquals(1_100, topScores.get(0).score());
        assertEquals(200, topScores.get(9).score());
    }

    @Test
    void trimsPlayerNameBeforeSaving() {
        HighScoreController controller = new HighScoreController(
                new SQLiteScoreRepository(temporaryDirectory.resolve("trim-test.db"))
        );

        controller.saveScore("  Gia  ", 500);

        assertEquals("Gia", controller.getTopScores().get(0).playerName());
    }
}
