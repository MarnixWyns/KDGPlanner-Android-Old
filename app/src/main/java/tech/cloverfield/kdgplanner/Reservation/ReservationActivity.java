package tech.cloverfield.kdgplanner.Reservation;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import tech.cloverfield.kdgplanner.R;

public class ReservationActivity extends AppCompatActivity {

    Calendar myCalendar = Calendar.getInstance();

    //Default values, remove before deployment
    String datum = "";
    String startTijd = "";
    String eindTijd = "";
    String naam = "John Doe";
    String aantal = "";
    String beamerReq = "een";

    String[] outputStrings = {
            datum, startTijd, eindTijd, naam, aantal
    };

    EditText tbDate;
    EditText tbStartTime;
    EditText tbEndTime;
    EditText tbName;
    EditText tbAmount;
    CheckBox cbBeamer;

    EditText[] inputs = new EditText[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        this.tbDate = findViewById(R.id.tbDate);
        this.tbStartTime = findViewById(R.id.tbStartTime);
        this.tbEndTime = findViewById(R.id.tbEndTime);
        this.tbName = findViewById(R.id.tbName);
        this.tbAmount = findViewById(R.id.tbAmmount);
        this.cbBeamer = findViewById(R.id.cbBeamer);

        inputs[0] = tbDate;
        inputs[1] = tbStartTime;
        inputs[2] = tbEndTime;
        inputs[3] = tbName;
        inputs[4] = tbAmount;

        Button sendMail = findViewById(R.id.btnSend);
        sendMail.setOnClickListener(sendMailListener);


        tbStartTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(ReservationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String s = selectedHour + ":" + selectedMinute;
                            tbStartTime.setText(s);
                        }
                    }, hour, minute, true);
                    mTimePicker.show();
                }
            }
        });

        tbEndTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(ReservationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String s = selectedHour + ":" + selectedMinute;
                            tbEndTime.setText(s);
                        }
                    }, hour, minute, true);
                    mTimePicker.show();
                }
            }
        });

        tbDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(ReservationActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

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
                Toast.makeText(ReservationActivity.this, "Er zijn geen mail clients geinstalleerd", Toast.LENGTH_SHORT).show();
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
                "Ik zou graag een lokaal willen reserveren op " + outputStrings[0] + " van " + outputStrings[1] + " tot " + outputStrings[2] + ".\n" +
                "Ik zou het willen reserveren op naam van " + outputStrings[3].trim() + " en we gaan er met " + outputStrings[4] + " personen zitten.\n" +
                "We hebben " + beamerReq + " beamer nodig.\n\n" +
                "Met vriendelijke groet, \n" + outputStrings[3].trim() + "\n\n" + "Reservatie verzonden via KDGPlanner.";

        return values;
    }
}
