package com.nuvoex.fileuploadersample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.nuvoex.fileuploader.UploadBroadcastReceiver;

/**
 * Created by dilip on 10/01/17.
 */

public class UploadReceiver extends UploadBroadcastReceiver {
    @Override
    public void onStart(Context context, String uploadId, Bundle extras) {

    }

    @Override
    public void onFail(Context context, String uploadId, Bundle extras) {

    }

    @Override
    public void onComplete(Context context, String uploadId, Bundle extras) {
        Log.v("upload test", "Upload with id " + uploadId + " completed successfully");
    }

    @Override
    public void onCancel(Context context, String uploadId, Bundle extras) {

    }
}
