package tech.cloverfield.kdgplanner.Main;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import tech.cloverfield.kdgplanner.R;
import tech.cloverfield.kdgplanner.Reservation.ReservationActivity;
import tech.cloverfield.kdgplanner.WebRequest.Manager;


//TODO: Option to sync/obtain CSV from interwebs


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public Button button;
    private CSVReader reader;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        while (!RequestPermissions.isGranted()) {
            if (!RequestPermissions.getRequestInProgress())
                RequestPermissions.requestPermissions(this);
        }

        // Get items from resources string array and put them in spinner values.
        spinner = (Spinner) findViewById(R.id.spinnerMain);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.campussen, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Manager manager = new Manager(this);
        manager.sendRequest();

        button = findViewById(R.id.btnSelectHour);
        button.setOnClickListener(btnOnClickListener);

        FloatingActionButton fab = findViewById(R.id.fabReservation);
        fab.setOnClickListener(fabOnClick);

        reader = new CSVReader();
        reader.readCSV(this, spinner.getSelectedItem().toString());

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
            try {
                DialogFragment dialog = new TimePickerFragment();
                dialog.show(getFragmentManager(), "timePicker");

            } catch (Exception e) {

                Snackbar.make(findViewById(R.id.coordinator), "De database is nog niet geladen... even geduld. (" + reader.loadPercentage() + "%)", Snackbar.LENGTH_SHORT).show();
            }
        }


    };


    public void displayAvailable(ArrayList<Classroom> classrooms) {
        ListView classroomList = findViewById(R.id.lvClassrooms);
        ArrayList<String> adapterList = new ArrayList<>();
        ArrayList<String> sort = new ArrayList<>();
        HashMap<String, Classroom> ordered = new HashMap<>();

        if (classrooms.size() == 0) {
            adapterList.add("Vandaag geen lesactiviteiten");
        } else {
            for (Classroom classroom : classrooms) {
                String id = classroom.getAvailability() + ";" + classroom.getClassNumber();
                sort.add(id);
                ordered.put(id, classroom);
            }

            Collections.sort(sort, Collections.<String>reverseOrder());

            for (String id : sort) {
                Classroom classroom = ordered.get(id);
                adapterList.add("Lokaal: " + classroom.getClassNumber() + "\n" + "Beschikbaarheid: " + classroom.getAvailability());
            }
        }


        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adapterList);
        classroomList.setAdapter(adapter);
    }


    public CSVReader getReader() {
        return reader;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == RequestPermissions.getRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RequestPermissions.setGranted(true);
            } else {

            }
            RequestPermissions.setRequestInProgress(false);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Als het default text is probeer dan niet om CSV op te halen
        if (!button.getText().equals("Kies uur")) {
            Toast.makeText(this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Auto generated stub
    }
}
