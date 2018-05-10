package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.sam.blutoothsocketreceiver.firebase_classes.Match;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {
    protected SuperScoutApplication app;
    Activity context;
    EditText numberOfMatch;
    EditText teamNumberOne;
    EditText teamNumberTwo;
    EditText teamNumberThree;
    EditText searchBar;
    TextView alliance;
    ListView listView;
    Boolean isRed = false;
    Integer matchNumber = 0;
    DatabaseReference dataBase;
    String previousScore, previousFoul, previousAllianceSimple;
    Boolean facedTheBoss = false, didAutoQuest = false;
    Integer previousBoost = 0, previousLevitate = 0, previousForce = 0;

    final static String dataBaseUrl = Constants.dataBaseUrl;
    boolean isMute = false;
    boolean isOverriden;
    ToggleButton mute;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        isOverriden = false;

        Constants.teamOneNoteHolder = "";
        Constants.teamTwoNoteHolder = "";
        Constants.teamThreeNoteHolder = "";

        numberOfMatch = (EditText) findViewById(R.id.matchNumber);
        teamNumberOne = (EditText) findViewById(R.id.teamOneNumber);
        teamNumberTwo = (EditText) findViewById(R.id.teamTwoNumber);
        teamNumberThree = (EditText) findViewById(R.id.teamThreeNumber);
        mute = (ToggleButton) findViewById(R.id.mute);
        alliance = (TextView) findViewById(R.id.allianceName);
        dataBase = FirebaseDatabase.getInstance().getReference();
        //If got intent from the last activity
        checkPreviousMatchNumAndAlliance();
        updateUI();
        numberOfMatch.setText(matchNumber.toString());
        matchNumber = Integer.parseInt(numberOfMatch.getText().toString());
        disenableEditTextEditing();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView = (ListView) findViewById(R.id.view_files_received);
        listView.setAdapter(adapter);
        updateListView();


        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    updateUI();
                } catch (NullPointerException NPE) {
                    toasts("Teams not available", true);
                }
            }
        }, new IntentFilter("matches_updated"));

        //Change team numbers as the user changes the match number
        changeTeamsByMatchName();
        commitSharedPreferences();

        listenForResendClick();
        listLongClick();


    }

    //resends all data on the currently viewed list of data
    public void resendAllClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle("RESEND ALL?")
                .setMessage("RESEND ALL DATA?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        List<JSONObject> dataPoints = new ArrayList<>();
                        for (int i = 0; i < adapter.getCount(); i++) {
                            String content;
                            String name = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data/" + adapter.getItem(i);
                            content = readFile(name);
                            if (content != null) {
                                try {
                                    JSONObject data = new JSONObject(content);
                                    dataPoints.add(data);
                                } catch (JSONException json) {
                                    Log.i("JSON info", "Failed to parse JSON for resend all. unimportant");
                                }
                            }
                        }
                        resendSuperData(dataPoints);

                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void getSuperData(View view) {
        searchBar = (EditText) findViewById(R.id.searchEditText);
        searchBar.setFocusable(false);
        updateListView();
        searchBar.setFocusableInTouchMode(true);
    }

    public void catClicked(View view){
        if(mute.isChecked()){
            //Don't Do anything
            isMute = true;
        }else {
            isMute = false;
            int randNum = (int) (Math.random() * 3);
            playSound(randNum);
        }
    }
    public void playSound(int playTrak){
        if (playTrak == 0){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.catsound);
            mp.start();
        }else if(playTrak == 1){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.catsound2);
            mp.start();
        }else if(playTrak == 2){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.dog);
            mp.start();
        }else if(playTrak == 3){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.kittenmeow);
            mp.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scout, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.changeAlliance) {
            isRed = !isRed;
            SuperScoutApplication.isRed = true;
            commitSharedPreferences();
            updateUI();
        }
        if (id == R.id.scout) {
            if (!FirebaseLists.matchesList.getKeys().contains(matchNumber.toString()) && !isOverriden){
                Toast.makeText(context, "This Match Does Not Exist!", Toast.LENGTH_LONG).show();
                disenableEditTextEditing();
            }else{
                if (numberOfMatch.getText().toString().equals("")) {
                    Toast.makeText(context, "Input match name!", Toast.LENGTH_SHORT).show();
                } else if (teamNumberOne.getText().toString().equals("")) {
                    Toast.makeText(context, "Input team one number!", Toast.LENGTH_SHORT).show();
                } else if (teamNumberTwo.getText().toString().equals("")) {
                    Toast.makeText(context, "Input team two number!", Toast.LENGTH_SHORT).show();
                } else if (teamNumberThree.getText().toString().equals("")) {
                    Toast.makeText(context, "Input team three number!", Toast.LENGTH_SHORT).show();
                }
                else {
                    commitSharedPreferences();
                    Intent intent = new Intent(context, FieldSetupPage.class);
                    intent.putExtra("matchNumber", numberOfMatch.getText().toString());
                    intent.putExtra("teamNumberOne", teamNumberOne.getText().toString());
                    intent.putExtra("teamNumberTwo", teamNumberTwo.getText().toString());
                    intent.putExtra("teamNumberThree", teamNumberThree.getText().toString());
                    intent.putExtra("alliance", alliance.getText().toString());
                    intent.putExtra("dataBaseUrl", dataBaseUrl);
                    intent.putExtra("mute", isMute);
                    intent.putExtra("allianceColor", isRed);
                    startActivity(intent);
                }
            }

        } else if (id == R.id.action_override) {
            if (item.getTitle().toString().equals("Override Match and Team Number")) {
                enableEditTextEditing();
                item.setTitle("Automate");
            } else if (item.getTitle().toString().equals("Automate")) {
                View view = context.getCurrentFocus();
                updateUI();
                commitSharedPreferences();
                disenableEditTextEditing();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                item.setTitle("Override Match and Team Number");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void updateListView() {

        final EditText searchBar = (EditText)findViewById(R.id.searchEditText);
        final File dir;
        dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data");
        if (!dir.mkdir()) {
            Log.i("File Info", "Failed to make Directory. Unimportant");
        }
        final File[] files = dir.listFiles();
        adapter.clear();
        try {
            for (File tmpFile : files) {
                adapter.add(tmpFile.getName());
            }
        } catch(Exception JE) {
            Log.e("json error", "failed to add tempfile to adapter");
            toasts("Failed to show past matches.", true);
        }

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence Register, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (searchBar.getText().toString().equals("")){
                    adapter.clear();
                    searchBar.setFocusable(false);
                    try {
                        for (File tmpFile : files) {
                            adapter.add(tmpFile.getName());
                        }
                    } catch(Exception JE) {
                        Log.e("json error", "failed to add tempfile to adapter");
                        toasts("Failed to show past matches.", true);
                    }
                    searchBar.setFocusableInTouchMode(true);
                    adapter.sort(new Comparator<String>() {
                        @Override
                        public int compare(String lhs, String rhs) {
                            File lhsFile = new File(dir, lhs);
                            File rhsFile = new File(dir, rhs);
                            Date lhsDate = new Date(lhsFile.lastModified());
                            Date rhsDate = new Date(rhsFile.lastModified());
                            return rhsDate.compareTo(lhsDate);
                        }
                    });
                }else{
                    for (int i = 0; i < adapter.getCount();){
                        if(adapter.getItem(i).startsWith((searchBar.getText().toString()).toUpperCase()) || adapter.getItem(i).contains((searchBar.getText().toString()).toUpperCase())){
                            i++;
                        }else{
                            adapter.remove(adapter.getItem(i));
                        }
                    }
                }
                adapter.sort(new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        File lhsFile = new File(dir, lhs);
                        File rhsFile = new File(dir, rhs);
                        Date lhsDate = new Date(lhsFile.lastModified());
                        Date rhsDate = new Date(rhsFile.lastModified());
                        return rhsDate.compareTo(lhsDate);
                    }
                });
            }
        });

        adapter.notifyDataSetChanged();
    }
    //updates the team numbers in the front screen according to the match number and the alliance;
    private void updateUI() {
        try {
            if (FirebaseLists.matchesList.getKeys().contains(matchNumber.toString())) {
                Match match = FirebaseLists.matchesList.getFirebaseObjectByKey(matchNumber.toString());

                List<Integer> teamsOnAlliance = new ArrayList<>();
                teamsOnAlliance.addAll((isRed) ? match.redAllianceTeamNumbers : match.blueAllianceTeamNumbers);

                teamNumberOne.setText(teamsOnAlliance.get(0).toString());
                teamNumberTwo.setText(teamsOnAlliance.get(1).toString());
                teamNumberThree.setText(teamsOnAlliance.get(2).toString());
                teamNumberOne.setHint("Enter a team number");
                teamNumberTwo.setHint("Enter a team number");
                teamNumberThree.setHint("Enter a team number");
            } else {
                teamNumberOne.setHint("Not Available");
                teamNumberTwo.setHint("Not Available");
                teamNumberThree.setHint("Not Available");
                teamNumberOne.setText("");
                teamNumberTwo.setText("");
                teamNumberThree.setText("");
            }
        }catch(NullPointerException NPE){
            toasts("Teams not available", true);
        }

        alliance.setTextColor((isRed) ? Color.RED : Color.BLUE);
        alliance.setText((isRed) ? "Red Alliance" : "Blue Alliance");
    }

    public void commitSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("prefs", MODE_PRIVATE).edit();
        editor.putInt("match_number", matchNumber);
        editor.putBoolean("allianceColor", isRed);
        editor.commit();
    }

    //changes the team numbers while the user changes the match number
    public void changeTeamsByMatchName() {
        EditText numberOfMatch = (EditText) findViewById(R.id.matchNumber);
        numberOfMatch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    matchNumber = Integer.parseInt(s.toString());
                } catch (NumberFormatException NFE) {
                    matchNumber = 0;
                }
                updateUI();
            }
        });
    }

    public void enableEditTextEditing() {

        numberOfMatch.setFocusableInTouchMode(true);
        teamNumberOne.setFocusableInTouchMode(true);
        teamNumberTwo.setFocusableInTouchMode(true);
        teamNumberThree.setFocusableInTouchMode(true);
        isOverriden = true;
    }

    public void disenableEditTextEditing() {

        numberOfMatch.setFocusable(false);
        teamNumberOne.setFocusable(false);
        teamNumberTwo.setFocusable(false);
        teamNumberThree.setFocusable(false);
        isOverriden = false;
    }
    //reads the data of the clicked file
    public String readFile(String name) {
        BufferedReader file;
        try {
            file = new BufferedReader(new InputStreamReader(new FileInputStream(
                    new File(name))));
        } catch (IOException ioe) {
            Toast.makeText(context, "Failed To Open File", Toast.LENGTH_LONG).show();
            return null;
        }
        String dataOfFile = "";
        String buf;
        try {
            while ((buf = file.readLine()) != null) {
                dataOfFile = dataOfFile.concat(buf + "\n");
            }
        } catch (IOException ioe) {
            Toast.makeText(context, "Failed To Read From File", Toast.LENGTH_LONG).show();
            return null;
        }
        return dataOfFile;
    }

    public void resendSuperData(final List<JSONObject> dataPoints) {
        new Thread() {
            @Override
            public void run() {
                //read data from file
                for (int j = 0; j < dataPoints.size(); j++) {

                    try {JSONObject superData = dataPoints.get(j);

                        String allianceString = superData.getString("alliance");
                        String allianceSimple = allianceString.substring(0,1).toLowerCase() + allianceString.substring(1,allianceString.indexOf(" "));

                        String teamOneNumber = superData.getString("teamOne");
                        String teamTwoNumber = superData.getString("teamTwo");
                        String teamThreeNumber = superData.getString("teamThree");
                        String matchNum = superData.get("matchNumber").toString();
                        String matchAndTeamOne = teamOneNumber + "Q" + matchNum;
                        String matchAndTeamTwo = teamTwoNumber + "Q" + matchNum;
                        String matchAndTeamThree = teamThreeNumber + "Q" + matchNum;

                        Boolean didAutoQuest = superData.getBoolean(allianceSimple + "DidAutoQuest");
                        Boolean didFaceBoss = superData.getBoolean(allianceSimple + "DidFaceBoss");

                        Integer foulPointsGained = superData.getInt(allianceSimple + "FoulPointsGained");
                        Integer score = superData.getInt(allianceSimple + "Score");

                        JSONObject blueSwitch = superData.getJSONObject("blueSwitch");
                        JSONObject redSwitch = superData.getJSONObject("redSwitch");
                        JSONObject scale = superData.getJSONObject("scale");

                        Map<String, Object> blueSwitchJsonMap = new Gson().fromJson(blueSwitch.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
                        Map<String, Object> redSwitchJsonMap = new Gson().fromJson(redSwitch.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
                        Map<String, Object> scaleJsonMap = new Gson().fromJson(scale.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());

                        JSONObject cubesInVaultFinal = superData.getJSONObject(allianceSimple + "CubesInVaultFinal");
                        JSONObject cubesForPowerup = superData.getJSONObject(allianceSimple + "CubesForPowerup");

                        Map<String, Object> cubesInVaultFinalJsonMap = new Gson().fromJson(cubesInVaultFinal.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
                        Map<String, Object> cubesForPowerupJsonMap = new Gson().fromJson(cubesForPowerup.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());

                        JSONObject teamOneData = superData.getJSONObject(teamOneNumber);
                        JSONObject teamTwoData = superData.getJSONObject(teamTwoNumber);
                        JSONObject teamThreeData = superData.getJSONObject(teamThreeNumber);

                        Map<String, Object> teamOneDataJsonMap = new Gson().fromJson(teamOneData.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
                        Map<String, Object> teamTwoDataJsonMap = new Gson().fromJson(teamTwoData.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
                        Map<String, Object> teamThreeDataJsonMap = new Gson().fromJson(teamThreeData.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());

                        ArrayList<Integer> teamNumbers = new ArrayList<>(Arrays.asList(Integer.valueOf(teamOneNumber), Integer.valueOf(teamTwoNumber), Integer.valueOf(teamThreeNumber)));

                        for(int i = 0; i < teamNumbers.size(); i++) {
                            dataBase.child("TeamInMatchDatas").child(teamNumbers.get(i) + "Q" + matchNum).child("teamNumber").setValue(teamNumbers.get(i));
                            dataBase.child("TeamInMatchDatas").child(teamNumbers.get(i) + "Q" + matchNum).child("matchNumber").setValue(Integer.parseInt(matchNum));
                        }

                        dataBase.child("TeamInMatchDatas").child(matchAndTeamOne).updateChildren(teamOneDataJsonMap);
                        dataBase.child("TeamInMatchDatas").child(matchAndTeamTwo).updateChildren(teamTwoDataJsonMap);
                        dataBase.child("TeamInMatchDatas").child(matchAndTeamThree).updateChildren(teamThreeDataJsonMap);

                        dataBase.child("Matches").child(matchNum).child(allianceSimple + "Score").setValue(score);
                        dataBase.child("Matches").child(matchNum).child("foulPointsGained" + allianceSimple.substring(0,1).toUpperCase() + allianceSimple.substring(1)).setValue(foulPointsGained);
                        dataBase.child("Matches").child(matchNum).child("number").setValue(Integer.valueOf(matchNum));
                        dataBase.child("Matches").child(matchNum).child(allianceSimple + "DidAutoQuest").setValue(didAutoQuest);
                        dataBase.child("Matches").child(matchNum).child(allianceSimple + "DidFaceBoss").setValue(didFaceBoss);
                        dataBase.child("Matches").child(matchNum).child(allianceSimple + "CubesInVaultFinal").setValue(cubesInVaultFinalJsonMap);
                        dataBase.child("Matches").child(matchNum).child(allianceSimple + "CubesForPowerup").setValue(cubesForPowerupJsonMap);

                        dataBase.child("Matches").child(matchNum).child("blueSwitch").setValue(blueSwitchJsonMap);
                        dataBase.child("Matches").child(matchNum).child("scale").setValue(scaleJsonMap);
                        dataBase.child("Matches").child(matchNum).child("redSwitch").setValue(redSwitchJsonMap);

                    } catch (JSONException JE) {
                        Log.e("json error", "failed to get super json");
                    }
                }
                toasts("Resent Super data!", false);
            }
        }.start();
    }

    public void toasts(final String message, boolean isLongMessage) {
        if (!isLongMessage) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void checkPreviousMatchNumAndAlliance(){
        Intent backToHome = getIntent();
        if (backToHome.hasExtra("number")) {
            matchNumber = Integer.parseInt(backToHome.getExtras().getString("number")) + 1;
        } else {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            matchNumber = prefs.getInt("match_number", 1);
        }
        if (backToHome.hasExtra("shouldBeRed")) {
            isRed = getIntent().getBooleanExtra("shouldBeRed", false);
        } else {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            isRed = prefs.getBoolean("allianceColor", false);
        }
        if (!backToHome.hasExtra("mute")) {
            mute.setChecked(false);
        } else if (backToHome.hasExtra("mute")) {
            mute.setChecked(true);
        }
    }

    public void listenForResendClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                name = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data/" + name;

                final String fileName = name;
                final String[] nameOfResendMatch = name.split("Q");
                new AlertDialog.Builder(context)
                        .setTitle("RESEND DATA?")
                        .setMessage("RESEND " + "Q" + nameOfResendMatch[1] + "?")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String content = readFile(fileName);
                                JSONObject superData;
                                try {
                                    superData = new JSONObject(content);
                                } catch (JSONException jsone) {
                                    Toast.makeText(context, "Not a valid JSON", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                List<JSONObject> dataPoints = new ArrayList<>();
                                dataPoints.add(superData);
                                resendSuperData(dataPoints);
                            }
                        }).show();
            }
        });
    }

    public void listLongClick(){

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

                final String name = parent.getItemAtPosition(position).toString();
                String splitName[] = name.split("_");
                final String editMatchNumber = splitName[0].replace("Q", "");
                String filePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data/" + name;
                final String content = readFile(filePath);
                final JSONObject superData;
                try {
                    superData = new JSONObject(content);

                    String allianceString = superData.getString("alliance");
                    previousAllianceSimple = "";
                    if(allianceString.equals("Blue Alliance")){
                        previousAllianceSimple = "blue";
                    }else if(allianceString.equals("Red Alliance")){
                        previousAllianceSimple = "red";
                    }

                    previousScore = superData.get(previousAllianceSimple + "Score").toString();
                    previousFoul = superData.get(previousAllianceSimple + "FoulPointsGained").toString();
                    facedTheBoss = superData.getBoolean(previousAllianceSimple + "DidFaceBoss");
                    didAutoQuest = superData.getBoolean(previousAllianceSimple + "DidAutoQuest");

                    JSONObject jsonCubesInVaultFinal = superData.getJSONObject(previousAllianceSimple + "CubesInVaultFinal");
                    previousBoost = jsonCubesInVaultFinal.getInt("Boost");
                    previousForce = jsonCubesInVaultFinal.getInt("Force");
                    previousLevitate = jsonCubesInVaultFinal.getInt("Levitate");

                } catch (JSONException JE) {
                    Log.e("read Super Data", "failed");
                    toasts("Failed to read Super Data.", false);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Alliance Score for " + name + ": ");
                final View finalDataPtsView = LayoutInflater.from(context).inflate(R.layout.finaldatapoints, null);
                ((EditText) finalDataPtsView.findViewById(R.id.finalScoreEditText)).setText(previousScore);
                ((EditText) finalDataPtsView.findViewById(R.id.finalFoulEditText)).setText(previousFoul);
                ((Counter) finalDataPtsView.findViewById(R.id.BoostCounter)).refreshCounter(previousBoost);
                ((Counter) finalDataPtsView.findViewById(R.id.LevitateCounter)).refreshCounter(previousLevitate);
                ((Counter) finalDataPtsView.findViewById(R.id.ForceCounter)).refreshCounter(previousForce);
                ((Switch) finalDataPtsView.findViewById(R.id.didFaceBossBoolean)).setChecked(facedTheBoss);
                ((Switch) finalDataPtsView.findViewById(R.id.didAutoQuestBoolean)).setChecked(didAutoQuest);

                if(previousAllianceSimple.equals("blue")) {
                    ((TextView) finalDataPtsView.findViewById(R.id.finalScoreTextView)).setTextColor(Color.BLUE);
                } else if(previousAllianceSimple.equals("red")) {
                    ((TextView) finalDataPtsView.findViewById(R.id.finalScoreTextView)).setTextColor(Color.RED);
                } else {
                    ((TextView) finalDataPtsView.findViewById(R.id.finalScoreTextView)).setTextColor(Color.BLACK);
                }

                builder.setView(finalDataPtsView);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog d = (Dialog) dialog;
                        File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data/" + name);
                        dir.mkdir();
                        previousScore = ((EditText) d.findViewById(R.id.finalScoreEditText)).getText().toString(); //Now it's the new score
                        previousFoul = ((EditText) d.findViewById(R.id.finalFoulEditText)).getText().toString(); //Foul refers to foul points gained by that team
                        previousBoost = ((Counter) d.findViewById(R.id.BoostCounter)).getDataValue();
                        previousLevitate = ((Counter) d.findViewById(R.id.LevitateCounter)).getDataValue();
                        previousForce = ((Counter) d.findViewById(R.id.ForceCounter)).getDataValue();
                        facedTheBoss = ((Switch) d.findViewById(R.id.didFaceBossBoolean)).isChecked();
                        didAutoQuest = ((Switch) d.findViewById(R.id.didAutoQuestBoolean)).isChecked();

                        if(!previousScore.equals("") && !previousFoul.equals("")) {
                            try {
                                JSONObject superScore = new JSONObject(content);
                                PrintWriter dirWriter = new PrintWriter(new FileOutputStream(dir, false));

                                JSONObject jsonCubesInVaultFinal = new JSONObject();
                                jsonCubesInVaultFinal.put("Boost", previousBoost);
                                jsonCubesInVaultFinal.put("Levitate", previousLevitate);
                                jsonCubesInVaultFinal.put("Force", previousForce);

                                superScore.put(previousAllianceSimple + "Score", Integer.valueOf(previousScore));
                                superScore.put(previousAllianceSimple + "FoulPointsGained", Integer.valueOf(previousFoul));
                                superScore.put(previousAllianceSimple + "DidFaceBoss", facedTheBoss);
                                superScore.put(previousAllianceSimple + "DidAutoQuest", didAutoQuest);
                                superScore.put(previousAllianceSimple + "CubesInVaultFinal", jsonCubesInVaultFinal);

                                dataBase.child("Matches").child(editMatchNumber).child(previousAllianceSimple + "Score").setValue(Integer.parseInt(previousScore));
                                dataBase.child("Matches").child(editMatchNumber).child("foulPointsGained" + previousAllianceSimple.substring(0,1).toUpperCase() + previousAllianceSimple.substring(1)).setValue(Integer.parseInt(previousFoul));
                                dataBase.child("Matches").child(editMatchNumber).child(previousAllianceSimple + "DidFaceBoss").setValue(facedTheBoss);
                                dataBase.child("Matches").child(editMatchNumber).child(previousAllianceSimple + "DidAutoQuest").setValue(didAutoQuest);
                                dataBase.child("Matches").child(editMatchNumber).child(previousAllianceSimple + "CubesInVaultFinal").child("Boost").setValue(previousBoost);
                                dataBase.child("Matches").child(editMatchNumber).child(previousAllianceSimple + "CubesInVaultFinal").child("Levitate").setValue(previousLevitate);
                                dataBase.child("Matches").child(editMatchNumber).child(previousAllianceSimple + "CubesInVaultFinal").child("Force").setValue(previousForce);

                                dirWriter.println(superScore.toString());
                                dirWriter.close();
                                toasts("Score Updated.", false);
                            } catch (JSONException JSONex) {
                                JSONex.printStackTrace();
                                toasts("Failed to Save to Storage", false);
                            } catch (FileNotFoundException fnfe) {
                                fnfe.printStackTrace();
                                toasts("Storage Not Located.", false);
                            }
                        } else {
                            toasts("Please Enter Valid Inputs", false);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                return true;
            }
        });

    }

}
