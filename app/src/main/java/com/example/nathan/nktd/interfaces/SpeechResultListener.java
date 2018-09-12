package com.example.nathan.nktd.interfaces;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.nathan.nktd.R;

public abstract class SpeechResultListener {

    RecognizedActivity activity;
    final Animation soundHeardAnimation;
    final Animation recognitionFailedAnimation;

    public SpeechResultListener(RecognizedActivity activity) {
        this.activity = activity;
        soundHeardAnimation = AnimationUtils.loadAnimation(activity, R.anim.sound_heard);
        recognitionFailedAnimation = AnimationUtils.loadAnimation(activity, R.anim.recognition_failed);
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

    public void onSoundHeard() {
        activity.recognizerButton.startAnimation(soundHeardAnimation);
    }
    public void onFinishedRecognition() {
    }

    public void onFailedRecognition() {
        activity.recognizerButton.startAnimation(recognitionFailedAnimation);
    }

}
