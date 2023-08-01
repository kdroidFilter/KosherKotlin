package com.kosherjava.zmanim.java.zmanim;

import com.kosherjava.zmanim.java.zmanim.hebrewcalendar.JewishDate;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;

public class Main {
    public static boolean printAndReturn(boolean value) {
        System.out.println("Value: " + value);
        return value;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new JewishDate(LocalDate.now())); // 14 Av, 5783
        System.out.println(new JewishDate(LocalDate.of(1, Month.JANUARY, 1))); // 18 Teves, 3761
        System.out.println(new JewishDate());
        System.out.println(LocalDate.of(1, Month.JANUARY, 1));
        Calendar instance = Calendar.getInstance();
        instance.set(0, Calendar.SEPTEMBER, 5);
        instance.roll(Calendar.DATE, 365);
        System.out.print("Calendar:");
        //get num days in year 0:
        //System.out.println(instance.getActualMaximum(Calendar.DAY_OF_YEAR));
        System.out.println(instance.toInstant());
        System.out.println(new JewishDate(3762, JewishDate.TISHREI, 1).getGregorianCalendar().toString());
        Instant DISTANT_FUTURE = Instant.ofEpochSecond(3093527980800L);
        java.util.Date distantFutureDate = Date.from(DISTANT_FUTURE);
//        LocalDate distantFutureLocalDate = java.time.LocalDate.ofInstant(DISTANT_FUTURE, ZoneId.systemDefault());
//        JewishDate distantFutureJewishDate = new JewishDate(distantFutureLocalDate);
//        System.out.println(distantFutureLocalDate.minusDays(1L));
        /*Instant DISTANT_FUTURE = Instant.ofEpochSecond(3093527980800L);
        java.util.Date distantFutureDate = Date.from(DISTANT_FUTURE);
        LocalDate distantFutureLocalDate = LocalDate.ofInstant(DISTANT_FUTURE, ZoneId.systemDefault());
        JewishDate distantFutureJewishDate = new JewishDate(distantFutureLocalDate);
        JewishDate currentJewishDate = new JewishDate(3761, JewishDate.TEVES, 18); //start of hillel hazaken's calender
        ArrayList<String> SBs = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        while (
                currentJewishDate.getJewishYear() != distantFutureJewishDate.getJewishYear() ||
                        currentJewishDate.getJewishMonth() != distantFutureJewishDate.getJewishMonth() ||
                        currentJewishDate.getJewishDayOfMonth() != distantFutureJewishDate.getJewishDayOfMonth()
        ) {
            try {
                if (current.length() > Integer.MAX_VALUE - 50 ||
                        (currentJewishDate.getJewishYear() % 79981 == 0 &&
                                currentJewishDate.getJewishMonth() == 1 &&
                                currentJewishDate.getJewishDayOfMonth() == 1
                        )) {
                    System.out.println("Length: " + current.length() + ", Jewish year % 79982 = " + currentJewishDate.getJewishYear() % 79982);
                    System.out.println("Current jewish date: " + currentJewishDate);
                    SBs.add(current.toString());
                    current = new StringBuilder();
                    System.gc();
                }
                current
                        .append(currentJewishDate.getJewishYear())
                        .append("-")
                        .append(currentJewishDate.getJewishMonth())
                        .append("-")
                        .append(currentJewishDate.getJewishDayOfMonth())

                        .append(',')

                        .append(currentJewishDate.getGregorianYear())
                        .append("-")
                        .append(currentJewishDate.getGregorianMonth() + 1)
                        .append("-")
                        .append(currentJewishDate.getGregorianDayOfMonth())
                        .append('\n');
                currentJewishDate.forward(Calendar.DATE, 1);

            } catch (Throwable t) {
                System.gc();
                t.printStackTrace();
                System.out.println("Current jewish date: " + currentJewishDate);
//            System.out.println("Current: " + current);
            }
        }
        System.out.println(
                "Current Hebrew: " +
                        currentJewishDate.getJewishYear() + "-" +
                        currentJewishDate.getJewishMonth() + "-" +
                        currentJewishDate.getJewishDayOfMonth()
        );
        System.out.println(
                "Current Gregorian: " +
                        currentJewishDate.getGregorianYear() + "-" +
                        currentJewishDate.getGregorianMonth() + "-" +
                        currentJewishDate.getGregorianDayOfMonth()
        );
        System.out.println(
                "Distant Hebrew: " +
                        distantFutureJewishDate.getJewishYear() + "-" +
                        distantFutureJewishDate.getJewishMonth() + "-" +
                        distantFutureJewishDate.getJewishDayOfMonth()
        );
        System.out.println(
                "Distant Gregorian: " +
                        distantFutureJewishDate.getGregorianYear() + "-" +
                        distantFutureJewishDate.getGregorianMonth() + "-" +
                        distantFutureJewishDate.getGregorianDayOfMonth()
        );
        BufferedWriter writer = Files.newBufferedWriter(new File("output.csv").toPath());
        for (String sb : SBs) writer.write(sb);
        writer.close();*/
    }
}
