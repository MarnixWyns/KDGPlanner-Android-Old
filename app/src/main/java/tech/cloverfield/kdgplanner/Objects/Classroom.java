package tech.cloverfield.kdgplanner.Objects;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Classroom {

    private String identifier;
    private ArrayList<Uur> uurLijst = new ArrayList<>();
    private String duration;

    public Classroom(String identifier) {
        this.identifier = identifier;
    }

    public void addUur(Uur uur) {
        uurLijst.add(uur);
    }

    public boolean isAvailable(Date date) {
        for (int i = 0; i < uurLijst.size(); i++) {
            Date endDate = uurLijst.get(i).getEnd();
            if (endDate.before(date)) {
                if (uurLijst.size() == (i + 1)) {
                    if (setAvailability(date, null)) return true;
                } else {
                    Date startUur = uurLijst.get(i + 1).getStart();
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

    public boolean setAvailability(Date huidigeTijd, Date endUur) {
        if (huidigeTijd == null) {
            return false;
        } else if (endUur == null) {
            duration = "Voor de rest van de dag";
            return true;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endUur);
            long diff = endUur.getTime() - huidigeTijd.getTime();

            if (!((diff / (60 * 60 * 1000) % 24) == 0 && (diff / (60 * 1000) % 60) < 20)) {
                if ((diff / (60 * 60 * 1000) % 24) == 0) {
                    duration = String.format("%d minuten (tot %02d:%02d)", diff / (60 * 1000) % 60, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    return true;
                } else if ((diff / (60 * 1000) % 60) == 0) {
                    duration = String.format("%d uur (tot %02d:%02d)", diff / (60 * 60 * 1000) % 24, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    return true;
                } else {
                    duration = String.format("%d uur en %s minuten (tot %02d:%02d)", diff / (60 * 1000) % 60, diff / (60 * 1000) % 60, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
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
}