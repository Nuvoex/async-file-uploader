package com.nuvoex.fileuploader;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.nuvoex.fileuploader.utils.Consts;

/**
 * Created by dilip on 04/01/17.
 */

public class UploadQueue {

    private static FirebaseJobDispatcher dispatcher;

    public static FirebaseJobDispatcher getDispatcher(Context context) {
        if (dispatcher == null) {
            Driver driver = new GooglePlayDriver(context.getApplicationContext());
            dispatcher = new FirebaseJobDispatcher(driver);
        }
        return dispatcher;
    }

    public static void schedule(Context context, String filePath, String uploadUrl) {
        SharedPreferences prefs = context.getSharedPreferences(Consts.Configs.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(filePath, uploadUrl).commit();
        FirebaseJobDispatcher dispatcher = getDispatcher(context);
        Job job = dispatcher.newJobBuilder()
                .setService(UploadService.class)
                .setTag(BuildConfig.APPLICATION_ID + ".uploadqueue")
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.NOW)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(false)
                .setReplaceCurrent(true)
                .build();
        int result = dispatcher.schedule(job);
        if (result == FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            Log.v(Consts.TAG, "Job scheduled");
        } else {
            Log.v(Consts.TAG, "Job not scheduled");
        }
    }

    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Consts.Configs.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }
}
