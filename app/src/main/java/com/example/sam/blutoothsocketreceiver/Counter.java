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
    private int startingValue;
    TextView counterTextView;
    TextView counterTitleTextView;
    Button addButton;
    Button subtractButton;
    SuperScoutingPanel superScoutingPanel;

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
            this.min = a.getInt(R.styleable.Counter_min, 1);
            this.startingValue = a.getInt(R.styleable.Counter_startingValue, 2);
            this.increment = a.getInt(R.styleable.Counter_increment, 1);

            this.value = this.startingValue;

            listenForAddClicked();
            listenForMinusClicked();

            refreshCounter(startingValue);

        } finally {
            a.recycle();
        }
    }

    private void listenForAddClicked(){
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value + increment <= max) {
                    value += increment;
                    refreshCounter(value);
                }
            }
        });
    }

    private void listenForMinusClicked(){
        subtractButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value - increment >= min) {
                    value -= increment;
                    refreshCounter(value);
                }
            }
        });
    }


    /*private*/ public void refreshCounter(int someValue) {
        value = someValue;
        counterTitleTextView.setText(dataName);
        counterTextView.setText(someValue + "");
    }

    public String getDataName() {
        return dataName;
    }

    public Integer getDataValue() {
        return value;
    }
}
