package gr.grnet.eseal.utils;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class Utils {


    /**
     * {@link #formatTimePeriod(long)} accepts the beginning (unix time) of period and formats its duration in seconds, minutes, hours and days
     *
     * @param start
     * @return
     */
    public static String formatTimePeriod(long start) {
        long currentTime = System.currentTimeMillis();
        long timePeriod = currentTime - start;
        String timePeriodToString = DurationFormatUtils.formatDuration(timePeriod, "d") + "d";
        if ("0d".equals(timePeriodToString)) {
            timePeriodToString = DurationFormatUtils.formatDuration(timePeriod, "H") + "h";
            if ("0h".equals(timePeriodToString)) {
                timePeriodToString = DurationFormatUtils.formatDuration(timePeriod, "m") + "m";
                if ("0m".equals(timePeriodToString)) {
                    timePeriodToString = DurationFormatUtils.formatDuration(timePeriod, "s") + "s";
                    if ("0s".equals(timePeriodToString)) {
                        timePeriodToString = timePeriod + "ms";
                    }
                }
            }
        }
        return timePeriodToString;
    }


}
