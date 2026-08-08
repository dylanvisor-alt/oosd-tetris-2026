package tetris;

import tetris.controller.GameController;
import tetris.ui.GameScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* Starts the application. Game rules live in GameController and JavaFX drawing lives in GameScreen. */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameScreen gameScreen = new GameScreen();
        GameController gameController = new GameController(gameScreen);

        Scene scene = new Scene(gameScreen.getRoot());
        primaryStage.setTitle("Tetris - 2006ICT");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();

        gameController.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
