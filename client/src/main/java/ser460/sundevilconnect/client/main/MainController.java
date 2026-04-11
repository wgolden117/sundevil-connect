package ser460.sundevilconnect.client.main;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.client.SceneController;
import ser460.sundevilconnect.shared.proto.AuthServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Role;

import java.util.List;

public class MainController {

    // UI elements
    @FXML private Label roleLabel;
    @FXML private TabPane mainTabPane;

    // Tabs
    @FXML private Tab eventsTab;
    @FXML private Tab clubsTab;
    @FXML private Tab myEventsTab;
    @FXML private Tab createEventTab;
    @FXML private Tab approveMembersTab;
    @FXML private Tab postUpdatesTab;
    @FXML private Tab manageClubsTab;
    @FXML private Tab approveClubsTab;
    @FXML private Tab flaggedContentTab;

    // AnchorPanes
    @FXML private AnchorPane eventsPane;
    @FXML private AnchorPane myEventsPane;

    // Load state
    private boolean eventsLoaded = false;
    private boolean myEventsLoaded = false;

    @FXML
    private void initialize() {
        javafx.application.Platform.runLater(() -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) mainTabPane.getScene().getWindow();

            // Set square-ish size
            stage.setWidth(750);
            stage.setHeight(700);

            // Optional: prevent super tall resizing
            stage.setMinWidth(700);
            stage.setMinHeight(650);
        });

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == eventsTab) {
                loadEventsView();
            } else if (newTab == myEventsTab) {
                loadMyEventsView();
            }
        });
    }

    /**
     * Configure UI based on user role
     */
    public void setupForRole(Role role) {

        // Show role at top
        roleLabel.setText("Role: " + role.name());

        // Clear all tabs first
        mainTabPane.getTabs().clear();

        // Add only relevant tabs
        switch (role) {

            case STUDENT -> {
                mainTabPane.getTabs().addAll(eventsTab, myEventsTab, clubsTab);
            }

            case CLUB_LEADER -> {
                mainTabPane.getTabs().addAll(
                        eventsTab,
                        clubsTab,
                        createEventTab,
                        approveMembersTab,
                        postUpdatesTab
                );
            }

            case ADMIN -> {
                mainTabPane.getTabs().addAll(
                        eventsTab,
                        manageClubsTab,
                        approveClubsTab,
                        flaggedContentTab
                );
            }
        }
    }

    private void loadViewIntoPane(AnchorPane pane, String fxmlPath, String loadingMessage, Runnable onLoaded) {
        // show spinner
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(50,50);
        Label loadingText = new Label(loadingMessage);
        VBox box = new VBox(10, spinner, loadingText);
        box.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        pane.getChildren().setAll(box);

        // load fxml in background
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(fxmlPath)
                );
                return loader.load();
            }
        };

        loadTask.setOnSucceeded(event -> {
            pane.getChildren().setAll(loadTask.getValue());
            onLoaded.run();
        });

        loadTask.setOnFailed(event -> {
            System.err.println("Failed to load view: " + fxmlPath);
            loadTask.getException().printStackTrace();
        });

        new Thread(loadTask).start();
    }

    private void loadEventsView() {
        if (eventsLoaded) return;
        loadViewIntoPane(eventsPane,
                "/fxml/events/event_list.fxml",
                "Loading events...",
                () -> eventsLoaded = true);
    }

    private void loadMyEventsView() {
        if (myEventsLoaded) return;
        loadViewIntoPane(myEventsPane,
                "/fxml/events/my_events.fxml",
                "Loading your events...",
                () -> myEventsLoaded = true);
    }

    @FXML
    private void handleLogout() {
        CurrentUser user = CurrentUser.getInstance();

        AuthServiceProto.LogoutRequest request = AuthServiceProto.LogoutRequest
                .newBuilder()
                .setUserId(user.getUserId())
                .setToken(user.getSessionToken())
                .build();

        Task<AuthServiceProto.LogoutResponse> logoutTask = new Task<>() {
            @Override
            protected AuthServiceProto.LogoutResponse call() {
                return ConnectionManager.getInstance().getAuthStub().logout(request);
            }
        };

        logoutTask.setOnSucceeded(event -> {
            AuthServiceProto.LogoutResponse response = logoutTask.getValue();

            if (response.getSuccess()) {
                System.out.println("Logged out successfully");
                CurrentUser.getInstance().logout();
                SceneController.getInstance().changeSceneToLogin();
            } else {
                System.out.println("Logged out failed");
            }
        });

        logoutTask.setOnFailed(event -> {
            System.out.println("Communication Error on Logout");
        });

        new Thread(logoutTask).start();
    }
}