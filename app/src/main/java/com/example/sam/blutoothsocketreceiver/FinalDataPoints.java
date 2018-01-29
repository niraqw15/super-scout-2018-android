package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
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

public class FinalDataPoints extends ActionBarActivity {
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String teamOneNotes;
    String teamTwoNotes;
    String teamThreeNotes;
    String alliance;
    String dataBaseUrl;
    String allianceScoreData, allianceFoulData;
    String blueSwitch;
    String redSwitch;
    String scale;
    TextView finalScore;
    EditText allianceScore, allianceFoul;
    Switch facedTheBoss;
    Switch completedAutoQuest;
    Counter boostCounterView;
    Counter levitateCounterView;
    Counter forceCounterView;
    Integer forceForPowerup;
    Integer boostForPowerup;
    Integer levitateForPowerup;
    JSONObject superExternalData;
    ArrayList<String> teamOneDataName;
    ArrayList<String> teamOneDataScore;
    ArrayList<String> teamTwoDataName;
    ArrayList<String> teamTwoDataScore;
    ArrayList<String> teamThreeDataName;
    ArrayList<String> teamThreeDataScore;
    Boolean isMute;
    File dir;
    PrintWriter file;
    DatabaseReference firebaseRef;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finaldatapoints);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        intent = getIntent();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        superExternalData = new JSONObject();

        boostCounterView = (Counter) findViewById(R.id.BoostCounter);
        levitateCounterView = (Counter) findViewById(R.id.LevitateCounter);
        forceCounterView = (Counter) findViewById(R.id.ForceCounter);

        getExtrasForFinalData();
        firebaseRef = FirebaseDatabase.getInstance().getReference();
        allianceScore = (EditText) findViewById(R.id.finalScoreEditText);
        allianceFoul = (EditText) findViewById(R.id.finalFoulEditText);
        //
        facedTheBoss = (Switch) findViewById(R.id.didFaceBossBoolean);
        facedTheBoss.setChecked(intent.getExtras().getBoolean("facedTheBoss"));
        completedAutoQuest = (Switch) findViewById(R.id.didAutoQuestBoolean);
        completedAutoQuest.setChecked(intent.getExtras().getBoolean("completedAutoQuest"));
        boostCounterView = (Counter) findViewById(R.id.BoostCounter);
        boostCounterView.refreshCounter(intent.getExtras().getInt("boostCount"));
        levitateCounterView = (Counter) findViewById(R.id.LevitateCounter);
        levitateCounterView.refreshCounter(intent.getExtras().getInt("levitateCount"));
        forceCounterView = (Counter) findViewById(R.id.ForceCounter);
        forceCounterView.refreshCounter(intent.getExtras().getInt("forceCount"));
        //
        finalScore = (TextView)findViewById(R.id.finalScoreTextView);
        allianceScore.setCursorVisible(false);

        if(alliance.equals("Blue Alliance")){
            finalScore.setTextColor(Color.BLUE);
        }else if(alliance.equals("Red Alliance")){
            finalScore.setTextColor(Color.RED);
        }

        allianceScore.setText(allianceScoreData);
        allianceFoul.setText(allianceFoulData);
        dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data");
    }
    @Override
    public void onBackPressed(){
        final Activity activity = this;
        new AlertDialog.Builder(this)
                .setTitle("WARNING!")
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.submit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Submit) {
            final Activity context = this;
            int score;
            int foul;
            try {
                score = Integer.parseInt(allianceScore.getText().toString());
                foul = Integer.parseInt(allianceFoul.getText().toString());
            } catch (NumberFormatException nfe) {
                Toast.makeText(this, "Invalid inputs", Toast.LENGTH_LONG).show();
                return false;
            } catch (NullPointerException npe) {
                Toast.makeText(this, "Enter a score and foul", Toast.LENGTH_LONG).show();
                return false;
            }
            final int allianceScoreNum = score;
            final int allianceFoulNum = foul;

            //Send the data of the super scout on a separate thread
            new Thread() {
                @Override
                public void run() {
                    try {
                        file = null;
                        //make the directory of the file
                        dir.mkdir();
                        //can delete when doing the actual thing
                        file = new PrintWriter(new FileOutputStream(new File(dir, ("Q" + numberOfMatch + "_"  + new SimpleDateFormat("MM-dd-yyyy-H:mm:ss").format(new Date())))));
                    } catch (IOException IOE) {
                        return;
                    }
                    teamOneDataName.add("superNotes");
                    teamOneDataScore.add(teamOneNotes);
                    teamTwoDataName.add("superNotes");
                    teamTwoDataScore.add(teamTwoNotes);
                    teamThreeDataName.add("superNotes");
                    teamThreeDataScore.add(teamThreeNotes);
                    try {
                        String JsonStringTeamOne = "{";
                        String JsonStringTeamTwo = "{";
                        String JsonStringTeamThree = "{";
                        for(int a = 0; a <= teamOneDataScore.size() - 1; a++) {
                            JsonStringTeamOne = JsonStringTeamOne + ("\"" + reformatDataNames(teamOneDataName.get(a)) + "\": " + teamOneDataScore.get(a));
                            if (a != teamOneDataScore.size() - 1) {
                                JsonStringTeamOne = JsonStringTeamOne + ",";
                            } else {
                                JsonStringTeamOne = JsonStringTeamOne + "}";
                            }
                        }

                        for(int a = 0; a <= teamTwoDataScore.size() - 1; a++){
                            JsonStringTeamTwo = JsonStringTeamTwo + ("\"" + reformatDataNames(teamTwoDataName.get(a)) + "\": " + teamTwoDataScore.get(a));
                            if(a != teamTwoDataScore.size() - 1){
                                JsonStringTeamTwo = JsonStringTeamTwo + ",";
                            } else {
                                JsonStringTeamTwo = JsonStringTeamTwo + "}";
                            }
                        }
                        for(int a = 0; a <= teamThreeDataScore.size() - 1; a++){
                            JsonStringTeamThree = JsonStringTeamThree + ("\"" + reformatDataNames(teamThreeDataName.get(a)) + "\": " + teamThreeDataScore.get(a));
                            if(a != teamThreeDataScore.size() - 1){
                                JsonStringTeamThree = JsonStringTeamThree + ",";
                            } else {
                                JsonStringTeamThree = JsonStringTeamThree + "}";
                            }
                        }
                        JSONObject JsonTeamOne = new JSONObject(JsonStringTeamOne);
                        JSONObject JsonTeamTwo = new JSONObject(JsonStringTeamTwo);
                        JSONObject JsonTeamThree = new JSONObject(JsonStringTeamThree);

                        superExternalData.put("matchNumber", numberOfMatch);
                        superExternalData.put("alliance", alliance);
                        superExternalData.put(alliance + " Score", allianceScoreNum);
                        superExternalData.put(alliance + " Foul", allianceFoulNum);
                        superExternalData.put(teamNumberOne, JsonTeamOne);
                        superExternalData.put(teamNumberTwo, JsonTeamTwo);
                        superExternalData.put(teamNumberThree, JsonTeamThree);
                        superExternalData.put("teamOne", teamNumberOne);
                        superExternalData.put("teamTwo", teamNumberTwo);
                        superExternalData.put("teamThree", teamNumberThree);
                        superExternalData.put("teamOneNotes", Constants.teamOneNoteHolder);
                        superExternalData.put("teamTwoNotes", Constants.teamTwoNoteHolder);
                        superExternalData.put("teamThreeNotes", Constants.teamThreeNoteHolder);
                        superExternalData.put("blueSwitch", blueSwitch);
                        superExternalData.put("redSwitch", redSwitch);
                        superExternalData.put("scale", scale);
                    }catch(JSONException JE){
                        Log.e("JSON Error", "couldn't put keys and values in json object");
                    }
                    ArrayList<String> teamNumbers = new ArrayList<>(Arrays.asList(teamNumberOne, teamNumberTwo, teamNumberThree));

                    for (int i = 0; i < teamNumbers.size(); i++){
                        firebaseRef.child("TeamInMatchDatas").child(teamNumbers.get(i) + "Q" + numberOfMatch).child("teamNumber").setValue(Integer.parseInt(teamNumbers.get(i)));
                        firebaseRef.child("TeamInMatchDatas").child(teamNumbers.get(i) + "Q" + numberOfMatch).child("matchNumber").setValue(Integer.parseInt(numberOfMatch));
                        firebaseRef.child("TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("superNotes").setValue(Constants.teamOneNoteHolder);
                        firebaseRef.child("TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child("superNotes").setValue(Constants.teamTwoNoteHolder);
                        firebaseRef.child("TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child("superNotes").setValue(Constants.teamThreeNoteHolder);
                    }
                    sendAfterMatchData();

                    System.out.println(superExternalData.toString());

                    file.println(superExternalData.toString());
                    file.close();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Sent Match Data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }.start();
            Intent backToHome = new Intent(context, MainActivity.class);
            if(alliance.equals("Red Alliance")){
                backToHome.putExtra("shouldBeRed", true);
            }else {
                backToHome.putExtra("shouldBeRed", false);
            }
            backToHome.putExtra("number", numberOfMatch);
            backToHome.putExtra("mute", isMute);
            startActivity(backToHome);
        }

        if(id == R.id.finalSuperNotes){
            final Activity context = this;
            Intent finalNotesIntent = new Intent(context, finalNotes.class);
            finalNotesIntent.putExtra("teamNumOne", teamNumberOne);
            finalNotesIntent.putExtra("teamNumTwo", teamNumberTwo);
            finalNotesIntent.putExtra("teamNumThree", teamNumberThree);
            finalNotesIntent.putExtra("teamOneNotes", teamOneNotes);
            finalNotesIntent.putExtra("teamTwoNotes", teamTwoNotes);
            finalNotesIntent.putExtra("teamThreeNotes", teamThreeNotes);
            finalNotesIntent.putExtra("qualNum", numberOfMatch);
            startActivity(finalNotesIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public String reformatDataNames(String dataName){
        String reformattedDataName = "";
        if(!dataName.equals("Good Decisions") && !dataName.equals("Bad Decisions")){
            reformattedDataName = "rank" + dataName.replace(" ", "");
        }else if(dataName.equals("Good Decisions") || dataName.equals("Bad Decisions")){
            reformattedDataName = "num" + dataName.replace(" ", "");
        }
        return reformattedDataName;
    }

    public void getExtrasForFinalData(){

        teamOneNotes = intent.getExtras().getString("teamOneNotes");
        teamTwoNotes = intent.getExtras().getString("teamTwoNotes");
        teamThreeNotes = intent.getExtras().getString("teamThreeNotes");

        numberOfMatch = intent.getExtras().getString("matchNumber");
        teamNumberOne = intent.getExtras().getString("teamNumberOne");
        teamNumberTwo = intent.getExtras().getString("teamNumberTwo");
        teamNumberThree = intent.getExtras().getString("teamNumberThree");
        alliance = intent.getExtras().getString("alliance");

        teamOneDataName = intent.getStringArrayListExtra("dataNameOne");
        teamOneDataScore = intent.getStringArrayListExtra("ranksOfOne");
        teamTwoDataName = intent.getStringArrayListExtra("dataNameTwo");
        teamTwoDataScore = intent.getStringArrayListExtra("ranksOfTwo");
        teamThreeDataName = intent.getStringArrayListExtra("dataNameThree");
        teamThreeDataScore = intent.getStringArrayListExtra("ranksOfThree");

        dataBaseUrl = intent.getExtras().getString("dataBaseUrl");
        allianceScoreData = intent.getExtras().getString("allianceScore");
        allianceFoulData = intent.getExtras().getString("allianceFoul");
        isMute = intent.getExtras().getBoolean("mute");

        blueSwitch = intent.getExtras().getString("blueSwitch");
        redSwitch = intent.getExtras().getString("redSwitch");
        scale = intent.getExtras().getString("scale");

        forceForPowerup = intent.getExtras().getInt("forceForPowerup");
        boostForPowerup = intent.getExtras().getInt("boostForPowerup");
        levitateForPowerup = intent.getExtras().getInt("levitateForPowerup");
    }

    public void sendAfterMatchData(){
        if (alliance.equals("Blue Alliance")) {
            firebaseRef.child("/Matches").child(numberOfMatch).child("blueScore").setValue(Integer.parseInt(allianceScore.getText().toString()));
            firebaseRef.child("/Matches").child(numberOfMatch).child("foulPointsGainedBlue").setValue(Integer.parseInt(allianceFoul.getText().toString()));
            firebaseRef.child("/Matches").child(numberOfMatch).child("blueDidFaceBoss").setValue(facedTheBoss.isChecked());
            firebaseRef.child("/Matches").child(numberOfMatch).child("blueDidAutoQuest").setValue(completedAutoQuest.isChecked());
            firebaseRef.child("/Matches").child(numberOfMatch).child("blueCubesInVaultFinal").child("Boost").setValue(boostCounterView.getDataValue());
            firebaseRef.child("/Matches").child(numberOfMatch).child("blueCubesInVaultFinal").child("Levitate").setValue(levitateCounterView.getDataValue());
            firebaseRef.child("/Matches").child(numberOfMatch).child("blueCubesInVaultFinal").child("Force").setValue(forceCounterView.getDataValue());

        } else if (alliance.equals("Red Alliance")) {
            firebaseRef.child("/Matches").child(numberOfMatch).child("redScore").setValue(Integer.parseInt(allianceScore.getText().toString()));
            firebaseRef.child("/Matches").child(numberOfMatch).child("foulPointsGainedRed").setValue(Integer.parseInt(allianceFoul.getText().toString()));
            firebaseRef.child("/Matches").child(numberOfMatch).child("redDidFaceBoss").setValue(facedTheBoss.isChecked());
            firebaseRef.child("/Matches").child(numberOfMatch).child("redDidAutoQuest").setValue(completedAutoQuest.isChecked());
            firebaseRef.child("/Matches").child(numberOfMatch).child("redCubesInVaultFinal").child("Boost").setValue(boostCounterView.getDataValue());
            firebaseRef.child("/Matches").child(numberOfMatch).child("redCubesInVaultFinal").child("Levitate").setValue(levitateCounterView.getDataValue());
            firebaseRef.child("/Matches").child(numberOfMatch).child("redCubesInVaultFinal").child("Force").setValue(forceCounterView.getDataValue());

        }
    }
}