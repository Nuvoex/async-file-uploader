package com.nuvoex.fileuploader.utils;

import android.util.Log;

import com.nuvoex.fileuploader.UploadQueue;

/**
 * Created by dilip on 10/02/17.
 */

/**
 * Utility class for logging messages to the system log. By default no message is logged.
 * {@link UploadQueue#setLogLevel(int)} sets the log level.
 */
public class Logger {

    /**
     * Send a {@link Log#VERBOSE} message.
     * @param msg The message to be logged.
     */
    public static void v(String msg) {
        if (UploadQueue.getLogLevel() <= Log.VERBOSE) {
            Log.v(Constants.TAG, msg);
        }
    }

    /**
     * Send a {@link Log#DEBUG} message.
     * @param msg The message to be logged.
     */
    public static void d(String msg) {
        if (UploadQueue.getLogLevel() <= Log.DEBUG) {
            Log.v(Constants.TAG, msg);
        }
    }

    /**
     * Send a {@link Log#INFO} message.
     * @param msg The message to be logged.
     */
    public static void i(String msg) {
        if (UploadQueue.getLogLevel() <= Log.INFO) {
            Log.v(Constants.TAG, msg);
        }
    }

    /**
     * Send a {@link Log#WARN} message.
     * @param msg The message to be logged.
     */
    public static void w(String msg) {
        if (UploadQueue.getLogLevel() <= Log.WARN) {
            Log.v(Constants.TAG, msg);
        }
    }

    /**
     * Send a {@link Log#ERROR} message.
     * @param msg The message to be logged.
     */
    public static void e(String msg) {
        if (UploadQueue.getLogLevel() <= Log.ERROR) {
            Log.v(Constants.TAG, msg);
        }
    }

    /**
     * Send a {@link Log#ERROR} message.
     * @param msg The message to be logged.
     * @param tr An exception to log.
     */
    public static void e(String msg, Throwable tr) {
        if (UploadQueue.getLogLevel() <= Log.ERROR) {
            Log.v(Constants.TAG, msg, tr);
        }
    }
}
