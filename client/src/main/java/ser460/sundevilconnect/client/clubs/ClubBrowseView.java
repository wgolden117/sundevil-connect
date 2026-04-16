package ser460.sundevilconnect.client.clubs;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.client.ConnectionManager;
import ser460.sundevilconnect.client.CurrentUser;
import ser460.sundevilconnect.client.NavigationController;
import ser460.sundevilconnect.shared.proto.ClubApprovalServiceProto;
import ser460.sundevilconnect.shared.proto.ClubBrowsingServiceProto;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClubBrowseView {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryDropdown;
    @FXML private VBox root;
    @FXML private ListView<EntitiesProto.Club> clubListView;
    @FXML private AnchorPane detailsPane;
    @FXML private Button createClubButton;

    private List<EntitiesProto.Club> allClubs =  new ArrayList<>();

    @FXML
    private void initialize() {
        loadClubs();

        root.setUserData(this);

        if (CurrentUser.getInstance().getRole() == EntitiesProto.Role.ADMIN) {
            createClubButton.setVisible(false);
        }

        // set the event action for clicking on a list item
        clubListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldClub, newClub) -> {
                    if (newClub != null) {
                        ClubDetailsView.loadInto(detailsPane, newClub);
                    }
                }
        );

        // set the cell styling for the list
        // double-click opens club page
        clubListView.setCellFactory(lv -> {
            ListCell<EntitiesProto.Club> cell = new ListCell<>() {
                @Override
                protected void updateItem(EntitiesProto.Club club, boolean empty) {
                    super.updateItem(club, empty);
                    setText(empty || club == null ? null : club.getName());
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    NavigationController.getInstance().openClubPageTab(cell.getItem());
                }
            });
            return cell;
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
            allClubs = task.getValue();
            clubListView.setItems(FXCollections.observableArrayList(allClubs));
            populateCategoryDropdown();
        });

        task.setOnFailed(event -> {
            System.err.println("Failed to load clubs");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    public void refresh() { loadClubs(); }

    private void populateCategoryDropdown() {
        List<String> categories = allClubs.stream()
                .map(EntitiesProto.Club::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
        categories.addFirst("All Categories");
        categoryDropdown.setItems(FXCollections.observableList(categories));
        categoryDropdown.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase().trim();
        String category = categoryDropdown.getValue();

        List<EntitiesProto.Club> filtered = allClubs.stream()
                .filter(club -> keyword.isEmpty() ||
                        club.getName().toLowerCase().contains(keyword) ||
                        club.getDescription().toLowerCase().contains(keyword))
                .filter(club -> category == null ||
                        category.equals("All Categories") ||
                        club.getCategory().equals(category))
                .toList();

        clubListView.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleClear() {
        searchField.clear();
        categoryDropdown.getSelectionModel().selectFirst();
        clubListView.setItems(FXCollections.observableArrayList(allClubs));
    }

    @FXML
    private void handleCreateClub() {
        Dialog<EntitiesProto.Club> dialog = new Dialog<>();
        dialog.setTitle("Create Club");

        TextField nameField = new TextField();
        nameField.setPromptText("Club name");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(3);

        VBox content = new VBox(8,
                new Label("Name:"), nameField,
                new Label("Category:"), categoryField,
                new Label("Description:"), descriptionArea);
        content.setPadding(new javafx.geometry.Insets(8));

        DialogPane pane = new DialogPane();
        pane.setContent(content);
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setDialogPane(pane);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK && !nameField.getText().isBlank()) {
                return EntitiesProto.Club.newBuilder()
                        .setName(nameField.getText().trim())
                        .setCategory(categoryField.getText().trim())
                        .setDescription(descriptionArea.getText().trim())
                        .build();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(club -> {
            Task<ClubApprovalServiceProto.ClubApprovalActionResponse> task = new Task<>() {
                @Override
                protected ClubApprovalServiceProto.ClubApprovalActionResponse call() {
                    return ConnectionManager.getInstance().getClubApprovalStub()
                            .submitClubForApproval(ClubApprovalServiceProto.SubmitClubForApprovalRequest.newBuilder()
                                    .setNewClub(club)
                                    .setSubmitter(EntitiesProto.UserSummary.newBuilder()
                                            .setUserId(CurrentUser.getInstance().getUserId())
                                            .setDisplayName(CurrentUser.getInstance().getDisplayName())
                                            .build())
                                    .build());
                }
            };

            task.setOnSucceeded(e -> {
                if (task.getValue().getSuccess()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Club Submitted");
                    alert.setHeaderText(null);
                    alert.setContentText("Your club has been submitted for approval.");
                    alert.showAndWait();
                }
            });

            task.setOnFailed(e -> {
                System.err.println("Failed to submit club");
                task.getException().printStackTrace();
            });

            new Thread(task).start();
        });
    }
}
