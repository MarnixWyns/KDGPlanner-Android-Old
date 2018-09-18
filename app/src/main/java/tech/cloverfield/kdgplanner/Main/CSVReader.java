package tech.cloverfield.kdgplanner.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import tech.cloverfield.kdgplanner.DateFormatter;

public class CSVReader {

    private int progress, total = 0;
    private boolean loading = true;
    private ArrayList<Classroom> classrooms;


    //Lokalen die niet gebruikt kunnen worden
    private String[] forbiddenRooms = {"-107", "001", "004", "008", "114", "115", "116", "117", "118", "213", "214", "217", "306", "311", "400", "411", "501", "510"};

    //Nodig om van Nederlandse CSV maanden om te zetten naar engelse maanden
    private HashMap<String, String> conversionMap = new HashMap<String, String>() {{
        put("januari", "january");
        put("februari", "february");
        put("maart", "march");
        put("april", "april");
        put("mei", "may");
        put("juni", "june");
        put("juli", "july");
        put("augustus", "august");
        put("september", "september");
        put("oktober", "october");
        put("november", "november");
        put("december", "december");
        put("maandag", "monday");
        put("dinsdag", "tuesday");
        put("woensdag", "wednesday");
        put("donderdag", "thursday");
        put("vrijdag", "friday");
        put("zaterdag", "saturday");
        put("zondag", "sunday");
    }};



    public CSVReader() {
    }

    @SuppressLint("StaticFieldLeak")
    //Om te voorkomen dat android programma vasthangt moet de CSV async ingelezen worden
    public void readCSV(Context context, final String campus) {
        final Context c = context;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                loading = true;
                try {

                    Scanner scanner = new Scanner(c.getAssets().open("timetable_" + campus + ".csv")).useDelimiter(",");
                    while (scanner.hasNextLine()) {
                        total++;
                        scanner.nextLine();
                    }

                    scanner = new Scanner(c.getAssets().open("timetable_Groenplaats.csv")).useDelimiter(",");

                    boolean hasIndexes = false;

                    int startDatumIndex = 0;
                    int eindDatumIndex = 0;
                    int lokaalIndex = 0;

                    while (scanner.hasNext()) {
                        progress++;
                        String rawLine = scanner.nextLine();

                        //Filter bad lines
                        if (!rawLine.equals("") && !rawLine.equals(",,,") && !rawLine.contains("Start") && hasIndexes) {
                            String line = DateFormatter.translate(rawLine, conversionMap);
                            String[] split = line.split(",");

                            Date startTijdDate = DateFormatter.toDate(split[startDatumIndex], DateType.FULL_DATE);
                            Date eindTijdDate = DateFormatter.toDate(split[eindDatumIndex], DateType.FULL_DATE);
                            Calendar startTijd = Calendar.getInstance();
                            startTijd.setTime(startTijdDate);
                            Calendar eindTijd = Calendar.getInstance();
                            eindTijd.setTime(eindTijdDate);
                            String datum = DateFormatter.fixDateString(startTijd.get(Calendar.YEAR) + "-" + (startTijd.get(Calendar.MONTH) + 1) + "-" + startTijd.get(Calendar.DAY_OF_MONTH));
                            String startUur = DateFormatter.fixTimeString(startTijd.get(Calendar.HOUR_OF_DAY) + ":" + startTijd.get(Calendar.MINUTE));
                            String eindUur = DateFormatter.fixTimeString(eindTijd.get(Calendar.HOUR_OF_DAY) + ":" + eindTijd.get(Calendar.MINUTE));

                            //Filter klas
                            String klas = split[lokaalIndex].trim().substring(split[lokaalIndex].indexOf(" ") + 1, split[lokaalIndex].indexOf(" ", split[lokaalIndex].indexOf(" ") + 1));

                            //Als klas forbidden is
                            if (!checkForbidden(klas)) {
                                //Als arraylist nog niet bestaat, maak het aan
                                if (classrooms == null) classrooms = new ArrayList<>();

                                //Klas bestaat (nog) niet
                                boolean added = false;
                                for (Classroom classroom : classrooms) {
                                    if (classroom.getClassNumber().equals(klas)) {
                                        //Klas bestaat
                                        //Voeg start + eind toe aan hashmap
                                        classroom.addToStartTimes(datum, startUur);
                                        classroom.addToEndTimes(datum, eindUur);
                                        //Verander boolean naar true omdat klas bestaat
                                        added = true;
                                    }
                                }
                                //Als boolean nog false is bestaat klas nog niet, maak nieuwe aan
                                if (!added) {
                                    Classroom classroom = new Classroom(klas);
                                    classroom.addToStartTimes(datum, startUur);
                                    classroom.addToEndTimes(datum, eindUur);
                                    classrooms.add(classroom);
                                }
                            }
                        } else {
                            String[] split = rawLine.split(",");
                            int index = 0;
                            for (String s : split) {
                                switch (s) {
                                    case "Start":
                                        startDatumIndex = index;
                                        break;
                                    case "Einde":
                                        eindDatumIndex = index;
                                        break;
                                    case "Lokaal":
                                        lokaalIndex = index;
                                        break;
                                }
                                index++;
                            }
                            hasIndexes = true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void v){
                loading = false;
            }
        }.execute();
    }

    private boolean checkForbidden(String s) {
        boolean bad = false;

        for (String forbiddenRoom : forbiddenRooms) {
            if (s.contains(forbiddenRoom)) {
                bad = true;
            }
        }

        return bad;
    }

    public boolean hasLoaded(){
        return !loading;
    }

    public int loadPercentage(){
        if (total == 0) {
            return 100;
        } else return progress * 100 / total;
    }

    public ArrayList<Classroom> getClassrooms() {
        return classrooms;
    }
}
