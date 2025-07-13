package liqp.filters.date.fuzzy.extractors;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

class Chart {

    public static void main(String[] args) {
        String key = "z";
        for (int i = 1; i < 10; i++) {
            printPattern(key, i);
        }
    }

    static void printPattern(String key, int count) {
        String fullKey = new String(new char[count]).replace("\0", key);
//        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime now = ZonedDateTime.of(
                LocalDate.of(2020, 1, 1),
                LocalTime.of(1, 1, 1),
                ZoneId.of("Europe/Kiev")
//                ZoneOffset.systemDefault()
//                ZoneOffset.UTC
        );
        try {
            String formatted = now.format(DateTimeFormatter.ofPattern(fullKey));
            System.out.println(fullKey + " -> " + formatted);
        } catch (Exception e) {
            System.out.println(fullKey + " -> " + "Error: " + e.getMessage());
        }
    }
    /**
     G - era designator
     GG -> AD / BC
     GGGG -> Anno Domini / Before Christ

     yy -> 24
     yyyy -> 2024

     Y - do not use (year of the week)

     MM -> 12
     MMM -> Dec/груд.
     MMMM -> December/грудня

     L - do not use (non-contextual month)
     LLLL -> December/грудень

     w - do not use (week of the year)
     W - do not use (Week in month)

     D - do not use (day of the year)

     d - day of the month
     d -> 5
     dd -> 05

     F - do not use (day of the week in month)

     EEE -> Thu
     EEEE -> Thursday

     u - day of the week (1 = Monday, ..., 7 = Sunday)
     u - do not use (day of the week)

     a - am/pm marker
     a -> PM

     H - hour in day (0-23)
     H -> 9
     HH -> 09

     k - do not use (hour in day (1-24))

     K - do not use (hour in am/pm (0-11))

     h	Hour in am/pm (1-12) ONLY IF am/pm marker is present
     h -> 1
     hh -> 01

     m - minute in hour
     m -> 1
     mm -> 01

     s - second in minute
     s -> 1
     ss -> 01

     S - millisecond (already defined)

     z - General time zone

     z -> UTC / EET BUT ZoneOffset.UTC -> Z
     zzz -> UTC / EET BUT ZoneOffset.UTC -> Z
     zzzz -> "Coordinated Universal Time" / "Eastern European Standard" Time BUT ZoneOffset.UTC -> Z

     Z - RFC 822 time zone
     Z -> +0200
     ZZ -> +0200
     ZZZ -> +0200
     ZZZZ -> GMT+02:00
     ZZZZZ -> +02:00

     X - ISO 8601 time zone
     X -> +02
     XX -> +0200
     XXX -> +02:00
     XXXX -> +0200
     XXXXX -> +02:00

     V	time-zone ID
     VV -> Europe/Kiev / Z for ZoneOffset.UTC and "Etc/UTC" for systemDefault

     v	generic time-zone name
     v -> EET
     vvvv -> Eastern European Time


     */
}
