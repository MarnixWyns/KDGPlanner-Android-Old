package tech.cloverfield.kdgplanner.Domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import tech.cloverfield.kdgplanner.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Classroom implements Comparable {

    private String identifier;
    private ArrayList<Uur> uurLijst = new ArrayList<>();
    private String duration;
    private String durationSort;
    private Context context;

    public Classroom(String identifier, Context context) {
        this.identifier = identifier;
        this.context = context;
    }

    public void addUur(Uur uur) {
        uurLijst.add(uur);
    }

    public boolean isAvailable(Date date) {
        for (int i = 0; i < uurLijst.size(); i++) {
            Date endDate = uurLijst.get(i).getEnd();
            Date startDate = uurLijst.get(i).getStart();

            if (uurLijst.size() == 1) {
                if (date.before(startDate)) {
                    if (setAvailability(date, startDate)) return true;
                } else if (date.after(endDate)) {
                    if (setAvailability(date, null)) return true;
                }
            } else if (i == 0) {
                if (date.before(startDate)) {
                    if (setAvailability(date, startDate)) return true;
                } else if (date.after(endDate)) {
                    startDate = uurLijst.get(i+1).getStart();
                    if (date.before(startDate)) {
                        if (setAvailability(date, startDate)) return true;
                    }
                }

            } else if (i == uurLijst.size() - 1) {
                if (date.after(endDate))  {
                    if (setAvailability(date, null)) return true;
                }
            } else {
                if (date.after(endDate)) {
                    startDate = uurLijst.get(i + 1).getStart();
                    if (date.before(startDate)) {
                        if (setAvailability(date, startDate)) return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressLint({"DefaultLocale", "StringFormatMatches"})
    private boolean setAvailability(Date huidigeTijd, Date endUur) {
        if (huidigeTijd == null) {
            return false;
        } else if (endUur == null) {
            duration = context.getString(R.string.rest_of_the_day);
            durationSort = duration;
            return true;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endUur);
            long diff = endUur.getTime() - huidigeTijd.getTime();

            if ((diff / (60 * 60 * 1000) % 24) > 0 || (diff / (60 * 1000) % 60) > 20) {
                durationSort = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                if ((diff / (60 * 60 * 1000) % 24) == 0) {
                    duration = String.format(context.getString(R.string.minutes_until), diff / (60 * 1000) % 60, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    return true;
                } else if ((diff / (60 * 1000) % 60) == 0) {
                    duration = String.format(context.getString(R.string.hours_until), diff / (60 * 60 * 1000) % 24, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    return true;
                } else {
                    duration = String.format(context.getString(R.string.hours_minutes_until), diff / (60 * 60 * 1000) % 24, diff / (60 * 1000) % 60, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public String getDuration() {
        return this.duration;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        String duration1 = this.durationSort;
        String duration2 = ((Classroom) o).durationSort;

        String className1 = this.identifier;
        String className2 = ((Classroom) o).identifier;

        if (duration1.equals(duration2)) {
            return className1.compareTo(className2);
        } else {
            return duration2.compareTo(duration1);
        }
    }
}