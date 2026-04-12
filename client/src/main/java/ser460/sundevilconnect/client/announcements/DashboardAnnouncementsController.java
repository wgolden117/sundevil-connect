package ser460.sundevilconnect.client.announcements;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class DashboardAnnouncementsController {
    @FXML private VBox root;
    @FXML private ListView<EntitiesProto.Announcement> announcementsListView;

    @FXML
    private void onCreateAnnouncementClicked(ActionEvent actionEvent) {
    }
}
