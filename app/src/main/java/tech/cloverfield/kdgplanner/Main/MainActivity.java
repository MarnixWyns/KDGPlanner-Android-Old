package tech.cloverfield.kdgplanner.Main;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import tech.cloverfield.kdgplanner.DateFormatter;
import tech.cloverfield.kdgplanner.R;
import tech.cloverfield.kdgplanner.Reservation.ReservationActivity;
import tech.cloverfield.kdgplanner.WebRequest.Manager;


//TODO: Option to sync/obtain CSV from interwebs


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public Button button;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Spinner spinner;
    private Lokalen_DB lokalen_db;
    private String selectedCammpus = "Groenplaats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        while (!RequestPermissions.isGranted()) {
            if (!RequestPermissions.getRequestInProgress())
                RequestPermissions.requestPermissions(this);
        }

        // Get items from resources string array and put them in spinner values.
        spinner = findViewById(R.id.spinnerMain);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.campussen, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Manager manager = new Manager(this);
        //manager.sendRequest();

        button = findViewById(R.id.btnSelectHour);
        button.setOnClickListener(btnOnClickListener);

        FloatingActionButton fab = findViewById(R.id.fabReservation);
        fab.setOnClickListener(fabOnClick);

        lokalen_db = new Lokalen_DB(this);
        //lokalen_db.drop();
        //lokalen_db.create();
        lokalen_db.update();
        //reader.readCSV(this, spinner.getSelectedItem().toString());

        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        //Ni zeker da da hier moet staan maar anders wordt compiler boos
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        //TODO: U method vo database te update

                        Toast toast = Toast.makeText(getApplicationContext(), "Please stand by,\nrefreshing database", Toast.LENGTH_SHORT);
                        toast.show();


                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

    }

    public Lokalen_DB getLokalen_db() {
        return lokalen_db;
    }

    public String getSelectedCampus() {
        return selectedCammpus;
    }

    private View.OnClickListener fabOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent reservation = new Intent(MainActivity.this, ReservationActivity.class);
            MainActivity.this.startActivity(reservation);

        }
    };

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!lokalen_db.hasInternet()) {
                Snackbar.make(findViewById(R.id.coordinator), "De database kon niet worden geladen. Controleer uw internet verbinding of probeer het later opnieuw.", Snackbar.LENGTH_SHORT).show();
            } else if (!lokalen_db.isLoaded()) {
                Snackbar.make(findViewById(R.id.coordinator), "De database is nog niet geladen... even geduld.", Snackbar.LENGTH_SHORT).show();
            } else {
                DialogFragment dialog = new TimePickerFragment();
                dialog.show(getFragmentManager(), "timePicker");
            }
        }


    };


    public void displayAvailable(ArrayList<Classroom> classrooms) {
        ListView classroomList = findViewById(R.id.lvClassrooms);
        ArrayList<String> adapterList = new ArrayList<>();
        //ArrayList<String> sort = new ArrayList<>();
        //HashMap<String, Classroom> ordered = new HashMap<>();

        if (classrooms.size() == 0) {
            adapterList.add("Vandaag geen lesactiviteiten");
        } else {
             for (Classroom classroom : classrooms) {
                 Date classStartTime = DateFormatter.toDate(classroom.getStartHour(), DateType.TIME);
                 Date currentTime = Calendar.getInstance().getTime();
                 long diff = classStartTime.getTime() - currentTime.getTime();

                 adapterList.add("Lokaal: " + classroom.getClassNumber() + "\nBeschikbaarheid: " + (diff / (60*60*1000) % 24) + " uren en " + (diff / (60*1000) % 60) + " minuten (tot: " + classStartTime.getHours() + ":" + classStartTime.getMinutes() + ")");
                /*String id = classroom.getAvailability() + ";" + classroom.getClassNumber();
                sort.add(id);
                ordered.put(id, classroom);*/
            }

            /*Collections.sort(sort, Collections.<String>reverseOrder());

            for (String id : sort) {
                Classroom classroom = ordered.get(id);
                adapterList.add("Lokaal: " + classroom.getClassNumber() + "\n" + "Beschikbaarheid: " + classroom.getAvailability());
            }*/
        }


        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adapterList);
        classroomList.setAdapter(adapter);
    }


    /*public CSVReader getReader() {
        return reader;
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == RequestPermissions.getRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RequestPermissions.setGranted(true);
                RequestPermissions.setRequestInProgress(false);
            } else {
                RequestPermissions.setGranted(false);
                RequestPermissions.setRequestInProgress(true);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCammpus = spinner.getSelectedItem().toString();
        //Als het default text is probeer dan niet om CSV op te halen
        /*if (!button.getText().equals("Kies uur")) {
            Toast.makeText(this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Auto generated stub
    }
}
