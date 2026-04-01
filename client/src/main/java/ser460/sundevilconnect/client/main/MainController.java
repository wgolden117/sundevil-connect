package ser460.sundevilconnect.client.main;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Role;

import java.util.List;

public class MainController {

    // Tabs
    @FXML private TabPane mainTabPane;
    @FXML private Tab eventsTab;
    @FXML private Tab clubsTab;

    // UI elements
    @FXML private Label roleLabel;
    @FXML private ListView<Event> eventListView;
    @FXML private Tab createEventTab;
    @FXML private Tab approveMembersTab;
    @FXML private Tab postUpdatesTab;

    @FXML private Tab manageClubsTab;
    @FXML private Tab approveClubsTab;
    @FXML private Tab flaggedContentTab;

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
                mainTabPane.getTabs().addAll(eventsTab, clubsTab);
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

    /**
     * Load events into the ListView
     */
    public void loadEvents(List<Event> events) {

        eventListView.getItems().clear();
        eventListView.getItems().addAll(events);

        // Customize how events display
        eventListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);

                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(event.getTitle() + " | " + event.getCategory());
                }
            }
        });
    }
}