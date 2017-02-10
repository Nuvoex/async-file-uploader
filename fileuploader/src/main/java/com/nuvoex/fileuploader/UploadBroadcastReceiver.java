package com.nuvoex.fileuploader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nuvoex.fileuploader.utils.Constants;

import java.util.Map;

/**
 * Created by dilip on 11/01/17.
 */

/**
 * A {@link BroadcastReceiver} that receives file upload actions and calls the appropriate methods.
 * Applications should implement the abstract methods for responding to various upload events.
 * This {@link BroadcastReceiver} must be registered in the manifest with the proper intent filter.
 * <pre>
 *     {@code
 *     <receiver android:name=".UploadBroadcastReceiverSubclass">
 *          <intent-filter>
 *              <action android:name="com.nuvoex.fileuploader.ACTION_STATUS_CHANGE" />
 *              <category android:name="app.package.name.CATEGORY_UPLOAD" />
 *          </intent-filter>
 *     </receiver>
 *     }
 * </pre>
 * Replace app.package.name with the correct application package name.
 */
public abstract class UploadBroadcastReceiver extends BroadcastReceiver {

    /**
     * Delegates the generic {@code onReceive} event to a file upload event. Subclasses should override
     * the event methods and not this method.
     * @param context The context in which the receiver is running.
     * @param intent An {@code Intent} containing data about the file upload.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !intent.hasExtra(Constants.Keys.EXTRA_UPLOAD_STATUS) || !intent.hasExtra(Intent.EXTRA_UID)) {
            return;
        }

        String uploadId = intent.getStringExtra(Intent.EXTRA_UID);
        Map<String, String> extras = (Map<String, String>) intent.getSerializableExtra(Constants.Keys.EXTRA_EXTRAS);

        switch (intent.getIntExtra(Constants.Keys.EXTRA_UPLOAD_STATUS, 0)) {
            case Constants.Status.STARTED:
                onStart(context, uploadId, extras);
                break;
            case Constants.Status.FAILED:
                onFail(context, uploadId, extras, (UploadError) intent.getSerializableExtra(Constants.Keys.EXTRA_UPLOAD_ERROR));
                break;
            case Constants.Status.COMPLETED:
                onComplete(context, uploadId, extras);
                break;
            case Constants.Status.CANCELLED:
                onCancel(context, uploadId, extras);
                break;
        }
    }

    /**
     * Called when a file upload begins.
     * @param context The context in which the receiver is running.
     * @param uploadId Unique ID of the file upload request.
     * @param extras Any extra information associated with this file upload.
     *               @see UploadInfo#setExtras(Map) Adding extra information to file upload
     */
    public abstract void onStart(Context context, String uploadId, Map<String, String> extras);

    /**
     * Called when a file upload fails.
     * @param context The context in which the receiver is running.
     * @param uploadId Unique ID of the file upload request.
     * @param extras Any extra information associated with this file upload.
     *               @see UploadInfo#setExtras(Map) Adding extra information to file upload
     * @param error {@link UploadError} with details of the failure.
     */
    public abstract void onFail(Context context, String uploadId, Map<String, String> extras, UploadError error);

    /**
     * Called when a file upload is completed.
     * @param context The context in which the receiver is running.
     * @param uploadId Unique ID of the file upload request.
     * @param extras Any extra information associated with this file upload.
     *               @see UploadInfo#setExtras(Map) Adding extra information to file upload
     */
    public abstract void onComplete(Context context, String uploadId, Map<String, String> extras);

    /**
     * Called when a file upload is cancelled.
     * @param context The context in which the receiver is running.
     * @param uploadId Unique ID of the file upload request.
     * @param extras Any extra information associated with this file upload.
     *               @see UploadInfo#setExtras(Map) Adding extra information to file upload
     */
    public abstract void onCancel(Context context, String uploadId, Map<String, String> extras);
}
