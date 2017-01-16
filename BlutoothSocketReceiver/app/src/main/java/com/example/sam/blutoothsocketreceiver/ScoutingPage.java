package com.example.sam.blutoothsocketreceiver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Arrays;

public class ScoutingPage extends ActionBarActivity {
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String alliance;
    String dataBaseUrl;
    String allianceScoreData;
    TextView teamNumberOneTextview;
    TextView teamNumberTwoTextview;
    TextView teamNumberThreeTextview;
    ArrayList<String> teamOneDataName;
    ArrayList<String> teamOneDataScore;
    ArrayList<String> teamTwoDataName;
    ArrayList<String> teamTwoDataScore;
    ArrayList<String> teamThreeDataName;
    ArrayList<String> teamThreeDataScore;
    Boolean rotorRP = false;
    Boolean boilerRP = false;
    Boolean isMute;
    JSONObject object;
    Intent next;
    DatabaseReference dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_scouting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        next = getIntent();
        object = new JSONObject();
        getExtrasForScouting();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.super_scouting_panel, null);
        Log.e("Super Scouting", dataBaseUrl);
        dataBase = FirebaseDatabase.getInstance().getReference();
        setPanels();
        initializeTeamTextViews();

    }

    //warns the user that going back will change data
    @Override
    public void onBackPressed() {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finaldata, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.endDataShortcut){
            final AlertDialog.Builder endDataBuilder = new AlertDialog.Builder(this);
            endDataBuilder.setCancelable(false);
            endDataBuilder.setView(R.layout.finaldatapoints);
            endDataBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            endDataBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Dialog d = (Dialog) dialog;
                    EditText scoreText = (EditText) d.findViewById(R.id.finalScoreEditText);
                    ToggleButton rotorsToggle = (ToggleButton) d.findViewById(R.id.rotorsToggleButton);
                    ToggleButton boilerToggle = (ToggleButton) d.findViewById(R.id.boilerToggleButton);
                    rotorRP = rotorsToggle.isChecked();
                    boilerRP = boilerToggle.isChecked();
                    allianceScoreData = scoreText.getText().toString();
                    dialog.cancel();
                }
            });
            AlertDialog endDataDialog = endDataBuilder.create();
            endDataDialog.show();
        }

        if (id == R.id.finalNext) {
            final SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
            final SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
            final SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);
            listDataValues();
            //new added code
            new Thread() {
                @Override
                public void run() {
                    try {

                        for (int i = 0; i < panelOne.getDataNameCount() - 1; i++) {
                            Log.e("Scouting", "4");
                            dataBase.child("/TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child(reformatDataNames(teamOneDataName.get(i))).setValue(Integer.parseInt(teamOneDataScore.get(i)));
                        }
                        for (int i = 0; i < panelTwo.getDataNameCount() - 1; i++) {
                            Log.e("Scouting", "5");
                            dataBase.child("/TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child(reformatDataNames(teamTwoDataName.get(i))).setValue(Integer.parseInt(teamTwoDataScore.get(i)));
                        }
                        for (int i = 0; i < panelThree.getDataNameCount() - 1; i++) {
                            Log.e("Scouting", "6");
                            dataBase.child("/TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child(reformatDataNames(teamThreeDataName.get(i))).setValue(Integer.parseInt(teamThreeDataScore.get(i)));
                        }
                    } catch (DatabaseException FBE) {
                        Log.e("firebase", "scoutingPage");
                    } catch (IndexOutOfBoundsException IOB) {
                        Log.e("ScoutingPage", "Index");
                    }
                }
            }.start();
            //New Added Code//

            sendExtras();

        }

        return super.onOptionsItemSelected(item);
    }

    public void getExtrasForScouting() {

        numberOfMatch = next.getExtras().getString("matchNumber");
        teamNumberOne = next.getExtras().getString("teamNumberOne");
        teamNumberTwo = next.getExtras().getString("teamNumberTwo");
        teamNumberThree = next.getExtras().getString("teamNumberThree");
        alliance = next.getExtras().getString("alliance");
        dataBaseUrl = next.getExtras().getString("dataBaseUrl");
        isMute = next.getExtras().getBoolean("mute");
    }

    public void setPanels() {

        SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
        SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
        SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);
        panelOne.setAllianceColor(SuperScoutApplication.isRed);
        panelOne.setTeamNumber(teamNumberOne);
        panelTwo.setAllianceColor(SuperScoutApplication.isRed);
        panelTwo.setTeamNumber(teamNumberTwo);
        panelThree.setAllianceColor(SuperScoutApplication.isRed);
        panelThree.setTeamNumber(teamNumberThree);
    }

    public void sendExtras() {
        Intent intent = new Intent(this, FinalDataPoints.class);
        intent.putExtra("matchNumber", numberOfMatch);
        intent.putExtra("teamNumberOne", teamNumberOne);
        intent.putExtra("teamNumberTwo", teamNumberTwo);
        intent.putExtra("teamNumberThree", teamNumberThree);
        intent.putExtra("alliance", alliance);
        intent.putExtra("dataBaseUrl", dataBaseUrl);
        intent.putExtra("allianceScore", allianceScoreData);
        intent.putExtra("scoutRotorRPGained", rotorRP);
        intent.putExtra("scoutBoilerRPGained", boilerRP);
        intent.putExtra("mute", isMute);
        intent.putStringArrayListExtra("dataNameOne", teamOneDataName);
        intent.putStringArrayListExtra("ranksOfOne", teamOneDataScore);
        intent.putStringArrayListExtra("dataNameTwo", teamTwoDataName);
        intent.putStringArrayListExtra("ranksOfTwo", teamTwoDataScore);
        intent.putStringArrayListExtra("dataNameThree", teamThreeDataName);
        intent.putStringArrayListExtra("ranksOfThree", teamThreeDataScore);
        startActivity(intent);
    }

    public void listDataValues() {
        SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
        SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
        SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);

        teamOneDataName = new ArrayList<>(panelOne.getData().keySet());
        teamTwoDataName = new ArrayList<>(panelTwo.getData().keySet());
        teamThreeDataName = new ArrayList<>(panelThree.getData().keySet());
        teamOneDataScore = new ArrayList<>();
        teamTwoDataScore = new ArrayList<>();
        teamThreeDataScore = new ArrayList<>();

        for (int i = 0; i < teamOneDataName.size(); i++) {
            teamOneDataScore.add(panelOne.getData().get(teamOneDataName.get(i)).toString());
        }
        for (int i = 0; i < teamTwoDataName.size(); i++) {
            teamTwoDataScore.add(panelTwo.getData().get(teamTwoDataName.get(i)).toString());
        }
        for (int i = 0; i < teamThreeDataName.size(); i++) {
            teamThreeDataScore.add(panelThree.getData().get(teamThreeDataName.get(i)).toString());
        }
        Log.e("teamOneDataKeys", panelOne.getData().keySet().toString());
        Log.e("teamTwoDataKeys", panelTwo.getData().keySet().toString());
        Log.e("teamThreeDataKeys", panelThree.getData().keySet().toString());

        Log.e("teamOneDataNameSize", Integer.toString(teamOneDataName.size()));
        Log.e("teamTwoDataNameSize", Integer.toString(teamTwoDataName.size()));
        Log.e("teamThreeDataNameSize", Integer.toString(teamThreeDataName.size()));

        Log.e("teamOneDataName", teamOneDataName.toString());
        Log.e("teamOneDataScore", teamOneDataScore.toString());
        Log.e("teamTwoDataName", teamTwoDataName.toString());
        Log.e("teamTwoDataScore", teamTwoDataScore.toString());
        Log.e("teamThreeDataName", teamThreeDataName.toString());
        Log.e("teamThreeDataScore", teamThreeDataScore.toString());

    }

    public String reformatDataNames(String dataName){
        return ("rank" + dataName.replace(" ", ""));
    }

    public void initializeTeamTextViews(){
        SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
        SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
        SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);
        teamNumberOneTextview = (TextView)panelOne.getView().findViewById(R.id.teamNumberTextView);
        teamNumberTwoTextview = (TextView)panelTwo.getView().findViewById(R.id.teamNumberTextView);
        teamNumberThreeTextview = (TextView)panelThree.getView().findViewById(R.id.teamNumberTextView);
    }
}



