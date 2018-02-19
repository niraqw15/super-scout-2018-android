package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class finalNotes extends ActionBarActivity {

    Intent intent;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String teamOneNotes, teamTwoNotes, teamThreeNotes;
    String qualNum;
    EditText teamOneEditText;
    EditText teamTwoEditText;
    EditText teamThreeEditText;
    DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_notes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        firebaseRef = FirebaseDatabase.getInstance().getReference();

        intent = getIntent();

        qualNum = intent.getStringExtra("qualNum");

        teamNumberOne = intent.getStringExtra("teamNumOne");
        teamNumberTwo = intent.getStringExtra("teamNumTwo");
        teamNumberThree = intent.getStringExtra("teamNumThree");
        teamOneNotes = intent.getStringExtra("teamOneNotes");
        teamTwoNotes = intent.getStringExtra("teamTwoNotes");
        teamThreeNotes = intent.getStringExtra("teamThreeNotes");

        TextView teamOneTextView = (TextView) findViewById(R.id.teamNumberOneTextView);
        TextView teamTwoTextView = (TextView) findViewById(R.id.teamNumberTwoTextView);
        TextView teamThreeTextView = (TextView) findViewById(R.id.teamNumberThreeTextView);

        setTextViewText(teamNumberOne, teamOneTextView);
        setTextViewText(teamNumberTwo, teamTwoTextView);
        setTextViewText(teamNumberThree, teamThreeTextView);

        teamOneEditText = (EditText) findViewById(R.id.teamOneSuperNotesEditText);
        teamTwoEditText = (EditText) findViewById(R.id.teamTwoSuperNotesEditText);
        teamThreeEditText = (EditText) findViewById(R.id.teamThreeSuperNotesEditText);

        teamOneEditText.setText(teamOneNotes);
        teamTwoEditText.setText(teamTwoNotes);
        teamThreeEditText.setText(teamThreeNotes);

        teamOneEditText.setFocusable(true);
        teamTwoEditText.setFocusable(true);
        teamThreeEditText.setFocusable(true);
    }

    @Override
    public void onBackPressed(){
        final Activity activity = this;
        new AlertDialog.Builder(this)
                .setTitle("WARNING!")
                .setMessage("Send before leaving?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            Constants.teamOneNoteHolder = teamOneEditText.getText().toString();
                            Constants.teamTwoNoteHolder = teamTwoEditText.getText().toString();
                            Constants.teamThreeNoteHolder = teamThreeEditText.getText().toString();
                        } catch (NullPointerException npe){
                            Constants.teamOneNoteHolder = "";
                            Constants.teamTwoNoteHolder = "";
                            Constants.teamThreeNoteHolder = "";
                        }

                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finalnotes, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.submitFinalNotes){
            try {
                Constants.teamOneNoteHolder = teamOneEditText.getText().toString();
                Constants.teamTwoNoteHolder = teamTwoEditText.getText().toString();
                Constants.teamThreeNoteHolder = teamThreeEditText.getText().toString();
            } catch (NullPointerException npe){
                Constants.teamOneNoteHolder = "";
                Constants.teamTwoNoteHolder = "";
                Constants.teamThreeNoteHolder = "";
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTextViewText(String number, TextView textView){
        textView.setText(number);
    }
}
