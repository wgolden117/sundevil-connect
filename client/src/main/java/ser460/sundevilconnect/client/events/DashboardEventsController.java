package ser460.sundevilconnect.client.events;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.client.clubs.DashboardSectionController;
import ser460.sundevilconnect.shared.proto.EntitiesProto;
import ser460.sundevilconnect.shared.proto.EventManagementServiceProto;

import java.util.Comparator;
import java.util.List;

public class DashboardEventsController extends DashboardSectionController {
    @FXML private VBox root;
    @FXML private ListView<EntitiesProto.Event> eventsListView;
    @FXML private EventDetailsView eventDetailsController;

    private EntitiesProto.Club club;

    @Override
    public void setClub(EntitiesProto.Club club) {
        this.club = club;
        loadEvents();
    }

    @FXML
    private void initialize() {
        eventsListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        eventDetailsController.setEvent(newVal);
                        eventDetailsController.hideRegisterButton();
                    }
                });
    }

    private void loadEvents() {
        Task<List<EntitiesProto.Event>> task = new Task<>() {
            @Override
            protected List<EntitiesProto.Event> call() {
                return ConnectionManager.getInstance().getEventManagementStub()
                        .getEventsForClub(EventManagementServiceProto.GetEventsForClubRequest
                                .newBuilder().setClubId(club.getClubId())
                                .build())
                        .getEventsList()
                        .stream()
                        .sorted(Comparator.comparing(EntitiesProto.Event::getEventDate))
                        .toList();
            }
        };

        task.setOnSucceeded(event -> {
            eventsListView.setItems(FXCollections.observableArrayList(task.getValue()));
            eventsListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(EntitiesProto.Event e, boolean empty) {
                    super.updateItem(e, empty);
                    if (empty || e == null) {
                        setGraphic(null);
                        return;
                    }

                    Label titleLabel = new Label(e.getTitle());
                    Label dateLabel = new Label(e.getEventDate());

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button editButton = new Button("Edit");
                    editButton.setOnAction(ev -> handleEdit(e));

                    Button cancelButton = new Button("Cancel Event");
                    cancelButton.setOnAction(ev -> handleCancel(e));

                    HBox cell = new HBox(8, titleLabel, dateLabel, spacer, editButton, cancelButton);
                    cell.setMaxWidth(Double.MAX_VALUE);
                    setGraphic(cell);
                }
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load events");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    private void onCreateEventClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/events/event_dialog.fxml"));
            DialogPane dialogPane = loader.load();
            EventDialogController dialogController = loader.getController();
            dialogController.setClub(club);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Create Event");
            dialog.setDialogPane(dialogPane);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    EntitiesProto.Event event = dialogController.getEvent();
                    Task<EventManagementServiceProto.EventManagementActionResponse> task = new Task<>() {
                        @Override
                        protected EventManagementServiceProto.EventManagementActionResponse call() {
                            return ConnectionManager.getInstance().getEventManagementStub()
                                    .createEvent(EventManagementServiceProto.CreateEventRequest
                                            .newBuilder()
                                            .setUserId(CurrentUser.getInstance().getUserId())
                                            .setEvent(event)
                                            .build());
                        }
                    };
                    task.setOnSucceeded(e -> {
                        if (task.getValue().getSuccess()) loadEvents();
                    });
                    task.setOnFailed(e -> {
                        System.err.println("Failed to create event");
                        task.getException().printStackTrace();
                    });
                    new Thread(task).start();
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to open event dialog");
            e.printStackTrace();
        }
    }

    private void handleEdit(EntitiesProto.Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/events/event_dialog.fxml"));
            DialogPane dialogPane = loader.load();
            EventDialogController dialogController = loader.getController();
            dialogController.setClub(club);
            dialogController.setEvent(event);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Event");
            dialog.setDialogPane(dialogPane);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    EntitiesProto.Event updatedEvent = dialogController.getEvent();
                    Task<EventManagementServiceProto.EventManagementActionResponse> task = new Task<>() {
                        @Override
                        protected EventManagementServiceProto.EventManagementActionResponse call() {
                            return ConnectionManager.getInstance().getEventManagementStub()
                                    .updateEvent(EventManagementServiceProto.UpdateEventRequest
                                            .newBuilder()
                                            .setUpdatedEvent(updatedEvent)
                                            .build());
                        }
                    };
                    task.setOnSucceeded(e -> {
                        if (task.getValue().getSuccess()) loadEvents();
                    });
                    task.setOnFailed(e -> {
                        System.err.println("Failed to update event");
                        task.getException().printStackTrace();
                    });
                    new Thread(task).start();
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to open event dialog");
            e.printStackTrace();
        }
    }

    private void handleCancel(EntitiesProto.Event event) {
        if (!confirmAction("Cancel Event", "Are you sure you want to cancel \"" + event.getTitle() + "\"?")) return;

        Task<EventManagementServiceProto.EventManagementActionResponse> task = new Task<>() {
            @Override
            protected EventManagementServiceProto.EventManagementActionResponse call() {
                return ConnectionManager.getInstance().getEventManagementStub()
                        .cancelEvent(EventManagementServiceProto.CancelEventRequest
                                .newBuilder()
                                .setEventId(event.getEventId())
                                .build());
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue().getSuccess()) loadEvents();
        });

        task.setOnFailed(e -> {
            System.err.println("Failed to cancel event");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
}
