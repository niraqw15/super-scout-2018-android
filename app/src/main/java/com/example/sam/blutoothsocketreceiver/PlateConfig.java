package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.view.View;
import android.widget.Button;

import org.jcodec.common.DictionaryCompressor;
import org.jcodec.common.RunLength;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by niraq on 1/15/2018.
 */

//This class is for getting and setting the color of plates in FieldSetup.
public class PlateConfig {

    private Context context;
    private boolean isRed;

    private Button blueTopPlateButton;
    private Button blueBottomPlateButton;
    private Button scaleTopPlateButton;
    private Button scaleBottomPlateButton;
    private Button redTopPlateButton;
    private Button redBottomPlateButton;

    private Map<Integer, String> configMap;

    private String red;
    private String blue;
    private String grey;

    public PlateConfig(Context context, boolean isRed) {
        this.context = context;
        this.isRed = isRed;

        red = "#FF0000";
        blue = "#0000FF";
        grey = "#CCCCCC";

        configMap = new HashMap<>();

        blueTopPlateButton = (Button) ((Activity)context).findViewById(R.id.blueTopPlateButton);
        configMap.put(R.id.blueTopPlateButton, "noColor");
        blueBottomPlateButton = (Button) ((Activity)context).findViewById(R.id.blueBottomPlateButton);
        configMap.put(R.id.blueBottomPlateButton, "noColor");
        scaleTopPlateButton = (Button) ((Activity)context).findViewById(R.id.scaleTopPlateButton);
        configMap.put(R.id.scaleTopPlateButton, "noColor");
        scaleBottomPlateButton = (Button) ((Activity)context).findViewById(R.id.scaleBottomPlateButton);
        configMap.put(R.id.scaleBottomPlateButton, "noColor");
        redTopPlateButton = (Button) ((Activity)context).findViewById(R.id.redTopPlateButton);
        configMap.put(R.id.redTopPlateButton, "noColor");
        redBottomPlateButton = (Button) ((Activity)context).findViewById(R.id.redBottomPlateButton);
        configMap.put(R.id.redBottomPlateButton, "noColor");

        blueTopPlateButton.setBackgroundColor(Color.parseColor(grey));
        blueBottomPlateButton.setBackgroundColor(Color.parseColor(grey));
        scaleTopPlateButton.setBackgroundColor(Color.parseColor(grey));
        scaleBottomPlateButton.setBackgroundColor(Color.parseColor(grey));
        redTopPlateButton.setBackgroundColor(Color.parseColor(grey));
        redBottomPlateButton.setBackgroundColor(Color.parseColor(grey));
    }

    public Map<Integer, String> getConfig() {
        return configMap;
    }

    public void swapColor(View button) {
        Integer buttonId = button.getId();
        String oppositeButtonState;
        String oppositeButtonColor;

        if(isRed) {
            if(configMap.get(buttonId).equals("red")) {
                button.setBackgroundColor(Color.parseColor(blue));
                configMap.put(buttonId, "blue");
                oppositeButtonState = "red";
                oppositeButtonColor = red;
            } else {
                button.setBackgroundColor(Color.parseColor(red));
                configMap.put(buttonId, "red");
                oppositeButtonState = "blue";
                oppositeButtonColor = blue;
            }
        } else {
            if(configMap.get(buttonId).equals("blue")) {
                button.setBackgroundColor(Color.parseColor(red));
                configMap.put(buttonId, "red");
                oppositeButtonState = "blue";
                oppositeButtonColor = blue;
            } else {
                button.setBackgroundColor(Color.parseColor(blue));
                configMap.put(buttonId, "blue");
                oppositeButtonState = "red";
                oppositeButtonColor = red;
            }
        }

        switch(buttonId)
        {
            case R.id.blueTopPlateButton:
                blueBottomPlateButton.setBackgroundColor(Color.parseColor(oppositeButtonColor));
                configMap.put(R.id.blueBottomPlateButton, oppositeButtonState);
                break;

            case R.id.blueBottomPlateButton:
                blueTopPlateButton.setBackgroundColor(Color.parseColor(oppositeButtonColor));
                configMap.put(R.id.blueTopPlateButton, oppositeButtonState);
                break;

            case R.id.scaleTopPlateButton:
                scaleBottomPlateButton.setBackgroundColor(Color.parseColor(oppositeButtonColor));
                configMap.put(R.id.scaleBottomPlateButton, oppositeButtonState);
                break;

            case R.id.scaleBottomPlateButton:
                scaleTopPlateButton.setBackgroundColor(Color.parseColor(oppositeButtonColor));
                configMap.put(R.id.scaleTopPlateButton, oppositeButtonState);
                break;

            case R.id.redTopPlateButton:
                redBottomPlateButton.setBackgroundColor(Color.parseColor(oppositeButtonColor));
                configMap.put(R.id.redBottomPlateButton, oppositeButtonState);
                break;

            case R.id.redBottomPlateButton:
                redTopPlateButton.setBackgroundColor(Color.parseColor(oppositeButtonColor));
                configMap.put(R.id.redTopPlateButton, oppositeButtonState);
                break;
        }

    }
}
