package com.nuvoex.fileuploadersample;

import android.app.Application;

import com.nuvoex.fileuploader.UploadQueue;

/**
 * Created by dilip on 19/01/17.
 */

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UploadQueue.flush(this);
    }
}
