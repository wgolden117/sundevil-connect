package ser460.sundevilconnect.client.auth;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.shared.proto.AuthServiceProto.*;

public class LoginPage {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLoginButton() {
        // build LoginRequest from emailField and passwordField
        LoginRequest request = LoginRequest
                .newBuilder()
                .setEmail(emailField.getText())
                .setPassword(passwordField.getText())
                .build();

        // We run this as a Task since the stubs are blocking- which should not
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
            // back on UI thread here - safe to update UI
            if (response.getSuccess()) {
                // TODO: load main view
                errorLabel.setVisible(false);
            } else {
                errorLabel.setText("Invalid login");
                errorLabel.setVisible(true);
            }
        });

        loginTask.setOnFailed(event -> {
            // gRPC call failed entirely - network error etc.
            errorLabel.setText("Connection error");
            errorLabel.setVisible(true);
        });

        new Thread(loginTask).start();
    }
}
