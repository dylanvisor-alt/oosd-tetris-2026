package tetris;

import tetris.controller.GameController;
import tetris.ui.GameScreen;
import tetris.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/* Starts the application. Game rules live in GameController and JavaFX drawing lives in GameScreen. */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager sceneManager = new SceneManager(primaryStage);
        GameScreen gameScreen = new GameScreen();
        GameController gameController = new GameController(gameScreen);

        primaryStage.setTitle("Tetris - 2006ICT");
        primaryStage.setResizable(false);
        sceneManager.show(gameScreen.getRoot());

        gameController.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
