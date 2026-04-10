package ser460.sundevilconnect.client.events;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class EventFilterPanel {

    @FXML
    private ComboBox<String> categoryDropdown;
    @FXML
    private CheckBox paidCheckbox;
    @FXML
    private ComboBox<String> clubDropdown;
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
        // Populate dropdown (simple static list for now)
        categoryDropdown.getItems().addAll(
                "Technology",
                "Music",
                "Business",
                "Art",
                "Health",
                "Recreation"
        );
        categoryDropdown.setPromptText("Select category");
        clubDropdown.setPromptText("Select club");
    }

    @FXML
    private void handleApplyFilters() {
        String selectedCategory = categoryDropdown.getValue();
        boolean isPaid = paidCheckbox.isSelected();

        if (filterListener != null) {
            filterListener.onFiltersApplied(
                    selectedCategory,
                    isPaid,
                    fromDatePicker.getValue(),
                    toDatePicker.getValue(),
                    clubDropdown.getValue()
            );

            System.out.println("Filters selected:");
            System.out.println("Category: " + selectedCategory);
            System.out.println("Paid: " + isPaid);
        }
    }
}