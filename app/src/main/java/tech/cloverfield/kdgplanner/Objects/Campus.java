package tech.cloverfield.kdgplanner.Objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import tech.cloverfield.kdgplanner.DateFormatter;

public class Campus {

    private String initials;
    private HashMap<String, Datum> datesList = new HashMap<>();

    public Campus(String initials) {

        this.initials = initials;
    }

    public String getInitials() {
        return initials;
    }

    public void addUur(String identifier, Date date, Uur uur) {
        String queryDate = DateFormatter.decode(date, DateType.DATE);
        Datum datum = datesList.get(queryDate);
        if (datum == null) datum = new Datum();
        datum.addUur(identifier, uur);
        datesList.put(queryDate, datum);
    }

    public ArrayList<Classroom> getAvailableClassrooms(Date date) {
        String queryDate = DateFormatter.decode(date, DateType.DATE);
        ArrayList<Classroom> classrooms = new ArrayList<>();
        if (datesList.containsKey(queryDate))
        for (Classroom classroom : datesList.get(queryDate).getClassrooms()) {
            if (classroom.isAvailable(date)) classrooms.add(classroom);
        }
        return classrooms;
    }

}
