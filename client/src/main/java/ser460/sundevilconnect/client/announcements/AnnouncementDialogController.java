package ser460.sundevilconnect.client.announcements;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class AnnouncementDialogController {
    @FXML private TextField titleField;
    @FXML private TextArea bodyField;
    @FXML private CheckBox publishCheckBox;

    private EntitiesProto.Club club;
    private String existingAnnouncementId = null;

    @FXML
    private void initialize() {
        BooleanBinding allFilled = titleField.textProperty().isNotEmpty()
                .and(bodyField.textProperty().isNotEmpty());

        publishCheckBox.disableProperty().bind(allFilled.not());

        // uncheck if fields are cleared after being checked
        allFilled.addListener((obs, wasfilled, isFilled) -> {
            if (!isFilled) publishCheckBox.setSelected(false);
        });
    }

    public void setClub(EntitiesProto.Club club) {
        this.club = club;
    }

    public void setAnnouncement(EntitiesProto.Announcement announcement) {
        this.existingAnnouncementId = announcement.getAnnouncementId();
        titleField.setText(announcement.getTitle());
        bodyField.setText(announcement.getBody());
        publishCheckBox.setSelected("PUBLISHED".equals(announcement.getStatus()));
    }

    public EntitiesProto.Announcement getAnnouncement() {
        EntitiesProto.Announcement.Builder builder = EntitiesProto.Announcement.newBuilder()
                .setTitle(titleField.getText().trim())
                .setBody(bodyField.getText().trim())
                .setStatus(publishCheckBox.isSelected() ? "PUBLISHED" : "DRAFT")
                .setPostedTo(club)
                .setCreatedBy(EntitiesProto.UserSummary.newBuilder()
                        .setUserId(CurrentUser.getInstance().getUserId())
                        .setDisplayName(CurrentUser.getInstance().getDisplayName())
                        .build());

        if (existingAnnouncementId != null) {
            builder.setAnnouncementId(existingAnnouncementId);
        }

        return builder.build();
    }
}
