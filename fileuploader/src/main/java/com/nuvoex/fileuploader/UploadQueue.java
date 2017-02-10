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
import com.nuvoex.fileuploader.utils.JobList;
import com.nuvoex.fileuploader.utils.Logger;

import java.util.HashMap;

/**
 * Created by dilip on 04/01/17.
 */

/**
 * The {@code UploadQueue} class contains static functions for managing file uploads as well as
 * logging.
 */
public class UploadQueue {

    private static FirebaseJobDispatcher dispatcher;
    private static int logLevel = Integer.MAX_VALUE;

    private static FirebaseJobDispatcher getDispatcher(Context context) {
        if (dispatcher == null) {
            Driver driver = new GooglePlayDriver(context.getApplicationContext());
            dispatcher = new FirebaseJobDispatcher(driver);
        }
        return dispatcher;
    }

    /**
     * Sets the log level. Logging is disabled by default.
     * @param level The log level. Must be one of {@link Log#VERBOSE}, {@link Log#DEBUG},
     * {@link Log#INFO}, {@link Log#WARN} or {@link Log#ERROR}.
     */
    public static void setLogLevel(int level) {
        logLevel = level;
    }

    /**
     * Gets the log level.
     * @see #setLogLevel(int)
     * @return The log level. Default value is {@link Integer#MAX_VALUE}.
     */
    public static int getLogLevel() {
        return logLevel;
    }

    /**
     * Schedules a file upload to be performed in the background. Uploads are queued for
     * immediate execution as long as a network connection is available. Otherwise it will wait
     * until a connection is established. Uploads are automatically retried if they fail and
     * across system reboots. The exact time at which the upload is attempted is decided by the
     * system for optimizing device resources.
     * @param context The application context.
     * @param uploadInfo {@link UploadInfo} for this file upload.
     * @return {@code true} if the upload was scheduled successfully, {@code false} otherwise.
     */
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

    /**
     * Attempts to execute all scheduled file uploads. Uploads are not immediately performed, but
     * performed at an optimum time decided by the system for efficient use of device resources.
     * @param context The application context.
     * @return {@code true} if successful, {@code false} otherwise.
     */
    public static boolean flush(Context context) {
        return scheduleJob(context);
    }

    /**
     * Removes all scheduled file uploads.
     * @param context The application context.
     */
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
