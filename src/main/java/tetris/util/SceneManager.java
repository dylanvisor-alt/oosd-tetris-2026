package tetris.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

/**
 * Manages navigation inside the application's primary window.
 *
 * <p>The same Stage and Scene are reused for every screen. A screen only
 * supplies a new root node, so navigation does not create extra windows or
 * depend on the game model.</p>
 */
public final class SceneManager {

    private static final String STYLESHEET_PATH = "/tetris/styles/style.css";

    private final Stage primaryStage;
    private Scene scene;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = Objects.requireNonNull(primaryStage, "primaryStage");
    }

    /** Displays a screen in the existing window, creating the Scene once. */
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
