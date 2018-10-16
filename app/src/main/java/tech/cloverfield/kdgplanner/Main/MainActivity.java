package tech.cloverfield.kdgplanner.Main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import tech.cloverfield.kdgplanner.DateFormatter;
import tech.cloverfield.kdgplanner.Objects.Classroom;
import tech.cloverfield.kdgplanner.Objects.DateType;
import tech.cloverfield.kdgplanner.R;
import tech.cloverfield.kdgplanner.Reservation.ReservationActivity;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public Button button;
    public SwipeRefreshLayout swipeRefreshLayout;

    private Spinner spinner;
    private Lokalen_DB lokalen_db;
    private String selectedCampus = "Groenplaats";
    private HashMap<String, String> campusTranslator = new HashMap<>();

    private int PERMISSION_KEY = 64556;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (requestPermissions()) {
            permissionGranted();
        }
    }

    public void permissionGranted() {
        campusTranslator.put("groenplaats", "GR");
        campusTranslator.put("pothoek", "PH");
        campusTranslator.put("stadswaag", "SW");

        spinner = findViewById(R.id.spinnerMain);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.campussen, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        button = findViewById(R.id.btnSelectHour);
        button.setOnClickListener(btnOnClickListener);

        FloatingActionButton fab = findViewById(R.id.fabReservation);
        fab.setOnClickListener(fabOnClick);

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        lokalen_db = new Lokalen_DB(this);
        //Refreshing ON
        lokalen_db.update(convertCampus(selectedCampus));
    }

    public boolean requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, PERMISSION_KEY);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_KEY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted();
            } else {
                displayWarning(getString(R.string.error_perm_required));
                System.exit(0);
            }
        }
    }

    public String convertCampus(String campus) {
        return campusTranslator.get(campus.toLowerCase());
    }

    public Lokalen_DB getLokalen_db() {
        return lokalen_db;
    }

    public String getSelectedCampus() {
        return selectedCampus;
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
            if (!lokalen_db.isLoaded()) {
                Snackbar.make(findViewById(R.id.coordinator), String.format(getString(R.string.db_init_load), lokalen_db.getLoadedPercentage()), Snackbar.LENGTH_SHORT).show();
            } else if (!lokalen_db.isLoaded() && !lokalen_db.hasInternet()) {
                    Snackbar.make(findViewById(R.id.coordinator), getString(R.string.server_connect_error), Snackbar.LENGTH_SHORT).show();
            } else {
                DialogFragment dialog = new TimePickerFragment();
                dialog.show(getFragmentManager(), "timePicker");
            }
        }


    };

    public void displayAvailable(ArrayList<Classroom> classrooms) {
        ListView classroomList = findViewById(R.id.lvClassrooms);
        ArrayList<String> adapterList = new ArrayList<>();

        if (classrooms == null) {
            adapterList.add(getString(R.string.init_select_hour));
        } else if (classrooms.size() == 0) {
            adapterList.add(getString(R.string.no_lessons_today));
        } else {
                for (Classroom classroom : classrooms) {
                    String classroomDisplay = "";

                    classroomDisplay += String.format(getString(R.string.class_availability), classroom.getIdentifier(), classroom.getDuration());

                    adapterList.add(classroomDisplay);
                }
        }


        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adapterList);
        classroomList.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCampus = spinner.getSelectedItem().toString();
        Calendar calendar = Calendar.getInstance();
        String buttonText = (String) button.getText();

        if (buttonText.contains(":")) {
            Date date = DateFormatter.toDate(String.format("%s:00.000 %04d-%02d-%02d", buttonText, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)), DateType.FULL_DATE_US);
            displayAvailable(lokalen_db.getRooms(convertCampus(selectedCampus), date));
        } else {
            displayAvailable(null);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Auto generated stub
    }

    public void displayWarning(String warning) {
        Toast toast = Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_SHORT);
        toast.show();
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(true);
            //Refreshing ON
            lokalen_db.update(convertCampus(selectedCampus));
            displayWarning(getString(R.string.info_refreshing_db));
        }
    };
}
