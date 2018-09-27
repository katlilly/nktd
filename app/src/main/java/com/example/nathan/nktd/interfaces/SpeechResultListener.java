package com.example.nathan.nktd.interfaces;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.nathan.nktd.R;

/**
 * Listens from a RecognizedActivity to a Recognizer service.
 */
public abstract class SpeechResultListener {

    RecognizedActivity activity;

    final Animation soundHeardAnimation;
    final Animation recognitionFailedAnimation;

    public SpeechResultListener(RecognizedActivity activity) {
        this.activity = activity;
        soundHeardAnimation = AnimationUtils.loadAnimation(activity, R.anim.sound_heard);
        recognitionFailedAnimation = AnimationUtils.loadAnimation(activity, R.anim.recognition_failed);
    }

    /**
     * To be implemented in the RecognizedActivity. Called when the Recognizer
     * has a new result.
     */
    public abstract void onSpeechResult();

    /**
     * Called when Recognizer starts.
     */
    public void onStartRecognition() {
        activity.recognizerButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.listening));
    }

    /**
     * Called when Recognizer stops.
     */
    public void onStopRecognition() {
        activity.recognizerButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.notlistening));
    }

    /**
     * Called when Recognizer switches to 'number' mode.
     */
    public void onNumberRecognition() {
        activity.recognizerButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.listening_number));
    }

    /**
     * Called when a sound is heard, before a result is found.
     */
    public void onSoundHeard() {
        activity.recognizerButton.startAnimation(soundHeardAnimation);
    }

    /**
     * Called when recognition stops or Recognizer times out.
     */
    public void onFinishedRecognition() {
    }

    /**
     * Called when Recognizer recieves simultaneous identical results without
     * finding a finished result.
     */
    public void onFailedRecognition() {
        activity.recognizerButton.startAnimation(recognitionFailedAnimation);
    }

    /**
     * Called when the Recognizer service is bound to a RecognizedActivity.
     */
    public void onBound() {
        if (activity.recognizerListening) {
            activity.startRecognition();
        }
    }

}
