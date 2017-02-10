package com.nuvoex.fileuploadersample;

import android.app.Application;
import android.util.Log;

import com.nuvoex.fileuploader.UploadQueue;

/**
 * Created by dilip on 19/01/17.
 */

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UploadQueue.setLogLevel(Log.VERBOSE);
        UploadQueue.flush(this);
    }
}
