package tech.cloverfield.kdgplanner.Main;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import tech.cloverfield.kdgplanner.DateFormatter;
import tech.cloverfield.kdgplanner.R;
import tech.cloverfield.kdgplanner.Reservation.ReservationActivity;


//TODO: Option to sync/obtain CSV from interwebs


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public Button button;
    public SwipeRefreshLayout swipeRefreshLayout;

    private Spinner spinner;
    private Lokalen_DB lokalen_db;
    private String selectedCammpus = "Groenplaats";
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
        // Populate campusses
        campusTranslator.put("groenplaats", "gr");
        campusTranslator.put("pothoek", "ph");
        campusTranslator.put("stadswaag", "sw");

        // Get items from resources string array and put them in spinner values.
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
        lokalen_db.setForced(false);
        lokalen_db.setRefreshing(false);
        lokalen_db.update(convertCampus(selectedCammpus));
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
                Toast.makeText(this, "Permission required.", Toast.LENGTH_SHORT).show();
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


    public void displayAvailable(ArrayList<Classroom> classrooms, boolean includeClassroom) {
        ListView classroomList = findViewById(R.id.lvClassrooms);
        ArrayList<String> adapterList = new ArrayList<>();

        if (classrooms.size() == 0) {
            adapterList.add("Vandaag geen lesactiviteiten");
        } else {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date currentTime = simpleDateFormat.parse("1970-01-01");
                currentTime.setHours(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                currentTime.setMinutes(Calendar.getInstance().get(Calendar.MINUTE));

                for (Classroom classroom : classrooms) {
                    Date classStartTime = DateFormatter.toDate(classroom.getStartHour(), DateType.TIME);

                    String buttonText = (String) button.getText();
                    String classroomDisplay = classroom.getClassNumber();
                    if (includeClassroom)
                        classroomDisplay += " (" + convertCampus(selectedCammpus) + ")";

                    if (buttonText.contains(":")) {
                        currentTime = DateFormatter.toDate(buttonText, DateType.TIME);
                    }

                    long diff = classStartTime.getTime() - currentTime.getTime();

                    if (!((diff / (60 * 60 * 1000) % 24) == 0 && (diff / (60 * 1000) % 60) < 20)) {
                        adapterList.add("Lokaal: " + classroomDisplay + "\nBeschikbaarheid: " + (diff / (60 * 60 * 1000) % 24) + " uur en " + (diff / (60 * 1000) % 60) + " minuten (tot: " + DateFormatter.fixTimeString(classStartTime.getHours() + ":" + classStartTime.getMinutes()) + "u)");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adapterList);
        classroomList.setAdapter(adapter);
    }


    /*public CSVReader getReader() {
        return reader;
    }*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCammpus = spinner.getSelectedItem().toString();
        Calendar calendar = Calendar.getInstance();
        String buttonText = (String) button.getText();
        String time = DateFormatter.fixTimeString(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

        if (buttonText.contains(":")) time = buttonText;
        displayAvailable(lokalen_db.getRooms(convertCampus(selectedCammpus), time), false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Auto generated stub
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (lokalen_db.hasInternet()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Please stand by,\nrefreshing database", Toast.LENGTH_SHORT);
                toast.show();

                lokalen_db.setForced(true);
                lokalen_db.setRefreshing(true);
                lokalen_db.update(convertCampus(selectedCammpus));
                Calendar calendar = Calendar.getInstance();
                String buttonText = (String) button.getText();
                String time = DateFormatter.fixTimeString(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

                if (buttonText.contains(":")) time = buttonText;
                displayAvailable(lokalen_db.getRooms(convertCampus(selectedCammpus), time), false);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Error while connecting to the server,\nplease check your connection or try again later", Toast.LENGTH_SHORT);
                toast.show();

                swipeRefreshLayout.setRefreshing(false);
            }
        }
    };
}
