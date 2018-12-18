package tech.cloverfield.kdgplanner.controller;

import java.util.HashMap;

import tech.cloverfield.kdgplanner.business.ClassroomManager;
import tech.cloverfield.kdgplanner.business.domain.Campus;
import tech.cloverfield.kdgplanner.business.domain.Student;

public class KDGPlannerController {

    private Campus activeCampus;
    private ClassroomManager classroomManager;
    private StudentManager studentManager;


    private tech.cloverfield.kdgplanner.persistence.SQLiteClassroomRepo SQLiteClassroomRepo;
    private HashMap<String, String> campusTranslator = new HashMap<>();

    public KDGPlannerController() {
        activeCampus = Campus.GROENPLAATS;
         classroomManager = new ClassroomManager();
    }



    public Campus getActiveCampus() {
        return activeCampus;
    }

    public void setActiveCampus(Campus activeCampus) {
        this.activeCampus = activeCampus;
    }

    public Student addStudent() {
        return null;
    }
}
