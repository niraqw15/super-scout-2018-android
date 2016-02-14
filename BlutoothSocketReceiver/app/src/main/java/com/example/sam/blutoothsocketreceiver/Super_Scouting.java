package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;

public class Super_Scouting extends ActionBarActivity {
    TextView teamNumber1;
    TextView teamNumber2;
    TextView teamNumber3;
    EditText inputTeamOne;
    EditText inputTeamTwo;
    EditText inputTeamThree;
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String firstDefense;
    String secondDefense;
    String thirdDefense;
    String fourthDefense;
    String alliance;
    String teamOneNote;
    String teamTwoNote;
    String teamThreeNote;
    ArrayList<String> defenses;
    ArrayList<String> dataScore;
    ArrayList<String> teamOneDataName;
    ArrayList<String> teamOneDataScore;
    ArrayList<String> teamTwoDataName;
    ArrayList<String> teamTwoDataScore;
    ArrayList<String> teamThreeDataName;
    ArrayList<String> teamThreeDataScore;
    ArrayList<String> data;
    JSONObject object;
    Intent next;
    Firebase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_scouting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        next = getIntent();
        object = new JSONObject();
        //Get authorized to use the database
        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {}

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {}
        };
        dataBase = new Firebase("https://1678-scouting-2016.firebaseio.com");
        dataBase.authWithCustomToken("qVIARBnAD93iykeZSGG8mWOwGegminXUUGF2q0ee", authResultHandler);
        getExtrasForScouting();
        teamNumber1 = (TextView) findViewById(R.id.team1);
        teamNumber2 = (TextView) findViewById(R.id.team2);
        teamNumber3 = (TextView) findViewById(R.id.team3);
        //set team numbers and their alliance colors on the top
        if (alliance.equals("Blue Alliance")) {
            teamNumber1.setText(teamNumberOne);
            teamNumber1.setTextColor(Color.BLUE);
            teamNumber2.setText(teamNumberTwo);
            teamNumber2.setTextColor(Color.BLUE);
            teamNumber3.setText(teamNumberThree);
            teamNumber3.setTextColor(Color.BLUE);
        } else if (alliance.equals("Red Alliance")) {
            teamNumber1.setText(teamNumberOne);
            teamNumber1.setTextColor(Color.RED);
            teamNumber2.setText(teamNumberTwo);
            teamNumber2.setTextColor(Color.RED);
            teamNumber3.setText(teamNumberThree);
            teamNumber3.setTextColor(Color.RED);
        }
        defenses = new ArrayList<>(Arrays.asList(firstDefense, secondDefense, thirdDefense, fourthDefense));
        dataScore = new ArrayList<>();
        teamOneDataName = new ArrayList<>();
        teamOneDataScore = new ArrayList<>();
        teamTwoDataName = new ArrayList<>();
        teamTwoDataScore = new ArrayList<>();
        teamThreeDataName = new ArrayList<>();
        teamThreeDataScore = new ArrayList<>();
        data = new ArrayList<>(Arrays.asList("Speed", "Torque", "Defense", "Evasion", "Ball Control"));

        setUpDataRanking();

    }
