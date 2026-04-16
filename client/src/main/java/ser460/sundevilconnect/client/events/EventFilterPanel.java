package ser460.sundevilconnect.client.events;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import ser460.sundevilconnect.client.ConnectionManager;

public class EventFilterPanel {

    @FXML
    private ComboBox<String> categoryDropdown;
    @FXML
    private CheckBox paidCheckbox;
    @FXML
    private ComboBox<ser460.sundevilconnect.shared.proto.EntitiesProto.Club> clubDropdown;
    @FXML
    private javafx.scene.control.DatePicker fromDatePicker;
    @FXML
    private javafx.scene.control.DatePicker toDatePicker;
    private FilterListener filterListener;


    public void setFilterListener(FilterListener listener) {
        this.filterListener = listener;
    }

    @FXML
    private void initialize() {
        // Category dropdown
        categoryDropdown.getItems().addAll(
                "Technology",
                "Music",
                "Business",
                "Art",
                "Health",
                "Recreation"
        );
        categoryDropdown.setPromptText("Select category");

        // Club dropdown
        clubDropdown.setPromptText("Select club");

        // Display only club name in dropdown
        clubDropdown.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(ser460.sundevilconnect.shared.proto.EntitiesProto.Club club, boolean empty) {
                super.updateItem(club, empty);
                setText(empty || club == null ? null : club.getName());
            }
        });

        clubDropdown.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(ser460.sundevilconnect.shared.proto.EntitiesProto.Club club, boolean empty) {
                super.updateItem(club, empty);
                setText(empty || club == null ? null : club.getName());
            }
        });

        loadClubs();
    }

    private void loadClubs() {
        try {
            var response = ConnectionManager.getInstance()
                    .getClubBrowsingStub()
                    .getAllClubs(
                            ser460.sundevilconnect.shared.proto.ClubBrowsingServiceProto
                                    .GetAllClubsRequest.newBuilder().build()
                    );

            for (var club : response.getClubsList()) {
                clubDropdown.getItems().add(club);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApplyFilters() {
        String selectedCategory = categoryDropdown.getValue();
        boolean isPaid = paidCheckbox.isSelected();

        String clubId = null;
        var selectedClub = clubDropdown.getValue();

        if (selectedClub != null) {
            clubId = selectedClub.getClubId();
        }

        if (filterListener != null) {
            filterListener.onFiltersApplied(
                    selectedCategory,
                    isPaid,
                    fromDatePicker.getValue(),
                    toDatePicker.getValue(),
                    clubId
            );

            System.out.println("Filters selected:");
            System.out.println("Category: " + selectedCategory);
            System.out.println("Paid: " + isPaid);
            System.out.println("ClubId: " + clubId);
        }
    }

    @FXML
    private void handleClearFilters() {
        // Reset UI inputs
        categoryDropdown.setValue(null);
        paidCheckbox.setSelected(false);
        clubDropdown.setValue(null);
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);

        // Notify listener to reset data
        if (filterListener != null) {
            filterListener.onFiltersCleared();
        }
    }
}