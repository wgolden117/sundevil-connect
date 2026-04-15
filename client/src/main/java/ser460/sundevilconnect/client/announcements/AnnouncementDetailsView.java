package ser460.sundevilconnect.client.announcements;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.ContentModerationServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class AnnouncementDetailsView {
    @FXML private Label titleLabel;
    @FXML private Label clubLabel;
    @FXML private Label postedByLabel;
    @FXML private Label dateLabel;
    @FXML private TextArea bodyArea;
    @FXML private Button flagButton;

    private EntitiesProto.Announcement announcement;

    public void setAnnouncement(EntitiesProto.Announcement announcement) {
        this.announcement = announcement;
        titleLabel.setText(announcement.getTitle());
        clubLabel.setText(announcement.getPostedTo().getName());
        postedByLabel.setText(announcement.getCreatedBy().getDisplayName());
        dateLabel.setText(announcement.getPostedDate().isEmpty() ? "Draft" : announcement.getPostedDate());
        bodyArea.setText(announcement.getBody());
        loadFlagStatus();
    }

    private void loadFlagStatus() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return ConnectionManager.getInstance().getContentModerationStub()
                        .getContentFlagStatus(ContentModerationServiceProto.GetContentFlagStatusRequest.newBuilder()
                                .setAnnouncementId(announcement.getAnnouncementId())
                                .build())
                        .getIsFlagged();
            }
        };
        task.setOnSucceeded(e -> flagButton.setVisible(!task.getValue()));
        task.setOnFailed(e -> {
            System.err.println("Failed to load flag status");
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    @FXML
    private void handleFlag() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Flag Content");
        dialog.setHeaderText(null);
        dialog.setContentText("Reason for flagging:");
        dialog.showAndWait().ifPresent(reason -> {
            if (reason.isBlank()) return;
            Task<ContentModerationServiceProto.ContentActionResponse> task = new Task<>() {
                @Override
                protected ContentModerationServiceProto.ContentActionResponse call() {
                    return ConnectionManager.getInstance().getContentModerationStub()
                            .flagContent(ContentModerationServiceProto.FlagContentRequest.newBuilder()
                                    .setAnnouncementId(announcement.getAnnouncementId())
                                    .setReason(reason)
                                    .setUserId(CurrentUser.getInstance().getUserId())
                                    .build());
                }
            };
            task.setOnSucceeded(e -> {
                if (task.getValue().getSuccess()) flagButton.setVisible(false);
            });
            task.setOnFailed(e -> {
                System.err.println("Failed to flag content");
                task.getException().printStackTrace();
            });
            new Thread(task).start();
        });
    }
}
