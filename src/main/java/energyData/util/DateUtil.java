package energyData.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    /**
     * Returns difference/distance between two dates in given timeUnit.
     * @param date1
     * @param date2
     * @param timeUnit use TimeUnit enums
     * @return non-negative difference
     */
    public static long getDateDifference(Date date1, Date date2, TimeUnit timeUnit) {
        long difference = Math.abs(date2.getTime() - date1.getTime());
        return timeUnit.convert(difference, TimeUnit.MILLISECONDS);
    }

    /**
     * Returns difference/distance between two dates in days.
     * @param date1
     * @param date2
     * @return non-negative difference
     */
    public static long getDateDifferenceInDays(Date date1, Date date2) {
        return getDateDifference(date1, date2, TimeUnit.DAYS);
    }
}
