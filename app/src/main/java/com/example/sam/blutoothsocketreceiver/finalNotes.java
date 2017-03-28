package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    String qualNum;
    EditText teamOneEditText;
    EditText teamTwoEditText;
    EditText teamThreeEditText;
    public static String teamOneFinalNotes;
    public static String teamTwoFinalNotes;
    public static String teamThreeFinalNotes;
    DatabaseReference firebaseRef;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_notes);

        firebaseRef = FirebaseDatabase.getInstance().getReference();

        intent = getIntent();

        qualNum = intent.getStringExtra("qualNum");

        teamNumberOne = intent.getStringExtra("teamNumOne");
        teamNumberTwo = intent.getStringExtra("teamNumTwo");
        teamNumberThree = intent.getStringExtra("teamNumThree");

        TextView teamOneTextView = (TextView) findViewById(R.id.teamNumberOneTextView);
        TextView teamTwoTextView = (TextView) findViewById(R.id.teamNumberTwoTextView);
        TextView teamThreeTextView = (TextView) findViewById(R.id.teamNumberThreeTextView);

        setTextViewText(teamNumberOne, teamOneTextView);
        setTextViewText(teamNumberTwo, teamTwoTextView);
        setTextViewText(teamNumberThree, teamThreeTextView);

        editor = getSharedPreferences("SuperNotesPref", MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences("SuperNotesPref", MODE_PRIVATE);

        teamOneEditText = (EditText) findViewById(R.id.teamOneSuperNotesEditText);
        teamTwoEditText = (EditText) findViewById(R.id.teamTwoSuperNotesEditText);
        teamThreeEditText = (EditText) findViewById(R.id.teamThreeSuperNotesEditText);

        teamOneEditText.setText(prefs.getString("teamOneFinalNotes", ""));
        teamTwoEditText.setText(prefs.getString("teamTwoFinalNotes", ""));
        teamThreeEditText.setText(prefs.getString("teamThreeFinalNotes", ""));
    }

    @Override
    public void onBackPressed(){
        final Activity activity = this;
        new AlertDialog.Builder(this)
                .setTitle("WARNING!")
                .setMessage("Send before leaving?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        teamOneFinalNotes = teamOneEditText.getText().toString();
                        teamTwoFinalNotes = teamTwoEditText.getText().toString();
                        teamThreeFinalNotes = teamThreeEditText.getText().toString();

                        editor.putString("teamOneFinalNotes", teamOneFinalNotes);
                        editor.putString("teamTwoFinalNotes", teamTwoFinalNotes);
                        editor.putString("teamThreeFinalNotes", teamThreeFinalNotes);
                        editor.commit();

                        firebaseRef.child("TeamInMatchDatas").child(teamNumberOne + "Q" + qualNum).child("superNotes").child("finalNotes").setValue(teamOneEditText.getText().toString());
                        firebaseRef.child("TeamInMatchDatas").child(teamNumberTwo + "Q" + qualNum).child("superNotes").child("finalNotes").setValue(teamTwoEditText.getText().toString());
                        firebaseRef.child("TeamInMatchDatas").child(teamNumberThree + "Q" + qualNum).child("superNotes").child("finalNotes").setValue(teamThreeEditText.getText().toString());

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
            teamOneFinalNotes = teamOneEditText.getText().toString();
            teamTwoFinalNotes = teamTwoEditText.getText().toString();
            teamThreeFinalNotes = teamThreeEditText.getText().toString();

            editor.putString("teamOneFinalNotes", teamOneFinalNotes);
            editor.putString("teamTwoFinalNotes", teamTwoFinalNotes);
            editor.putString("teamThreeFinalNotes", teamThreeFinalNotes);
            editor.commit();

            firebaseRef.child("TeamInMatchDatas").child(teamNumberOne + "Q" + qualNum).child("superNotes").child("finalNotes").setValue(teamOneEditText.getText().toString());
            firebaseRef.child("TeamInMatchDatas").child(teamNumberTwo + "Q" + qualNum).child("superNotes").child("finalNotes").setValue(teamTwoEditText.getText().toString());
            firebaseRef.child("TeamInMatchDatas").child(teamNumberThree + "Q" + qualNum).child("superNotes").child("finalNotes").setValue(teamThreeEditText.getText().toString());

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTextViewText(String number, TextView textView){
        textView.setText(number);
    }
}
