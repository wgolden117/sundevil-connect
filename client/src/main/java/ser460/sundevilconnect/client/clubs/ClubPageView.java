package ser460.sundevilconnect.client.clubs;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.client.NavigationController;
import ser460.sundevilconnect.client.announcements.AnnouncementDetailsView;
import ser460.sundevilconnect.client.events.EventDetailsView;
import ser460.sundevilconnect.shared.proto.*;

import java.util.List;

public class ClubPageView {
    @FXML private Label clubNameLabel;
    @FXML private Button manageClubButton;
    @FXML private Label categoryLabel;
    @FXML private Label foundedDateLabel;
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
        checkLeaderAccess();
        loadEvents();
        loadAnnouncements();
        loadMembershipStatus();
    }

    private void loadClubDetails() {
        clubNameLabel.setText(club.getName());
        categoryLabel.setText(club.getCategory());
        foundedDateLabel.setText(club.getFoundedDate());
        descriptionLabel.setText(club.getDescription());

        if(CurrentUser.getInstance().getRole() == EntitiesProto.Role.ADMIN)
            joinButton.setVisible(false);
    }

    private void checkLeaderAccess() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                ClubBrowsingServiceProto.ClubMembersResponse response =
                        ConnectionManager.getInstance().getClubBrowsingStub()
                                .getClubMembers(ClubBrowsingServiceProto.GetClubMembersRequest
                                        .newBuilder().setClubId(club.getClubId())
                                        .build());
                return response.getMembersList().stream()
                        .anyMatch(m -> m.getRole().equals("LEADER") &&
                                m.getStudent().getUserId().equals(CurrentUser.getInstance().getUserId()));
            }
        };

        task.setOnSucceeded(event -> manageClubButton.setVisible(task.getValue()));

        task.setOnFailed(event -> {
            System.err.println("Failed to check leader access");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void createPopUp(Node content, String title) {
        DialogPane pane = new DialogPane();
        pane.setContent(content);
        pane.getButtonTypes().add(ButtonType.CLOSE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setDialogPane(pane);
        dialog.showAndWait();
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
                    setOnMouseClicked(click -> {
                        if (click.getClickCount() == 2 && e != null && !empty) {
                            showEventPopup(e);
                        }
                    });
                }
            });
        });
        task.setOnFailed(event -> {
            System.err.println("Failed to load events");
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private void showEventPopup(EntitiesProto.Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/events/event_details.fxml"));
            javafx.scene.Node content = loader.load();
            EventDetailsView controller = loader.getController();
            controller.setEvent(event);

            createPopUp(content, event.getTitle());
        } catch (Exception e) {
            System.err.println("Failed to open event popup");
            e.printStackTrace();
        }
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
                    setOnMouseClicked(click -> {
                        if (click.getClickCount() == 2 && a != null && !empty) {
                            showAnnouncementPopup(a);
                        }
                    });
                }
            });
        });
        task.setOnFailed(event -> {
            System.err.println("Failed to load announcements");
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private void showAnnouncementPopup(EntitiesProto.Announcement announcement) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/announcements/announcement_details.fxml"));
            javafx.scene.Node content = loader.load();
            AnnouncementDetailsView controller = loader.getController();
            controller.setAnnouncement(announcement);

            createPopUp(content, announcement.getTitle());
        } catch (Exception e) {
            System.err.println("Failed to open announcement popup");
            e.printStackTrace();
        }
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
