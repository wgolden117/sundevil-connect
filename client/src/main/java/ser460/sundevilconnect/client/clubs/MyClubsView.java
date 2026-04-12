package ser460.sundevilconnect.client.clubs;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.ClubMembershipServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.util.List;

public class MyClubsView {
    @FXML private VBox root;
    @FXML private ListView<EntitiesProto.Club> clubListView;
    @FXML private AnchorPane detailsPane;

    @FXML
    private void initialize() {
        root.setUserData(this);

        loadClubs();

        clubListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(EntitiesProto.Club club, boolean empty) {
                super.updateItem(club, empty);
                setText(empty || club == null ? null : club.getName());
            }
        });

        clubListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldClub, newClub) -> {
                    if (newClub != null) ClubDetailsView.loadInto(detailsPane, newClub);
                });
    }

    public void refresh() { loadClubs(); }

    private void loadClubs() {
        Task<List<EntitiesProto.Club>> task = new Task<>() {
            @Override
            protected List<EntitiesProto.Club> call() {
                return ConnectionManager.getInstance().getClubMembershipStub()
                        .getMembershipsForStudent(
                                ClubMembershipServiceProto.GetMembershipsForStudentRequest.newBuilder()
                                        .setUserId(CurrentUser.getInstance().getUserId())
                                        .build()
                        ).getMembershipsList().stream()
                        .map(m -> m.getClub())
                        .toList();
            }
        };

        task.setOnSucceeded(event -> {
            clubListView.setItems(FXCollections.observableList(task.getValue()));
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load my clubs");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
}
