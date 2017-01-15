package com.example.sam.blutoothsocketreceiver.firebase_classes;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by citruscircuits on 1/17/16
 */
@IgnoreExtraProperties
public class TeamInMatchData extends Object {
	public Integer teamNumber;
	public Integer matchNumber;
	public String scoutName;
	public Boolean didReachBaselineAuto;
	public Boolean didPotentiallyConflictingAuto;
	public Integer numHoppersOpenedAuto;
	public Integer numGearsPlacedAuto;
	public Integer numGearsPlacedTele;
	public Integer numHoppersOpenedTele;
	public Integer numGearGroundIntakesTele;
	public Integer numGearHumanIntakesTele;
	public Boolean didLiftoff;
	public Boolean didStartDisabled;
	public Boolean didBecomeIncapacitated;
	public Integer rankSpeed;
	public Integer rankAgility;
	public Integer rankGearControl;
	public Integer rankBallControl;
	public Integer rankDefense;
	public Integer numHighShotsTele;
	public Integer numHighShotsAuto;
	public Integer numLowShotsTele;
	public Integer numLowShotsAuto;

	public List<Map<String, Object>> highShotTimesForBoilerAuto;
	public List<Map<String, Object>> lowShotTimesForBoilerAuto;
	public List<Map<String, Object>> highShotTimesForBoilerTele;
	public List<Map<String, Object>> lowShotTimesForBoilerTele;
}