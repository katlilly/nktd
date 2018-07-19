package com.example.nathan.nktd.interfaces;

import android.content.Context;
import android.widget.ImageView;

public interface SpeechResultListener {
    public void onSpeechResult();
    public void onStartRecognition();
    public void onStopRecognition();
    public void onNumberRecognition();
}
