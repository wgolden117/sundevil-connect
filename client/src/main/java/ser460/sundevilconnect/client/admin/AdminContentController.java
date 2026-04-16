package ser460.sundevilconnect.client.admin;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.announcements.AnnouncementDetailsView;
import ser460.sundevilconnect.client.events.EventDetailsView;
import ser460.sundevilconnect.shared.proto.ContentModerationServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.util.List;

public class AdminContentController extends AdminControllerBase {
    @FXML private ListView<EntitiesProto.Content> flaggedContentListView;

    @FXML
    public void initialize() {
        loadFlaggedContent();
    }

    private void loadFlaggedContent() {
        Task<List<EntitiesProto.Content>> task = new Task<>() {
            @Override
            protected List<EntitiesProto.Content> call() {
                return ConnectionManager.getInstance().getContentModerationStub()
                        .getFlaggedContent(ContentModerationServiceProto.GetFlaggedContentRequest.newBuilder().build())
                        .getFlaggedContentList();
            }
        };

        task.setOnSucceeded(event -> {
            flaggedContentListView.setItems(FXCollections.observableArrayList(task.getValue()));
            flaggedContentListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(EntitiesProto.Content content, boolean empty) {
                    super.updateItem(content, empty);
                    if (empty || content == null) {
                        setGraphic(null);
                        return;
                    }

                    String title = content.hasEvent()
                            ? content.getEvent().getTitle()
                            : content.getAnnouncement().getTitle();
                    String typeTag = content.hasEvent() ? "[Event]" : "[Announcement]";

                    Label titleLabel = new Label(title);
                    titleLabel.setStyle("-fx-font-weight: bold;");
                    Label typeLabel = new Label(typeTag);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    HBox topRow = new HBox(8, titleLabel, typeLabel, spacer);

                    Label reasonLabel = new Label("Reason: " + content.getFlagReason());
                    reasonLabel.setWrapText(true);

                    Button approveButton = new Button("Approve");
                    approveButton.setOnAction(e -> handleApprove(content));

                    Button removeButton = new Button("Remove");
                    removeButton.setOnAction(e -> handleRemove(content));

                    Region actionSpacer = new Region();
                    HBox.setHgrow(actionSpacer, Priority.ALWAYS);

                    HBox actionRow = new HBox(8, actionSpacer, approveButton, removeButton);

                    VBox cell = new VBox(4, topRow, reasonLabel, actionRow);
                    cell.setMaxWidth(Double.MAX_VALUE);

                    cell.setOnMouseClicked(click -> {
                        if (click.getClickCount() == 2) {
                            if (content.hasEvent()) showEventPopup(content.getEvent());
                            else showAnnouncementPopup(content.getAnnouncement());
                        }
                    });

                    setGraphic(cell);
                }
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load flagged content");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void handleApprove(EntitiesProto.Content content) {
        if (!confirmAction("Approve Content", "Clear the flag on \"" +
                (content.hasEvent() ? content.getEvent().getTitle() : content.getAnnouncement().getTitle()) + "\"?"))
            return;

        Task<ContentModerationServiceProto.ContentActionResponse> task = new Task<>() {
            @Override
            protected ContentModerationServiceProto.ContentActionResponse call() {
                return ConnectionManager.getInstance().getContentModerationStub()
                        .approveFlaggedContent(ContentModerationServiceProto.ApproveFlaggedContentRequest.newBuilder()
                                .setContentId(content.getContentId())
                                .build());
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue().getSuccess()) loadFlaggedContent();
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to approve content");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void handleRemove(EntitiesProto.Content content) {
        if (!confirmAction("Remove Content", "Permanently remove \"" +
                (content.hasEvent() ? content.getEvent().getTitle() : content.getAnnouncement().getTitle()) + "\"?"))
            return;

        Task<ContentModerationServiceProto.ContentActionResponse> task = new Task<>() {
            @Override
            protected ContentModerationServiceProto.ContentActionResponse call() {
                return ConnectionManager.getInstance().getContentModerationStub()
                        .removeFlaggedContent(ContentModerationServiceProto.RemoveFlaggedContentRequest.newBuilder()
                                .setContentId(content.getContentId())
                                .build());
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue().getSuccess()) loadFlaggedContent();
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to remove content");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void showEventPopup(EntitiesProto.Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/events/event_details.fxml"));
            Node content = loader.load();
            EventDetailsView controller = loader.getController();
            controller.setEvent(event);
            createPopup(content, event.getTitle());
        } catch (Exception e) {
            System.err.println("Failed to open event popup");
            e.printStackTrace();
        }
    }

    private void showAnnouncementPopup(EntitiesProto.Announcement announcement) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/announcements/announcement_details.fxml"));
            Node content = loader.load();
            AnnouncementDetailsView controller = loader.getController();
            controller.setAnnouncement(announcement);
            createPopup(content, announcement.getTitle());
        } catch (Exception e) {
            System.err.println("Failed to open announcement popup");
            e.printStackTrace();
        }
    }

    private void createPopup(Node content, String title) {
        DialogPane pane = new DialogPane();
        pane.setContent(content);
        pane.getButtonTypes().add(ButtonType.CLOSE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setDialogPane(pane);
        dialog.showAndWait();
    }
}
