package tetris.persistence;

import tetris.model.ScoreEntry;

import java.util.List;

/** Defines high-score storage without exposing database details to controllers. */
public interface ScoreRepository {

    void save(ScoreEntry scoreEntry);

    List<ScoreEntry> findTopScores(int limit);
}
