package com.example.sam.blutoothsocketreceiver.firebase_classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by citruscircuits on 1/17/16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@IgnoreExtraProperties
public class Match extends Object {
	//TODO : Keep up with schema changes
	public CalculatedMatchData calculatedMatchData;
	@PropertyName("number")
	public Integer number;
	@PropertyName("redAllianceTeamNumbers")
	public List<Integer> redAllianceTeamNumbers;
	@PropertyName("blueAllianceTeamNumbers")
	public List<Integer> blueAllianceTeamNumbers;
}