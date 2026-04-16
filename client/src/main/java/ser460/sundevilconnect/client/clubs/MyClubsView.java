package ser460.sundevilconnect.client.clubs;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.client.NavigationController;
import ser460.sundevilconnect.shared.proto.ClubMembershipServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.util.List;

public class MyClubsView {
    @FXML private VBox root;
    @FXML private ListView<EntitiesProto.Club> membershipsListView;
    @FXML private ListView<ClubMembershipServiceProto.MembershipRequest> requestsListView;
    @FXML private AnchorPane detailsPane;

    @FXML
    private void initialize() {
        root.setUserData(this);

        loadClubs();
        loadRequests();

        membershipsListView.setCellFactory(lv -> {
            ListCell<EntitiesProto.Club> cell = new ListCell<>() {
                @Override
                protected void updateItem(EntitiesProto.Club club, boolean empty) {
                    super.updateItem(club, empty);
                    setText(empty || club == null ? null : club.getName());
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    NavigationController.getInstance().openClubPageTab(cell.getItem());
                }
            });
            return cell;
        });

        requestsListView.setCellFactory(lv -> {
            ListCell<ClubMembershipServiceProto.MembershipRequest> cell = new ListCell<>() {
                @Override
                protected void updateItem(ClubMembershipServiceProto.MembershipRequest request, boolean empty) {
                    super.updateItem(request, empty);
                    if (empty || request == null) {
                        setGraphic(null);
                    } else {
                        Label nameLabel = new Label(request.getClub().getName());
                        Label statusLabel = new Label(request.getStatus() + " : " + request.getRequestDate());
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);
                        HBox cell = new HBox(nameLabel, spacer, statusLabel);
                        setGraphic(cell);
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    NavigationController.getInstance().openClubPageTab(cell.getItem().getClub());
                }
            });
            return cell;
        });

        membershipsListView.setOnMouseClicked(event -> {
            EntitiesProto.Club selected = membershipsListView.getSelectionModel().getSelectedItem();
            if (selected != null) ClubDetailsView.loadInto(detailsPane, selected);
        });

        requestsListView.setOnMouseClicked(event -> {
            EntitiesProto.Club selected = requestsListView.getSelectionModel().getSelectedItem().getClub();
            ClubDetailsView.loadInto(detailsPane, selected);
        });
    }

    public void refresh() {
        loadClubs();
        loadRequests();
    }

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
                        .map(EntitiesProto.ClubMembership::getClub)
                        .toList();
            }
        };

        task.setOnSucceeded(event -> membershipsListView.setItems(FXCollections.observableList(task.getValue())));

        task.setOnFailed(event -> {
            System.err.println("Failed to load my clubs");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadRequests() {
        Task<List<ClubMembershipServiceProto.MembershipRequest>> task = new Task<>() {
            @Override
            protected List<ClubMembershipServiceProto.MembershipRequest> call() {
                return ConnectionManager.getInstance().getClubMembershipStub()
                        .getRequestsForStudent(
                                ClubMembershipServiceProto.GetMembershipsForStudentRequest.newBuilder()
                                        .setUserId(CurrentUser.getInstance().getUserId())
                                        .build()
                        ).getRequestsList();
            }
        };

        task.setOnSucceeded(event -> requestsListView.setItems(FXCollections.observableArrayList(task.getValue())));

        task.setOnFailed(event -> {
            System.err.println("Failed to load membership requests");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
}
