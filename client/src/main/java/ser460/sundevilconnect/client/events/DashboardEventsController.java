package ser460.sundevilconnect.client.events;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class DashboardEventsController {
    @FXML private VBox root;
    @FXML private ListView<EntitiesProto.Event> eventsListView;

    @FXML
    private void onCreateEventClicked(ActionEvent actionEvent) {
    }
}
