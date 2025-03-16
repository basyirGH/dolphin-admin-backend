package com.dolphin.adminbackend.utility;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import com.dolphin.adminbackend.enums.TimeframeEnum;
import com.dolphin.adminbackend.model.dto.pojo.Timeframe;

public class DateUtility {

    public static Timeframe getStartOfTimeFrame(TimeframeEnum timeframeEnum, LocalDateTime now, LocalDateTime firstSimulationOrderDate) {
        LocalDateTime currentStartDate = null, prevStartDate = null, prevLastSecond = null;
        String message = "";
        DateTimeFormatter formatter;

        switch (timeframeEnum) {
            case SIMULATION:
                currentStartDate = firstSimulationOrderDate;
                prevStartDate = null;
                prevLastSecond = null;
                message = message.concat("the beginning of the simulation. No previous amounts are available for comparison.");
                break;
            case HOURLY:
                currentStartDate = now.truncatedTo(ChronoUnit.HOURS);
                formatter = DateTimeFormatter.ofPattern("h:mm a");
                prevStartDate = currentStartDate.withMinute(0).withSecond(0).withNano(0).minusHours(1);
                prevLastSecond = prevStartDate.withMinute(59).withSecond(59).withNano(999_999_999);
                // message = message.concat(currentStartDate.format(formatter)).concat(", and their progress compared to " + prevStartDate.format(DateTimeFormatter.ofPattern("h a")) + "'s full tracking.");
                message = message.concat("the beginning of the current hour, and their progress compared to last hour's full tracking.");
                break;
            case DAILY:
                currentStartDate = now.toLocalDate().atStartOfDay(); // 12:00 AM today
                prevStartDate = currentStartDate.minusDays(1);
                prevLastSecond = prevStartDate.toLocalDate().atTime(23,59).withSecond(59).withNano(999_999_999);
                message = message.concat("12:00 am").concat(", and their progress compared to yesterday's full tracking.");
                break;
            case WEEKLY:
                currentStartDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) // Start of this week (Monday)
                        .toLocalDate()
                        .atStartOfDay();
                formatter = DateTimeFormatter.ofPattern("d MMMM");
                prevStartDate = currentStartDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay().minusWeeks(1);
                prevLastSecond = prevStartDate.plusDays(6).toLocalDate().atTime(23,59).withSecond(59).withNano(999_999_999);
                message = message.concat("Monday, " + currentStartDate.format(formatter)).concat(", and their progress compared to last week's full tracking.");
                break;
            case MONTHLY:
                currentStartDate = now.with(TemporalAdjusters.firstDayOfMonth()) // Start of this month (1st day)
                        .toLocalDate()
                        .atStartOfDay();
                formatter = DateTimeFormatter.ofPattern("d MMMM YYYY");
                prevStartDate = currentStartDate.minusMonths(1);
                prevLastSecond = prevStartDate.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(23,59).withSecond(59).withNano(999_999_999);
                message = message.concat(currentStartDate.format(formatter)).concat(", and their progress compared to last month's full tracking.");
                break;
            case YEARLY:
                currentStartDate = now.with(TemporalAdjusters.firstDayOfYear()) // Start of this year (1st january)
                        .toLocalDate()
                        .atStartOfDay();
                formatter = DateTimeFormatter.ofPattern("d MMMM YYYY");
                prevStartDate = currentStartDate.minusYears(1);
                prevLastSecond = prevStartDate.with(TemporalAdjusters.lastDayOfYear()).toLocalDate().atTime(23,59).withSecond(59).withNano(999_999_999);
                message = message.concat(currentStartDate.format(formatter)).concat(", and their progress compared to last year's full tracking.");
                break;
            case ALL_TIME:
                currentStartDate = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0); // Unix epoch
                message = message.concat("inception.");
                break;
            default:
                currentStartDate = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0); // Unix epoch
                message = null;
        }
        Timeframe timeframe = new Timeframe();
        timeframe.setCurrentStartDate(currentStartDate);
        timeframe.setMessage(message);
        timeframe.setPrevStartDate(prevStartDate);
        timeframe.setPrevLastSecond(prevLastSecond);
        return timeframe;
    }

    public static Date convertToDate(LocalDateTime local) {
        if (local == null) {
            return null;
        }
        // Convert LocalDateTime to ZonedDateTime
        ZonedDateTime zonedDateTime = local.atZone(ZoneId.systemDefault());

        // Convert ZonedDateTime to java.util.Date
        Date date = Date.from(zonedDateTime.toInstant());

        return date;
    }

    public static String getAgeRange(int age) {
        if (age <= 25) {
            return "(18-25)";
        } else if (age <= 30) {
            return "(26-30)";
        } else if (age <= 40) {
            return "(31-40)";
        } else if (age <= 50) {
            return "(41-50)";
        } else if (age <= 60) {
            return "(51-60)";
        } else { 
            return "(Over 60)";
        }
    }
}
