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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    public static boolean schedule(Context context, String uploadId, String filePath, String uploadUrl, Map<String, String> extras) {
        if (extras == null) {
            extras = new HashMap<>();
        }
        extras.put(Consts.Keys.EXTRA_FILE_PATH, filePath);
        extras.put(Consts.Keys.EXTRA_UPLOAD_URL, uploadUrl);
        String uploadInfo = writeMapToJson(extras);
        if (uploadInfo == null) {
            return false;
        }
        SharedPreferences prefs = context.getSharedPreferences(Consts.Configs.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(uploadId, uploadInfo).commit();
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
            return true;
        } else {
            Log.v(Consts.TAG, "Job not scheduled");
            return false;
        }
    }

    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Consts.Configs.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    private static String writeMapToJson(Map<String, String> map) {
        try {
            JSONObject json = new JSONObject();
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
