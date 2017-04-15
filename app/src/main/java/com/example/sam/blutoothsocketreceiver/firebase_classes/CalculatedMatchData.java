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
    public Integer predictedRedScore;
    public Integer predictedBlueScore;
    public Float sdPredictedRedScore;
    public Float sdPredictedBlueScore;
    public Float redWinChance;
    public Float blueWinChance;
    public Integer predictedBlueRPs;
    public Integer actualBlueRPs;
    public Integer predictedRedRPs;
    public Integer actualRedRPs;
    public Float fortykPAChanceRed;
    public Float fortykPAChanceBlue;
    public Float allRotorsTurningChanceRed;
    public Float allRotorsTurningChanceBlue;
    public Float disfunctionalPercentage;
    public Float gearAbility;
    public Float allRotorsAbility;

    //Last four match data
    public Float lfmDisabledPercentage;
    public Float lfmIncapacitatedPercentage;
    public Float lmfAvgGearsPlacedAuto;
    public Float lfmAvgHighShotsAuto;
    public Float lfmAvgLowShotsAuto;
    public Float lfmBaselineReachedPercentage;

    public Float lfmAvgGearsPlacedTele;
    public Float lfmAvgGearLoaderIntakesTele;
    public Float lfmAvgHighShotsTele;
    public Float lfmAvgLowShotsTele;
    public Float lfmAvgKeyShotTime;
    public Float lfmAvgLiftoffTime;

    public Float lfmLiftoffPercentage;
    public Float lfmFirstPickAbility;
    public Float lfmSecondPickAbility;
    public Float lfmThirdPickAbility;
    public Float lfmGearAbility;
    public Float lfmAvgAgility;
    public Float lfmAvgSpeed;
    public Float lfmAvgBallControl;
    public Float lfmAvgGearControl;
    public Float lfmAvgDefense;

}