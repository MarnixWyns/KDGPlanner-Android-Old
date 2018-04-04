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

    public ArrayList<Date> getStartTimes(Date date) {
        if (startTimes == null) startTimes = new HashMap<>();
        if (startTimes.get(date) == null) return new ArrayList<>();
        return startTimes.get(date);
    }

    public ArrayList<Date> getEndTimes(Date date) {
        if (endTimes == null) endTimes = new HashMap<>();
        if (endTimes.get(date) == null) return new ArrayList<>();
        return endTimes.get(date);
    }

    public void addToStartTimes(Date date, Date time) {
        ArrayList<Date> tempTimes = getStartTimes(date);
        tempTimes.add(time);
        startTimes.put(date, tempTimes);
    }

    public void addToEndTimes(Date date, Date time) {
        ArrayList<Date> tempTimes = getEndTimes(date);
        tempTimes.add(time);
        endTimes.put(date, tempTimes);
    }


    public boolean isAvailable(Date date, Date time) {
        int i = 0;

        for (Date startTime : this.getStartTimes(date)) {
            if (i == this.getStartTimes(date).size() - 1) {
                Date endTime = this.getEndTimes(date).get(i);
                if (time.after(endTime)) {
                    i++;
                    return true;
                }
            } else if (i == 0) {
                if (time.before(startTime)) {
                    i++;
                    return true;
                }
            } else {
                Date endTime = this.getEndTimes(date).get(i - 1);
                if (time.after(endTime) && time.before(startTime)) {
                    i++;
                    return true;
                }
            }

        }
        return false;
    }

    public Date availableUntil(Date curDate) {
        ArrayList<Date> lessonsStart = startTimes.get(curDate);
        ArrayList<Date> lessonEnds = endTimes.get(curDate);

        int i = 0;
        for (Date date : lessonsStart) {
            if (curDate.before(lessonsStart.get(0)) || (curDate.before(lessonsStart.get(i)) && curDate.after(lessonEnds.get(i - 1)))) {
                return lessonsStart.get(i);
            }
            i++;
        }

        return null;
    }
}
