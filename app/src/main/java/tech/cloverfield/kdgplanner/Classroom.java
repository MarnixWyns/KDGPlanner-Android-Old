package tech.cloverfield.kdgplanner;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Classroom {
    private String classNumber;

    private HashMap<String, ArrayList<String>> startTimes;
    private HashMap<String, ArrayList<String>> endTimes;
    private String availability = "";

    public Classroom(String classNumber) {
        this.classNumber = classNumber;

        startTimes = new HashMap<>();
        endTimes = new HashMap<>();
    }

    public String getAvailability() { return availability; }

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
        assert time != null;
        ArrayList<String> dates = this.getStartTimes(date);
        for (String startTimeValue : dates) {
            Date startTime = DateFormatter.toDate(startTimeValue, DateType.TIME);
            Date endTime = DateFormatter.toDate(this.getEndTimes(date).get(i), DateType.TIME);
            if (dates.size() == 1) {
                if (time.before(startTime)) {
                    availability = "Tot " + DateFormatter.fix0(startTime.getHours()) + ":" + DateFormatter.fix0(startTime.getMinutes()) + " (" + (TimeUnit.MILLISECONDS.toMinutes(startTime.getTime() - time.getTime())) + " minuten)";
                    return true;
                } else if (time.after(endTime)) {
                    availability = "Voor de rest van de dag";
                    return true;
                }
            } else if (i == 0) {
                if (time.before(startTime)) {
                    availability = "Tot " + DateFormatter.fix0(startTime.getHours()) + ":" + DateFormatter.fix0(startTime.getMinutes()) + " (" + (TimeUnit.MILLISECONDS.toMinutes(startTime.getTime() - time.getTime())) + " minuten)";
                    return true;
                }
            } else if (i == this.getStartTimes(date).size() - 1) {
                Date endTimePrevious = DateFormatter.toDate(this.getEndTimes(date).get(i - 1), DateType.TIME);
                if ((time.before(startTime) && time.after(endTimePrevious))) {
                    availability = "Tot " + DateFormatter.fix0(startTime.getHours()) + ":" + DateFormatter.fix0(startTime.getMinutes()) + " (" + (TimeUnit.MILLISECONDS.toMinutes(startTime.getTime() - time.getTime())) + " minuten)";
                    return true;
                } else if (time.after(endTime)) {
                    availability = "Voor de rest van de dag";
                    return true;
                }
            } else {
                Date endTimePrevious = DateFormatter.toDate(this.getEndTimes(date).get(i - 1), DateType.TIME);
                if (time.after(endTimePrevious) && time.before(startTime)) {
                    availability = "Tot " + DateFormatter.fix0(startTime.getHours()) + ":" + DateFormatter.fix0(startTime.getMinutes()) + " (" + (TimeUnit.MILLISECONDS.toMinutes(startTime.getTime() - time.getTime())) + " minuten)";
                    return true;
                }
            }
            i++;

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
