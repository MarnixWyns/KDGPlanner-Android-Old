package tech.cloverfield.kdgplanner.business.domain.classrooms;

import java.util.Collection;
import java.util.Date;

import tech.cloverfield.kdgplanner.business.domain.Campus;
import tech.cloverfield.kdgplanner.business.domain.Classroom;
import tech.cloverfield.kdgplanner.business.domain.Lesson;

public interface IClassroomManager {
    void updateClassrooms(Campus activeCampus);
    Collection<Classroom> getAvailableClassrooms(Campus campus, Date date);
    Collection<Lesson> getLessonsByRoom(Campus campus, Classroom room);
    int getLoadPercentage();
    void syncCache();
}
