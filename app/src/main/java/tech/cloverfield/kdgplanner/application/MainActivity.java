package tech.cloverfield.kdgplanner.application;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import tech.cloverfield.kdgplanner.R;
import tech.cloverfield.kdgplanner.business.domain.Campus;
import tech.cloverfield.kdgplanner.business.domain.Classroom;
import tech.cloverfield.kdgplanner.business.domain.DateType;
import tech.cloverfield.kdgplanner.controller.ClassroomController;
import tech.cloverfield.kdgplanner.controller.SettingsController;
import tech.cloverfield.kdgplanner.foundation.DateFormatter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public Button button;
    public SwipeRefreshLayout swipeRefreshLayout;

    private Spinner spinner;

    private ClassroomController roomController;
    private SettingsController settingsController;

    private int PERMISSION_KEY = 64556;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomController = new ClassroomController(this);
        settingsController = new SettingsController(this);
        setContentView(R.layout.activity_main);
        if (requestPermissions()) {
            permissionGranted();
        }
    }

    public void permissionGranted() {
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

        roomController.updateClassrooms();
    }

    public boolean requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, PERMISSION_KEY);
                return false;
            }
         }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_KEY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted();
            } else {
                displayMessageToast(getString(R.string.error_perm_required));
                System.exit(0);
            }
        }
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
            if (!roomController.hasRoomsLoaded()) {
                displayMessageSnackbar(String.format("%s %s%%", getString(R.string.db_init_load), roomController.getLoadedPercentage()), Snackbar.LENGTH_SHORT);
            } else if (!roomController.hasRoomsLoaded() && settingsController.HasInternet()) {
                displayMessageSnackbar(getString(R.string.error_connection), Snackbar.LENGTH_SHORT);
            } else {
                DialogFragment dialog = new TimePickerFragment();
                dialog.show(getFragmentManager(), "timePicker");
            }
        }


    };

    public void displayAvailable(Collection<Classroom> classrooms) {
        ListView classroomList = findViewById(R.id.lvClassrooms);
        List<String> adapterList = new ArrayList<>();

        if (classrooms == null) {
            adapterList.add(getString(R.string.init_select_hour));
        } else if (classrooms.size() == 0) {
            //TODO distinction between no lessons and no free classrooms
            adapterList.add(getString(R.string.no_lessons_today));
        } else {
                for (Classroom classroom : classrooms) {
                    /*
                    String classroomDisplay = "";
                    classroomDisplay += classroom.toString();
                    */
                    adapterList.add(classroom.toString());
                }
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adapterList);
        classroomList.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        roomController.setActiveCampus(Campus.valueOf(spinner.getSelectedItem().toString().toUpperCase()));

        Calendar calendar = Calendar.getInstance();
        String buttonText = (String) button.getText();

        if (buttonText.contains(":")) {
            Date date = DateFormatter.toDate(String.format("%s:00.000 %04d-%02d-%02d", buttonText, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)), DateType.FULL_DATE_US);

            displayAvailable(roomController.getAvailableClassrooms(date));
        } else {
            displayAvailable(null);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Auto generated stub
    }

    public ClassroomController getRoomController() {
        return roomController;
    }

    private void displayMessageToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void displayMessageSnackbar(String message, int snackbarLength){
        Snackbar.make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_LONG).show();
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(true);
            roomController.updateClassrooms();
            displayMessageToast(getString(R.string.info_refreshing_db));
        }
    };
}
