package tech.cloverfield.kdgplanner.business.domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import tech.cloverfield.kdgplanner.R;

import java.util.Calendar;
import java.util.Date;

public class Classroom implements Comparable {

    private String identifier;
    private String duration;
    private String durationSort;
    private Context context;

    public Classroom(String identifier, Date endDate, Date currentTime, Context context) {
        this.identifier = identifier;
        this.context = context;
        setAvailability(currentTime, endDate);
    }

    @SuppressLint({"DefaultLocale", "StringFormatMatches"})
    private void setAvailability(Date huidigeTijd, Date endUur) {
        Calendar end = Calendar.getInstance();
        end.setTime(endUur);
        if (huidigeTijd != null) {
            if (end.get(Calendar.HOUR_OF_DAY) == 23 && end.get(Calendar.MINUTE) == 59 && end.get(Calendar.SECOND) == 59) {
                duration = context.getString(R.string.rest_of_the_day);
                durationSort = duration;
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endUur);
                long diff = endUur.getTime() - huidigeTijd.getTime();

                if ((diff / (60 * 60 * 1000) % 24) > 0 || (diff / (60 * 1000) % 60) > 20) {
                    durationSort = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    if ((diff / (60 * 60 * 1000) % 24) == 0) {
                        duration = String.format(context.getString(R.string.minutes_until), diff / (60 * 1000) % 60, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    } else if ((diff / (60 * 1000) % 60) == 0) {
                        duration = String.format(context.getString(R.string.hours_until), diff / (60 * 60 * 1000) % 24, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    } else {
                        duration = String.format(context.getString(R.string.hours_minutes_until), diff / (60 * 60 * 1000) % 24, diff / (60 * 1000) % 60, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format(context.getString(R.string.class_availability), this.identifier, this.duration);
    }

    @Override
    public int compareTo(@NonNull Object o) throws NullPointerException {
        String duration1 = this.durationSort;
        String duration2 = ((Classroom) o).durationSort;

        String className1 = this.identifier;
        String className2 = ((Classroom) o).identifier;

        if (duration1 != null && duration2 != null) {
            if (duration1.equals(duration2)) {
                return className1.compareTo(className2);
            } else {
                return duration2.compareTo(duration1);
            }
        } else {
            throw new NullPointerException(duration1 + "\n" + duration2);
        }
    }
}