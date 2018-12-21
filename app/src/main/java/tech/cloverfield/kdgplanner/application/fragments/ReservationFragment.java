package tech.cloverfield.kdgplanner.application.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import tech.cloverfield.kdgplanner.R;

public class ReservationFragment extends Fragment {

    Calendar myCalendar = Calendar.getInstance();

    //Default values, remove before deployment
    String datum = "";
    String startTijd = "";
    String eindTijd = "";
    String campus = "";
    String naam = "John Doe";
    String aantal = "";
    String beamerReq = "een";

    String[] outputStrings = {
            datum, startTijd, campus ,eindTijd, naam, aantal
    };

    EditText tbDate;
    EditText tbStartTime;
    EditText tbEndTime;
    EditText tbName;
    EditText tbAmount;
    CheckBox cbBeamer;

    Spinner spinner;



    EditText[] inputs = new EditText[5];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_reservation, container, false);

        this.spinner = inflatedView.findViewById(R.id.spinnerReservation);
        this.tbDate = inflatedView.findViewById(R.id.tbDate);
        this.tbStartTime = inflatedView.findViewById(R.id.tbStartTime);
        this.tbEndTime = inflatedView.findViewById(R.id.tbEndTime);
        this.tbName = inflatedView.findViewById(R.id.tbName);
        this.tbAmount = inflatedView.findViewById(R.id.tbAmmount);
        this.cbBeamer = inflatedView.findViewById(R.id.cbBeamer);

        inputs[0] = tbDate;
        inputs[1] = tbStartTime;
        inputs[2] = tbEndTime;
        inputs[3] = tbName;
        inputs[4] = tbAmount;

        Button sendMail = inflatedView.findViewById(R.id.btnSend);
        sendMail.setOnClickListener(sendMailListener);

        // Get items from resources string array and put them in spinner values.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.campussen, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        tbStartTime.setOnFocusChangeListener(onFocusChangetbStartTime);
        tbEndTime.setOnFocusChangeListener(onFocusChangetbEndTime);
        tbDate.setOnFocusChangeListener(onFocusChangetbDate);

        return inflatedView;
    }

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            tbDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
        }
    };


    private View.OnClickListener sendMailListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            processInputs();

            //region Mail intent
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");

            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"lokaalreservering.heb@kdg.be"});

            i.putExtra(Intent.EXTRA_SUBJECT, makeMail()[0]);
            i.putExtra(Intent.EXTRA_TEXT, makeMail()[1]);

            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), R.string.error_no_mail_client, Toast.LENGTH_SHORT).show();
            }
            //endregion
        }


    };

    private void processInputs() {
        if (cbBeamer.isChecked()) beamerReq = "een";
        else beamerReq = "geen";

        for (int i = 0; i < inputs.length; i++) {
            outputStrings[i] = inputs[i].getText().toString();
        }


    }

    private String[] makeMail() {
        String[] values = new String[2];

        values[0] = "Lokaalreservatie " + outputStrings[0];

        values[1] = "Beste, \n\n" +
                "Ik zou graag een lokaal willen reserveren op " + outputStrings[0] + " van " + outputStrings[1] + " tot " + outputStrings[2] + " op campus " + spinner.getItemAtPosition(0) + ".\n" +
                "Ik zou het willen reserveren op naam van " + outputStrings[3].trim() + " en we gaan er met " + outputStrings[4] + " personen zitten.\n" +
                "We hebben " + beamerReq + " beamer nodig.\n\n" +
                "Met vriendelijke groet, \n" + outputStrings[3].trim() + "\n\n" + "Reservatie verzonden via KDGPlanner.";

        return values;
    }

    private View.OnFocusChangeListener onFocusChangetbStartTime = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String s = selectedHour + ":" + selectedMinute;
                        tbStartTime.setText(s);
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        }
    };

    private View.OnFocusChangeListener onFocusChangetbEndTime = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String s = selectedHour + ":" + selectedMinute;
                        tbEndTime.setText(s);
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        }
    };

    private View.OnFocusChangeListener onFocusChangetbDate = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        }
    };
}
