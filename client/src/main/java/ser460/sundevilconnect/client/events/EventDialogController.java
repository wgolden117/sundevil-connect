package ser460.sundevilconnect.client.events;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ser460.sundevilconnect.shared.proto.EntitiesProto;

import java.time.LocalDate;

public class EventDialogController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private TextField locationField;
    @FXML private TextField categoryField;
    @FXML private TextField capacityField;
    @FXML private CheckBox isPaidCheckBox;

    private EntitiesProto.Club club;
    private String existingEventId = null;

    public void setClub(EntitiesProto.Club club) {
        this.club = club;
    }

    @FXML
    private void initialize() {
        capacityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                capacityField.setText(newVal.replaceAll("\\D", ""));
            }
        });

        // grab the 'ok' button and disable it until all fields are filled
        javafx.application.Platform.runLater(() -> {
            javafx.scene.Node okButton = ((DialogPane) titleField.getScene().getRoot())
                    .lookupButton(ButtonType.OK);

            BooleanBinding allFilled = titleField.textProperty().isNotEmpty()
                    .and(descriptionField.textProperty().isNotEmpty())
                    .and(datePicker.valueProperty().isNotNull())
                    .and(locationField.textProperty().isNotEmpty())
                    .and(categoryField.textProperty().isNotEmpty())
                    .and(capacityField.textProperty().isNotEmpty());

            okButton.disableProperty().bind(allFilled.not());
        });
    }

    // called when editing an existing event
    public void setEvent(EntitiesProto.Event event) {
        this.existingEventId = event.getEventId();
        titleField.setText(event.getTitle());
        descriptionField.setText(event.getDescription());
        datePicker.setValue(LocalDate.parse(event.getEventDate()));
        locationField.setText(event.getLocation());
        categoryField.setText(event.getCategory());
        capacityField.setText(String.valueOf(event.getCapacity()));
        isPaidCheckBox.setSelected(event.getIsPaid());
    }

    // builds and returns the Event proto from form fields
    public EntitiesProto.Event getEvent() {
        EntitiesProto.Event.Builder builder = EntitiesProto.Event.newBuilder()
                .setTitle(titleField.getText().trim())
                .setDescription(descriptionField.getText().trim())
                .setEventDate(datePicker.getValue().toString())
                .setLocation(locationField.getText().trim())
                .setCategory(categoryField.getText().trim())
                .setCapacity(Integer.parseInt(capacityField.getText().trim()))
                .setIsPaid(isPaidCheckBox.isSelected())
                .setHostedBy(club);

        if (existingEventId != null) {
            builder.setEventId(existingEventId);
        }

        return builder.build();
    }
}
