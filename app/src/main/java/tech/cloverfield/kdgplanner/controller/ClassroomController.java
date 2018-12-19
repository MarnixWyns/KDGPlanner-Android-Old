package tech.cloverfield.kdgplanner.controller;

import android.content.Context;

import java.util.Collection;
import java.util.Date;

import tech.cloverfield.kdgplanner.business.classrooms.ClassroomManager;
import tech.cloverfield.kdgplanner.business.classrooms.IClassroomManager;
import tech.cloverfield.kdgplanner.business.students.IStudentManager;
import tech.cloverfield.kdgplanner.business.domain.Campus;
import tech.cloverfield.kdgplanner.business.domain.Classroom;
import tech.cloverfield.kdgplanner.business.domain.Lesson;
import tech.cloverfield.kdgplanner.business.domain.Student;

public class ClassroomController {

    private Campus activeCampus;
    private Student activeStudent;
    private IClassroomManager classroomManager;
    private Boolean roomsLoaded;

    private Context context;

    public ClassroomController(Context context) {
        this.context = context;
        activeCampus = Campus.GROENPLAATS;
        classroomManager = new ClassroomManager(context);
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

    public void updateClassrooms() {
        classroomManager.updateClassrooms(activeCampus);
    }

    public Collection<Classroom> getAvailableClassrooms(Date time){
        return classroomManager.getAvailableClassrooms(activeCampus, time);
    }

    public Collection<Lesson> getLessonsByRoom(Classroom room){
        return classroomManager.getLessonsByRoom(activeCampus, room);
    }

    public boolean hasRoomsLoaded() {
        if (roomsLoaded == null && classroomManager.getLoadPercentage() >= 100) {
            roomsLoaded = true;
        }
        return roomsLoaded;
    }

    public int getLoadedPercentage() {
        return classroomManager.getLoadPercentage();
    }
}
