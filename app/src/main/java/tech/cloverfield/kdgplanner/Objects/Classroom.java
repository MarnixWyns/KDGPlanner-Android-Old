package tech.cloverfield.kdgplanner.Objects;

import android.annotation.SuppressLint;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Classroom implements Comparable {

    private String identifier;
    private ArrayList<Uur> uurLijst = new ArrayList<>();
    private String duration;
    private String durationSort;

    public Classroom(String identifier) {
        this.identifier = identifier;
    }

    public void addUur(Uur uur) {
        uurLijst.add(uur);
    }

    public boolean isAvailable(Date date) {
        for (int i = 0; i < uurLijst.size(); i++) {
            Date endDate = uurLijst.get(i).getEnd();
            Log.d(this.identifier, "End| " + endDate.getHours()+":"+endDate.getMinutes());
            if (i == 0) {
                Date startUur = uurLijst.get(i).getStart();
                Log.d(this.identifier, "Start| " + startUur.getHours()+":"+startUur.getMinutes());
                if (startUur.after(date)) {
                    if (setAvailability(date, startUur)) return true;
                }
            } if (endDate.before(date)) {
                if (uurLijst.size() == (i + 1)) {
                    if (setAvailability(date, null)) return true;
                } else {
                    Date startUur = uurLijst.get(i + 1).getStart();
                    Log.d(this.identifier, "Start| " + startUur.getHours()+":"+startUur.getMinutes());
                    if (startUur.after(date)) {
                        if (setAvailability(date, startUur)) return true;
                    } else {
                        if (setAvailability(null, null)) return true;
                    }
                }
            }
        }
        return setAvailability(null, null);
    }

    @SuppressLint("DefaultLocale")
    private boolean setAvailability(Date huidigeTijd, Date endUur) {
        if (huidigeTijd == null) {
            return false;
        } else if (endUur == null) {
            duration = "Voor de rest van de dag";
            durationSort = duration;
            return true;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endUur);
            long diff = endUur.getTime() - huidigeTijd.getTime();

            if (!((diff / (60 * 60 * 1000) % 24) == 0 && (diff / (60 * 1000) % 60) < 20)) {
                durationSort = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                if ((diff / (60 * 60 * 1000) % 24) == 0) {
                    duration = String.format("%d minuten (tot %02d:%02d)", diff / (60 * 1000) % 60, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    return true;
                } else if ((diff / (60 * 1000) % 60) == 0) {
                    duration = String.format("%d uur (tot %02d:%02d)", diff / (60 * 60 * 1000) % 24, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    return true;
                } else {
                    duration = String.format("%d uur en %s minuten (tot %02d:%02d)", diff / (60 * 60 * 1000) % 24, diff / (60 * 1000) % 60, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
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
        return duration2.compareTo(duration1);
    }
}