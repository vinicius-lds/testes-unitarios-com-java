package br.ce.wcaquino.matchers;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterData;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class TestMatchers {

    public static DayOfWeekMatcher isMonday() {
        return new DayOfWeekMatcher(Calendar.MONDAY);
    }

    public static DateSumMatcher isToday() {
        return new DateSumMatcher(0);
    }

    public static DateSumMatcher isTomorrow() {
        return new DateSumMatcher(1);
    }

    public static DateSumMatcher isTreeDaysFromToday() {
        return new DateSumMatcher(3);
    }

    public static DateMatcher isDate(int year, int month, int day) {
        return new DateMatcher(year, month, day);
    }

    public static CalendarMatcher isCalendar(int year, int month, int day) {
        return new CalendarMatcher(year, month, day);
    }

    private static class DayOfWeekMatcher extends TypeSafeMatcher<Date> {

        private int expectedDayOfWeek;

        public DayOfWeekMatcher(int expectedDayOfWeek) {
            this.expectedDayOfWeek = expectedDayOfWeek;
        }

        @Override
        protected boolean matchesSafely(Date actualDate) {
            return DataUtils.verificarDiaSemana(actualDate, expectedDayOfWeek);
        }

        @Override
        public void describeTo(Description description) {
            var currentDate = Calendar.getInstance();
            currentDate.set(Calendar.DAY_OF_WEEK, expectedDayOfWeek);
            var dateToString = currentDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
            description.appendText(dateToString);
        }

    }

    private static class DateSumMatcher extends TypeSafeMatcher<Date> {

        private int days;

        public DateSumMatcher(int days) {
            this.days = days;
        }

        @Override
        protected boolean matchesSafely(Date date) {
            return isMesmaData(DataUtils.obterDataComDiferencaDias(days), date);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.format("<%s>", DataUtils.obterDataComDiferencaDias(days).toString()));
        }
    }

    private static class DateMatcher extends TypeSafeMatcher<Date> {

        private int year;
        private int month;
        private int day;

        public DateMatcher(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        @Override
        protected boolean matchesSafely(Date date) {
            return isMesmaData(obterData(day, month, year), date);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.format("<%s>", obterData(day, month, year).toString()));
        }
    }

    private static class CalendarMatcher extends TypeSafeMatcher<Calendar> {

        private int year;
        private int month;
        private int day;

        public CalendarMatcher(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        @Override
        protected boolean matchesSafely(Calendar calendar) {
            return calendar.get(Calendar.DAY_OF_MONTH) == day && calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.YEAR) == year;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.format("<%s-%s-%s>", year, month, day));
        }
    }
}
