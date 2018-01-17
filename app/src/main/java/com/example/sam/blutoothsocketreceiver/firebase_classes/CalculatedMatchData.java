package com.example.sam.blutoothsocketreceiver.firebase_classes;

import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * Created by citruscircuits on 1/17/16
 */
@IgnoreExtraProperties
public class CalculatedMatchData extends Object {
    //TODO : Add stuff in here according to the schema changes page or the server people
    public Float predictedRedScore;
    public Float predictedBlueScore;
    public Float predictedBlueRPs;
    public Float predictedRedRPs;
    public Integer actualBlueRPs;
    public Integer actualRedRPs;
    public Boolean predictedBlueAutoQuest;
    public Boolean predictedRedAutoQuest;
    public Float redWinChance;
    public Float blueWinChance;

//LFMD (Last Four Match Data)
    public Float lfmAvgSpeed;
    public Float lfmAvgDefense;
    public Float lfmAvgAgility;
    public Float lfmAvgDrivingAbility;

    //TODO: Update other LFMDs when they are sent out
}