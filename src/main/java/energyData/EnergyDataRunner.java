package energyData;

import energyData.service.EnergyDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

@Component
public class EnergyDataRunner implements CommandLineRunner {
    private final EnergyDataService energyDataService;

    public EnergyDataRunner(EnergyDataService energyDataService) {

        this.energyDataService = energyDataService;
    }

    @Override
    public void run(String... args) {

        System.out.println("This application  will fetch, parse and save energy consumption data fetched from agora-energiewende.de.\n" +
                "Please be sure to specify correct database configuration in application.properties.");


        startConsoleLoop();

        System.out.println("\n\nGoodbye!");
    }

    private void startConsoleLoop() {
        Scanner scanner = new Scanner(System.in);
        boolean applicationLoopActive = true;

        while (applicationLoopActive) {
            int actionNumber = getActionNumberFromInput(scanner);

            applicationLoopActive = performSelectedAction(actionNumber, scanner);
        }

        scanner.close();
    }

    private int getActionNumberFromInput(Scanner scanner) {
        int actionNumber = 0;
        boolean hasNoValidInput = true;

        while (hasNoValidInput) {
            System.out.println("Please enter number of action you want to perform: ");
            System.out.println("1. Fetch, parse and save energy data");
            System.out.println("2. Close application");

            try {
                actionNumber = scanner.nextInt();

                if (actionNumber == 1 || actionNumber == 2) {
                    hasNoValidInput = false;
                } else {
                    System.out.println("Invalid input. Please enter valid option number!");
                }
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("Invalid input. Only number is allowed!");
                scanner.next();
            }
        }

        return actionNumber;
    }

    private boolean performSelectedAction(int actionNumber, Scanner scanner) {
        if (actionNumber == 1) {
            performFetchAndSaveAction(scanner);

            return true;
        }

        if (actionNumber == 2) return false;

        throw new IllegalArgumentException("actionNumber has unknown number " + actionNumber);
    }

    private void performFetchAndSaveAction(Scanner scanner) {
        System.out.println("You selected action for fetching and saving energy data to database.");
        System.out.println("For this action you will need to specify 2 dates: start date and end date for time period to fetch.\n");
        System.out.println("Please enter dates in format dd.MM.yyyy (for example: 30.11.2020)\n");

        System.out.println("Please enter start date:");
        LocalDate startDate = getDateFromInput(scanner);

        System.out.println("Please enter end date:");
        LocalDate endDate = getDateFromInput(scanner);

        if (startDate.isAfter(endDate)) {
            System.out.println("ERROR! Start date should before or equals to end date! Please try again:\n");
            return;
        } else {
            System.out.println("\nStarting energy data fetching and saving process for given time period. This will take few minutes...");

            energyDataService.fetchAndSaveData(startDate, endDate);

            System.out.println("\nProcess is completed!\n");
        }
    }

    private LocalDate getDateFromInput(Scanner scanner) {
        DateTimeFormatter inputDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        while (true) {
            String dateString = scanner.next();

            try {
                return LocalDate.parse(dateString, inputDateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please only use dd.MM.yyyy (for example: 30.11.2020)");
            }
        }
    }
}
