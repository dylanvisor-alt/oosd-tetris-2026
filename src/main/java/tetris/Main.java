package tetris;

import tetris.controller.GameController;
import tetris.controller.HighScoreController;
import tetris.ui.GameScreen;
import tetris.ui.HighScoreScreen;
import tetris.ui.MainMenuScreen;
import tetris.ui.SplashScreen;
import tetris.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/* Starts the application. Game rules live in GameController and JavaFX drawing lives in GameScreen. */

public class Main extends Application {

    private SceneManager sceneManager;
    private MainMenuScreen mainMenuScreen;
    private final HighScoreController highScoreController = new HighScoreController();

    @Override
    public void start(Stage primaryStage) {
        sceneManager = new SceneManager(primaryStage);
        primaryStage.setTitle("Tetris - 2006ICT");
        primaryStage.setResizable(false);

        mainMenuScreen = new MainMenuScreen(
                this::showGame,
                null,
                this::showHighScores,
                primaryStage::close
        );

        SplashScreen splashScreen = new SplashScreen();
        sceneManager.show(splashScreen.getRoot());
        splashScreen.start(this::showMainMenu);
    }

    private void showMainMenu() {
        sceneManager.show(mainMenuScreen.getRoot());
    }

    private void showGame() {
        GameScreen gameScreen = new GameScreen();
        GameController gameController = new GameController(gameScreen);
        sceneManager.show(gameScreen.getRoot());
        gameController.startGame();
    }

    private void showHighScores() {
        HighScoreScreen highScoreScreen = new HighScoreScreen(
                highScoreController.getTopScores(),
                this::showMainMenu
        );
        sceneManager.show(highScoreScreen.getRoot());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
