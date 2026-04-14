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
import ser460.sundevilconnect.client.NavigationController;
import ser460.sundevilconnect.client.SceneController;
import ser460.sundevilconnect.client.clubs.ClubBrowseView;
import ser460.sundevilconnect.client.clubs.ClubDashboard;
import ser460.sundevilconnect.client.clubs.ClubPageView;
import ser460.sundevilconnect.client.clubs.MyClubsView;
import ser460.sundevilconnect.client.events.EventsListView;
import ser460.sundevilconnect.client.events.MyEventsView;
import ser460.sundevilconnect.shared.proto.AuthServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Role;

public class MainController {

    // UI elements
    @FXML private Label nameLabel;
    @FXML private TabPane mainTabPane;

    // Tabs
    @FXML private Tab eventsTab;
    @FXML private Tab clubsTab;
    @FXML private Tab myClubsTab;
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
    @FXML private AnchorPane clubsPane;
    @FXML private AnchorPane myClubsPane;

    // Load state
    private boolean eventsLoaded = false;
    private boolean myEventsLoaded = false;
    private boolean clubsLoaded = false;
    private boolean myClubsLoaded = false;

    @FXML
    private void initialize() {
        javafx.application.Platform.runLater(() -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) mainTabPane.getScene().getWindow();

            // Set square-ish size
            stage.setWidth(900);
            stage.setHeight(825);

            // Optional: prevent super tall resizing
            stage.setMinWidth(700);
            stage.setMinHeight(650);
        });

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == null) return;
            if (newTab == eventsTab) loadEventsView();
            else if (newTab == myEventsTab) loadMyEventsView();
            else if (newTab == clubsTab) loadClubsView();
            else if (newTab == myClubsTab) loadMyClubsView();
        });

        // register with Navigation Controller
        NavigationController.getInstance().setMainController(this);
    }

    /**
     * Configure UI based on user role
     */
    public void setupForRole(Role role) {

        // Show role at top
        nameLabel.setText("Name: " + CurrentUser.getInstance().getUserName());

        // Clear all tabs first
        mainTabPane.getTabs().clear();

        // Add only relevant tabs
        switch (role) {

            case STUDENT -> {
                mainTabPane.getTabs().addAll(
                        eventsTab,
                        myEventsTab,
                        clubsTab,
                        myClubsTab
                );
            }

            case CLUB_LEADER -> {
                mainTabPane.getTabs().addAll(
                        eventsTab,
                        clubsTab,
                        myClubsTab,
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
        if (!eventsLoaded) loadViewIntoPane(eventsPane,
                "/fxml/events/event_list.fxml",
                "Loading events...",
                () -> eventsLoaded = true);
        else {
            EventsListView controller = (EventsListView) eventsPane.getChildren().getFirst().getUserData();
            controller.refresh();
        }
    }

    private void loadMyEventsView() {
        if (!myEventsLoaded) loadViewIntoPane(myEventsPane,
                "/fxml/events/my_events.fxml",
                "Loading your events...",
                () -> myEventsLoaded = true);
        else {
            MyEventsView controller = (MyEventsView) myEventsPane.getChildren().getFirst().getUserData();
            controller.refresh();
        }
    }

    private void loadClubsView() {
        if (!clubsLoaded) loadViewIntoPane(clubsPane,
                "/fxml/clubs/club_browse.fxml",
                "Loading clubs...",
                () -> clubsLoaded = true);
        else {
            ClubBrowseView controller = (ClubBrowseView) clubsPane.getChildren().getFirst().getUserData();
            controller.refresh();
        }
    }

    private void loadMyClubsView() {
        if (!myClubsLoaded) loadViewIntoPane(myClubsPane,
                "/fxml/clubs/club_myclubs.fxml",
                "Loading my clubs...",
                () -> myClubsLoaded = true);
        else {
            MyClubsView controller = (MyClubsView) myClubsPane.getChildren().getFirst().getUserData();
            controller.refresh();
        }
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

    // dynamically opens and creates the club page tab
    public void openClubPageTab(EntitiesProto.Club club) {
        // check for existing tab for club
        for (Tab tab : mainTabPane.getTabs()) {
            if(club.getClubId().equals(tab.getUserData())) {
                mainTabPane.getSelectionModel().select(tab);
                return;
            }
        }
        // load club page view
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/clubs/club_page.fxml")
                );
                Parent root = loader.load();
                ClubPageView controller = loader.getController();
                controller.initWithClub(club);
                return root;
            }
        };
        loadTask.setOnSucceeded(event -> {
            Tab clubTab = new Tab(club.getName());
            clubTab.setUserData(club.getClubId());
            clubTab.setContent(loadTask.getValue());
            clubTab.setClosable(true);
            mainTabPane.getTabs().add(clubTab);
            mainTabPane.getSelectionModel().select(clubTab);
        });
        loadTask.setOnFailed(event -> {
            System.err.println("Failed to load club page: " + club.getName());
            loadTask.getException().printStackTrace();
        });
        new Thread(loadTask).start();
    }

    public void openClubDashboardTab(EntitiesProto.Club club) {
        String tabId = "dashboard-" + club.getClubId();

        for (Tab tab : mainTabPane.getTabs()) {
            if(tabId.equals(tab.getUserData())) {
                mainTabPane.getSelectionModel().select(tab);
                return;
            }
        }

        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/clubs/club_dashboard.fxml")
                );
                Parent root = loader.load();
                ClubDashboard controller = loader.getController();
                controller.setClub(club);
                return root;
            }
        };

        loadTask.setOnSucceeded(event -> {
            Tab dashboardTab = new Tab(club.getName() + " - Dashboard");
            dashboardTab.setUserData(tabId);
            dashboardTab.setContent(loadTask.getValue());
            dashboardTab.setClosable(true);
            mainTabPane.getTabs().add(dashboardTab);
            mainTabPane.getSelectionModel().select(dashboardTab);
        });

        loadTask.setOnFailed(event -> {
            System.err.println("Failed to load dashboard for: " + club.getName());
            loadTask.getException().printStackTrace();
        });

        new Thread(loadTask).start();
    }
}