package tech.cloverfield.kdgplanner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    public static Date toDate(String input, DateType type) {
        try {
            DateFormat template;

            if (type == DateType.TIME) template = new SimpleDateFormat("kk:mm");
            else if (type == DateType.DATE) template = new SimpleDateFormat("yyyy-MM-dd");
            else return null;

            return template.parse(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String fixDateString(String date) {
        String newDate = "";
        String[] split = date.split("-");
        newDate += fix0(Integer.parseInt(split[0]));
        newDate += "-";
        newDate += fix0(Integer.parseInt(split[1]));
        newDate += "-";
        newDate += fix0(Integer.parseInt(split[2]));
        return newDate;
    }

    private static String fix0(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }
}