package ser460.sundevilconnect.client.events;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.shared.proto.EntitiesProto.Event;
import ser460.sundevilconnect.shared.proto.EventBrowsingServiceProto;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class EventsListView implements FilterListener{

    @FXML private VBox root;
    @FXML
    private ListView<Event> eventListView;

    @FXML
    @SuppressWarnings("unused")
    private AnchorPane filterPanel;

    @FXML
    private EventFilterPanel filterPanelController;

    @FXML
    private void initialize() {
        setupListView();
        setupClickHandler();
        loadEvents();

        root.setUserData(this);

        if (filterPanelController != null) {
            filterPanelController.setFilterListener(this);
        }
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

    @Override
    public void onFiltersApplied(
            String category,
            boolean paid,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            String club
    ) {
        System.out.println("EventsListView received filters!");
        System.out.println("Category: " + category);
        System.out.println("Paid: " + paid);
        System.out.println("From: " + fromDate);
        System.out.println("To: " + toDate);
        System.out.println("Club: " + club);

        fetchFilteredEvents(category, paid, fromDate, toDate, club);
    }

    @Override
    public void onFiltersCleared() {
        System.out.println("Filters cleared → reloading all events");

        loadEvents(); // reload full event list
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

    private void fetchFilteredEvents(
            String category,
            boolean paid,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            String club
    ) {
        try {
            var stub = ConnectionManager.getInstance().getEventBrowsingStub();

            var requestBuilder = EventBrowsingServiceProto.GetFilteredEventsRequest.newBuilder();

            // Category filter (always apply if selected)
            if (category != null && !category.isEmpty()) {
                var categoryFilter = EventBrowsingServiceProto.CategoryFilter.newBuilder()
                        .setCategory(category)
                        .build();

                var filter = EventBrowsingServiceProto.EventFilter.newBuilder()
                        .setType(EventBrowsingServiceProto.FilterType.CATEGORY)
                        .setCategory(categoryFilter)
                        .build();

                requestBuilder.addFilters(filter);
            }

            // Paid filter (ONLY if checkbox is checked)
            if (paid) {
                var paidFilter = EventBrowsingServiceProto.PaidFilter.newBuilder()
                        .setIsPaid(true)
                        .build();

                var paidEventFilter = EventBrowsingServiceProto.EventFilter.newBuilder()
                        .setType(EventBrowsingServiceProto.FilterType.PAID)
                        .setPaid(paidFilter)
                        .build();

                requestBuilder.addFilters(paidEventFilter);
            }

            // DATE RANGE
            if (fromDate != null && toDate != null) {
                var dateFilter = EventBrowsingServiceProto.DateFilter.newBuilder()
                        .setFromDate(fromDate.atStartOfDay().toString())
                        .setToDate(toDate.atStartOfDay().toString())
                        .build();

                requestBuilder.addFilters(
                        EventBrowsingServiceProto.EventFilter.newBuilder()
                                .setType(EventBrowsingServiceProto.FilterType.DATE)
                                .setDate(dateFilter)
                                .build()
                );
            }

            // CLUB
            if (club != null && !club.isEmpty()) {
                var clubFilter = EventBrowsingServiceProto.ClubFilter.newBuilder()
                        .setClubId(club)
                        .build();

                requestBuilder.addFilters(
                        EventBrowsingServiceProto.EventFilter.newBuilder()
                                .setType(EventBrowsingServiceProto.FilterType.CLUB)
                                .setClub(clubFilter)
                                .build()
                );
            }

            // Call backend
            var response = stub.getFilteredEvents(requestBuilder.build());

            var events = response.getEventsList();
            eventListView.getItems().setAll(events);

            detailsPane.getChildren().clear();

        } catch (Exception e) {
            System.err.println("Error fetching filtered events: " + e.getMessage());
        }
    }

    public void refresh() { loadEvents(); }
}