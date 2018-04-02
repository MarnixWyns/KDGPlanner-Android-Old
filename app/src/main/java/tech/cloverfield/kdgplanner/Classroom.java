package tech.cloverfield.kdgplanner;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Classroom {
    private String classNumber;

    private HashMap<Date, ArrayList<Date>> startTimes;
    private HashMap<Date, ArrayList<Date>> endTimes;

    private Date date;


    public Classroom(String classNumber, Date date) {
        this.classNumber = classNumber;
        this.date = date;

        startTimes = new HashMap<>();
        endTimes = new HashMap<>();
    }

    public String getClassNumber() {
        return classNumber;
    }

    public HashMap<Date, ArrayList<Date>> getStartTimes() {
        return startTimes;
    }

    public HashMap<Date, ArrayList<Date>> getEndTimes() {
        return endTimes;
    }



    public boolean isAvailable(){



        return false;
    }

    public Date availableUntil(Date curDate){
        ArrayList<Date> lessonsStart = startTimes.get(curDate);
        ArrayList<Date> lessonEnds = endTimes.get(curDate);

        int i = 0;
        for (Date date : lessonsStart) {
            if (curDate.before(lessonsStart.get(0)) || (curDate.before(lessonsStart.get(i)) && curDate.after(lessonEnds.get(i-1)))){
                return lessonsStart.get(i);
            }
            i++;
        }

        return null;
    }
}
