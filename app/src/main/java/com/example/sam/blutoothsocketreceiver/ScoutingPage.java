package com.example.sam.blutoothsocketreceiver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

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
    String NextString;
    TextView teamNumberOneTextview;
    TextView teamNumberTwoTextview;
    TextView teamNumberThreeTextview;
    ArrayList<String> teamOneDataName;
    ArrayList<String> teamOneDataScore;
    ArrayList<String> teamTwoDataName;
    ArrayList<String> teamTwoDataScore;
    ArrayList<String> teamThreeDataName;
    ArrayList<String> teamThreeDataScore;
    Integer allianceScoreInt = 0;
    Integer allianceFoulInt = 0;
    Boolean isMute;
    JSONObject object;
    Intent next;
    DatabaseReference dataBase;
    Boolean isRed;
    String teamOneNotes;
    String teamTwoNotes;
    String teamThreeNotes;


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
        dataBase = FirebaseDatabase.getInstance().getReference();
        setPanels();
        initializeTeamTextViews();
        context = this;

        teamOneNotes = "";
        teamTwoNotes = "";
        teamThreeNotes = "";
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

    // work these two below
    public Boolean canProceed() {
        Boolean canProceed = true;
        ArrayList<String> dataNames = new ArrayList<>(Arrays.asList("Speed", "Agility", "Defense"));
        SuperScoutingPanel panelone = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(panelOne);
        SuperScoutingPanel paneltwo = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(panelTwo);
        SuperScoutingPanel panelthree = (SuperScoutingPanel) getSupportFragmentManager().findFragmentById(R.id.panelThree);
        for (int i = 0; i < 3; i++) {
            if (panelone.getData().get(dataNames.get(i)) == paneltwo.getData().get(dataNames.get(i)) || (panelone.getData().get(dataNames.get(i))) == (panelthree.getData().get(dataNames.get(i))) || (paneltwo.getData().get(dataNames.get(i)) == panelthree.getData().get(dataNames.get(i))) ){
            canProceed = false;
            return canProceed;}
           /* if (panelone.getData().get(dataNames.get(i)) == paneltwo.getData().get(dataNames.get(i))){
            }*/
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
                final String NextString = "Teams cannot have the same ranking values!";

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
        if (allianceScoreInt != null && allianceScoreInt != 0) {
            ((EditText) finalDataView.findViewById(R.id.finalScoreEditText)).setText(String.valueOf(allianceScoreInt));
        }
        if (allianceFoulInt != null && allianceFoulInt != 0) {
            ((EditText) finalDataView.findViewById(R.id.finalFoulEditText)).setText(String.valueOf(allianceFoulInt));
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
                allianceFoulData = foulText.getText().toString();
                allianceScoreData = scoreText.getText().toString();
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

                } else if (alliance.equals("Red Alliance")) {
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

                final EditText pilotNotesETOne = new EditText(context);

                if (!teamOneNotes.equals("")) {
                    pilotNotesETOne.setText(teamOneNotes);
                }
                pilotNotesETOne.setTextColor(Color.BLACK);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("SuperNotes for team " + teamNumber)
                        .setView(pilotNotesETOne)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                teamOneNotes = pilotNotesETOne.getText().toString();
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

                final EditText pilotNotesETTwo = new EditText(context);

                if (!teamTwoNotes.equals("")) {
                    pilotNotesETTwo.setText(teamTwoNotes);
                }
                pilotNotesETTwo.setTextColor(Color.BLACK);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Pilot Notes for " + teamNumber)
                        .setView(pilotNotesETTwo)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                teamTwoNotes = pilotNotesETTwo.getText().toString();
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

                final EditText pilotNotesETThree = new EditText(context);

                if (!teamThreeNotes.equals("")) {
                    pilotNotesETThree.setText(teamThreeNotes);
                }
                pilotNotesETThree.setTextColor(Color.BLACK);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Pilot Notes for " + teamNumber)
                        .setView(pilotNotesETThree)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                teamThreeNotes = pilotNotesETThree.getText().toString();
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


    public void ForceDialogs(View view) {
        Button ForceButton;
        ForceButton = (Button)findViewById(R.id.Force);
        final Dialog forceDialog = new Dialog(context); // Context, this, etc.
        forceDialog.setContentView(R.layout.force_dialog);
        forceDialog.setTitle("Force");
        forceDialog.show();
    }

    public void BoostDialogs(View view) {
        Button BoostButton;
        BoostButton = (Button)findViewById(R.id.Force);
        final Dialog forceDialog = new Dialog(context); // Context, this, etc.
        forceDialog.setContentView(R.layout.boost_dialog);
        forceDialog.setTitle("Boost");
        forceDialog.show();
    }

    public void Lev(View view) {
        final ToggleButton Levtoggle = (ToggleButton) findViewById(R.id.Lev);
        Levtoggle.setChecked(true);
    }

    public void forceone(View view){
        final ToggleButton forceonetoggle = (ToggleButton) findViewById(R.id.forceone);
        forceonetoggle.setChecked(true);
    }

    public void forcetwo(View view) {
        final ToggleButton forcetwotoggle = (ToggleButton) findViewById(R.id.forcetwo);
        forcetwotoggle.setChecked(true);
    }

    public void forcethree(View view) {
        final ToggleButton forcethreetoggle = (ToggleButton) findViewById(R.id.forcethree);
        forcethreetoggle.setChecked(true);
    }

    public void boostone(View view) {
        final ToggleButton boostonetoggle = (ToggleButton) findViewById(R.id.boostone);
        boostonetoggle.setChecked(true);
    }

    public void boosttwo(View view) {
        final ToggleButton boosttwotoggle = (ToggleButton) findViewById(R.id.boosttwo);
        boosttwotoggle.setChecked(true);
    }

    public void boostthree(View view) {
        final ToggleButton boostthreetoggle = (ToggleButton) findViewById(R.id.boostthree);
        boostthreetoggle.setChecked(true);
    }
}


