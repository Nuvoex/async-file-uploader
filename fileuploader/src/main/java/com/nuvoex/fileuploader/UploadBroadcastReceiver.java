package com.nuvoex.fileuploader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nuvoex.fileuploader.utils.Consts;

import java.util.Map;

/**
 * Created by dilip on 11/01/17.
 */

public abstract class UploadBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !intent.hasExtra(Consts.Keys.EXTRA_UPLOAD_STATUS) || !intent.hasExtra(Intent.EXTRA_UID)) {
            return;
        }

        String uploadId = intent.getStringExtra(Intent.EXTRA_UID);
        Map<String, String> extras = (Map<String, String>) intent.getSerializableExtra(Consts.Keys.EXTRA_EXTRAS);

        switch (intent.getIntExtra(Consts.Keys.EXTRA_UPLOAD_STATUS, 0)) {
            case Consts.Status.STARTED:
                onStart(context, uploadId, extras);
                break;
            case Consts.Status.FAILED:
                onFail(context, uploadId, extras, (UploadError) intent.getSerializableExtra(Consts.Keys.EXTRA_UPLOAD_ERROR));
                break;
            case Consts.Status.COMPLETED:
                onComplete(context, uploadId, extras);
                break;
            case Consts.Status.CANCELLED:
                onCancel(context, uploadId, extras);
                break;
        }
    }

    public abstract void onStart(Context context, String uploadId, Map<String, String> extras);

    public abstract void onFail(Context context, String uploadId, Map<String, String> extras, UploadError error);

    public abstract void onComplete(Context context, String uploadId, Map<String, String> extras);

    public abstract void onCancel(Context context, String uploadId, Map<String, String> extras);
}
