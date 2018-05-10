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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jcodec.common.DictionaryCompressor;
import org.jcodec.common.RunLength;
import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FinalDataPoints extends ActionBarActivity {
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String teamOneNotes;
    String teamTwoNotes;
    String teamThreeNotes;
    String alliance;
    String allianceSimple;
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
    boolean hasRun;

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
        finalScore = (TextView)findViewById(R.id.finalScoreTextView);
        allianceScore.setCursorVisible(false);

        if(alliance.equals("Blue Alliance")){
            finalScore.setTextColor(Color.BLUE);
        }else if(alliance.equals("Red Alliance")){
            finalScore.setTextColor(Color.RED);
        }

        allianceSimple = alliance.substring(0,1).toLowerCase() + alliance.substring(1,alliance.indexOf(" "));

        allianceScore.setText(allianceScoreData);
        allianceFoul.setText(allianceFoulData);
        dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data");

        hasRun = false;
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

            final int boostFinal = boostCounterView.getDataValue();
            final int levitateFinal = levitateCounterView.getDataValue();
            final int forceFinal = forceCounterView.getDataValue();

            if(boostFinal < boostForPowerup) {
                Toast.makeText(this, "Boost can't be lower than " + boostForPowerup, Toast.LENGTH_LONG).show();
                return false;
            }
            if(levitateFinal < levitateForPowerup) {
                Toast.makeText(this, "Levitate can't be lower than " + levitateForPowerup, Toast.LENGTH_LONG).show();
                return false;
            }
            if(forceFinal < forceForPowerup) {
                Toast.makeText(this, "Force can't be lower than " + forceForPowerup, Toast.LENGTH_LONG).show();
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

                    if(!hasRun) {
                        updateNotes();
                        teamOneDataName.add("superNotes");
                        teamOneDataScore.add(teamOneNotes);
                        teamTwoDataName.add("superNotes");
                        teamTwoDataScore.add(teamTwoNotes);
                        teamThreeDataName.add("superNotes");
                        teamThreeDataScore.add(teamThreeNotes);
                        hasRun = true;
                    }

                    try {

                        JSONObject jsonTeamOne = new JSONObject();
                        JSONObject jsonTeamTwo = new JSONObject();
                        JSONObject jsonTeamThree= new JSONObject();
                        JSONObject jsonCubesForPowerup = new JSONObject();
                        JSONObject jsonCubesInVaultFinal = new JSONObject();

                        JSONObject jsonBlueSwitch = new JSONObject(blueSwitch);
                        JSONObject jsonRedSwitch = new JSONObject(redSwitch);
                        JSONObject jsonScale = new JSONObject(scale);

                        for(int position = 0; position < teamOneDataScore.size(); position++) {
                            if(teamOneDataName.get(position).equals("superNotes")) {
                                jsonTeamOne.put(reformatDataNames(teamOneDataName.get(position)), teamOneDataScore.get(position));
                            } else {
                                jsonTeamOne.put(reformatDataNames(teamOneDataName.get(position)), Integer.parseInt(teamOneDataScore.get(position)));
                            }
                        }
                        for(int position = 0; position < teamTwoDataScore.size(); position++){
                            if(teamOneDataName.get(position).equals("superNotes")) {
                                jsonTeamTwo.put(reformatDataNames(teamTwoDataName.get(position)), teamTwoDataScore.get(position));
                            } else {
                                jsonTeamTwo.put(reformatDataNames(teamTwoDataName.get(position)), Integer.parseInt(teamTwoDataScore.get(position)));
                            }
                        }
                        for(int position = 0; position < teamThreeDataScore.size(); position++){
                            if(teamOneDataName.get(position).equals("superNotes")) {
                                jsonTeamThree.put(reformatDataNames(teamThreeDataName.get(position)), teamThreeDataScore.get(position));
                            } else {
                                jsonTeamThree.put(reformatDataNames(teamThreeDataName.get(position)), Integer.parseInt(teamThreeDataScore.get(position)));
                            }
                        }

                        jsonCubesInVaultFinal.put("Boost", boostFinal);
                        jsonCubesInVaultFinal.put("Levitate", levitateFinal);
                        jsonCubesInVaultFinal.put("Force", forceFinal);

                        jsonCubesForPowerup.put("Boost", boostForPowerup);
                        jsonCubesForPowerup.put("Levitate", levitateForPowerup);
                        jsonCubesForPowerup.put("Force", forceForPowerup);

                        superExternalData.put(allianceSimple + "DidAutoQuest", completedAutoQuest.isChecked());
                        superExternalData.put(allianceSimple + "DidFaceBoss", facedTheBoss.isChecked());
                        superExternalData.put(allianceSimple + "CubesInVaultFinal", jsonCubesInVaultFinal);
                        superExternalData.put(allianceSimple + "CubesForPowerup", jsonCubesForPowerup);
                        superExternalData.put("matchNumber", numberOfMatch);
                        superExternalData.put("alliance", alliance);
                        superExternalData.put(allianceSimple + "Score", allianceScoreNum);
                        superExternalData.put(allianceSimple + "FoulPointsGained", allianceFoulNum);
                        superExternalData.put(teamNumberOne, jsonTeamOne);
                        superExternalData.put(teamNumberTwo, jsonTeamTwo);
                        superExternalData.put(teamNumberThree, jsonTeamThree);
                        superExternalData.put("teamOne", teamNumberOne);
                        superExternalData.put("teamTwo", teamNumberTwo);
                        superExternalData.put("teamThree", teamNumberThree);
                        superExternalData.put("blueSwitch", jsonBlueSwitch);
                        superExternalData.put("redSwitch", jsonRedSwitch);
                        superExternalData.put("scale", jsonScale);
                    } catch(JSONException JE) {
                        Log.e("JSON Error", "couldn't put keys and values in json object");
                    }
                    ArrayList<String> teamNumbers = new ArrayList<>(Arrays.asList(teamNumberOne, teamNumberTwo, teamNumberThree));

                    for (int i = 0; i < teamNumbers.size(); i++){
                        firebaseRef.child("TeamInMatchDatas").child(teamNumbers.get(i) + "Q" + numberOfMatch).child("teamNumber").setValue(Integer.parseInt(teamNumbers.get(i)));
                        firebaseRef.child("TeamInMatchDatas").child(teamNumbers.get(i) + "Q" + numberOfMatch).child("matchNumber").setValue(Integer.parseInt(numberOfMatch));
                    }
                    firebaseRef.child("TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child("superNotes").setValue(teamOneNotes);
                    firebaseRef.child("TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child("superNotes").setValue(teamTwoNotes);
                    firebaseRef.child("TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child("superNotes").setValue(teamThreeNotes);
                    sendAfterMatchData();

                    System.out.println("SuperExternalData: " + superExternalData.toString());

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
            updateNotes();
            finalNotesIntent.putExtra("teamOneNotes", teamOneNotes);
            finalNotesIntent.putExtra("teamTwoNotes", teamTwoNotes);
            finalNotesIntent.putExtra("teamThreeNotes", teamThreeNotes);
            finalNotesIntent.putExtra("qualNum", numberOfMatch);
            startActivity(finalNotesIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public String reformatDataNames(String dataName) {
        String reformattedDataName = "";
        if(dataName.equals("Good Decisions") || dataName.equals("Bad Decisions")){
            reformattedDataName = "num" + dataName.replace(" ", "");
        } else if(dataName.equals("superNotes")) {
            reformattedDataName = dataName;
        } else {
            reformattedDataName = "rank" + dataName.replace(" ", "");
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
        JSONObject allianceTeams = new JSONObject();
        int teamIntOne = Integer.parseInt(teamNumberOne);
        int teamIntTwo = Integer.parseInt(teamNumberTwo);
        int teamIntThree = Integer.parseInt(teamNumberThree);

        try {
            allianceTeams.put("0", teamIntOne);
            allianceTeams.put("1", teamIntTwo);
            allianceTeams.put("2", teamIntThree);
        } catch(JSONException JE) {
            Log.e("JSONException", "Failed to make allianceTeams");
        }

        Map<String, Object> allianceTeamsJsonMap = new Gson().fromJson(allianceTeams.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());

        String uppercaseAllianceSimple = alliance.substring(0, alliance.indexOf(" "));

        firebaseRef.child("/Matches").child(numberOfMatch).child(allianceSimple + "AllianceTeamNumbers").setValue(allianceTeamsJsonMap);
        firebaseRef.child("/Matches").child(numberOfMatch).child(allianceSimple + "Score").setValue(Integer.parseInt(allianceScore.getText().toString()));
        firebaseRef.child("/Matches").child(numberOfMatch).child("foulPointsGained" + uppercaseAllianceSimple).setValue(Integer.parseInt(allianceFoul.getText().toString()));
        firebaseRef.child("/Matches").child(numberOfMatch).child(allianceSimple + "DidFaceBoss").setValue(facedTheBoss.isChecked());
        firebaseRef.child("/Matches").child(numberOfMatch).child(allianceSimple + "DidAutoQuest").setValue(completedAutoQuest.isChecked());
        firebaseRef.child("/Matches").child(numberOfMatch).child(allianceSimple + "CubesInVaultFinal").child("Boost").setValue(boostCounterView.getDataValue());
        firebaseRef.child("/Matches").child(numberOfMatch).child(allianceSimple + "CubesInVaultFinal").child("Levitate").setValue(levitateCounterView.getDataValue());
        firebaseRef.child("/Matches").child(numberOfMatch).child(allianceSimple + "CubesInVaultFinal").child("Force").setValue(forceCounterView.getDataValue());
    }

    private void updateNotes() {
        if(!Constants.teamOneNoteHolder.equals("")) {
            teamOneNotes = Constants.teamOneNoteHolder;
        }
        if(!Constants.teamTwoNoteHolder.equals("")) {
            teamTwoNotes = Constants.teamTwoNoteHolder;
        }
        if(!Constants.teamThreeNoteHolder.equals("")) {
            teamThreeNotes = Constants.teamThreeNoteHolder;
        }
    }
}