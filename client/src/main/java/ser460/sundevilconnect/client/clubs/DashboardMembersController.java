package ser460.sundevilconnect.client.clubs;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.shared.proto.ClubMembershipServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class DashboardMembersController {
    @FXML private VBox root;
    @FXML private ListView<ClubMembershipServiceProto.MembershipRequest> requestsListView;
    @FXML private ListView<EntitiesProto.ClubMembership> membersListView;
}
