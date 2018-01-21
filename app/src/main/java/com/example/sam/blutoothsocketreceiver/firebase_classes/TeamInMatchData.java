package com.example.sam.blutoothsocketreceiver.firebase_classes;

import com.google.firebase.database.IgnoreExtraProperties;

import org.jcodec.common.DictionaryCompressor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by citruscircuits on 1/17/16
 */
@IgnoreExtraProperties
public class TeamInMatchData extends Object {
	//TODO : Keep up with schema changes
	public Integer teamNumber;
	public Integer matchNumber;
	public String scoutName;
	public String superNotes;
	public Integer rankAgility;
	public Integer rankDefense;
	public Integer rankSpeed;
	public Integer numGoodDecisions;
	public Integer numBadDecisions;



}
