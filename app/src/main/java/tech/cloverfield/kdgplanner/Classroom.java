package tech.cloverfield.kdgplanner;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Classroom {
    private String classNumber;

    private HashMap<String, ArrayList<String>> startTimes;
    private HashMap<String, ArrayList<String>> endTimes;

    public Classroom(String classNumber) {
        this.classNumber = classNumber;

        startTimes = new HashMap<>();
        endTimes = new HashMap<>();
    }

    public String getClassNumber() {
        return classNumber;
    }

    public ArrayList<String> getStartTimes(String date) {
        if (startTimes == null) startTimes = new HashMap<>();
        if (startTimes.get(date) == null) return new ArrayList<>();
        return startTimes.get(date);
    }

    public ArrayList<String> getEndTimes(String date) {
        if (endTimes == null) endTimes = new HashMap<>();
        if (endTimes.get(date) == null) return new ArrayList<>();
        return endTimes.get(date);
    }

    public void addToStartTimes(String date, String time) {
        ArrayList<String> tempTimes = getStartTimes(date);
        tempTimes.add(time);
        startTimes.put(date, tempTimes);
    }

    public void addToEndTimes(String date, String time) {
        ArrayList<String> tempTimes = getEndTimes(date);
        tempTimes.add(time);
        endTimes.put(date, tempTimes);
    }


    public boolean isAvailable(String date, String timeValue) {
        int i = 0;

        Date time = DateFormatter.toDate(timeValue, DateType.TIME);
        for (String startTimeValue : this.getStartTimes(date)) {
            Date startTime = DateFormatter.toDate(startTimeValue, DateType.DATE);
            if (i == 0) {
                Date endTime = DateFormatter.toDate(this.getEndTimes(date).get(i), DateType.TIME);
                if (time.after(endTime)) {
                    i++;
                    return true;
                }
            } else if (i == this.getStartTimes(date).size() - 1) {
                if (time.before(startTime)) {
                    i++;
                    return true;
                }
            } else {
                Date endTime = DateFormatter.toDate(this.getEndTimes(date).get(i - 1), DateType.DATE);
                if (time.after(endTime) && time.before(startTime)) {
                    i++;
                    return true;
                }
            }

        }
        return false;
    }

    /*public String availableUntil(String curDate) {
        ArrayList<String> lessonsStart = startTimes.get(curDate);
        ArrayList<String> lessonEnds = endTimes.get(curDate);

        int i = 0;
        for (String date : lessonsStart) {
            if (curDate.before(lessonsStart.get(0)) || (curDate.before(lessonsStart.get(i)) && curDate.after(lessonEnds.get(i - 1)))) {
                return lessonsStart.get(i);
            }
            i++;
        }

        return null;
    }*/
}
