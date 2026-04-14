package ser460.sundevilconnect.client.clubs;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceProto;
import ser460.sundevilconnect.shared.proto.ClubMembershipServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.util.List;

public class DashboardMembersController extends DashboardSectionController {
    @FXML private VBox root;
    @FXML private ListView<ClubMembershipServiceProto.MembershipRequest> requestsListView;
    @FXML private ListView<EntitiesProto.ClubMembership> membersListView;

    private EntitiesProto.Club club;

    public void setClub(EntitiesProto.Club club) {
        this.club = club;
        refresh();
    }

    private void refresh() {
        loadMembers();
        loadRequests();
    }

    private void loadMembers() {
        Task<List<EntitiesProto.ClubMembership>> task = new Task<>() {
            @Override
            protected List<EntitiesProto.ClubMembership> call() {
                return ConnectionManager.getInstance().getClubBrowsingStub()
                        .getClubMembers(ClubBrowsingServiceProto.GetClubMembersRequest
                                .newBuilder().setClubId(club.getClubId())
                                .build())
                        .getMembersList();
            }
        };

        task.setOnSucceeded(event -> {
            membersListView.setItems(FXCollections.observableArrayList(task.getValue()));
            membersListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(EntitiesProto.ClubMembership membership, boolean empty) {
                    super.updateItem(membership, empty);
                    if (empty || membership == null) {
                        setGraphic(null);
                        return;
                    }

                    Label nameLabel = new Label(membership.getStudent().getDisplayName());
                    Label roleLabel = new Label(membership.getRole());

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button removeButton = new Button("Remove");
                    removeButton.setOnAction(e -> handleRemove(membership));

                    Button promoteButton = new Button("Promote");
                    promoteButton.setOnAction(e -> handlePromote(membership));

                    // don't show promote if already a leader
                    promoteButton.setVisible(!membership.getRole().equals("LEADER"));

                    HBox cell = new HBox(8, nameLabel, roleLabel, spacer, promoteButton, removeButton);
                    cell.setMaxWidth(Double.MAX_VALUE);
                    setGraphic(cell);
                }
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load members");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadRequests() {
        Task<List<ClubMembershipServiceProto.MembershipRequest>> task = new Task<>() {
            @Override
            protected List<ClubMembershipServiceProto.MembershipRequest> call() {
                return ConnectionManager.getInstance().getClubMembershipStub()
                        .getPendingRequests(ClubMembershipServiceProto.GetPendingRequestsRequest
                                .newBuilder().setClubId(club.getClubId())
                                .build())
                        .getRequestsList();
            }
        };

        task.setOnSucceeded(event -> {
            requestsListView.setItems(FXCollections.observableArrayList(task.getValue()));
            requestsListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(ClubMembershipServiceProto.MembershipRequest request, boolean empty) {
                    super.updateItem(request, empty);
                    if (empty || request == null) {
                        setGraphic(null);
                        return;
                    }

                    Label nameLabel = new Label(request.getStudent().getDisplayName());
                    Label dateLabel = new Label(request.getRequestDate());

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button approveButton = new Button("Approve");
                    approveButton.setOnAction(e -> handleApprove(request));

                    Button rejectButton = new Button("Reject");
                    rejectButton.setOnAction(e -> handleReject(request));

                    HBox cell = new HBox(8, nameLabel, dateLabel, spacer, approveButton, rejectButton);
                    cell.setMaxWidth(Double.MAX_VALUE);
                    setGraphic(cell);
                }
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load requests");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void handleRemove(EntitiesProto.ClubMembership membership) {
        if (!confirmAction(
                "Remove Member",
                "Are you sure you want to remove " + membership.getStudent().getDisplayName() + "?"))
            return;

        Task<ClubMembershipServiceProto.MembershipActionResponse> task = new Task<>() {
            @Override
            protected ClubMembershipServiceProto.MembershipActionResponse call() {
                return ConnectionManager.getInstance().getClubMembershipStub()
                        .removeMember(ClubMembershipServiceProto.RemoveMemberRequest
                                .newBuilder().setMemberId(membership.getMembershipId())
                                .build());
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue().getSuccess()) refresh();
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to remove member");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void handlePromote(EntitiesProto.ClubMembership membership) {
        if (!confirmAction(
                "Promote Member",
                "Promote " + membership.getStudent().getDisplayName() + " to leader?"))
            return;

        Task<ClubMembershipServiceProto.MembershipActionResponse> task = new Task<>() {
            @Override
            protected ClubMembershipServiceProto.MembershipActionResponse call() {
                return ConnectionManager.getInstance().getClubMembershipStub()
                        .promoteMember(ClubMembershipServiceProto.PromoteMemberRequest
                                .newBuilder()
                                .setMembershipId(membership.getMembershipId())
                                .build());
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue().getSuccess()) refresh();
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to promote member");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void handleApprove(ClubMembershipServiceProto.MembershipRequest request) {
        Task<ClubMembershipServiceProto.MembershipActionResponse> task = new Task<>() {
            @Override
            protected ClubMembershipServiceProto.MembershipActionResponse call() {
                return ConnectionManager.getInstance().getClubMembershipStub()
                        .approveMembership(ClubMembershipServiceProto.ApproveMembershipRequest
                                .newBuilder()
                                .setRequestId(request.getRequestId())
                                .setApproverId(CurrentUser.getInstance().getUserId())
                                .build());
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue().getSuccess()) refresh();
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to approve request");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void handleReject(ClubMembershipServiceProto.MembershipRequest request) {
        if (!confirmAction(
                "Reject Request",
                "Reject " + request.getStudent().getDisplayName() + "'s membership request?"))
            return;

        Task<ClubMembershipServiceProto.MembershipActionResponse> task = new Task<>() {
            @Override
            protected ClubMembershipServiceProto.MembershipActionResponse call() {
                return ConnectionManager.getInstance().getClubMembershipStub()
                        .rejectMembership(ClubMembershipServiceProto.RejectMembershipRequest
                                .newBuilder()
                                .setRequestId(request.getRequestId())
                                .setApproverId(CurrentUser.getInstance().getUserId())
                                .build());
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue().getSuccess()) refresh();
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to reject request");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
}
