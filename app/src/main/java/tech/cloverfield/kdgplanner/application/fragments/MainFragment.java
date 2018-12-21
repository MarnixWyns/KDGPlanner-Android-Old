package tech.cloverfield.kdgplanner.application.fragments;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import tech.cloverfield.kdgplanner.application.activities.MainActivity;
import tech.cloverfield.kdgplanner.business.domain.Campus;
import tech.cloverfield.kdgplanner.business.domain.Classroom;
import tech.cloverfield.kdgplanner.business.domain.DateType;
import tech.cloverfield.kdgplanner.controller.ClassroomController;
import tech.cloverfield.kdgplanner.controller.SettingsController;
import tech.cloverfield.kdgplanner.foundation.DateFormatter;

public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static MainFragment mainFragment;

    public Button button;
    public SwipeRefreshLayout swipeRefreshLayout;

    private Spinner spinner;

    private ClassroomController roomController;
    private SettingsController settingsController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomController = ((MainActivity) getActivity()).getRoomController();
        settingsController = ((MainActivity) getActivity()).getSettingsController();
        mainFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_main, container, false);
        spinner = inflatedView.findViewById(R.id.spinnerMain);
        button = inflatedView.findViewById(R.id.btnSelectHour);
        swipeRefreshLayout = inflatedView.findViewById(R.id.swiperefresh);

        return inflatedView;
    }

    public static MainFragment getMainFragment() {
        return mainFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.campussen, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        button.setOnClickListener(btnOnClickListener);

        roomController.updateClassrooms();
    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!roomController.hasRoomsLoaded()) {
                Snackbar.make(getView().findViewById(R.id.coordinator), String.format("%s %s%%", getString(R.string.db_init_load), roomController.getLoadedPercentage()), Snackbar.LENGTH_LONG).show();
            } else if (!roomController.hasRoomsLoaded() && settingsController.HasInternet()) {
                Snackbar.make(getView().findViewById(R.id.coordinator), getString(R.string.error_connection), Snackbar.LENGTH_LONG).show();
            } else {
                DialogFragment dialog = new TimePickerFragment();
                dialog.show(getFragmentManager(), "timePicker");
            }
        }


    };

    public void displayAvailable(Collection<Classroom> classrooms) {
        ListView classroomList = getView().findViewById(R.id.lvClassrooms);
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

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, adapterList);
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

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(true);
            roomController.updateClassrooms();
            Snackbar.make(getView().findViewById(R.id.coordinator), getString(R.string.info_refreshing_db), Snackbar.LENGTH_LONG).show();
        }
    };
}
