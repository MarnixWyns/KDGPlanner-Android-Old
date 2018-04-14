package tech.cloverfield.kdgplanner;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        MainActivity mainActivity = (MainActivity) getActivity();
        String time = DateFormatter.fixTimeString(hourOfDay + ":" + minute);
        mainActivity.button.setText(time);

        Calendar cal = Calendar.getInstance();
        String rawDateValue = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        String dateValue = DateFormatter.fixDateString(rawDateValue);

        ArrayList<Classroom> available = new ArrayList<>();
        for (Classroom classroom : mainActivity.getClassrooms()) {
            if (classroom.isAvailable(dateValue, time)) {
                available.add(classroom);
            }
        }

        mainActivity.displayAvailable(available);
    }
}