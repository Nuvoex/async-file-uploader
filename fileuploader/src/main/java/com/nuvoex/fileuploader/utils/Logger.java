package com.nuvoex.fileuploader.utils;

import android.util.Log;

import com.nuvoex.fileuploader.UploadQueue;

/**
 * Created by dilip on 10/02/17.
 */

public class Logger {

    public static void v(String msg) {
        if (UploadQueue.getLogLevel() <= Log.VERBOSE) {
            Log.v(Consts.TAG, msg);
        }
    }

    public static void d(String msg) {
        if (UploadQueue.getLogLevel() <= Log.DEBUG) {
            Log.v(Consts.TAG, msg);
        }
    }

    public static void i(String msg) {
        if (UploadQueue.getLogLevel() <= Log.INFO) {
            Log.v(Consts.TAG, msg);
        }
    }

    public static void w(String msg) {
        if (UploadQueue.getLogLevel() <= Log.WARN) {
            Log.v(Consts.TAG, msg);
        }
    }

    public static void e(String msg) {
        if (UploadQueue.getLogLevel() <= Log.ERROR) {
            Log.v(Consts.TAG, msg);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (UploadQueue.getLogLevel() <= Log.ERROR) {
            Log.v(Consts.TAG, msg, tr);
        }
    }
}
