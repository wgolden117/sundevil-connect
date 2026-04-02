package ser460.sundevilconnect.client;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {
    private static SceneController instance;

    private Stage stage;

    private SceneController() {}

    public static SceneController getInstance() {
        if (instance == null) {
            instance = new SceneController();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void changeSceneToLogin() {
        changeScene(new FXMLLoader(getClass()
                .getResource("/fxml/auth/login.fxml")));
    }

    public void changeSceneToMain() {
        changeScene(new FXMLLoader(getClass()
                .getResource("/fxml/main/main_view.fxml")));
    }

    private void changeScene(FXMLLoader loader) {
        try {
            stage.setScene(new Scene(loader.load()));

            Platform.runLater(() -> {
                // reset window size
                stage.setMinWidth(0);
                stage.setMinHeight(0);
                stage.sizeToScene();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
