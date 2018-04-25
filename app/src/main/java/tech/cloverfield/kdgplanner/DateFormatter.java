package tech.cloverfield.kdgplanner;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import tech.cloverfield.kdgplanner.Main.DateType;

public class DateFormatter {

    @SuppressLint("SimpleDateFormat")
    public static Date toDate(String input, DateType type) {
        try {
            DateFormat template;

            if (type == DateType.TIME) template = new SimpleDateFormat("kk:mm");
            else if (type == DateType.DATE) template = new SimpleDateFormat("yyyy-MM-dd");
            else if (type == DateType.FULL_DATE) template = new SimpleDateFormat("EEEE d MMMM yyyy kk:mm");
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

    public static String fixTimeString(String time) {
        String newTime = "";
        String[] split = time.split(":");
        newTime += fix0(Integer.parseInt(split[0]));
        newTime += ":";
        newTime += fix0(Integer.parseInt(split[1]));
        return newTime;
    }

    public static String fix0(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

    public static String translate(String input, HashMap translation) {
        String returnString = input;
        for (int i = 0; i < translation.keySet().size(); i++) {
            String key = (String) translation.keySet().toArray()[i];
            String value = (String) translation.get(key);

            if (returnString.contains(key)) {
                returnString = returnString.replaceAll(key, value);
            }
        }

        return returnString;
    }
}