package energyData.util;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Handles datePicker converting, preventing exceptions that basic StringConverter throws with invalid input.s
 */
public class SecureLocalDateStringConverter extends StringConverter<LocalDate> {
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_PATTERN);

    private boolean hasParseError = false;

    public boolean hasParseError() {
        return hasParseError;
    }

    @Override
    public String toString(LocalDate localDate) {
        if (localDate == null) return "";

        return DATE_FORMATTER.format(localDate);
    }

    @Override
    public LocalDate fromString(String formattedString) {
        try {
            LocalDate date = LocalDate.from(DATE_FORMATTER.parse(formattedString));
            hasParseError = false;
            return date;
        } catch (DateTimeParseException parseExc) {
            hasParseError = true;
            return null;
        }
    }

}