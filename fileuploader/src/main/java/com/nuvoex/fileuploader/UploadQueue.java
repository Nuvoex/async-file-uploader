package com.nuvoex.fileuploader;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.nuvoex.fileuploader.utils.Consts;
import com.nuvoex.fileuploader.utils.JobList;
import com.nuvoex.fileuploader.utils.Logger;

import java.util.HashMap;

/**
 * Created by dilip on 04/01/17.
 */

public class UploadQueue {

    private static FirebaseJobDispatcher dispatcher;
    private static int logLevel = Integer.MAX_VALUE;

    public static FirebaseJobDispatcher getDispatcher(Context context) {
        if (dispatcher == null) {
            Driver driver = new GooglePlayDriver(context.getApplicationContext());
            dispatcher = new FirebaseJobDispatcher(driver);
        }
        return dispatcher;
    }

    public static void setLogLevel(int level) {
        logLevel = level;
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static boolean schedule(Context context, UploadInfo uploadInfo) {
        if (uploadInfo == null) {
            return false;
        }
        if (uploadInfo.getExtras() == null) {
            uploadInfo.setExtras(new HashMap<String, String>());
        }
        JobList jobList = JobList.getJobList(context);
        jobList.add(uploadInfo);
        jobList.commit();
        return scheduleJob(context);
    }

    public static boolean flush(Context context) {
        return scheduleJob(context);
    }

    public static void clear(Context context) {
        JobList jobList = JobList.getJobList(context);
        jobList.clear();
        jobList.commit();
    }

    private static boolean scheduleJob(Context context) {
        FirebaseJobDispatcher dispatcher = getDispatcher(context);
        Job job = dispatcher.newJobBuilder()
                .setService(UploadService.class)
                .setTag(context.getPackageName() + ".uploadqueue")
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.NOW)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(false)
                .setReplaceCurrent(true)
                .build();
        int result = dispatcher.schedule(job);
        if (result == FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            Logger.v("Job scheduled");
            return true;
        } else {
            Logger.v("Job not scheduled");
            return false;
        }
    }
}
