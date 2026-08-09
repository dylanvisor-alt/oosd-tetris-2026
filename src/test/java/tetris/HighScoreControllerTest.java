package tetris;

import org.junit.jupiter.api.Test;
import tetris.controller.HighScoreController;
import tetris.model.ScoreEntry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HighScoreControllerTest {

    @Test
    void returnsOnlyTenScoresInDescendingOrder() {
        List<ScoreEntry> scores = new ArrayList<>();
        for (int score = 0; score < 12; score++) {
            scores.add(new ScoreEntry("Player " + score, score * 100));
        }

        List<ScoreEntry> topScores = new HighScoreController(scores).getTopScores();

        assertEquals(10, topScores.size());
        assertEquals(1_100, topScores.get(0).score());
        assertEquals(200, topScores.get(9).score());
    }
}
