package com.skybirdbits.persiandatepicker.persiandate;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class PersianCalendar {

    private final int[] breaks = {-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
            1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178};
    private int year, monthOfYear, dayOfMonth;

    public PersianCalendar() {
        fromGregorian(new GregorianCalendar());
    }

    private boolean mLanguageFarsi;

    public PersianCalendar(int year, int monthOfYear, int dayOfMonth) {
        set(year, monthOfYear, dayOfMonth);
    }

    public PersianCalendar(GregorianCalendar gc) {
        fromGregorian(gc);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PersianCalendar(LocalDate ld) {
        fromGregorian(GregorianCalendar.from(ld.atStartOfDay(ZoneId.systemDefault())));
    }

    public PersianCalendar(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        fromGregorian(gc);
    }

    public PersianCalendar(long timeMillis) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timeMillis);
        fromGregorian(calendar);
    }

    public GregorianCalendar toGregorian() {
        int solarDay = toSolarDay();
        return solarDaysToGregorian(solarDay);
    }

    public void fromGregorian(GregorianCalendar gc) {
        int solarDay = gregorianToSolarDayNumbers(gc);
        fromSolarDay(solarDay);
    }

    public PersianCalendar getYesterday() {
        return getDateByDiff(Fields.DAY_OF_MONTH, -1);
    }

    public PersianCalendar getTomorrow() {
        return getDateByDiff(Fields.DAY_OF_MONTH, 1);
    }

    public long getTimeMillis() {
        return toGregorian().getTimeInMillis();
    }

    public void setTimeMillis(long timeMillis) {
        GregorianCalendar gregorian = toGregorian();
        gregorian.setTimeInMillis(timeMillis);
        fromGregorian(gregorian);
    }

    public boolean isLanguageFarsi() {
        return mLanguageFarsi;
    }

    public void setLanguageFarsi(boolean languageFarsi) {
        this.mLanguageFarsi = languageFarsi;
    }

    public int getHour() {
        return toGregorian().get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        return toGregorian().get(Calendar.MINUTE);
    }

    public int getSeconds() {
        return toGregorian().get(Calendar.SECOND);
    }

    public PersianCalendar getDateByDiff(Fields field, int amount) {
        GregorianCalendar gc = toGregorian();

        switch (field) {
            case YEAR:
                gc.add(Calendar.YEAR, amount);
                break;
            case MONTH:
                gc.add(Calendar.MONTH, amount);
                break;
            case WEEK_OF_YEAR:
                gc.add(Calendar.WEEK_OF_YEAR, amount);
                break;
            case WEEK_OF_MONTH:
                gc.add(Calendar.WEEK_OF_MONTH, amount);
                break;
            case DAY_OF_YEAR:
                gc.add(Calendar.DAY_OF_YEAR, amount);
                break;
            case DAY_OF_MONTH:
                gc.add(Calendar.DAY_OF_MONTH, amount);
                break;
            case DAY_OF_WEEK_IN_MONTH:
                gc.add(Calendar.DAY_OF_WEEK_IN_MONTH, amount);
                break;
            case DAY_OF_WEEK:
                gc.add(Calendar.DAY_OF_WEEK, amount);
                break;


        }

        return new PersianCalendar(gc);
    }


    /**
     * @return day Of Week
     */
    public int getDayOfWeek() {
        int dayOfWeek = toGregorian().get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            case 7:
                return 1;
        }

        return 0;
    }

    /**
     * @return get first day of week
     */
    public int getFirstDayOfWeek() {
        return 1;
    }

    /**
     * @return day name
     */

    public String getNameOfDayWeek(boolean supportFarsi) {
        return supportFarsi ?
                DateConstantsKt.getWeekDayNames()[getDayOfWeek() - 1] : DateConstantsKt.getWeekDayNamesLatin()[getDayOfWeek() - 1];

    }

    public String getNameOfDayWeek() {
        return getNameOfDayWeek(mLanguageFarsi);
    }

    /**
     * @return month name
     */

    public String getNameOfMonthYear(boolean supportFarsi) {
        return supportFarsi ? DateConstantsKt.getMonthNames()[getMonthOfYear() - 1]
                : DateConstantsKt.getMonthNamesLatin()[getMonthOfYear() - 1];
    }

    public String getNameOfMonthYear() {
        return getNameOfMonthYear(mLanguageFarsi);
    }


    public boolean isLeap() {
        return getLeapFactor(getYear()) == 0;
    }

    public int getYearLength() {
        return isLeap() ? 366 : 365;
    }

    public int getMonthLength() {
        if (getMonthOfYear() < 7) {
            return 31;
        } else if (getMonthOfYear() < 12) {
            return 30;
        } else if (getMonthOfYear() == 12) {
            if (isLeap())
                return 30;
            else
                return 29;
        }
        return 0;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public void setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void set(int year, int month, int day) {
        setYear(year);
        setMonthOfYear(month);
        setDayOfMonth(day);
    }


    public String getNumericDate() {

        String yearString = String.valueOf(getYear());
        String monthString = getMonthOfYear() < 10 ? ("0" + monthOfYear) : String.valueOf(monthOfYear);
        String dayString = getDayOfMonth() < 10 ? ("0" + dayOfMonth) : String.valueOf(dayOfMonth);

        return String.format(Locale.getDefault(),
                "%4s/%2s/%2s", yearString, monthString, dayString);

    }


    private int gregorianToSolarDayNumbers(GregorianCalendar gc) {
        int gregorianYear = gc.get(GregorianCalendar.YEAR);
        int gregorianMonth = gc.get(GregorianCalendar.MONTH) + 1;
        int gregorianDay = gc.get(GregorianCalendar.DAY_OF_MONTH);

        return (((1461 * (gregorianYear + 4800 + (gregorianMonth - 14) / 12)) / 4
                + (367 * (gregorianMonth - 2 - 12 * ((gregorianMonth - 14) / 12))) / 12
                - (3 * ((gregorianYear + 4900 + (gregorianMonth - 14) / 12) / 100)) / 4 + gregorianDay
                - 32075) - (gregorianYear + 100100 + (gregorianMonth - 8) / 6) / 100 * 3 / 4 + 752);
    }

    private int toSolarDayNumbers(SolarData data) {
        int julianYear = data.getYear();
        int julianMonth = data.getMonth();
        int JulianDay = data.getDay();

        return (1461 * (julianYear + 4800 + (julianMonth - 14) / 12)) / 4
                + (367 * (julianMonth - 2 - 12 * ((julianMonth - 14) / 12))) / 12
                - (3 * ((julianYear + 4900 + (julianMonth - 14) / 12) / 100)) / 4 + JulianDay
                - 32075;
    }

    private GregorianCalendar solarDaysToGregorian(int solarDayNumbers) {

        int j = 4 * solarDayNumbers + 139361631 + (4 * solarDayNumbers + 183187720) / 146097 * 3 / 4 * 4 - 3908;
        int i = (j % 1461) / 4 * 5 + 308;

        int gregorianDay = (i % 153) / 5 + 1;
        int gregorianMonth = ((i / 153) % 12) + 1;
        int gregorianYear = j / 1461 - 100100 + (8 - gregorianMonth) / 6;

        return new GregorianCalendar(gregorianYear, gregorianMonth - 1, gregorianDay);
    }

    private void fromSolarDay(int solarDayNumbers) {

        GregorianCalendar gc = solarDaysToGregorian(solarDayNumbers);
        int gregorianYear = gc.get(GregorianCalendar.YEAR);

        int solarYear, solarMonth, solarDay;
        solarYear = gregorianYear - 621;

        GregorianCalendar gregorianFirstFarvardin = new PersianCalendar(solarYear, 1, 1).getGregorianFirstFarvardin();
        int firstFarvardin = gregorianToSolarDayNumbers(gregorianFirstFarvardin);
        int diffFromFarvardinFirst = solarDayNumbers - firstFarvardin;


        if (diffFromFarvardinFirst >= 0) {
            if (diffFromFarvardinFirst <= 185) {
                solarMonth = 1 + diffFromFarvardinFirst / 31;
                solarDay = (diffFromFarvardinFirst % 31) + 1;
                set(solarYear, solarMonth, solarDay);
                return;
            } else {
                diffFromFarvardinFirst = diffFromFarvardinFirst - 186;
            }
        } else {
            diffFromFarvardinFirst = diffFromFarvardinFirst + 179;
            if (getLeapFactor(solarYear) == 1)
                diffFromFarvardinFirst = diffFromFarvardinFirst + 1;
            solarYear -= 1;
        }


        solarMonth = 7 + diffFromFarvardinFirst / 30;
        solarDay = (diffFromFarvardinFirst % 30) + 1;
        set(solarYear, solarMonth, solarDay);
    }

    private int toSolarDay() {
        int solarMonth = getMonthOfYear();
        int solarDay = getDayOfMonth();

        GregorianCalendar gregorianFirstFarvardin = getGregorianFirstFarvardin();
        int gregorianYear = gregorianFirstFarvardin.get(Calendar.YEAR);
        int gregorianMonth = gregorianFirstFarvardin.get(Calendar.MONTH) + 1;
        int gregorianDay = gregorianFirstFarvardin.get(Calendar.DAY_OF_MONTH);

        SolarData firstFarvardin = new SolarData(gregorianYear, gregorianMonth, gregorianDay);


        return toSolarDayNumbers(firstFarvardin) + (solarMonth - 1) * 31 - solarMonth / 7 * (solarMonth - 7)
                + solarDay - 1;
    }


    private GregorianCalendar getGregorianFirstFarvardin() {
        int marchDay = 0;

        int solarYear = getYear();
        int gregorianYear = solarYear + 621;

        int solarLeap = -14;
        int sp = breaks[0];

        int jump;
        for (int j = 1; j <= 19; j++) {
            int sm = breaks[j];
            jump = sm - sp;

            if (solarYear < sm) {
                int N = solarYear - sp;
                solarLeap = solarLeap + N / 33 * 8 + (N % 33 + 3) / 4;

                if ((jump % 33) == 4 && (jump - N) == 4)
                    solarLeap = solarLeap + 1;

                int GregorianLeap = (gregorianYear / 4) - (gregorianYear / 100 + 1) * 3 / 4 - 150;

                marchDay = 20 + (solarLeap - GregorianLeap);

                break;
            }

            solarLeap = solarLeap + jump / 33 * 8 + (jump % 33) / 4;
            sp = sm;
        }

        return new GregorianCalendar(gregorianYear, 2, marchDay);
    }

    private int getLeapFactor(int solarYear) {
        int leap = 0;

        int sp = breaks[0];

        int jump;
        for (int j = 1; j <= 19; j++) {
            int sm = breaks[j];
            jump = sm - sp;
            if (solarYear < sm) {
                int N = solarYear - sp;

                if ((jump - N) < 6)
                    N = N - jump + (jump + 4) / 33 * 33;

                leap = ((((N + 1) % 33) - 1) % 4);

                if (leap == -1)
                    leap = 4;

                break;
            }

            sp = sm;
        }

        return leap;
    }

    @NonNull
    @Override
    public String toString() {

        String nameOfDayWeek = getNameOfDayWeek();
        int dayOfMonth = getDayOfMonth();
        String nameOfMonthYear = getNameOfMonthYear();

        return String.format("%-" + (nameOfDayWeek.length() + 1)
                + "s%-3d%s", nameOfDayWeek, dayOfMonth, nameOfMonthYear);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersianCalendar that = (PersianCalendar) o;

        return year == that.year && monthOfYear == that.monthOfYear && dayOfMonth == that.dayOfMonth;
    }
}
