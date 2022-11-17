package energyData.controller;

import energyData.service.EnergyDataService;
import energyData.util.SecureLocalDateStringConverter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EnergyDataController {
    @FXML
    private TextArea textAreaFeedback;

    @FXML
    private DatePicker datePickerStart;

    @FXML
    private DatePicker datePickerEnd;

    @FXML
    private Button fetchAndSaveButton;

    private EnergyDataService energyDataService;

    public EnergyDataController(EnergyDataService energyDataService) {
        this.energyDataService = energyDataService;
    }

    @FXML
    public void initialize() {
        datePickerStart.setConverter(new SecureLocalDateStringConverter());
        datePickerEnd.setConverter(new SecureLocalDateStringConverter());
    }

    public void fetchAndSaveEnergyData() {
       LocalDate startDate = datePickerStart.getValue();
       LocalDate endDate = datePickerEnd.getValue();

       if (startDate == null || endDate == null) {
           displayEmptyDateError();
           return;
       }

       if (startDate.isAfter(endDate)) {
           displayInvalidTimePeriodError();
           return;
       }

       if (endDate.isAfter(LocalDate.now())) {
           displayFutureTimePeriodError();
           return;
       }

        displayProcessStartInfoMessage();
        fetchAndSaveButton.setDisable(true);

        startDataFetchAndSaveThread(startDate, endDate);
    }

    private void startDataFetchAndSaveThread(LocalDate startDate, LocalDate endDate) {
        // process has to be started in new thread to not freeze application for few minutes
        new Thread(() -> {
            boolean success = false;
            try {
                energyDataService.fetchAndSaveData(startDate, endDate);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();

                success = false;
            } finally {
                boolean finalSuccess = success;
                Platform.runLater(() -> {
                    if (finalSuccess) {
                        displayProcessFinishInfoMessage();
                    } else {
                        displayProcessErrorInfoMessage();
                    }
                    fetchAndSaveButton.setDisable(false);
                });
            }
        }).start();
    }
    private void displayEmptyDateError() {
        displayErrorMessage("Please enter valid dates for both start and end!");
    }

    private void displayInvalidTimePeriodError() {
        displayErrorMessage("Invalid time period! Start date should be before or equal to end date");
    }

    private void displayProcessErrorInfoMessage() {
        displayErrorMessage("Exception while trying to execute process, please check stack trace error message");
    }

    private void displayFutureTimePeriodError() {
        displayErrorMessage("Invalid time period! End date is in the future!");
    }


    private void displayProcessStartInfoMessage() {
        displaySuccessMessage("Process will be executed now. Please wait, it will take some time, especially for big time periods...");
    }

    private void displayProcessFinishInfoMessage() {
        displaySuccessMessage("Process is finished successfully!");
    }

    private void displayErrorMessage(String message) {
        textAreaFeedback.setText(message);
        textAreaFeedback.setStyle("-fx-background-color: #d98989");
    }

    private void displaySuccessMessage(String message) {
        textAreaFeedback.setText(message);
        textAreaFeedback.setStyle("-fx-background-color: #a0d989");
    }
}
