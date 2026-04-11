package ser460.sundevilconnect.client.clubs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class ClubPageView {
    @FXML private Label clubNameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label foundedDateLabel;
    @FXML private Label leaderLabel;
    @FXML private Label descriptionLabel;
    @FXML private ListView announcementsListView;
    @FXML private ListView eventsListView;

    private EntitiesProto.Club club;

    public void initWithClub(EntitiesProto.Club club) {
        this.club = club;
        // populate static fields immediately
        loadClubDetails();
        // then fire off the 3 async RPC calls
        loadLeader();
        loadEvents();
        loadAnnouncements();
    }

    private void loadClubDetails() {
        clubNameLabel.setText(club.getName());
        categoryLabel.setText(club.getCategory());
        foundedDateLabel.setText(club.getFoundedDate());
        descriptionLabel.setText(club.getDescription());
    }

    private void loadLeader() {}
    private void loadEvents() {}
    private void loadAnnouncements() {}
}
