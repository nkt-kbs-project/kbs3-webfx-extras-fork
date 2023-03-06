package dev.webfx.extras.timelayout;

import dev.webfx.platform.util.Dates;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Bruno Salmon
 */
public class TimeUtil {


    public static List<MonthDay> generateThisMonthDays() {
        return generateMonthDays(YearMonth.now());
    }

    public static List<MonthDay> generateMonthDays(YearMonth yearMonth) {
        return IntStream.range(1, yearMonth.atEndOfMonth().getDayOfMonth() + 1)
                .mapToObj(d -> MonthDay.of(yearMonth.getMonth(), d))
                .collect(Collectors.toList());
    }

    public static List<LocalDate> generateThisMonthDates() {
        return generateMonthDates(YearMonth.now());
    }

    public static List<LocalDate> generateMonthDates(YearMonth yearMonth) {
        return IntStream.range(1, yearMonth.atEndOfMonth().getDayOfMonth() + 1)
                .mapToObj(d -> LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), d))
                .collect(Collectors.toList());
    }

    public static List<DayOfWeek> generateDaysOfWeek() {
        return Arrays.asList(DayOfWeek.values());
    }

    public static List<YearMonth> generateThisYearMonths() {
        return generateYearMonths(Year.now().getValue());
    }

    public static List<YearMonth> generateYearMonths(int year) {
        return generateYearMonths(year, 1, 12);
    }

    public static List<YearMonth> generateYearMonthsRelativeToThisMonth(int startShift, int endShift) {
        YearMonth thisMonth = YearMonth.now();
        return generateYearMonths(thisMonth.plusMonths(startShift), thisMonth.plusMonths(endShift));
    }


    public static List<YearMonth> generateYearMonths(int year, int firstMonth, int lastMonth) {
        return generateYearMonths(YearMonth.of(year, firstMonth), YearMonth.of(year, lastMonth));
    }

    public static List<YearMonth> generateYearMonths(YearMonth first, YearMonth last) {
        List<YearMonth> yearMonths = new ArrayList<>();
        for (YearMonth yearMonth = first; yearMonth.isBefore(last) || yearMonth.equals(last); yearMonth = yearMonth.plusMonths(1)) {
            yearMonths.add(yearMonth);
        }
        return yearMonths;
    }

    public static <T> ChildTimeReader<LocalDate, T> localDateReader() {
        return new ChildTimeReader<>() {
            @Override
            public LocalDate getStartTime(T child) {
                return Dates.toLocalDate(child);
            }

            @Override
            public LocalDate getEndTime(T child) {
                return getStartTime(child).plusDays(1);
            }
        };
    }

    public static <T> ChildTimeReader<YearMonth, T> yearMonthReader() {
        return new ChildTimeReader<>() {
            @Override
            public YearMonth getStartTime(T child) {
                return (YearMonth) child; // temporary
            }

            @Override
            public YearMonth getEndTime(T child) {
                YearMonth startTime = getStartTime(child);
                return startTime.plusMonths(1);
            }
        };
    }

    public static <T> ChildTimeReader<MonthDay, T> monthDayReader() {
        return new ChildTimeReader<>() {
            @Override
            public MonthDay getStartTime(T child) {
                return (MonthDay) child; // temporary
            }

            @Override
            public MonthDay getEndTime(T child) {
                MonthDay startTime = getStartTime(child);
                LocalDate localDate = startTime.atYear(Year.now().getValue()).plusDays(1); // Assuming it's this year
                return MonthDay.of(localDate.getMonth(), localDate.getDayOfMonth());
            }
        };
    }

    public static ChildTimeReader<DayOfWeek, DayOfWeek> dayOfWeekReader() {
        return new ChildTimeReader<>() {
            @Override
            public DayOfWeek getStartTime(DayOfWeek child) {
                return child;
            }

            @Override
            public DayOfWeek getEndTime(DayOfWeek child) {
                return getStartTime(child).plus(1);
            }
        };
    }
}
