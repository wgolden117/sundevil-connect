package ser460.sundevilconnect.client.clubs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class ClubDetailsView {

    @FXML private Label nameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label foundedDateLabel;
    @FXML private Label descriptionLabel;
    @FXML private Button viewClubPageButton;

    public void initWithClub(EntitiesProto.Club club) {
        nameLabel.setText(club.getName());
        categoryLabel.setText(club.getCategory());
        foundedDateLabel.setText(club.getFoundedDate());
        descriptionLabel.setText(club.getDescription());
    }

    public void handleViewClubPage(ActionEvent actionEvent) {
    }
}
