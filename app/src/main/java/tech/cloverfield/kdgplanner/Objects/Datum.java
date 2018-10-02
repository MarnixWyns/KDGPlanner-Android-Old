package tech.cloverfield.kdgplanner.Objects;

import java.util.ArrayList;
import java.util.HashMap;

public class Datum {

    private HashMap<String, Classroom> classrooms = new HashMap<>();

    public void addUur(String identifier, Uur uur) {
        Classroom classroom = classrooms.get(identifier);
        if (classroom != null) {
        } else {
            classroom = new Classroom(identifier);
        }
        classroom.addUur(uur);
        this.classrooms.put(classroom.getIdentifier(), classroom);
    }

    public ArrayList<Classroom> getClassrooms() {
        return new ArrayList<>(this.classrooms.values());
    }

}
