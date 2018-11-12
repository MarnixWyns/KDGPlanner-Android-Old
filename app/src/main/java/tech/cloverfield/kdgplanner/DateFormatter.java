package tech.cloverfield.kdgplanner;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tech.cloverfield.kdgplanner.Domain.DateType;

public class DateFormatter {

    @SuppressLint("SimpleDateFormat")
    public static Date toDate(String input, DateType type) {
        try {
            DateFormat template;

            if (type == DateType.TIME) {
                input += ":00.000";
                template = new SimpleDateFormat("kk:mm:ss.SSS");
            }
            else if (type == DateType.DATE) template = new SimpleDateFormat("yyyy-MM-dd");
            else if (type == DateType.FULL_DATE_US) template = new SimpleDateFormat("kk:mm:ss.SSS yyyy-MM-dd");
            else if (type == DateType.FULL_DATE_BE) template = new SimpleDateFormat("kk:mm:ss.SSS dd-MM-yyyy");
            else return null;

            return template.parse(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("DefaultLocale")
    public static String decode(Date date, DateType type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String fixed = "";
        if (type == DateType.TIME) {
            fixed = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        } else if (type == DateType.DATE) {
            fixed = String.format("%02d-%02d-%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        } else if (type == DateType.FULL_DATE_BE) {
            fixed = String.format("%02d:%02d %02d-%02d-%04d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        } else if (type == DateType.FULL_DATE_US) {
            fixed = String.format("%02d:%02d %04d-%02d-%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        }
        return fixed;
    }
 }