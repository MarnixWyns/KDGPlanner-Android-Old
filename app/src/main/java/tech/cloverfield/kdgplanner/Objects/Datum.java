package tech.cloverfield.kdgplanner.Objects;

import java.util.ArrayList;
import java.util.Date;

public class Datum {

    private Date date;
    private ArrayList<Classroom> classrooms = new ArrayList<>();

    public Datum(Date date) {
        this.date = date;
    }

    public void addClassroom(Classroom room) {
        this.classrooms.add(room);
    }

    public ArrayList<Classroom> getClassrooms() {
        return this.classrooms;
    }

}
