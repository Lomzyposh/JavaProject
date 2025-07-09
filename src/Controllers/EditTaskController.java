package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditTaskController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker butt;
    @FXML private Button saveButton;

    @FXML
    private void initialize() {
        titleField.setText("Mock Task");
        descriptionField.setText("Mock description for editing.");
        butt.setValue(java.time.LocalDate.now());
    }

    @FXML
    private void onSave() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String dueDate = (butt.getValue() != null) ? butt.getValue().toString() : "No Date";

        System.out.println("Saving Task:");
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Due Date: " + dueDate);

        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
