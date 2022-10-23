package energyData.service.query.exception;

public class TooBigTimePeriodException extends RuntimeException {
    public TooBigTimePeriodException(String message) {
        super(message);
    }
}
