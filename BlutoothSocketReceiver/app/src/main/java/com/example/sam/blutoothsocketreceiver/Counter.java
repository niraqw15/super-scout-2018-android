package com.example.sam.blutoothsocketreceiver;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by sam on 5/11/16.
 */
public class Counter extends RelativeLayout {
    private String dataName;
    private int max;
    private int min;
    private int increment;
    private int value;
    TextView counterTextView;
    TextView counterTitleTextView;
    Button addButton;
    Button subtractButton;
    SuperScoutingPanel superScoutingPanel;
    Boolean fourApplied;

    public Counter(Context context, AttributeSet attrs) {
        super(context, attrs);

        superScoutingPanel = new SuperScoutingPanel();

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.counter, this, true);

        counterTextView = (TextView)findViewById(R.id.scoreCounter);
        counterTitleTextView = (TextView)findViewById(R.id.dataName);
        addButton = (Button)findViewById(R.id.plusButton);
        subtractButton = (Button)findViewById(R.id.minusButton);


        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Counter,
                0, 0);

        try {
            this.dataName = a.getString(R.styleable.Counter_dataName);
            this.max = a.getInt(R.styleable.Counter_max, 4);
            this.min = a.getInt(R.styleable.Counter_min, 0);
            this.increment = a.getInt(R.styleable.Counter_increment, 1);

            listenForAddClicked();
            listenForMinusClicked();

            refreshCounter();


        } finally {
            a.recycle();
        }
    }

    private void listenForAddClicked(){
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SuperScoutingPanel.FourApplied.get(dataName) == null){
                    superScoutingPanel.addToFourApplied(dataName,fourApplied);
                }

                fourApplied = SuperScoutingPanel.FourApplied.get(dataName);

                Log.e("check", fourApplied.toString());

                Log.e("dataName",dataName);
                if(fourApplied){
                    max = 3;
                }else if(!fourApplied){
                    max = 4;
                }

                if (value + increment <= max) {
                    value += increment;
                    refreshCounter();
                    Log.e("add", "clicked");
                }

                if(value == 4 && !fourApplied){
                    fourApplied = true;
                    SuperScoutingPanel.FourApplied.put(dataName,fourApplied);
                    Log.e("checkFor", SuperScoutingPanel.FourApplied.get(dataName).toString());
                    Log.e("check2", fourApplied.toString());
                }
                Log.e("check3", fourApplied.toString());

                Log.e("max", max+"");

                Log.e("value", value+"");
            }
        });
    }
    private void listenForMinusClicked(){
        subtractButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value - increment >= min && value == 4) {
                    value -= increment;
                    fourApplied = false;
                    SuperScoutingPanel.FourApplied.put(dataName,fourApplied);
                    refreshCounter();
                    Log.e("subtract", "clicked");
                }else if (value - increment >= min) {
                    value -= increment;
                    refreshCounter();
                    Log.e("subtract", "clicked");
                }
            }
        });
    }


    private void refreshCounter() {
        Log.e("test2", counterTitleTextView.toString());
        counterTitleTextView.setText(dataName);
        counterTextView.setText(value + "");
    }

    public String getDataName() {
        return dataName;
    }

    public Integer getDataValue() {
        return value;
    }
}
