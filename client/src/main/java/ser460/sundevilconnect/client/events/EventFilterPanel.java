package ser460.sundevilconnect.client.events;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class EventFilterPanel {

    @FXML private ComboBox<String> categoryDropdown;
    @FXML private CheckBox paidCheckbox;

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
    }

    @FXML
    private void handleApplyFilters() {
        String selectedCategory = categoryDropdown.getValue();
        boolean isPaid = paidCheckbox.isSelected();

        System.out.println("Filters selected:");
        System.out.println("Category: " + selectedCategory);
        System.out.println("Paid: " + isPaid);
    }
}