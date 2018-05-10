package com.example.sam.blutoothsocketreceiver.firebase_classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import com.shaded.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.shaded.fasterxml.jackson.databind.KeyDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by citruscircuits on 1/17/16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@IgnoreExtraProperties
public class Match extends Object {
	public CalculatedMatchData calculatedMatchData;
	public Integer redScore;
	public Integer blueScore;
	public Map<String, Object> blueCubesForPowerup;
	public Map<String, Object> blueCubesInVaultFinal;
	public Boolean blueDidAutoQuest;
	public Boolean blueDidFaceBoss;
	public Map<String, Object> blueSwitch;
	public Map<String, Object> redCubesForPowerup;
	public Map<String, Object> redCubesInVaultFinal;
	public Boolean redDidAutoQuest;
	public Boolean redDidFaceBoss;
	public Map<String, Object> redSwitch;
	public Map<String, Object> scale;
	public Integer foulPointsGainedRed;
	public Integer foulPointsGainedBlue;

	@PropertyName("number")
	public Integer number;
	@PropertyName("redAllianceTeamNumbers")
	public List<Integer> redAllianceTeamNumbers;
	@PropertyName("blueAllianceTeamNumbers")
	public List<Integer> blueAllianceTeamNumbers;
}