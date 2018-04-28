package tech.cloverfield.kdgplanner.Main;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import tech.cloverfield.kdgplanner.R;
import tech.cloverfield.kdgplanner.Reservation.ReservationActivity;


//TODO: Option to sync/obtain CSV from interwebs


public class MainActivity extends AppCompatActivity {

    public Button button;
    private CSVReader reader;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btnSelectHour);
        button.setOnClickListener(btnOnClickListener);

        FloatingActionButton fab = findViewById(R.id.fabReservation);
        fab.setOnClickListener(fabOnClick);

        reader = new CSVReader();
        reader.readCSV(this);

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
            if (reader.hasLoaded()) {
                DialogFragment dialog = new TimePickerFragment();
                dialog.show(getFragmentManager(), "timePicker");
            } else {
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
}
