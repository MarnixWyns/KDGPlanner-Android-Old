

package tech.cloverfield.kdgplanner.Reservation;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import tech.cloverfield.kdgplanner.R;

public class ReservationActivity extends AppCompatActivity {

    //Default values, remove before deployment
    String datum = "";
    String startTijd = "";
    String eindTijd = "";
    String naam = "John Doe";
    String aantal = "";
    boolean beamer = true;
    String beamerReq = "een";

    String[] outputStrings = {
            datum, startTijd, eindTijd, naam, aantal
    };

    EditText tbDate = findViewById(R.id.tbDate);
    EditText tbStartTime = findViewById(R.id.tbStartTime);
    EditText tbEndTime = findViewById(R.id.tbEndTime);
    EditText tbName = findViewById(R.id.tbName);
    EditText tbAmount = findViewById(R.id.tbAmmount);



    EditText[] inputs = {
            tbDate, tbStartTime, tbEndTime, tbName, tbAmount
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Button sendMail = findViewById(R.id.btnSend);
        sendMail.setOnClickListener(sendMailListener);
    }

    private void checkInputs(){
        for (int i = 0; i < inputs.length; i++) {
            if (!inputs[i].getText().toString().equals("")){
                outputStrings[i] = inputs[i].getText().toString();
            } else inputs[i].setBackgroundColor(Color.RED);
        }
    }



    private View.OnClickListener sendMailListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            checkInputs();


            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");

            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"lokaalreservering.heb@kdg.be"});

            i.putExtra(Intent.EXTRA_SUBJECT, makeMail()[0]);
            i.putExtra(Intent.EXTRA_TEXT, makeMail()[1]);


            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ReservationActivity.this, "Er zijn geen mail clients geinstalleerd", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private String[] makeMail(){


        String[] values = {"",""};

        values[0] = "Lokaalreservatie" + datum;

        values [1]= "Beste, \n\n" +
                "Ik zou graag een lokaal willen reserveren op " + datum + " van " + startTijd + " tot " + eindTijd + ".\n" +
                "Ik zou het willen reserveren op naam van " + naam.trim() + " en we gaan er met " + aantal + " personen zitten.\n" +
                "We hebben " + beamerReq + " beamer nodig.\n\n" +
                "Met vriendelijke groet, \n " + naam.trim() + "\n\n" + "Reservatie verzonden via KDGPlanner.";

        return values;
    }
}
