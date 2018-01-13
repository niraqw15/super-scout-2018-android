package com.example.sam.blutoothsocketreceiver;


import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final Map<String, String> dataBases = new HashMap<>();
    public static final String dataBaseUrl = "https://1678-scouting-2018.firebaseio.com/";
    public static String teamOneNoteHolder = "";
    public static String teamTwoNoteHolder = "";
    public static String teamThreeNoteHolder = "";
    static {
        dataBases.put("https://1678-scouting-2018.firebaseio.com/", "AIzaSyCbvnv8dQV3brle5g-wrZcdmgiF_7CnQRE");
        dataBases.put("https://1678-extreme-testing.firebaseio.com/", "lGufYCifprPw8p1fiVOs7rqYV3fswHHr9YLwiUWh");

    }

}
