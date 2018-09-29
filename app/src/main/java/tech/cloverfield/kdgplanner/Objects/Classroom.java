package tech.cloverfield.kdgplanner.Objects;


import java.util.ArrayList;
import java.util.Date;

public class Classroom {

    private String identifier;
    private ArrayList<Uur> uurLijst = new ArrayList<>();
    private String duration;

    public Classroom(String identifier) {
        this.identifier = identifier;
    }

    public void addUur(Date start, Date end) {
        uurLijst.add(new Uur(start, end));
    }

    public ArrayList<Uur> getUren() {
        return uurLijst;
    }

    public boolean isAvailable(Date date) {
        for (int i = 0; i < uurLijst.size(); i++) {
            if (uurLijst.get(i).getEnd().before(date)) {
                if (uurLijst.size() == i) {
                    return setAvailability(date, null);
                } else {
                    if (uurLijst.get(i + 1).getStart().after(date)) {
                        return setAvailability(date, uurLijst.get(i + 1).getStart());
                    } else {
                        return setAvailability(null, null);
                    }
                }
            }
        }
        return setAvailability(null, null);
    }

    public boolean setAvailability(Date huidigeTijd, Date endUur) {
        if (huidigeTijd == null && endUur == null) {
            return false;
        } else if (endUur == null) {
            duration = "Voor de rest van de dag";
            return true;
        } else {
            long diff = huidigeTijd.getTime() - endUur.getTime();

            if (!((diff / (60 * 60 * 1000) % 24) == 0 && (diff / (60 * 1000) % 60) < 20)) {
                if ((diff / (60 * 60 * 1000) % 24) == 0) {
                    duration = String.format("%s minuten", diff / (60 * 1000) % 60);
                    return true;
                } else if ((diff / (60 * 1000) % 60) == 0) {
                    duration = String.format("%s uur", diff / (60 * 60 * 1000) % 24);
                    return true;
                } else {
                    duration = String.format("%s uur en %s minuten", diff / (60 * 1000) % 60, diff / (60 * 1000) % 60);
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
}