package ser460.sundevilconnect.client.clubs;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.util.List;

public class ClubBrowseView {
    @FXML private ListView<EntitiesProto.Club> clubListView;
    @FXML private AnchorPane detailsPane;

    @FXML
    private void initialize() {
        loadClubs();

        // set the event action for clicking on a list item
        clubListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldClub, newClub) -> {
                    if (newClub != null) {
                        loadDetailsPane(newClub);
                    }
                }
        );
        // set the cell styling for the list
        clubListView.setCellFactory(lv -> new ListCell<EntitiesProto.Club>() {
            @Override
            protected void updateItem(EntitiesProto.Club club, boolean empty) {
                super.updateItem(club, empty);
                setText(empty || club == null ? null : club.getName());
            }
        });
    }

    private void loadClubs() {
        Task<List<EntitiesProto.Club>> task = new Task<>() {
            @Override
            protected List<EntitiesProto.Club> call() {
                ClubBrowsingServiceProto.ClubListResponse response =
                        ConnectionManager.getInstance().getClubBrowsingStub()
                                .getAllClubs(ClubBrowsingServiceProto.GetAllClubsRequest
                                        .newBuilder().build());
                return response.getClubsList();
            }
        };

        task.setOnSucceeded(event -> {
            clubListView.setItems(FXCollections.observableArrayList(task.getValue()));
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load clubs");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadDetailsPane(EntitiesProto.Club club) {
        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/clubs/club_details.fxml")
                );
                Parent root = loader.load();
                ClubDetailsView controller = loader.getController();
                controller.initWithClub(club);
                return root;
            }
        };

        task.setOnSucceeded(event -> {
            Parent view = task.getValue();
            detailsPane.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load club details");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
}
