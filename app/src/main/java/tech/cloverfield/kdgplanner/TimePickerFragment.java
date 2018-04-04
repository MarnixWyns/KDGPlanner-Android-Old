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
        try {
            MainActivity mainActivity = (MainActivity)  getActivity();
            mainActivity.button.setText(hourOfDay + ":" + minute);
            java.text.DateFormat dpd = new SimpleDateFormat("yyyy-MM-dd");

            java.text.DateFormat dpu = new SimpleDateFormat("kk:mm");
            Date time = dpu.parse(hourOfDay + ":" + minute);
            Date date = Calendar.getInstance().getTime();
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
            ArrayList<Classroom> available = new ArrayList<>();

            for (Classroom classroom : mainActivity.getClassrooms()) {
                if (classroom.isAvailable(date, time)) available.add(classroom);
            }

            mainActivity.displayAvailable(available);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}