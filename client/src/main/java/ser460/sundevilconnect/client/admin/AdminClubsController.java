package ser460.sundevilconnect.client.admin;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.ClubApprovalServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.util.List;

public class AdminClubsController extends AdminControllerBase {
    @FXML private ListView<EntitiesProto.Club> pendingClubsListView;

    @FXML
    public void initialize() {
        loadPendingClubs();
    }

    private void loadPendingClubs() {
        Task<List<EntitiesProto.Club>> task = new Task<>() {
            @Override
            protected List<EntitiesProto.Club> call() {
                return ConnectionManager.getInstance().getClubApprovalStub()
                        .getPendingClubs(ClubApprovalServiceProto.GetPendingClubsRequest.newBuilder().build())
                        .getPendingClubsList();
            }
        };

        task.setOnSucceeded(event -> {
            pendingClubsListView.setItems(FXCollections.observableArrayList(task.getValue()));
            pendingClubsListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(EntitiesProto.Club club, boolean empty) {
                    super.updateItem(club, empty);
                    if (empty || club == null) {
                        setGraphic(null);
                        return;
                    }

                    Label nameLabel = new Label(club.getName());
                    nameLabel.setStyle("-fx-font-weight: bold;");
                    Label categoryLabel = new Label(club.getCategory());

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    HBox topRow = new HBox(8, nameLabel, categoryLabel, spacer);

                    Label descriptionLabel = new Label(club.getDescription());
                    descriptionLabel.setWrapText(true);

                    Button approveButton = new Button("Approve");
                    approveButton.setOnAction(e -> handleApprove(club));

                    Button rejectButton = new Button("Reject");
                    rejectButton.setOnAction(e -> handleReject(club));

                    Region actionSpacer = new Region();
                    HBox.setHgrow(actionSpacer, Priority.ALWAYS);

                    HBox actionRow = new HBox(8, actionSpacer, approveButton, rejectButton);

                    VBox cell = new VBox(4, topRow, descriptionLabel, actionRow);
                    cell.setMaxWidth(Double.MAX_VALUE);
                    setGraphic(cell);
                }
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load pending clubs");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void handleApprove(EntitiesProto.Club club) {
        if (!confirmAction("Approve Club", "Approve \"" + club.getName() + "\"?")) return;

        Task<ClubApprovalServiceProto.ClubApprovalActionResponse> task = new Task<>() {
            @Override
            protected ClubApprovalServiceProto.ClubApprovalActionResponse call() {
                return ConnectionManager.getInstance().getClubApprovalStub()
                        .approveClub(ClubApprovalServiceProto.EvaluateClubRequest.newBuilder()
                                .setClubId(club.getClubId())
                                .setAdminId(CurrentUser.getInstance().getUserId())
                                .setIsApproved(true)
                                .build());
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue().getSuccess()) loadPendingClubs();
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to approve club");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void handleReject(EntitiesProto.Club club) {
        if (!confirmAction("Reject Club", "Reject \"" + club.getName() + "\"?")) return;

        Task<ClubApprovalServiceProto.ClubApprovalActionResponse> task = new Task<>() {
            @Override
            protected ClubApprovalServiceProto.ClubApprovalActionResponse call() {
                return ConnectionManager.getInstance().getClubApprovalStub()
                        .rejectClub(ClubApprovalServiceProto.EvaluateClubRequest.newBuilder()
                                .setClubId(club.getClubId())
                                .setAdminId(CurrentUser.getInstance().getUserId())
                                .setIsApproved(false)
                                .build());
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue().getSuccess()) loadPendingClubs();
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to reject club");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
}
