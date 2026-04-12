package ser460.sundevilconnect.client.clubs;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.shared.proto.AnnouncementServiceProto;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;
import ser460.sundevilconnect.shared.proto.EventBrowsingServiceProto;

import java.util.List;

public class ClubPageView {
    @FXML private Label clubNameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label foundedDateLabel;
    @FXML private Label leaderLabel;
    @FXML private Label descriptionLabel;
    @FXML private ListView<EntitiesProto.Announcement> announcementsListView;
    @FXML private ListView<EntitiesProto.Event> eventsListView;

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

    private void loadLeader() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                ClubBrowsingServiceProto.ClubMembersResponse response =
                        ConnectionManager.getInstance().getClubBrowsingStub()
                                .getClubMembers(ClubBrowsingServiceProto.GetClubMembersRequest
                                        .newBuilder().setClubId(club.getClubId())
                                        .build());
                return response.getMembersList().stream()
                        .filter(m -> m.getRole().equals("LEADER"))
                        .map(m -> m.getStudent().getDisplayName())
                        .findFirst()
                        .orElse("Unknown");
            }
        };
        task.setOnSucceeded(event -> leaderLabel.setText(task.getValue()));

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
                    setText(empty || a == null ? null : a.getTitle() + " — " + a.getPostedDate());
                }
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load announcements");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
}
