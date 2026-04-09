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

    // Tabs
    @FXML private TabPane mainTabPane;
    @FXML private Tab eventsTab;
    @FXML private Tab clubsTab;
    @FXML private Tab myEventsTab;
    @FXML private AnchorPane myEventsPane;


    // UI elements
    @FXML private Label roleLabel;
    @FXML private Tab createEventTab;
    @FXML private Tab approveMembersTab;
    @FXML private Tab postUpdatesTab;

    @FXML private Tab manageClubsTab;
    @FXML private Tab approveClubsTab;
    @FXML private Tab flaggedContentTab;
    @FXML private AnchorPane eventsPane;
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
                myEventsLoaded = false; // force reload
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

    private void loadEventsView() {
        if (eventsLoaded) return;

        // Step 1: Show loading spinner + text
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(50, 50);

        Label loadingText = new Label("Loading events...");

        VBox box = new VBox(10, spinner, loadingText);
        box.setAlignment(Pos.CENTER);

        eventsPane.getChildren().setAll(box);

        // Anchor the box
        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);

        // Step 2: Load view in background
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/events/event_list.fxml")
                );
                return loader.load();
            }
        };

        // Step 3: Replace spinner with actual view
        loadTask.setOnSucceeded(event -> {
            Parent view = loadTask.getValue();
            eventsPane.getChildren().setAll(view);
            eventsLoaded = true;
        });

        loadTask.setOnFailed(event -> {
            System.err.println("Failed to load events view");
            loadTask.getException().printStackTrace();
        });

        new Thread(loadTask).start();
    }

    private void loadMyEventsView() {
        if (myEventsLoaded) return;

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(50, 50);

        Label loadingText = new Label("Loading your events...");

        VBox box = new VBox(10, spinner, loadingText);
        box.setAlignment(Pos.CENTER);

        myEventsPane.getChildren().setAll(box);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);

        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/events/my_events.fxml")
                );
                return loader.load();
            }
        };

        loadTask.setOnSucceeded(event -> {
            Parent view = loadTask.getValue();
            myEventsPane.getChildren().setAll(view);
            myEventsLoaded = true;
        });

        loadTask.setOnFailed(event -> {
            System.err.println("Failed to load My Events view");
            loadTask.getException().printStackTrace();
        });

        new Thread(loadTask).start();
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