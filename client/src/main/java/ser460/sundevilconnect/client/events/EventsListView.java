package ser460.sundevilconnect.client.events;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.shared.proto.EventBrowsingServiceProto;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class EventsListView {

    @FXML
    private ListView<Event> eventListView;

    @FXML
    private void initialize() {
        setupListView();
        setupClickHandler();
        loadEvents();
    }

    @FXML
    private AnchorPane detailsPane;

    private void setupListView() {
        eventListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                setText(formatEvent(event, empty));
            }
        });
    }

    private String formatEvent(Event event, boolean empty) {
        if (empty || event == null) {
            return null;
        }
        return event.getTitle()
                + "\n" + event.getLocation()
                + " | " + event.getCategory();
    }

    private void setupClickHandler() {
        eventListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        loadEventDetails(newVal);
                    }
        });
    }

    private void loadEvents() {
        try {
            var stub = ConnectionManager.getInstance().getEventBrowsingStub();

            var request = EventBrowsingServiceProto.GetAllEventsRequest
                    .newBuilder()
                    .build();

            var response = stub.getAllEvents(request);

            var events = response.getEventsList();
            eventListView.getItems().setAll(events);
            detailsPane.getChildren().clear();

        } catch (Exception e) {
            System.err.println("Error loading events: " + e.getMessage());
        }
    }

    private void loadEventDetails(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/events/event_details.fxml")
            );

            Parent view = loader.load();

            // Pass data to details controller
            EventDetailsView controller = loader.getController();
            controller.setEvent(event);

            detailsPane.getChildren().setAll(view);

        } catch (Exception e) {
            System.err.println("Error loading details: " + e.getMessage());
        }
    }
}