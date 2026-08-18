package tetris.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public final class SceneManager {

    private static final String STYLESHEET_PATH = "/tetris/styles/style.css";

    private final Stage primaryStage;
    private Scene scene;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = Objects.requireNonNull(primaryStage, "primaryStage");
    }

    public void show(Parent screenRoot) {
        Objects.requireNonNull(screenRoot, "screenRoot");

        if (scene == null) {
            scene = new Scene(screenRoot);
            addSharedStylesheet();
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(screenRoot);
        }

        primaryStage.sizeToScene();
        if (!primaryStage.isShowing()) {
            primaryStage.show();
        }
        screenRoot.requestFocus();
    }

    private void addSharedStylesheet() {
        URL stylesheet = SceneManager.class.getResource(STYLESHEET_PATH);
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
    }
}
