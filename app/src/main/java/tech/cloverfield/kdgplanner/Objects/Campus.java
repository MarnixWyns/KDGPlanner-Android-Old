package tech.cloverfield.kdgplanner.Objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Campus {

    private String initials;
    private HashMap<Date, Datum> datesList = new HashMap<>();

    public Campus(String initials) {

        this.initials = initials;
    }

    public String getInitials() {
        return initials;
    }

    public void addClassroom(Date date, Classroom classroom) {
        Datum datum = new Datum(date);
        if (datesList.containsKey(date)) {
            datum = datesList.get(date);
            datum.addClassroom(classroom);
        }
        datum.addClassroom(classroom);
        datesList.put(date, datum);
    }

    public ArrayList<Classroom> getClassrooms(Date date) {
        return datesList.get(date).getClassrooms();
    }

}
