package tetris;

import tetris.controller.GameController;
import tetris.ui.GameScreen;
import tetris.ui.MainMenuScreen;
import tetris.ui.SplashScreen;
import tetris.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/* Starts the application. Game rules live in GameController and JavaFX drawing lives in GameScreen. */
public class Main extends Application {

    private SceneManager sceneManager;
    private MainMenuScreen mainMenuScreen;

    @Override
    public void start(Stage primaryStage) {
        sceneManager = new SceneManager(primaryStage);
        primaryStage.setTitle("Tetris - 2006ICT");
        primaryStage.setResizable(false);

        mainMenuScreen = new MainMenuScreen(
                this::showGame,
                null,
                null,
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

    public static void main(String[] args) {
        launch(args);
    }
}
