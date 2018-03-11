package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by niraq on 3/11/2018.
 */

public class ColorCycleTask {

    private Runnable runnable;
    private boolean stopThread, isRed;
    private boolean canRun = false;
    private Integer id;
    private Spannable wordToSpan;
    private Context context;

    private void initializeRunnable() {
        runnable = new Runnable()
        {

            @Override
            public void run() {
                canRun = false;
                int pos = 0;
                while (pos < 22 && !Thread.interrupted() && !stopThread) {

                    int pos2 = pos;
                    for (int i = 0; i < wordToSpan.length(); i++) {
                        int color = colorWheel(pos2);
                        wordToSpan.setSpan(new ForegroundColorSpan(color), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if(!(pos < 22)) pos2 = 0;
                        else pos2++;
                    }

                    pos++;
                    //if(pos == 22) pos = 0;
                    ((Activity) context).runOnUiThread(new Runnable() // start actions in UI thread
                        {

                            @Override
                            public void run() {
                                TextView view = (TextView) ((Activity) context).findViewById(id);
                                view.setText(wordToSpan);
                            }
                        });

                    try {
                        Thread.sleep(50);
                    } catch(InterruptedException e) {
                        canRun = true;
                        return;
                    }
                }
                canRun = true;

                if(!stopThread) startColorCycle(isRed);
            }
        };
    }

    //TODO: Use AsyncTask
    public void startColorCycle(boolean isRed) {

        stopThread = false;
        this.isRed = isRed;
        if(!canRun) return;
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();

    }

    public void initializeClass(Context context) {
        this.context = context;
        if(id != null) canRun = true;
    }

    public void setView(TextView textView) {
        id = textView.getId();
        wordToSpan = new SpannableString(textView.toString());
        if(context != null) canRun = true;
    }

    //TODO: Test if this is necessary (EditText extends TextView).
    public void setView(EditText editText) {
        id = editText.getId();
        wordToSpan = new SpannableString(editText.toString());
        if(context != null) canRun = true;
    }

    public void stopCycle() {
        stopThread = true;
    }

    private int colorWheel(int position) {
        double freq = 0.3;
        int red = (int) Math.round(Math.sin(freq*position + 0) * 127 + 128);
        int green = (int) Math.round(Math.sin(freq*position + 2) * 127 + 128);
        int blue  = (int) Math.round(Math.sin(freq*position + 4) * 127 + 128);
        return Color.argb(255, red, green, blue);
    }

}
