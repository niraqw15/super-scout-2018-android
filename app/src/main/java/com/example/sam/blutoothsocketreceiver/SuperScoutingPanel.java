package com.example.sam.blutoothsocketreceiver;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 5/12/16.
 */
public class SuperScoutingPanel extends Fragment {
    Boolean isRed;
    public static ArrayList<Integer> Speed;
    public static ArrayList<Integer> GearControl;
    public static ArrayList<Integer> BallControl;
    public static ArrayList<Integer> Agility;
    public static ArrayList<Integer> Defense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Speed = new ArrayList<>(Arrays.asList(0, 0, 3, 0, 0));
        GearControl = new ArrayList<>(Arrays.asList(0, 0, 3, 0, 0));
        BallControl = new ArrayList<>(Arrays.asList(0, 0, 3, 0, 0));
        Agility = new ArrayList<>(Arrays.asList(0, 0, 3, 0, 0));
        Defense = new ArrayList<>(Arrays.asList(0, 0, 3, 0, 0));

        return inflater.inflate(R.layout.super_scouting_panel, container, false);
    }

    public void setAllianceColor(boolean allianceColor) {
        TextView teamNumberTextView = (TextView)getView().findViewById(R.id.teamNumberTextView);
        this.isRed = allianceColor;
        if (isRed){
            teamNumberTextView.setTextColor(Color.RED);
        }else {
            teamNumberTextView.setTextColor(Color.BLUE);
        }
    }

    public void setTeamNumber(String teamNumber) {
        TextView teamNumberTextView = (TextView)getView().findViewById(R.id.teamNumberTextView);
        teamNumberTextView.setText(teamNumber);
    }

    public int getDataNameCount(){
        int numOfDataName = ((LinearLayout)getView()).getChildCount();
        Log.e("dataNameCount", Integer.toString(numOfDataName));
        return numOfDataName;
    }


    public Map getData(){
        Map<String, Integer> mapOfData = new HashMap<>();
        LinearLayout rootLayout = (LinearLayout)getView();
        Counter counter;
        for (int i = 0; i < ((LinearLayout)getView()).getChildCount() - 1; i++) {
            counter = (Counter)rootLayout.getChildAt(i + 1);
            String dataName = counter.getDataName();
            Integer dataScore = counter.getDataValue();
            mapOfData.put(dataName, dataScore);
        }

        return mapOfData;
    }

    public void addToFourApplied(String dataName, Boolean fourApplied){
        if(fourApplied == null){
            fourApplied = false;
        }

    }

}

