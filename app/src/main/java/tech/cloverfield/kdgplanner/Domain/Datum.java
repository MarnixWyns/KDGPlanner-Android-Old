package tech.cloverfield.kdgplanner.Domain;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

public class Datum {

    private HashMap<String, Classroom> classrooms = new HashMap<>();

    public void addUur(String identifier, Uur uur, Context context) {
        Classroom classroom = classrooms.get(identifier);
        if (classroom != null) {
        } else {
            classroom = new Classroom(identifier, context);
        }
        classroom.addUur(uur);
        this.classrooms.put(classroom.getIdentifier(), classroom);
    }

    public ArrayList<Classroom> getClassrooms() {
        return new ArrayList<>(this.classrooms.values());
    }

}
