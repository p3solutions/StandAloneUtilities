import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Timmer {
    static final int MINUTES_PER_HOUR = 60;
    static final int SECONDS_PER_MINUTE = 60;
    static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");


    private static Period getPeriod(LocalDateTime dob, LocalDateTime now) {
        return Period.between(dob.toLocalDate(), now.toLocalDate());
    }

    private static long[] getTime(LocalDateTime dob, LocalDateTime now) {
        LocalDateTime today = LocalDateTime.of(now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(), dob.getHour(), dob.getMinute(), dob.getSecond());
        Duration duration = Duration.between(today, now);

        long seconds = duration.getSeconds();
        long hours = seconds / SECONDS_PER_HOUR;
        long minutes = ((seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
        long secs = (seconds % SECONDS_PER_MINUTE);

        return new long[]{hours, minutes, secs};
    }

    public void getStartTime() {
        startTime = LocalDateTime.now();
        System.out.println("START TIME"+dtf.format(startTime));
    }
    public void getEndTime() {
       endTime = LocalDateTime.now();
       System.out.println("END TIME"+dtf.format(endTime));
       getTotalPeriod();

    }

    private void getTotalPeriod() {
        System.out.println("TOTAL TIME PERIOD TAKEN");
        Period period = getPeriod(startTime, endTime);
        long time[] = getTime(startTime, endTime);
        System.out.println(period.getYears() + " years " +
                period.getMonths() + " months " +
                period.getDays() + " days " +
                time[0] + " hours " +
                time[1] + " minutes " +
                time[2] + " seconds.");
    }
}