//Dialog pops up and lets the user input notes for each team
    public void teamOneNoteClick(View view) {

        final Dialog dialog = new Dialog(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        Button ok = (Button) dialogView.findViewById(R.id.OKButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText note = (EditText) dialogView.findViewById(R.id.note);
                teamOneNote = note.getText().toString();
                dialog.dismiss();
            }
        });
        Button cancel = (Button) dialogView.findViewById(R.id.CancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setTitle("Team " + teamNumberOne + " Note:");
        dialog.setContentView(dialogView);
        dialog.show();
    }
    public void teamTwoNoteClick(View view){
        final Dialog dialog = new Dialog(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        Button ok = (Button) dialogView.findViewById(R.id.OKButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText note = (EditText) dialogView.findViewById(R.id.note);
                teamTwoNote = note.getText().toString();
                dialog.dismiss();
            }
        });
        Button cancel = (Button) dialogView.findViewById(R.id.CancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setTitle("Team " + teamNumberTwo + " Note:");
        dialog.setContentView(dialogView);
        dialog.show();
    }
    public void teamThreeNoteClick(View view){
        final Dialog dialog = new Dialog(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        Button ok = (Button) dialogView.findViewById(R.id.OKButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText note = (EditText) dialogView.findViewById(R.id.note);
                teamThreeNote = note.getText().toString();
                dialog.dismiss();
            }
        });
        Button cancel = (Button) dialogView.findViewById(R.id.CancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setTitle("Team " + teamNumberThree + " Note:");
        dialog.setContentView(dialogView);
        dialog.show();
    }
    //warns the user that going back will change data
    @Override
    public void onBackPressed(){
        final Activity activity = this;
        new AlertDialog.Builder(this)
                .setTitle("WARNING")
                .setMessage("GOING BACK WILL CAUSE LOSS OF DATA")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

//a view that will be added in a loop
    private View createCounter(String title) {

        LayoutInflater inflater = getLayoutInflater();
        View counter = inflater.inflate(R.layout.counter, null);
        TextView dataNameTextView = (TextView) counter.findViewById(R.id.dataName);
        dataNameTextView.setText(title);
        final TextView incrementor = (TextView) counter.findViewById(R.id.scoreCounter);
        Button plusButton = (Button) counter.findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(incrementor.getText().toString());
                current++;
                if (current > 4) {
                    incrementor.setText(Integer.toString(4));
                } else {
                    incrementor.setText(Integer.toString(current));
                }
            }
        });
        Button minusButton = (Button) counter.findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(incrementor.getText().toString());
                current--;
                if (current < 0) {
                    incrementor.setText(Integer.toString(0));
                } else {
                    incrementor.setText(Integer.toString(current));
                }
            }
        });
        return counter;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finaldata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.finalNext) {
            Intent intent = new Intent(this, FinalDataPoints.class);
            intent.putExtra("matchNumber", numberOfMatch);
            intent.putExtra("teamNumberOne", teamNumberOne);
            intent.putExtra("teamNumberTwo", teamNumberTwo);
            intent.putExtra("teamNumberThree", teamNumberThree);
            intent.putExtra("firstDefensePicked", firstDefense);
            intent.putExtra("secondDefensePicked", secondDefense);
            intent.putExtra("thirdDefensePicked", thirdDefense);
            intent.putExtra("fourthDefensePicked", fourthDefense);
            intent.putExtra("alliance", alliance);
            intent.putExtra("teamOneNote", teamOneNote);
            intent.putExtra("teamTwoNote", teamTwoNote);
            intent.putExtra("teamThreeNote", teamThreeNote);

            getEachDataNameAndValue();
            intent.putStringArrayListExtra("dataNameOne", teamOneDataName);
            intent.putStringArrayListExtra("ranksOfOne", teamOneDataScore);
            intent.putStringArrayListExtra("dataNameTwo", teamTwoDataName);
            intent.putStringArrayListExtra("ranksOfTwo", teamTwoDataScore);
            intent.putStringArrayListExtra("dataNameThree", teamThreeDataName);
            intent.putStringArrayListExtra("ranksOfThree", teamThreeDataScore);
            new Thread(){
                public void run(){
                    dataBase.child("TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("superNotes").setValue(teamOneNote);
                    dataBase.child("TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child("superNotes").setValue(teamTwoNote);
                    dataBase.child("TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child("superNotes").setValue(teamThreeNote);
                }
            }.start();
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
//Get all the data names and their values
    public void getEachDataNameAndValue() {
        LinearLayout teamOneRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam1);
        LinearLayout teamTwoRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam2);
        LinearLayout teamThreeRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam3);
        teamOneDataName.clear();
        teamOneDataScore.clear();
        teamTwoDataName.clear();
        teamTwoDataScore.clear();
        teamThreeDataName.clear();
        teamThreeDataScore.clear();
        for (int i = 0; i < teamOneRelativeLayout.getChildCount(); i++) {
            View teamOneLayout = teamOneRelativeLayout.getChildAt(i);
            TextView nameOfData1 = (TextView) teamOneLayout.findViewById(R.id.dataName);
            TextView scoreOfData1 = (TextView) teamOneLayout.findViewById(R.id.scoreCounter);
            teamOneDataName.add(("rank" + (nameOfData1.getText().toString())).replace(" ", ""));
            teamOneDataScore.add(scoreOfData1.getText().toString());
        }
        for (int j = 0; j < teamTwoRelativeLayout.getChildCount(); j++) {
            View teamTwoLayout = teamTwoRelativeLayout.getChildAt(j);
            TextView nameOfData2 = (TextView) teamTwoLayout.findViewById(R.id.dataName);
            TextView scoreOfData2 = (TextView) teamTwoLayout.findViewById(R.id.scoreCounter);
            teamTwoDataName.add(("rank" + (nameOfData2.getText().toString())).replace(" ", ""));
            teamTwoDataScore.add(scoreOfData2.getText().toString());
        }
        for (int k = 0; k < teamThreeRelativeLayout.getChildCount(); k++) {
            View teamThreeLayout = teamThreeRelativeLayout.getChildAt(k);
            TextView nameOfData3 = (TextView) teamThreeLayout.findViewById(R.id.dataName);
            TextView scoreOfData3 = (TextView) teamThreeLayout.findViewById(R.id.scoreCounter);
            teamThreeDataName.add(("rank" + (nameOfData3.getText().toString())).replace(" ", ""));
            teamThreeDataScore.add(scoreOfData3.getText().toString());
        }
    }
    //sets up the counters to add value
    public void setUpDataRanking(){
        LinearLayout teamOneRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam1);
        LinearLayout teamTwoRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam2);
        LinearLayout teamThreeRelativeLayout = (LinearLayout) findViewById(R.id.scoutTeam3);

        for (String title : data) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
            View counter = createCounter(title);
            counter.setId(1 + data.indexOf(title));
            counter.setLayoutParams(param);
            teamOneRelativeLayout.addView(counter);
        }
        for (String title : data) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
            View counter = createCounter(title);
            counter.setId(2 + data.indexOf(title));
            counter.setLayoutParams(param);
            teamTwoRelativeLayout.addView(counter);
        }
        for (String title : data) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 1f);
            View counter = createCounter(title);
            counter.setId(3 + data.indexOf(title));
            counter.setLayoutParams(param);
            teamThreeRelativeLayout.addView(counter);
        }
    }
    public void getExtrasForScouting(){

        numberOfMatch = next.getExtras().getString("matchNumber");
        teamNumberOne = next.getExtras().getString("teamNumberOne");
        teamNumberTwo = next.getExtras().getString("teamNumberTwo");
        teamNumberThree = next.getExtras().getString("teamNumberThree");
        alliance = next.getExtras().getString("alliance");
        firstDefense = next.getExtras().getString("firstDefensePicked");
        secondDefense = next.getExtras().getString("secondDefensePicked");
        thirdDefense = next.getExtras().getString("thirdDefensePicked");
        fourthDefense = next.getExtras().getString("fourthDefensePicked");
    }
}



