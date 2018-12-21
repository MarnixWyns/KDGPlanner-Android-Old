package tech.cloverfield.kdgplanner.application.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tech.cloverfield.kdgplanner.application.activities.MainActivity;
import tech.cloverfield.kdgplanner.business.domain.Classroom;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @SuppressLint("DefaultLocale")
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        MainFragment mainFragment = MainFragment.getMainFragment();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date time = cal.getTime();

        mainFragment.button.setText(String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

        ArrayList<Classroom> available = new ArrayList<>(((MainActivity) getActivity()).getRoomController().getAvailableClassrooms(time));
        mainFragment.displayAvailable(available);
    }
}