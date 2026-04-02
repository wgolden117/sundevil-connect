package ser460.sundevilconnect.client.auth;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.AuthServiceProto.*;
import ser460.sundevilconnect.client.main.MainController;

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

                // store current user data
                CurrentUser.getInstance().login(
                        response.getUser().getUserId(),
                        response.getUser().getDisplayName(),
                        response.getRole(),
                        response.getToken());

                try {
                    var loader = new javafx.fxml.FXMLLoader(
                            getClass().getResource("/fxml/main/main_view.fxml")
                    );

                    javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());

                    // Get controller
                    MainController controller = loader.getController();

                    // Set role
                    controller.setupForRole(response.getRole());

                    // Fetch events (background thread)
                    Task<Void> eventTask = new Task<>() {
                        @Override
                        protected Void call() {

                            var eventStub = ConnectionManager.getInstance().getEventBrowsingStub();

                            var eventResponse = eventStub.getAllEvents(
                                    ser460.sundevilconnect.shared.proto.EventBrowsingServiceProto
                                            .GetAllEventsRequest.newBuilder()
                                            .build()
                            );

                            return null;
                        }
                    };

                    new Thread(eventTask).start();

                    // Switch screen
                    javafx.stage.Stage stage = (javafx.stage.Stage) emailField.getScene().getWindow();
                    stage.setScene(scene);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("LOGIN REJECTED");
                errorLabel.setText("Invalid login");
                errorLabel.setVisible(true);
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
