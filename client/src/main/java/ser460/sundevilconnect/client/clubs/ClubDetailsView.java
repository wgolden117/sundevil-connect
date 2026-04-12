package ser460.sundevilconnect.client.clubs;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import ser460.sundevilconnect.client.NavigationController;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

public class ClubDetailsView {

    @FXML private Label nameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label foundedDateLabel;
    @FXML private Label descriptionLabel;
    @FXML private Button viewClubPageButton;

    private EntitiesProto.Club club;

    public void initWithClub(EntitiesProto.Club club) {
        this.club = club;
        setClubDetails();
    }

    private void setClubDetails() {
        nameLabel.setText(club.getName());
        categoryLabel.setText(club.getCategory());
        foundedDateLabel.setText(club.getFoundedDate());
        descriptionLabel.setText(club.getDescription());
    }

    @FXML
    private void handleViewClubPage() {
        NavigationController.getInstance().openClubPageTab(club);
    }

    // static helper method for building the club details pane
    public static void loadInto(AnchorPane pane, EntitiesProto.Club club) {
        Task<Parent> task = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(
                        ClubDetailsView.class.getResource("/fxml/clubs/club_details.fxml")
                );
                Parent root = loader.load();
                ClubDetailsView controller = loader.getController();
                controller.initWithClub(club);
                return root;
            }
        };

        task.setOnSucceeded(event -> {
            Parent view = task.getValue();
            pane.getChildren().setAll(view);
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
