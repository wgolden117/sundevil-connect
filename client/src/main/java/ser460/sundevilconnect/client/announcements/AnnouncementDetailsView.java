package ser460.sundevilconnect.client.announcements;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class AnnouncementDetailsView {
    @FXML private Label titleLabel;
    @FXML private Label clubLabel;
    @FXML private Label postedByLabel;
    @FXML private Label dateLabel;
    @FXML private TextArea bodyArea;

    public void setAnnouncement(EntitiesProto.Announcement announcement) {
        titleLabel.setText(announcement.getTitle());
        clubLabel.setText(announcement.getPostedTo().getName());
        postedByLabel.setText(announcement.getCreatedBy().getDisplayName());
        dateLabel.setText(announcement.getPostedDate().isEmpty() ? "Draft" : announcement.getPostedDate());
        bodyArea.setText(announcement.getBody());
    }
}
