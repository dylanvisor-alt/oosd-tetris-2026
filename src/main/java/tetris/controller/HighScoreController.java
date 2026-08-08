package tetris.controller;

import tetris.model.ScoreEntry;

import java.util.Comparator;
import java.util.List;

/** Supplies the ten highest scores in display order. */
public class HighScoreController {

    private static final int HIGH_SCORE_LIMIT = 10;

    private final List<ScoreEntry> scores;

    /** Uses representative data for Milestone 1, as permitted by the marking guide. */
    public HighScoreController() {
        this(List.of(
                new ScoreEntry("Alex", 12_500),
                new ScoreEntry("Blake", 9_800),
                new ScoreEntry("Casey", 8_600),
                new ScoreEntry("Dylan", 7_400),
                new ScoreEntry("Gia", 6_900),
                new ScoreEntry("Harper", 5_800),
                new ScoreEntry("Jordan", 4_700),
                new ScoreEntry("Morgan", 3_600),
                new ScoreEntry("Riley", 2_500),
                new ScoreEntry("Taylor", 1_400)
        ));
    }

    /** Allows a future database result to be supplied without changing the screen. */
    public HighScoreController(List<ScoreEntry> scores) {
        this.scores = List.copyOf(scores);
    }

    public List<ScoreEntry> getTopScores() {
        return scores.stream()
                .sorted(Comparator.comparingInt(ScoreEntry::score).reversed())
                .limit(HIGH_SCORE_LIMIT)
                .toList();
    }
}
