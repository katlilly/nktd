package com.example.nathan.nktd.interfaces;

import android.content.Context;
import android.widget.ImageView;

import com.example.nathan.nktd.R;

public abstract class SpeechResultListener {

    RecognizedActivity activity;

    public SpeechResultListener(RecognizedActivity activity) {
        this.activity = activity;
    }

    public abstract void onSpeechResult();

    public void onStartRecognition() {
        activity.recognizerListening = true;
        activity.recognizerButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.listening));
    }

    public void onStopRecognition() {
        activity.recognizerListening = false;
        activity.recognizerButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.notlistening));
    }

    public void onNumberRecognition() {
        activity.recognizerButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.listening_number));
    }

    public void onSoundHeard() {}
    public void onFinishedRecognition() {}
}
