package ser460.sundevilconnect.client.announcements;

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
import ser460.sundevilconnect.shared.proto.AnnouncementServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.util.List;

public class DashboardAnnouncementsController extends DashboardSectionController {
    @FXML private VBox root;
    @FXML private ListView<EntitiesProto.Announcement> announcementsListView;
    @FXML private AnnouncementDetailsView announcementDetailsController;

    private EntitiesProto.Club club;

    @FXML
    private void initialize() {
        announcementsListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        announcementDetailsController.setAnnouncement(newVal);
                    }
                });
    }

    @Override
    public void setClub(EntitiesProto.Club club) {
        this.club = club;
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        Task<List<EntitiesProto.Announcement>> task = new Task<>() {
            @Override
            protected List<EntitiesProto.Announcement> call() {
                return ConnectionManager.getInstance().getAnnouncementStub()
                        .getAnnouncementsForClub(AnnouncementServiceProto.GetAnnouncementsForClubRequest
                                .newBuilder()
                                .setClubId(club.getClubId())
                                .setRequestingUserId(CurrentUser.getInstance().getUserId())
                                .build())
                        .getAnnouncementsList();
            }
        };

        task.setOnSucceeded(event -> {
            announcementsListView.setItems(FXCollections.observableArrayList(task.getValue()));
            announcementsListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(EntitiesProto.Announcement a, boolean empty) {
                    super.updateItem(a, empty);
                    if (empty || a == null) {
                        setGraphic(null);
                        return;
                    }

                    Label titleLabel = new Label(a.getTitle());
                    Label statusLabel = new Label(a.getStatus());

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button publishButton = new Button(
                            "PUBLISHED".equals(a.getStatus()) ? "Unpublish" : "Publish");
                    publishButton.setOnAction(ev -> handlePublish(a));

                    Button editButton = new Button("Edit");
                    editButton.setOnAction(ev -> handleEdit(a));

                    Button deleteButton = new Button("Delete");
                    deleteButton.setOnAction(ev -> handleDelete(a));

                    HBox cell = new HBox(8, titleLabel, statusLabel, spacer, publishButton, editButton, deleteButton);
                    cell.setMaxWidth(Double.MAX_VALUE);
                    setGraphic(cell);
                }
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load announcements");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    private void onCreateAnnouncementClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/announcements/announcement_dialog.fxml"));
            DialogPane dialogPane = loader.load();
            AnnouncementDialogController dialogController = loader.getController();
            dialogController.setClub(club);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Create Announcement");
            dialog.setDialogPane(dialogPane);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    EntitiesProto.Announcement announcement = dialogController.getAnnouncement();
                    Task<AnnouncementServiceProto.AnnouncementActionResponse> task = new Task<>() {
                        @Override
                        protected AnnouncementServiceProto.AnnouncementActionResponse call() {
                            return ConnectionManager.getInstance().getAnnouncementStub()
                                    .createAnnouncement(AnnouncementServiceProto.CreateAnnouncementRequest
                                            .newBuilder()
                                            .setAnnouncement(announcement)
                                            .setClubLeaderId(CurrentUser.getInstance().getUserId())
                                            .build());
                        }
                    };
                    task.setOnSucceeded(e -> {
                        if (task.getValue().getSuccess()) loadAnnouncements();
                    });
                    task.setOnFailed(e -> {
                        System.err.println("Failed to create announcement");
                        task.getException().printStackTrace();
                    });
                    new Thread(task).start();
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to open announcement dialog");
            e.printStackTrace();
        }
    }

    private void handleEdit(EntitiesProto.Announcement announcement) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/announcements/announcement_dialog.fxml"));
            DialogPane dialogPane = loader.load();
            AnnouncementDialogController dialogController = loader.getController();
            dialogController.setClub(club);
            dialogController.setAnnouncement(announcement);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Announcement");
            dialog.setDialogPane(dialogPane);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    EntitiesProto.Announcement updated = dialogController.getAnnouncement();
                    Task<AnnouncementServiceProto.AnnouncementActionResponse> task = new Task<>() {
                        @Override
                        protected AnnouncementServiceProto.AnnouncementActionResponse call() {
                            return ConnectionManager.getInstance().getAnnouncementStub()
                                    .editAnnouncement(AnnouncementServiceProto.EditAnnouncementRequest
                                            .newBuilder()
                                            .setEditedAnnouncement(updated)
                                            .build());
                        }
                    };
                    task.setOnSucceeded(e -> {
                        if (task.getValue().getSuccess()) loadAnnouncements();
                    });
                    task.setOnFailed(e -> {
                        System.err.println("Failed to edit announcement");
                        task.getException().printStackTrace();
                    });
                    new Thread(task).start();
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to open announcement dialog");
            e.printStackTrace();
        }
    }

    private void handlePublish(EntitiesProto.Announcement announcement) {
        boolean isPublished = "PUBLISHED".equals(announcement.getStatus());

        if (isPublished) {
            if (!confirmAction("Unpublish Announcement", "Unpublish \"" + announcement.getTitle() + "\"?")) return;
        }

        Task<AnnouncementServiceProto.AnnouncementActionResponse> task = new Task<>() {
            @Override
            protected AnnouncementServiceProto.AnnouncementActionResponse call() {
                return ConnectionManager.getInstance().getAnnouncementStub()
                        .publishAnnouncement(AnnouncementServiceProto.PublishAnnouncementRequest
                                .newBuilder()
                                .setAnnouncementId(announcement.getAnnouncementId())
                                .setIsPublished(!isPublished)
                                .build());
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue().getSuccess()) loadAnnouncements();
        });

        task.setOnFailed(e -> {
            System.err.println("Failed to publish/unpublish announcement");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void handleDelete(EntitiesProto.Announcement announcement) {
        if (!confirmAction("Delete Announcement", "Are you sure you want to delete \"" + announcement.getTitle() + "\"?")) return;

        Task<AnnouncementServiceProto.AnnouncementActionResponse> task = new Task<>() {
            @Override
            protected AnnouncementServiceProto.AnnouncementActionResponse call() {
                return ConnectionManager.getInstance().getAnnouncementStub()
                        .deleteAnnouncement(AnnouncementServiceProto.DeleteAnnouncementRequest
                                .newBuilder()
                                .setAnnouncementId(announcement.getAnnouncementId())
                                .build());
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue().getSuccess()) loadAnnouncements();
        });

        task.setOnFailed(e -> {
            System.err.println("Failed to delete announcement");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
}
