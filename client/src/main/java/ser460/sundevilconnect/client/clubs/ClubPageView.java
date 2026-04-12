package ser460.sundevilconnect.client.clubs;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.client.NavigationController;
import ser460.sundevilconnect.shared.proto.*;

import java.util.List;

public class ClubPageView {
    @FXML private Label clubNameLabel;
    @FXML private Button manageClubButton;
    @FXML private Label categoryLabel;
    @FXML private Label foundedDateLabel;
    @FXML private Label leaderLabel;
    @FXML private Label descriptionLabel;
    @FXML private Button joinButton;
    @FXML private ListView<EntitiesProto.Announcement> announcementsListView;
    @FXML private ListView<EntitiesProto.Event> eventsListView;

    private EntitiesProto.Club club;

    public void initWithClub(EntitiesProto.Club club) {
        this.club = club;
        // populate static fields immediately
        loadClubDetails();
        // then fire off the 4 async RPC calls to populate data
        loadLeader();
        loadEvents();
        loadAnnouncements();
        loadMembershipStatus();
    }

    private void loadClubDetails() {
        clubNameLabel.setText(club.getName());
        categoryLabel.setText(club.getCategory());
        foundedDateLabel.setText(club.getFoundedDate());
        descriptionLabel.setText(club.getDescription());
    }

    private void loadLeader() {
        Task<EntitiesProto.ClubMembership> task = new Task<>() {
            @Override
            protected EntitiesProto.ClubMembership call() {
                ClubBrowsingServiceProto.ClubMembersResponse response =
                        ConnectionManager.getInstance().getClubBrowsingStub()
                                .getClubMembers(ClubBrowsingServiceProto.GetClubMembersRequest
                                        .newBuilder().setClubId(club.getClubId())
                                        .build());
                return response.getMembersList().stream()
                        .filter(m -> m.getRole().equals("LEADER"))
                        .findFirst()
                        .orElse(null);
            }
        };
        task.setOnSucceeded(event -> {
            EntitiesProto.ClubMembership leader = task.getValue();
            if (leader != null) {
                leaderLabel.setText(leader.getStudent().getDisplayName());
                manageClubButton.setVisible(leader.getStudent().getUserId().equals(CurrentUser.getInstance().getUserId()));
            } else {
                leaderLabel.setText("Unknown");
            }
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load leader");
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private void loadEvents() {
        Task<List<EntitiesProto.Event>> task = new Task<>() {
            @Override
            protected List<EntitiesProto.Event> call() {
                EventBrowsingServiceProto.GetFilteredEventsRequest request =
                        EventBrowsingServiceProto.GetFilteredEventsRequest.newBuilder()
                                .addFilters(EventBrowsingServiceProto.EventFilter.newBuilder()
                                        .setType(EventBrowsingServiceProto.FilterType.CLUB)
                                        .setClub(EventBrowsingServiceProto.ClubFilter.newBuilder()
                                                .setClubId(club.getClubId())
                                                .build())
                                        .build())
                                .build();

                return ConnectionManager.getInstance().getEventBrowsingStub()
                        .getFilteredEvents(request)
                        .getEventsList();
            }
        };
        task.setOnSucceeded(event -> {
            eventsListView.setItems(FXCollections.observableArrayList(task.getValue()));
            eventsListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(EntitiesProto.Event e, boolean empty) {
                    super.updateItem(e, empty);
                    setText(empty || e == null ? null : e.getTitle());
                }
            });
        });
        task.setOnFailed(event -> {
            System.err.println("Failed to load events");
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private void loadAnnouncements() {
        Task<List<EntitiesProto.Announcement>> task = new Task<>() {
            @Override
            protected List<EntitiesProto.Announcement> call() {
                AnnouncementServiceProto.GetAnnouncementsForClubRequest request =
                        AnnouncementServiceProto.GetAnnouncementsForClubRequest.newBuilder()
                                .setClubId(club.getClubId())
                                .setRequestingUserId(CurrentUser.getInstance().getUserId())
                                .build();

                return ConnectionManager.getInstance().getAnnouncementStub()
                        .getAnnouncementsForClub(request)
                        .getAnnouncementsList();
            }
        };
        task.setOnSucceeded(event -> {
            announcementsListView.setItems(FXCollections.observableArrayList(task.getValue()));
            announcementsListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(EntitiesProto.Announcement a, boolean empty) {
                    super.updateItem(a, empty);
                    setText(empty || a == null ? null : a.getTitle() + " - " + a.getPostedDate());
                }
            });
        });
        task.setOnFailed(event -> {
            System.err.println("Failed to load announcements");
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private void loadMembershipStatus() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return ConnectionManager.getInstance().getClubMembershipStub()
                        .getClubMembershipStatus(
                                ClubMembershipServiceProto.GetClubMembershipStatusRequest.newBuilder()
                                        .setUserId(CurrentUser.getInstance().getUserId())
                                        .setClubId(club.getClubId())
                                        .build()
                        ).getStatus();
            }
        };
        task.setOnSucceeded(event -> {
            String status = task.getValue();
            switch (status) {
                case "ACTIVE" -> {
                    joinButton.setText("Member");
                    joinButton.setDisable(true);
                }
                case "PENDING" -> {
                    joinButton.setText("Pending");
                    joinButton.setDisable(true);
                }
                default -> {
                    joinButton.setText("Join");
                    joinButton.setDisable(false);
                }
            }
        });
        task.setOnFailed(event -> {
            System.err.println("Failed to load membership status");
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    @FXML
    private void handleJoinClub() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                ClubMembershipServiceProto.RequestMembershipResponse response =
                        ConnectionManager.getInstance().getClubMembershipStub()
                                .requestMembership(
                                        ClubMembershipServiceProto.RequestMembershipRequest.newBuilder()
                                                .setStudentId(CurrentUser.getInstance().getUserId())
                                                .setClubId(club.getClubId())
                                                .build()
                                );
                return response.getSuccess();
            }
        };
        task.setOnSucceeded(event -> {
            if (task.getValue()) {
                joinButton.setText("Pending");
                joinButton.setDisable(true);
            }
        });
        task.setOnFailed(event -> {
            System.err.println("Failed to request membership");
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    @FXML
    private void onManageClubClicked() {
        NavigationController.getInstance().openClubDashboardTab(club);
    }
}
