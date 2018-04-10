package tech.cloverfield.kdgplanner;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {


    private String[] forbiddenRooms = {"-107", "001", "004", "008", "114", "115", "116", "117", "118", "213", "214", "217", "306", "311", "400", "411", "501", "510"};

    private ArrayList<Classroom> classrooms;
    public Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btnSelectHour);
        button.setOnClickListener(btnOnClickListener);
        readCSV();
    }

    public ArrayList<Classroom> getClassrooms() {
        return classrooms;
    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogFragment dialog = new TimePickerFragment();
            dialog.show(getFragmentManager(), "timePicker");
        }
    };

    private void readCSV() {
        try (Scanner scanner = new Scanner(getAssets().open("timetable.csv")).useDelimiter(",")) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();

                //Filter bad lines
                if (!line.equals("") && !line.equals(",,,") && !line.contains("Startdatum")) {

                    String[] split = line.split(",");

                    String datum = split[0];
                    String startUur = split[1];
                    String eindUur = split[2];

                    //Filter klas
                    String klas = split[3].trim().substring(split[3].indexOf(" ") + 1, split[3].indexOf(" ", split[3].indexOf(" ") + 1));

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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void displayAvailable(ArrayList<Classroom> classrooms) {
        ListView classroomList = findViewById(R.id.lvClassrooms);
        ArrayList<String> adapterList = new ArrayList<>();
        for (Classroom classroom : classrooms) {
            adapterList.add(classroom.getClassNumber());
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, adapterList);
        classroomList.setAdapter(adapter);
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
}
