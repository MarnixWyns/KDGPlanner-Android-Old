package tech.cloverfield.kdgplanner.business.domain;

public class Lesson {
    private static long maxLessonId = 1;

    private long lesson_id;
    private String lessonName;

    public Lesson(String lessonName) {
        lesson_id = getNewLessonId();
        this.lessonName = lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public long getLesson_id() {
        return lesson_id;
    }

    public String getLessonName() {
        return lessonName;
    }

    public static long getMaxLessonId(){
        return maxLessonId;
    }

    private static long getNewLessonId(){
        return maxLessonId++;
    }
}
