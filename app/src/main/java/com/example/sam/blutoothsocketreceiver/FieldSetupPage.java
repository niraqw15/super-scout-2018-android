package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by niraq on 1/13/2018.
 */

public class FieldSetupPage extends AppCompatActivity{

    Activity context;
    Intent previous;
    Intent next;

    String numberOfMatch;
    //Intent next;
    DatabaseReference dataBase;

    PlateConfig plateConfig;
    boolean isRed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fieldsetup);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        previous = getIntent();
        getExtrasForSetup();
        dataBase = FirebaseDatabase.getInstance().getReference();

        plateConfig = new PlateConfig(context, isRed);
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
        getMenuInflater().inflate(R.menu.teleop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.teleop) {
            Map<Integer, String> configMap = plateConfig.getConfig();

            if (configMap.containsValue("noColor")){
                //Toast.makeText(context, "Select a configuration for each plate!", Toast.LENGTH_LONG).show();
                Toast toast = Toast.makeText(context,"Select a configuration for each plate!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 65);
                toast.show();
            } else {
                next = new Intent(context, ScoutingPage.class);
                next.putExtras(previous);
                //TODO: Nathan: Add extras from this activity & save config to local.

                dataBase.child("Matches").child(numberOfMatch).child("blueSwitch").child("left").setValue(configMap.get(R.id.blueTopPlateButton));
                dataBase.child("Matches").child(numberOfMatch).child("blueSwitch").child("right").setValue(configMap.get(R.id.blueBottomPlateButton));
                dataBase.child("Matches").child(numberOfMatch).child("scale").child("left").setValue(configMap.get(R.id.scaleTopPlateButton));
                dataBase.child("Matches").child(numberOfMatch).child("scale").child("right").setValue(configMap.get(R.id.scaleBottomPlateButton));
                dataBase.child("Matches").child(numberOfMatch).child("redSwitch").child("left").setValue(configMap.get(R.id.redTopPlateButton));
                dataBase.child("Matches").child(numberOfMatch).child("redSwitch").child("right").setValue(configMap.get(R.id.redBottomPlateButton));

                //TODO: Nathan: Check data against other scout (check if what is currently in the switches and plates conflicts with what this has, notify).
                startActivity(next);
            }



        }
        return super.onOptionsItemSelected(item);
    }

    public void plateButtonPress(View plateButton)
    {
        plateConfig.swapColor(plateButton);
    }

    public void getExtrasForSetup() {

        numberOfMatch = previous.getExtras().getString("matchNumber");
        isRed = previous.getExtras().getBoolean("allianceColor");
    }
}
