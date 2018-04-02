package tech.cloverfield.kdgplanner;

import android.app.DialogFragment;
import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOError;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    String klas = split[3].trim().substring(split[3].indexOf(" "), split[3].indexOf(" ", split[3].indexOf(" ") + 1));


                    //See if classroom already exists
                    if (classrooms.isEmpty()) {
                        classrooms.add(new Classroom(klas, datum));
                    } else {
                        for (Classroom classroom : classrooms) {
                            if (classroom.getClassNumber().equals(klas)) {
                                //Voeg start + eind toe aan hashmap
                            }
                        }
                        //Anders nieuwe klas object


                    }
                }

                //classrooms.add(new Classroom())
            }
        } catch (IOException | ParseException e)

        {
            e.printStackTrace();
        }
    }
}
