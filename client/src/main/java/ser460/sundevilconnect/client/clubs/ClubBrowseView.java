package ser460.sundevilconnect.client.clubs;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ser460.sundevilconnect.client.ConnectionManager;
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

    private List<EntitiesProto.Club> allClubs =  new ArrayList<>();

    @FXML
    private void initialize() {
        loadClubs();

        root.setUserData(this);

        // set the event action for clicking on a list item
        clubListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldClub, newClub) -> {
                    if (newClub != null) {
                        ClubDetailsView.loadInto(detailsPane, newClub);
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
}
