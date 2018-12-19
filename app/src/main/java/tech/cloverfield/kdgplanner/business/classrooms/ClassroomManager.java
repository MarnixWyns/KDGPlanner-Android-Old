package tech.cloverfield.kdgplanner.business.classrooms;

import android.content.Context;

import java.util.Collection;
import java.util.Date;

import tech.cloverfield.kdgplanner.business.domain.Campus;
import tech.cloverfield.kdgplanner.business.domain.Classroom;
import tech.cloverfield.kdgplanner.business.domain.Lesson;
import tech.cloverfield.kdgplanner.persistence.classrooms.ClassroomRepoSQLite;
import tech.cloverfield.kdgplanner.persistence.classrooms.IClassroomRepo;

public class ClassroomManager implements IClassroomManager {

    private final Context context;
    private IClassroomRepo repo;

    public ClassroomManager(Context context) {
        this.context = context;
        repo = new ClassroomRepoSQLite(context);
    }

    @Override
    public void updateClassrooms(Campus activeCampus) {
        repo.updateClassrooms(activeCampus);
    }

    @Override
    public Collection<Classroom> getAvailableClassrooms(Campus campus, Date date) {
        return repo.getAvailableClassrooms(campus, date);
    }

    @Override
    public Collection<Lesson> getLessonsByRoom(Campus campus, Classroom room) {
        return repo.getLessonsByRoom(campus, room);
    }

    @Override
    public int getLoadPercentage() {
        return repo.getLoadPercentage();
    }

}
