package com.example.sam.blutoothsocketreceiver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jcodec.common.DictionaryCompressor;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.sam.blutoothsocketreceiver.R.id.Boost;
import static com.example.sam.blutoothsocketreceiver.R.id.Force;
import static com.example.sam.blutoothsocketreceiver.R.id.boostone;
import static com.example.sam.blutoothsocketreceiver.R.id.panelOne;
import static com.example.sam.blutoothsocketreceiver.R.id.panelTwo;

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
    Map<String, Integer> allianceCubesForPowerup = new HashMap<>();
    Integer allianceScoreInt = 0;
    Integer allianceFoulInt = 0;
    Boolean facedTheBoss = false;
    Boolean didAutoQuest = false;
    Integer boostC = 0;
    Integer levitateC = 0;
    Integer forceC = 0;
    Boolean isMute;
    JSONObject object;
    Intent next;
    DatabaseReference dataBase;
    Boolean isRed;
    String teamOneNotes;
    String teamTwoNotes;
    String teamThreeNotes;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ToggleButton levitate;
    Integer levitateNum = 0;
    Counter boostCounterView;
    Counter levitateCounterView;
    Counter forceCounterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.super_scouting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        next = getIntent();
        object = new JSONObject();
        getExtrasForScouting();
        dataBase = FirebaseDatabase.getInstance().getReference();
        setPanels();
        initializeTeamTextViews();
        context = this;
        levitate = (ToggleButton) findViewById(R.id.Lev);

        teamOneNotes = "";
        teamTwoNotes = "";
        teamThreeNotes = "";

        allianceCubesForPowerup.put("Force", 0);
        allianceCubesForPowerup.put("Boost", 0);
        allianceCubesForPowerup.put("Levitate", 0);
    }

    //Warns the user that going back will change data
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

    public Boolean canProceed() {
        Boolean canProceed = true;
        ArrayList<String> dataNames = new ArrayList<>(Arrays.asList("Speed", "Agility", "Defense"));
        SuperScoutingPanel panelone = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(panelOne);
        SuperScoutingPanel paneltwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(panelTwo);
        SuperScoutingPanel panelthree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);

        for (int i = 0; i < 3; i++) {
            String dataName = dataNames.get(i);
            int valOne = panelone.getData().get(dataName);
            int valTwo = paneltwo.getData().get(dataName);
            int valThree = panelthree.getData().get(dataName);

            if(dataName.equals("Defense")) {
                if ((valOne != 0 && valTwo != 0 && valOne != 1 && valTwo != 1 && valOne == valTwo) || (valOne != 0 && valThree != 0 && valOne != 1 && valThree != 1 && valOne == valThree) || (valTwo != 0 && valThree != 0 && valTwo != 1 && valThree != 1 && valTwo == valThree)){
                    canProceed = false;
                    return canProceed;
                }
            } else {
                if ((valOne != 0 && valTwo != 0 && valOne == valTwo) || (valOne != 0 && valThree != 0 && valOne == valThree) || (valTwo != 0 && valThree != 0 && valTwo == valThree)){
                    canProceed = false;
                    return canProceed;
                }
            }
        }
        return canProceed;
    }


    //The next Button, to see if boolean r valid
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.endDataShortcut) {
            inflateFinalDataMenu();
        }


        if (id == R.id.finalNext) {
            if (canProceed()) {
                final SuperScoutingPanel panelOne = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelOne);
                final SuperScoutingPanel panelTwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelTwo);
                final SuperScoutingPanel panelThree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);

                levitateNum = 0;

                if(levitate.isChecked()) {
                    levitateNum = 3;
                }




                listDataValues();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < panelOne.getDataNameCount() - 1; i++) {
                                dataBase.child("/TeamInMatchDatas").child(teamNumberOne + "Q" + numberOfMatch).child(reformatDataNames(teamOneDataName.get(i))).setValue(Integer.parseInt(teamOneDataScore.get(i)));
                            }
                            for (int i = 0; i < panelTwo.getDataNameCount() - 1; i++) {
                                dataBase.child("/TeamInMatchDatas").child(teamNumberTwo + "Q" + numberOfMatch).child(reformatDataNames(teamTwoDataName.get(i))).setValue(Integer.parseInt(teamTwoDataScore.get(i)));
                            }
                            for (int i = 0; i < panelThree.getDataNameCount() - 1; i++) {
                                dataBase.child("/TeamInMatchDatas").child(teamNumberThree + "Q" + numberOfMatch).child(reformatDataNames(teamThreeDataName.get(i))).setValue(Integer.parseInt(teamThreeDataScore.get(i)));
                            }
                            if(alliance.equals("Blue Alliance")){
                                for (int i = 0; i < allianceCubesForPowerup.size(); i++){
                                    dataBase.child("/Matches").child(numberOfMatch).child("blueCubesForPowerup").child(allianceCubesForPowerup.keySet().toArray()[i].toString()).setValue(allianceCubesForPowerup.get(allianceCubesForPowerup.keySet().toArray()[i]));
                                }

                            }else{
                                for (int i = 0; i < allianceCubesForPowerup.size(); i++){
                                    dataBase.child("/Matches").child(numberOfMatch).child("redCubesForPowerup").child(allianceCubesForPowerup.keySet().toArray()[i].toString()).setValue(allianceCubesForPowerup.get(allianceCubesForPowerup.keySet().toArray()[i]));
                                }
                            }

                            if(alliance.equals("Blue Alliance")){
                                dataBase.child("/Matches").child(numberOfMatch).child("blueCubesForPowerup").child("Levitate").setValue(levitateNum);
                            }
                            else{
                                dataBase.child("/Matches").child(numberOfMatch).child("redCubesForPowerup").child("Levitate").setValue(levitateNum);
                            }
                        } catch (DatabaseException FBE) {
                            Log.e("firebase", "scoutingPage");
                        } catch (IndexOutOfBoundsException IOB) {
                            Log.e("ScoutingPage", "Index");
                        }
                    }
                }.start();
                sendExtras();
            } else {
                //toast
                final String NextString = "Active teams cannot have the same ranking values!";

                Toast.makeText(getApplicationContext(), NextString, Toast.LENGTH_LONG).show();


            }

        }


        return super.onOptionsItemSelected(item);
    }






    public void inflateFinalDataMenu() {
        final AlertDialog.Builder endDataBuilder = new AlertDialog.Builder(context);
        endDataBuilder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View finalDataView = inflater.inflate(R.layout.finaldatapoints, null);
        boostCounterView = (Counter) finalDataView.findViewById(R.id.BoostCounter);
        levitateCounterView = (Counter) finalDataView.findViewById(R.id.LevitateCounter);
        forceCounterView = (Counter) finalDataView.findViewById(R.id.ForceCounter);
        if (allianceScoreInt != null && allianceScoreInt != 0) {
            ((EditText) finalDataView.findViewById(R.id.finalScoreEditText)).setText(String.valueOf(allianceScoreInt));
        }
        if (allianceFoulInt != null && allianceFoulInt != 0) {
            ((EditText) finalDataView.findViewById(R.id.finalFoulEditText)).setText(String.valueOf(allianceFoulInt));
        }
        ((Switch) finalDataView.findViewById(R.id.didAutoQuestBoolean)).setChecked(didAutoQuest);
        ((Switch) finalDataView.findViewById(R.id.didFaceBossBoolean)).setChecked(facedTheBoss);
        if (boostC != null) {
            boostCounterView.refreshCounter(boostC);
        }
        if (levitateC != null) {
            levitateCounterView.refreshCounter(levitateC);
        }
        if (forceC != null) {
            forceCounterView.refreshCounter(forceC);
        }
        endDataBuilder.setView(finalDataView);
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
                Switch facedBoss = (Switch) d.findViewById(R.id.didFaceBossBoolean);
                Switch completedAutoQuest = (Switch) d.findViewById(R.id.didAutoQuestBoolean);

                allianceFoulData = foulText.getText().toString();
                allianceScoreData = scoreText.getText().toString();
                didAutoQuest = completedAutoQuest.isChecked();
                facedTheBoss = facedBoss.isChecked();
                boostC = boostCounterView.getDataValue();
                forceC = forceCounterView.getDataValue();
                levitateC = levitateCounterView.getDataValue();

                try {
                    allianceScoreInt = Integer.parseInt(allianceScoreData);
                    allianceFoulInt = Integer.parseInt(allianceFoulData);
                } catch (NumberFormatException nfe) {
                    allianceScoreInt = 0;
                    allianceFoulInt = 0;
                } catch (NullPointerException npe) {
                    allianceScoreInt = 0;
                    allianceFoulInt = 0;
                }

                if (alliance.equals("Blue Alliance")) {
                    dataBase.child("/Matches").child(numberOfMatch).child("blueScore").setValue(allianceScoreInt);
                    dataBase.child("/Matches").child(numberOfMatch).child("foulPointsGainedBlue").setValue(allianceFoulInt);
                    dataBase.child("/Matches").child(numberOfMatch).child("blueDidFaceBoss").setValue(facedTheBoss);
                    dataBase.child("/Matches").child(numberOfMatch).child("blueDidAutoQuest").setValue(didAutoQuest);
                    dataBase.child("/Matches").child(numberOfMatch).child("blueCubesInVaultFinal").child("Boost").setValue(boostC);
                    dataBase.child("/Matches").child(numberOfMatch).child("blueCubesInVaultFinal").child("Levitate").setValue(levitateC);
                    dataBase.child("/Matches").child(numberOfMatch).child("blueCubesInVaultFinal").child("Force").setValue(forceC);
                } else if (alliance.equals("Red Alliance")) {
                    dataBase.child("/Matches").child(numberOfMatch).child("redScore").setValue(allianceScoreInt);
                    dataBase.child("/Matches").child(numberOfMatch).child("foulPointsGainedRed").setValue(allianceFoulInt);
                    dataBase.child("/Matches").child(numberOfMatch).child("redDidFaceBoss").setValue(facedTheBoss);
                    dataBase.child("/Matches").child(numberOfMatch).child("redDidAutoQuest").setValue(didAutoQuest);
                    dataBase.child("/Matches").child(numberOfMatch).child("redCubesInVaultFinal").child("Boost").setValue(boostC);
                    dataBase.child("/Matches").child(numberOfMatch).child("redCubesInVaultFinal").child("Levitate").setValue(levitateC);
                    dataBase.child("/Matches").child(numberOfMatch).child("redCubesInVaultFinal").child("Force").setValue(forceC);

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
        intent.putExtra("teamOneNotes", teamOneNotes);
        intent.putExtra("teamTwoNotes", teamTwoNotes);
        intent.putExtra("teamThreeNotes", teamThreeNotes);
        intent.putExtra("matchNumber", numberOfMatch);
        intent.putExtra("teamNumberOne", teamNumberOne);
        intent.putExtra("teamNumberTwo", teamNumberTwo);
        intent.putExtra("teamNumberThree", teamNumberThree);
        intent.putExtra("alliance", alliance);
        intent.putExtra("dataBaseUrl", dataBaseUrl);
        intent.putExtra("allianceScore", allianceScoreData);
        intent.putExtra("allianceFoul", allianceFoulData);
        intent.putExtra("levitateCount", levitateC);
        intent.putExtra("forceCount", forceC);
        intent.putExtra("boostCount", boostC);
        intent.putExtra("completedAutoQuest", didAutoQuest);
        intent.putExtra("facedTheBoss", facedTheBoss);
        intent.putExtra("forceForPowerup", allianceCubesForPowerup.get("Force"));
        intent.putExtra("boostForPowerup", allianceCubesForPowerup.get("Boost"));
        intent.putExtra("levitateForPowerup", levitateNum);
        intent.putExtra("mute", isMute);
        intent.putStringArrayListExtra("dataNameOne", teamOneDataName);
        intent.putStringArrayListExtra("ranksOfOne", teamOneDataScore);
        intent.putStringArrayListExtra("dataNameTwo", teamTwoDataName);
        intent.putStringArrayListExtra("ranksOfTwo", teamTwoDataScore);
        intent.putStringArrayListExtra("dataNameThree", teamThreeDataName);
        intent.putStringArrayListExtra("ranksOfThree", teamThreeDataScore);
        intent.putExtras(next);
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
    }

    public String reformatDataNames(String dataName) {
        String reformattedDataName = "";
        if(!dataName.equals("Good Decisions") && !dataName.equals("Bad Decisions")){
            reformattedDataName = "rank" + dataName.replace(" ", "");
        }else if(dataName.equals("Good Decisions") || dataName.equals("Bad Decisions")){
            reformattedDataName = "num" + dataName.replace(" ", "");
        }
        return reformattedDataName;
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

                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final LinearLayout teamOneNotesLayout = (LinearLayout)layoutInflater.inflate(R.layout.team_notes, null);
                final EditText teamOneNotesEditText = (EditText)teamOneNotesLayout.findViewById(R.id.notesEditText);

                if (!teamOneNotes.equals("")) {
                    teamOneNotesEditText.setText(teamOneNotes);
                }
                teamOneNotesEditText.setTextColor(Color.BLACK);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Super Notes for team " + teamNumber)
                        .setView(teamOneNotesLayout)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                teamOneNotes = teamOneNotesEditText.getText().toString();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final LinearLayout teamTwoNotesLayout = (LinearLayout)layoutInflater.inflate(R.layout.team_notes, null);
                final EditText teamTwoNotesEditText = (EditText)teamTwoNotesLayout.findViewById(R.id.notesEditText);

                if (!teamTwoNotes.equals("")) {
                    teamTwoNotesEditText.setText(teamTwoNotes);
                }
                teamTwoNotesEditText.setTextColor(Color.BLACK);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Super Notes for " + teamNumber)
                        .setView(teamTwoNotesLayout)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                teamTwoNotes = teamTwoNotesEditText.getText().toString();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final LinearLayout teamThreeNotesLayout = (LinearLayout)layoutInflater.inflate(R.layout.team_notes, null);
                final EditText teamThreeNotesEditText = (EditText)teamThreeNotesLayout.findViewById(R.id.notesEditText);

                if (!teamThreeNotes.equals("")) {
                    teamThreeNotesEditText.setText(teamThreeNotes);
                }
                teamThreeNotesEditText.setTextColor(Color.BLACK);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Super Notes for " + teamNumber)
                        .setView(teamThreeNotesLayout)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                teamThreeNotes = teamThreeNotesEditText.getText().toString();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    public void ForceDialogs(View view){
        AlertDialog.Builder forceDialog = new AlertDialog.Builder(context);
        forceDialog.setCancelable(false);
        forceDialog.setView(R.layout.force_dialog);
        forceDialog.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog d = (Dialog) dialog;
                        ToggleButton f1 = (ToggleButton)d.findViewById(R.id.forceone);
                        ToggleButton f2 = (ToggleButton)d.findViewById(R.id.forcetwo);
                        ToggleButton f3 = (ToggleButton)d.findViewById(R.id.forcethree);
                        ArrayList<ToggleButton> forceToggles = new ArrayList<>(Arrays.asList(f1, f2, f3));
                        for (int i = 0; i < forceToggles.size(); i++){
                            if(forceToggles.get(i).isChecked()){
                                allianceCubesForPowerup.put("Force", Integer.parseInt(forceToggles.get(i).getText().toString()));
                                Log.e("MAP", allianceCubesForPowerup.toString());
                            }
                        }
                    }
                });

        forceDialog.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = forceDialog.create();
        alert11.show();


    }
    public void BoostDialogs(View view) {
        AlertDialog.Builder boostDialog = new AlertDialog.Builder(context);
        boostDialog.setCancelable(false);
        boostDialog.setView(R.layout.boost_dialog);
        boostDialog.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog d = (Dialog) dialog;
                        ToggleButton b1 = (ToggleButton)d.findViewById(R.id.boostone);
                        ToggleButton b2 = (ToggleButton)d.findViewById(R.id.boosttwo);
                        ToggleButton b3 = (ToggleButton)d.findViewById(R.id.boostthree);
                        ArrayList<ToggleButton> boostToggles = new ArrayList<>(Arrays.asList(b1, b2, b3));
                        for (int i = 0; i < boostToggles.size(); i++){
                            if(boostToggles.get(i).isChecked()){
                                allianceCubesForPowerup.put("Boost", Integer.parseInt(boostToggles.get(i).getText().toString()));
                                Log.e("MAP", allianceCubesForPowerup.toString());
                            }
                        }
                    }
                });

        boostDialog.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = boostDialog.create();
        alert11.show();
    }

}


