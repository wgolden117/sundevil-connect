package ser460.sundevilconnect.client.auth;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.client.SceneController;
import ser460.sundevilconnect.client.notifications.NotificationStore;
import ser460.sundevilconnect.shared.proto.AuthServiceProto.*;
import ser460.sundevilconnect.client.main.MainController;

public class LoginPage {
    @FXML private AnchorPane root;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);

        javafx.application.Platform.runLater(() -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) root.getScene().getWindow();

            // reset window size
            stage.setMinWidth(0);
            stage.setMinHeight(0);
            stage.sizeToScene();

            // Re-center after resize
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLoginButton();
            }
        });
    }

    @FXML
    private void handleLoginButton() {
        // build LoginRequest from emailField and passwordField
        LoginRequest request = LoginRequest
                .newBuilder()
                .setEmail(emailField.getText())
                .setPassword(passwordField.getText())
                .build();

        // We run this as a Task since the stubs are blocking-which should not
        // run on the JavaFX UI thread. By running it as a task, it is delegated to a
        // new thread, and when it completes, it runs either setOnSucceeded if the task
        // completes normally, or setOnFailed if the task throws an exception.
        // This is the pattern we'll want to use for any of the gRPC calls to not
        // stall the UI threads execution.
        Task<LoginResponse> loginTask = new Task<>() {
            @Override
            protected LoginResponse call() {
                return ConnectionManager.getInstance().getAuthStub().login(request);
            }
        };

        loginTask.setOnSucceeded(event -> {
            LoginResponse response = loginTask.getValue();

            if (response.getSuccess()) {
                System.out.println("LOGIN SUCCESS");
                NotificationStore.getInstance().clear();

                // store current user data
                CurrentUser.getInstance().login(
                        response.getUser().getUserId(),
                        response.getUser().getDisplayName(),
                        response.getRole(),
                        response.getToken());

                SceneController.getInstance().changeSceneToMain();

            } else {
                System.out.println("LOGIN REJECTED");
                errorLabel.setText("Invalid login");
                errorLabel.setVisible(true);
                passwordField.clear();
            }
        });

        loginTask.setOnFailed(event -> {
            System.out.println("CONNECTION FAILED");
            // gRPC call failed entirely - network error etc.
            errorLabel.setText("Connection error");
            errorLabel.setVisible(true);
        });

        new Thread(loginTask).start();
    }
}
