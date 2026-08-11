package tetris;

import tetris.controller.GameController;
import tetris.controller.HighScoreController;
import tetris.ui.ExitDialog;
import tetris.ui.GameScreen;
import tetris.ui.HighScoreScreen;
import tetris.ui.MainMenuScreen;
import tetris.ui.SplashScreen;
import tetris.util.SceneManager;
import javafx.application.Application;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
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

        ExitDialog exitDialog = new ExitDialog(primaryStage);
        Runnable requestExit = () -> {
            if (exitDialog.showAndWait()) {
                primaryStage.hide();
            }
        };
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            requestExit.run();
        });

        mainMenuScreen = new MainMenuScreen(
                this::showGame,
                null,
                this::showHighScores,
                requestExit
        );

        SplashScreen splashScreen = new SplashScreen();
        sceneManager.show(splashScreen.getRoot());
        splashScreen.start(this::showMainMenu);
    }

    private void showMainMenu() {
        sceneManager.show(mainMenuScreen.getRoot());
    }

    private void showGame() {
        GameScreen gameScreen = new GameScreen(this::showGame, this::saveHighScore);
        GameController gameController = new GameController(gameScreen);
        sceneManager.show(gameScreen.getRoot());
        gameController.startGame();
    }

    private void saveHighScore(int finalScore) {
        TextInputDialog nameDialog = new TextInputDialog("Player");
        nameDialog.setTitle("Save High Score");
        nameDialog.setHeaderText("Game over - Score: " + finalScore);
        nameDialog.setContentText("Player name:");
        nameDialog.getEditor().setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 20 ? change : null
        ));

        nameDialog.showAndWait()
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .ifPresent(name -> {
                    highScoreController.saveScore(name, finalScore);
                    showHighScores();
                });
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
