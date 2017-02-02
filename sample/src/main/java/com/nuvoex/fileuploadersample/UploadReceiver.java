package com.nuvoex.fileuploadersample;

import android.content.Context;
import android.util.Log;

import com.nuvoex.fileuploader.UploadBroadcastReceiver;
import com.nuvoex.fileuploader.UploadError;

import java.util.Map;

/**
 * Created by dilip on 10/01/17.
 */

public class UploadReceiver extends UploadBroadcastReceiver {
    @Override
    public void onStart(Context context, String uploadId, Map<String, String> extras) {

    }

    @Override
    public void onFail(Context context, String uploadId, Map<String, String> extras, UploadError error) {
        Log.e("upload test", "Upload failed");
        Log.e("upload test", error.getMessage());
    }

    @Override
    public void onComplete(Context context, String uploadId, Map<String, String> extras) {
        String awb = extras.get("awb");
        Log.v("upload test", "Upload with id " + uploadId + " for AWB " + awb + " completed successfully");
    }

    @Override
    public void onCancel(Context context, String uploadId, Map<String, String> extras) {

    }
}
