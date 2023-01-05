package energyData.controller;

import energyData.service.EnergyDataService;
import energyData.service.db.EnergyConsumptionEntryService;
import energyData.util.SecureLocalDateStringConverter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Component
public class EnergyDataController {
    private final Logger LOG = LoggerFactory.getLogger(EnergyDataController.class);
    @FXML
    private TextArea textAreaFeedback;

    @FXML
    private DatePicker datePickerStart;

    @FXML
    private DatePicker datePickerEnd;

    @FXML
    private Button fetchAndSaveButtonLatest;

    @FXML
    private Button fetchAndSaveButtonTimePeriod;

    private EnergyDataService energyDataService;

    private EnergyConsumptionEntryService energyConsumptionEntryService;

    public EnergyDataController(EnergyDataService energyDataService, EnergyConsumptionEntryService energyConsumptionEntryService) {
        this.energyDataService = energyDataService;
        this.energyConsumptionEntryService = energyConsumptionEntryService;
    }

    @FXML
    public void initialize() {
        datePickerStart.setConverter(new SecureLocalDateStringConverter());
        datePickerEnd.setConverter(new SecureLocalDateStringConverter());

        updateFetchAndSaveLatestButton();
    }

    public void fetchAndSaveEnergyDataWithPeriod() {
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

        fetchAndSaveEnergyData(startDate, endDate);
    }

    public void fetchAndSaveEnergyDataLatest() {
        Optional<LocalDate> startDateOptional = energyConsumptionEntryService.getDateOfLatestEnergyConsumptionEntry();

        if (startDateOptional.isEmpty()) {
        } else {
            LocalDate endDate = LocalDate.now(ZoneId.of("UTC"));

            fetchAndSaveEnergyData(startDateOptional.get(), endDate);
        }
    }

    private void fetchAndSaveEnergyData(LocalDate startDate, LocalDate endDate) {
        displayProcessStartInfoMessage();
        fetchAndSaveButtonLatest.setDisable(true);
        fetchAndSaveButtonTimePeriod.setDisable(true);

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
                LOG.error("exception while trying to fetch and save energy data: ", e);
            } finally {
                boolean finalSuccess = success;
                Platform.runLater(() -> {
                    onDataFetchAndSaveEnd(finalSuccess);
                });
            }
        }).start();
    }

    private void onDataFetchAndSaveEnd(boolean success) {
        if (success) {
            displayProcessFinishInfoMessage();
        } else {
            displayProcessErrorInfoMessage();
        }

        fetchAndSaveButtonLatest.setDisable(false);
        fetchAndSaveButtonTimePeriod.setDisable(false);

        updateFetchAndSaveLatestButton();
    }

    private void updateFetchAndSaveLatestButton() {
        Optional<LocalDate> startDateOptional = energyConsumptionEntryService.getDateOfLatestEnergyConsumptionEntry();

        if (startDateOptional.isEmpty()) {
            fetchAndSaveButtonLatest.setText("Save data starting from last fetch (no data found)");
            fetchAndSaveButtonLatest.setDisable(true);
        } else {
            fetchAndSaveButtonLatest.setText("Save data starting from last fetch (" + startDateOptional.get() + ")");
            fetchAndSaveButtonLatest.setDisable(false);
        }
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
