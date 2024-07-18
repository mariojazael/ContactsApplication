package src.Services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormaterService {
    public static String getFormattedCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return now.format(formatter);
    }
}
