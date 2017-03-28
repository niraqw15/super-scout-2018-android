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
import android.view.ViewGroup;
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

import org.jcodec.containers.mp4.boxes.Edit;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ScoutingPage extends ActionBarActivity {
    Activity context;
    String numberOfMatch;
    String teamNumberOne;
    String teamNumberTwo;
    String teamNumberThree;
    String alliance;
    String dataBaseUrl;
    String allianceScoreData, allianceFoulData;
    TextView teamNumberOneTextview;
    TextView teamNumberTwoTextview;
    TextView teamNumberThreeTextview;
    ArrayList<String> teamOneDataName;
    ArrayList<String> teamOneDataScore;
    ArrayList<String> teamTwoDataName;
    ArrayList<String> teamTwoDataScore;
    ArrayList<String> teamThreeDataName;
    ArrayList<String> teamThreeDataScore;
    Integer rotorNumAuto = 0;
    Integer rotorNumTele = 0;
    Integer allianceScoreInt = 0;
    Integer allianceFoulInt = 0;
    Boolean boilerRP = false;
    Boolean isMute;
    JSONObject object;
    Intent next;
    DatabaseReference dataBase;
    Boolean isRed;
    String previousNotesOne;
    String previousNotesTwo;
    String previousNotesThree;
    String teamOneFirstNotes;
    String teamTwoFirstNotes;
    String teamThreeFirstNotes;

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
        context = this;

        previousNotesOne = "";
        previousNotesTwo = "";
        previousNotesThree = "";
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

        if (id == R.id.endDataShortcut) {
//            LayoutInflater finalDataInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//            View finalDataView = finalDataInflater.inflate(R.layout.finaldatapoints, null);
//            ((EditText) finalDataView.findViewById(R.id.finalScoreEditText)).setText(allianceScoreInt);
//            ((EditText) finalDataView.findViewById(R.id.finalFoulEditText)).setText(allianceFoulInt);
//            ((EditText) finalDataView.findViewById(R.id.rotorAutoText)).setText(rotorNumAuto);
//            ((EditText) finalDataView.findViewById(R.id.rotorTeleText)).setText(rotorNumTele);
//            ((ToggleButton) finalDataView.findViewById(R.id.boilerToggleButton)).setChecked(boilerRP);
            //TODO: Fix resource not found exception above
            final AlertDialog.Builder endDataBuilder = new AlertDialog.Builder(this);
            endDataBuilder.setCancelable(false);
            endDataBuilder.setView(R.layout.finaldatapoints); //TODO: change to finalDataView when exception fixed
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
                    EditText foulText = (EditText) d.findViewById(R.id.finalFoulEditText);
                    EditText rotorsAutoText = (EditText) d.findViewById(R.id.rotorAutoText);
                    EditText rotorsTeleText = (EditText) d.findViewById(R.id.rotorTeleText);
                    ToggleButton boilerToggle = (ToggleButton) d.findViewById(R.id.boilerToggleButton);
                    boilerRP = boilerToggle.isChecked();
                    allianceFoulData = foulText.getText().toString();
                    allianceScoreData = scoreText.getText().toString();
                    try {
                        rotorNumAuto = Integer.parseInt(rotorsAutoText.getText().toString());
                        rotorNumTele = Integer.parseInt(rotorsTeleText.getText().toString());
                        allianceScoreInt = Integer.parseInt(allianceScoreData);
                        allianceFoulInt = Integer.parseInt(allianceFoulData);
                    } catch (NumberFormatException nfe) {
                        Log.i("Exception", "Number Format");
                        rotorNumAuto = 0;
                        rotorNumTele = 0;
                        allianceScoreInt = 0;
                        allianceFoulInt = 0;
                    } catch (NullPointerException npe) {
                        Log.i("Exception", "Null Pointer");
                        rotorNumAuto = 0;
                        rotorNumTele = 0;
                        allianceScoreInt = 0;
                        allianceFoulInt = 0;
                    }

                    if (alliance.equals("Blue Alliance")) {
                        dataBase.child("/Matches").child(numberOfMatch).child("numRotorsSpinningBlueAuto").setValue(rotorNumAuto);
                        dataBase.child("/Matches").child(numberOfMatch).child("numRotorsSpinningBlueTele").setValue(rotorNumTele);
                        dataBase.child("/Matches").child(numberOfMatch).child("blueDidReachFortyKilopascals").setValue(boilerRP);
                        dataBase.child("/Matches").child(numberOfMatch).child("blueScore").setValue(allianceScoreInt);
                        dataBase.child("/Matches").child(numberOfMatch).child("foulPointsGainedBlue").setValue(allianceFoulInt);

                    } else if (alliance.equals("Red Alliance")) {
                        dataBase.child("/Matches").child(numberOfMatch).child("numRotorsSpinningRedAuto").setValue(rotorNumAuto);
                        dataBase.child("/Matches").child(numberOfMatch).child("numRotorsSpinningRedTele").setValue(rotorNumTele);
                        dataBase.child("/Matches").child(numberOfMatch).child("redDidReachFortyKilopascals").setValue(boilerRP);
                        dataBase.child("/Matches").child(numberOfMatch).child("redScore").setValue(allianceScoreInt);
                        dataBase.child("/Matches").child(numberOfMatch).child("foulPointsGainedRed").setValue(allianceFoulInt);
                    }

                    dialog.cancel();
                }
            });
            AlertDialog endDataDialog = endDataBuilder.create();
            endDataDialog.show();
            if (isRed) {
                ((TextView) endDataDialog.findViewById(R.id.finalScoreTextView)).setTextColor(Color.RED);
            } else {
                ((TextView) endDataDialog.findViewById(R.id.finalScoreTextView)).setTextColor(Color.BLUE);
            }
        }

        if (id == R.id.finalNext) {
            boolean moreThanOne = false;
            for (int a = 1; a <= 4; a++) {
                if (SuperScoutingPanel.Defense.get(a) > 1 || SuperScoutingPanel.Speed.get(a) > 1 || SuperScoutingPanel.GearControl.get(a) > 1 || SuperScoutingPanel.BallControl.get(a) > 1 || SuperScoutingPanel.Agility.get(a) > 1) {
                    moreThanOne = true;
                }
            }

            if (!moreThanOne) {
                final SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
                final SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
                final SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);
                listDataValues();
                checkForSpecialZeros();

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

            } else {
                Toast.makeText(getBaseContext(), "Different Teams Cannot Have the Same Rank for one category", Toast.LENGTH_LONG).show();
            }

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
        isRed = next.getExtras().getBoolean("allianceColor");
    }

    public void setPanels() {
        SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
        SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
        SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);
        panelOne.setAllianceColor(isRed);
        panelOne.setTeamNumber(teamNumberOne);
        panelTwo.setAllianceColor(isRed);
        panelTwo.setTeamNumber(teamNumberTwo);
        panelThree.setAllianceColor(isRed);
        panelThree.setTeamNumber(teamNumberThree);
    }

    public void sendExtras() {
        Intent intent = new Intent(this, FinalDataPoints.class);
        intent.putExtra("teamOneFirstNotes", teamOneFirstNotes);
        intent.putExtra("teamTwoFirstNotes", teamTwoFirstNotes);
        intent.putExtra("teamThreeFirstNotes", teamThreeFirstNotes);
        intent.putExtra("matchNumber", numberOfMatch);
        intent.putExtra("teamNumberOne", teamNumberOne);
        intent.putExtra("teamNumberTwo", teamNumberTwo);
        intent.putExtra("teamNumberThree", teamNumberThree);
        intent.putExtra("alliance", alliance);
        intent.putExtra("dataBaseUrl", dataBaseUrl);
        intent.putExtra("allianceScore", allianceScoreData);
        intent.putExtra("allianceFoul", allianceFoulData);
        intent.putExtra("scoutRotorsAutoNum", rotorNumAuto);
        intent.putExtra("scoutRotorsTeleNum", rotorNumTele);
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

    public void checkForSpecialZeros(){
        if(teamOneDataScore.get(0).equals("0") || teamOneDataScore.get(2).equals("0")){
            for(int n = 0; n <= teamOneDataScore.size() - 1; n++){
                teamOneDataScore.set(n, "0");
            }
        }
        if(teamTwoDataScore.get(0).equals("0") || teamTwoDataScore.get(2).equals("0")){
            for(int n = 0; n <= teamTwoDataScore.size() - 1; n++){
                teamTwoDataScore.set(n, "0");
            }
        }
        if(teamThreeDataScore.get(0).equals("0") || teamThreeDataScore.get(2).equals("0")){
            for(int n = 0; n <= teamThreeDataScore.size() - 1; n++){
                teamThreeDataScore.set(n, "0");
            }
        }
    }

    public String reformatDataNames(String dataName) {
        return ("rank" + dataName.replace(" ", ""));
    }

    public void initializeTeamTextViews() {
        SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
        SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
        SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);
        teamNumberOneTextview = (TextView) panelOne.getView().findViewById(R.id.teamNumberTextView);
        teamNumberTwoTextview = (TextView) panelTwo.getView().findViewById(R.id.teamNumberTextView);
        teamNumberThreeTextview = (TextView) panelThree.getView().findViewById(R.id.teamNumberTextView);

        teamNumberOneTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String teamNumber = teamNumberOneTextview.getText().toString();
                final String qualNum = numberOfMatch.toString();

                final EditText pilotNotesETOne = new EditText(context);

                if(!previousNotesOne.equals("")) {
                    pilotNotesETOne.setText(previousNotesOne);
                }
                pilotNotesETOne.setTextColor(Color.BLACK);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("SuperNotes for team "+teamNumber)
                        .setView(pilotNotesETOne)
                        .setPositiveButton("Yep", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Dialog d = (Dialog) dialog;
                                Log.e("TEAMNUM",teamNumber);
                                previousNotesOne = pilotNotesETOne.getText().toString();
                                dataBase.child("TeamInMatchDatas").child(teamNumber + "Q" + qualNum).child("superNotes").child("firstNotes").setValue(pilotNotesETOne.getText().toString());
                                teamOneFirstNotes = pilotNotesETOne.getText().toString();
                            }
                        })
                        .setNegativeButton("Naw Bruh", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
        teamNumberTwoTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String teamNumber = teamNumberTwoTextview.getText().toString();
                final String qualNum = numberOfMatch.toString();

                final EditText pilotNotesETTwo = new EditText(context);

                if(!previousNotesTwo.equals("")) {
                    pilotNotesETTwo.setText(previousNotesTwo);
                }
                pilotNotesETTwo.setTextColor(Color.BLACK);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Pilot Notes for "+teamNumber)
                        .setView(pilotNotesETTwo)
                        .setPositiveButton("Yep", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Dialog d = (Dialog) dialog;
                                Log.e("TEAMNUM",teamNumber);
                                previousNotesTwo = pilotNotesETTwo.getText().toString();
                                dataBase.child("TeamInMatchDatas").child(teamNumber + "Q" + qualNum).child("superNotes").child("firstNotes").setValue(pilotNotesETTwo.getText().toString());
                                teamTwoFirstNotes = pilotNotesETTwo.getText().toString();
                            }
                        })
                        .setNegativeButton("Naw Bruh", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
        teamNumberThreeTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String teamNumber = teamNumberThreeTextview.getText().toString();
                final String qualNum = numberOfMatch.toString();

                final EditText pilotNotesETThree = new EditText(context);

                if(!previousNotesThree.equals("")) {
                    pilotNotesETThree.setText(previousNotesThree);
                }
                pilotNotesETThree.setTextColor(Color.BLACK);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Pilot Notes for "+teamNumber)
                        .setView(pilotNotesETThree)
                        .setPositiveButton("Yep", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Dialog d = (Dialog) dialog;
                                Log.e("TEAMNUM",teamNumber);
                                previousNotesThree = pilotNotesETThree.getText().toString();
                                dataBase.child("TeamInMatchDatas").child(teamNumber + "Q" + qualNum).child("superNotes").child("firstNotes").setValue(pilotNotesETThree.getText().toString());
                                teamThreeFirstNotes = pilotNotesETThree.getText().toString();
                            }
                        })
                        .setNegativeButton("Naw Bruh", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }
}



