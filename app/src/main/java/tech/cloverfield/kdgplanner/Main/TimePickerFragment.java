package tech.cloverfield.kdgplanner.Main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tech.cloverfield.kdgplanner.DateFormatter;
import tech.cloverfield.kdgplanner.Objects.Classroom;
import tech.cloverfield.kdgplanner.Objects.DateType;

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
        MainActivity mainActivity = (MainActivity) getActivity();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date time = cal.getTime();

        mainActivity.button.setText(String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

        ArrayList<Classroom> available = new ArrayList<>(mainActivity.getLokalen_db().getRooms(mainActivity.convertCampus(mainActivity.getSelectedCampus()), time));
        mainActivity.displayAvailable(available);
    }
}