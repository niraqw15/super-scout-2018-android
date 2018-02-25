package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    String previousScore, previousFoul;
    Boolean facedTheBoss = false, didAutoQuest = false;
    Integer boost = 0, levitate = 0, force = 0;
    final static String dataBaseUrl = Constants.dataBaseUrl;
    boolean isMute = false;
    boolean isOverriden;
    ToggleButton mute;
    ArrayAdapter<String> adapter;
    Spannable wordToSpan;
    int shade;
    Thread thread;
    Runnable runnable;
    boolean canRun = true;
    boolean stopThread = false;

    //THIS IS THE MASTER BRANCH

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
        updateUI(true);
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
                    updateUI(false);
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

        initializeRunnable();
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
        //listenForFileListClick();
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
            if(canRun) {
                isRed = !isRed;
                SuperScoutApplication.isRed = true;
                commitSharedPreferences();
                updateUI(false);

                //Important: Just for fun
                funColorChange(isRed);
            } else {
                stopThread = true;
            }
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
                } //else if(teamNumberOne.getText().toString().equals("Not Available")){
//                    Toast.makeText(context, "This Match Does Not Exist!", Toast.LENGTH_SHORT).show();
//                }
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
                updateUI(false);
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
    private void updateUI(boolean updateAlliance) {
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

        if(updateAlliance) {
            alliance.setTextColor((isRed) ? Color.RED : Color.BLUE);
            alliance.setText((isRed) ? "Red Alliance" : "Blue Alliance");
        }
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
                updateUI(false);
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

                        //TODO: Add this line in.
                        //dataBase.child("Matches").child(matchNum).child(allianceSimple + "AllianceTeamNumbers").setValue(teamNumbers); //TODO: Convert this to Gson
                        dataBase.child("Matches").child(matchNum).child(allianceSimple + "Score").setValue(score);
                        dataBase.child("Matches").child(matchNum).child("foulPointsGained" + allianceSimple.substring(0,1).toUpperCase() + allianceSimple.substring(1)).setValue(foulPointsGained);
                        dataBase.child("Matches").child(matchNum).child("number").setValue(Integer.valueOf(matchNum));
                        dataBase.child("Matches").child(matchNum).child(allianceSimple + "DidAutoQuest").setValue(didAutoQuest);
                        dataBase.child("Matches").child(matchNum).child(allianceSimple + "DidFaceBoss").setValue(didFaceBoss);
                        dataBase.child("Matches").child(matchNum).child(allianceSimple + "CubesInVaultFinal").setValue(cubesInVaultFinalJsonMap);
                        dataBase.child("Matches").child(matchNum).child(allianceSimple + "CubesForPowerup").setValue(cubesForPowerupJsonMap);

                        //TODO: Nathan: Add check against current Firebase (low priority)
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
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) { //TODO: Incomplete. Add ability to change all data and autofill with previous data.
                /* TODO: Add this back in when it works.
                final String name = parent.getItemAtPosition(position).toString();
                String splitName[] = name.split("_");
                final String editMatchNumber = splitName[0].replace("Q", "");
                String filePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data/" + name;
                final String content = readFile(filePath);
                final JSONObject superData;
                try {
                    superData = new JSONObject(content);
                    //TODO: Nathan: Show Faced the Boss, Did Auto Quest, and Cube numbers for final alliance data
                    //TODO: Nathan: Make sure all external data is received
                    String allianceString = superData.getString("alliance");
                    String allianceSimple = "";
                    if(allianceString.equals("Blue Alliance")){
                        allianceSimple = "blue";
                    }else if(allianceString.equals("Red Alliance")){
                        allianceSimple = "red";
                    }
                    previousScore = superData.get(allianceSimple + "").toString();
                    previousFoul = superData.get(allianceSimple + "").toString();
                    facedTheBoss = superData.getBoolean(allianceSimple + "DidFaceTheBoss");
                    didAutoQuest = superData.getBoolean(allianceSimple + "DidAutoQuest");

                    JSONObject jsonCubesInVaultFinal = superData.getJSONObject(allianceSimple + "CubesInVaultFinal");
                    boost = jsonCubesInVaultFinal.getInt("Boost");
                    force = jsonCubesInVaultFinal.getInt("Force");
                    levitate = jsonCubesInVaultFinal.getInt("Levitate");

                } catch (JSONException JE) {
                    Log.e("read Super Data", "failed");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context); //TODO: Nathan: Add an edit alertdialog for fieldsetup and notes (maybe-check)
                builder.setTitle("Edit Alliance Score for " + name + ": ");
                final View finalDataPtsView = LayoutInflater.from(context).inflate(R.layout.finaldatapoints, null);
                ((EditText) finalDataPtsView.findViewById(R.id.finalScoreEditText)).setText(previousScore);
                ((EditText) finalDataPtsView.findViewById(R.id.finalFoulEditText)).setText(previousFoul);
                ((Counter) finalDataPtsView.findViewById(R.id.BoostCounter)).refreshCounter(boost);
                ((Counter) finalDataPtsView.findViewById(R.id.LevitateCounter)).refreshCounter(levitate);
                ((Counter) finalDataPtsView.findViewById(R.id.ForceCounter)).refreshCounter(force);
                ((Switch) finalDataPtsView.findViewById(R.id.didFaceBossBoolean)).setChecked(facedTheBoss);
                ((Switch) finalDataPtsView.findViewById(R.id.didAutoQuestBoolean)).setChecked(didAutoQuest);

                builder.setView(finalDataPtsView);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog d = (Dialog) dialog;
                        File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Super_scout_data/" + name);
                        dir.mkdir();
                        //TODO: Nathan: Get all values here and send to firebase.
                        previousScore = ((EditText) d.findViewById(R.id.finalScoreEditText)).getText().toString(); //Now it's the new score
                        previousFoul = ((EditText) d.findViewById(R.id.finalFoulEditText)).getText().toString(); //Foul refers to foul points gained by that team
                        boost = ((Counter) d.findViewById(R.id.BoostCounter)).getDataValue();
                        levitate = ((Counter) d.findViewById(R.id.LevitateCounter)).getDataValue();
                        force = ((Counter) d.findViewById(R.id.ForceCounter)).getDataValue();
                        facedTheBoss = ((Switch) d.findViewById(R.id.didFaceBossBoolean)).isChecked();
                        didAutoQuest = ((Switch) d.findViewById(R.id.didAutoQuestBoolean)).isChecked();

                        if(!previousScore.equals("") && !previousFoul.equals("")) {
                            try {
                                JSONObject superScore = new JSONObject(content);
                                PrintWriter dirWriter = new PrintWriter(new FileOutputStream(dir, false));
                                if(isRed) {
                                    superScore.put("Red Alliance Score", Integer.valueOf(previousScore));
                                    superScore.put("Red Alliance Foul", Integer.valueOf(previousFoul));
                                    dataBase.child("Matches").child(editMatchNumber).child("redScore").setValue(Integer.parseInt(previousScore));
                                    dataBase.child("Matches").child(editMatchNumber).child("foulPointsGainedRed").setValue(Integer.parseInt(previousFoul));
                                } else {
                                    superScore.put("Blue Alliance Score", Integer.valueOf(previousScore));
                                    superScore.put("Blue Alliance Foul", Integer.valueOf(previousFoul));
                                    dataBase.child("Matches").child(editMatchNumber).child("blueScore").setValue(Integer.parseInt(previousScore));
                                    dataBase.child("Matches").child(editMatchNumber).child("foulPointsGainedBlue").setValue(Integer.parseInt(previousFoul));
                                }
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
                */
                //TODO: Temporary, remove when feature is re-implemented.
                toasts("That feature is not available right now.", false);


                return true;
            }
        });

    }

    //Important: Just for fun!
    private void initializeRunnable() {
        runnable = new Runnable()
        {

            @Override
            public void run() {
                canRun = false;
                shade = 0;
                boolean isR = isRed;
                /*
                while (!Thread.interrupted() && shade < 256) {
                    for (int i = 0; i < wordToSpan.length(); i++) {
                        //TODO: Use correct color depending on alliance
                        int color = (isR) ? Color.argb(255, shade, 0, 0) : Color.argb(255, 0, 0, shade);
                        wordToSpan.setSpan(new ForegroundColorSpan(color), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    shade += 10;
                    runOnUiThread(new Runnable() // start actions in UI thread
                    {

                        @Override
                        public void run() {
                            alliance.setText(wordToSpan);
                        }
                    });

                    try {
                        Thread.sleep(100);
                    } catch(InterruptedException e) {
                        //TODO: Add color reset?
                        canRun = true;
                        return;
                    }
                }
                */

                int pos = 0;
                while (!Thread.interrupted() && pos < 22 && !stopThread) {
                    wordToSpan.setSpan(new ForegroundColorSpan(colorWheel(pos)), 0, wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    pos++;
                    if(pos == 22) pos = 0;
                    runOnUiThread(new Runnable() // start actions in UI thread
                    {

                        @Override
                        public void run() {
                            alliance.setText(wordToSpan);
                        }
                    });

                    try {
                        Thread.sleep(100);
                    } catch(InterruptedException e) {
                        //TODO: Add color reset?
                        canRun = true;
                        return;
                    }
                }
                canRun = true;
            }
        };
    }

    private void funColorChange(boolean isRed) {

        stopThread = false;
        if(!canRun) return;
        Thread thread = new Thread(runnable);
        wordToSpan = new SpannableString((isRed) ? "Red Alliance" : "Blue Alliance");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();

        /*
        thread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                shade = 255;
                while (!Thread.interrupted() && shade > 0)
                    try
                    {
                        for (int i = 0; i < wordToSpan.length(); i++) {
                            int color = Color.argb(255, shade, 0, 0);
                            wordToSpan.setSpan(new ForegroundColorSpan(color), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        shade--;
                        runOnUiThread(new Runnable() // start actions in UI thread
                        {

                            @Override
                            public void run()
                            {
                                alliance.setText(wordToSpan);
                            }
                        });

                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        // ooops
                    }
            }
        });
        thread.start();
        /*
        while(condition) {
            for(shade = 0; shade < 256; shade++) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < wordToSpan.length(); i++) {
                            int color = Color.argb(255, shade, shade, shade);
                            wordToSpan.setSpan(new ForegroundColorSpan(color/*Color.parseColor("#0000FF")* /), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alliance.setText(wordToSpan);
                            }
                        });
                    }
                }, 100);
            }

            condition = false;
        }
        */
    }

    public int colorWheel(int position) {
        double freq = 0.3;
        int red = (int) Math.round(Math.sin(freq*position + 0) * 127 + 128);
        int green = (int) Math.round(Math.sin(freq*position + 2) * 127 + 128);
        int blue  = (int) Math.round(Math.sin(freq*position + 4) * 127 + 128);
        return Color.argb(255, red, green, blue);
    }

}