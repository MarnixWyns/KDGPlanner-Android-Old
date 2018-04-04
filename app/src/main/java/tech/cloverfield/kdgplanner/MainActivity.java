package tech.cloverfield.kdgplanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {


    private String[] forbiddenRooms = {""};
    private ArrayList<Classroom> classrooms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readCSV();
    }

    public void onTimeSelect(View v) {
        System.out.println("Button pressed");

    }


    private void readCSV() {
        try (Scanner scanner = new Scanner(getAssets().open("timetable.csv")).useDelimiter(",")) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();

                //Filter bad lines
                if (!line.equals("") && !line.equals(",,,") && !line.contains("Startdatum")) {

                    String[] split = line.split(",");
                    ArrayList<String> strings = new ArrayList<>();
                    strings.addAll(Arrays.asList(split));
                    //System.out.println(strings);

                    DateFormat dpd = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat dpu = new SimpleDateFormat("kk:mm");

                    Date datum = dpd.parse(split[0]);

                    Date startUur = dpu.parse(split[1]);

                    Date eindUur = dpu.parse(split[2]);
                    //System.out.printf("Datum: %s\tStart: %s\tEind: %s\n", datum, startUur,eindUur);

                    //Filter klas
                    String klas = split[3].trim().substring(split[3].indexOf(" ")+1, split[3].indexOf(" ", split[3].indexOf(" ") + 1));

                    //Als arraylist nog niet bestaat, maak het aan
                    classrooms = new ArrayList<>();

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
                        Classroom classroom = new Classroom(klas, datum);
                        classroom.addToStartTimes(datum, startUur);
                        classroom.addToEndTimes(datum, eindUur);
                        classrooms.add(classroom);
                    }

                    /* OLD METHOD
                    //See if classroom already exists
                    if (classrooms.isEmpty()) {
                        classrooms.add(new Classroom(klas, datum));
                    } else {
                        for (Classroom classroom : classrooms) {
                            if (classroom.getClassNumber().equals(klas)) {
                                //Voeg start + eind toe aan hashmap
                                classroom.addToStartTimes(datum, startUur);
                                classroom.addToEndTimes(datum, eindUur);
                            }
                        }
                        //Anders nieuwe klas object

                    }
                    */
                }

                //classrooms.add(new Classroom())
            }
            System.out.println("Done");
        } catch (IOException | ParseException e)

        {
            e.printStackTrace();
        }
    }
}
