package com.nuvoex.fileuploader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.nuvoex.fileuploader.network.ApiManager;
import com.nuvoex.fileuploader.network.ApiService;
import com.nuvoex.fileuploader.utils.Consts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dilip on 04/01/17.
 */

public class UploadService extends JobService {

    private int mRemainingFiles = 0;
    private int mPendingUploads = 0;
    private FileWorkerThread mFileWorkerThread;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.v(Consts.TAG, "Job started");

        mFileWorkerThread = new FileWorkerThread();
        mFileWorkerThread.start();

        SharedPreferences prefs = getSharedPreferences(Consts.Configs.PREF_NAME, MODE_PRIVATE);
        Map<String, String> files = (Map<String, String>) prefs.getAll();

        mRemainingFiles = files.size();
        mPendingUploads = mRemainingFiles;

        if (mRemainingFiles == 0) {
            Log.v(Consts.TAG, "Nothing to upload");
            return false; //nothing to upload, all done
        }

        Log.v(Consts.TAG, mRemainingFiles + " files to upload");

        for (Map.Entry<String, String> entry : files.entrySet()) {
            String uploadId = entry.getKey();
            Bundle uploadInfo = parseUploadInfo(entry.getValue());
            uploadFile(jobParameters, uploadId, uploadInfo);
        }
        return true; //still doing work
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        boolean needsReschedule = (mPendingUploads > 0);
        Log.v(Consts.TAG, "Job stopped. Needs reschedule: " + needsReschedule);
        return needsReschedule;
    }

    @Override
    public void onDestroy() {
        quitWorkerThread();
        super.onDestroy();
    }

    private void quitWorkerThread() {
        if (mFileWorkerThread != null && mFileWorkerThread.isAlive())
            mFileWorkerThread.quit();
    }

    private void uploadFile(final JobParameters jobParameters, final String uploadId, final Bundle uploadInfo) {
        final String filePath = uploadInfo.getString(Consts.Keys.EXTRA_FILE_PATH);
        final String uploadUrl = uploadInfo.getString(Consts.Keys.EXTRA_UPLOAD_URL);

        File file = new File(filePath);
        Log.v(Consts.TAG, "Uploading " + filePath + " (" + file.length() + " bytes)");

        if (!file.exists()) {
            Log.v(Consts.TAG, "Error: File not found");
            mPendingUploads--;
            mRemainingFiles--;
            SharedPreferences prefs = getSharedPreferences(Consts.Configs.PREF_NAME, MODE_PRIVATE);
            prefs.edit().remove(uploadId).commit();
            sendStatusBroadcast(Consts.Status.CANCELLED, uploadId, uploadInfo);
            checkCompletion(jobParameters);
            return;
        }

        RequestBody body = RequestBody.create(MediaType.parse(""), file);

        ApiService service = ApiManager.getApiService();
        Call<ResponseBody> call = service.uploadFile(uploadUrl, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v(Consts.TAG, filePath);
                Log.v(Consts.TAG, uploadUrl);
                Log.v(Consts.TAG, "Status: " + response.code());
                if (response.isSuccessful()) {
                    Log.v(Consts.TAG, "Success");
                    mPendingUploads--;
                    SharedPreferences prefs = getSharedPreferences(Consts.Configs.PREF_NAME, MODE_PRIVATE);
                    prefs.edit().remove(uploadId).commit();
                    mFileWorkerThread.postTask(new DeleteFileTask(filePath));
                    sendStatusBroadcast(Consts.Status.COMPLETED, uploadId, uploadInfo);
                } else {
                    Log.v(Consts.TAG, "Failure");
                }
                mRemainingFiles--;
                checkCompletion(jobParameters);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v(Consts.TAG, filePath);
                Log.v(Consts.TAG, "Error");
                Log.v(Consts.TAG, t.toString());
                sendStatusBroadcast(Consts.Status.FAILED, uploadId, uploadInfo);
                mRemainingFiles--;
                checkCompletion(jobParameters);
            }
        });
        sendStatusBroadcast(Consts.Status.STARTED, uploadId, uploadInfo);
    }

    private void checkCompletion(JobParameters jobParameters) {
        if (!isComplete()) {
            return;
        }

        //  if any upload is not successful, reschedule job for remaining files
        boolean needsReschedule = (mPendingUploads > 0);
        Log.v(Consts.TAG, "Job finished. Pending files: " + mPendingUploads);
        jobFinished(jobParameters, needsReschedule);
    }

    // returns whether an attempt was made to upload every file at least once
    private boolean isComplete() {
        return mRemainingFiles == 0;
    }

    private Bundle parseUploadInfo(String uploadInfo) {
        Bundle bundle = new Bundle();
        try {
            JSONObject json = new JSONObject(uploadInfo);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                bundle.putString(key, json.getString(key));
            }
        } catch (JSONException e) {
            Log.e(Consts.TAG, "Upload info parse failed");
        }
        return bundle;
    }

    private void sendStatusBroadcast(int status, String uploadId, Bundle uploadInfo) {
        Intent intent = new Intent(Consts.Actions.STATUS_CHANGE);
        intent.putExtra(Intent.EXTRA_UID, uploadId);
        intent.putExtra(Consts.Keys.EXTRA_UPLOAD_STATUS, status);
        intent.putExtras(uploadInfo);
        sendBroadcast(intent);
    }

    private class DeleteFileTask implements Runnable {

        private String filePath;

        private DeleteFileTask(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            File file = new File(filePath);
            File parentFolder = file.getParentFile();
            //If file delete is successful
            if (file.delete()) {
                Log.v(Consts.TAG, "File " + file.getName() + " is deleted!");
                //if the folder is empty, then delete it
                if (parentFolder.list().length == 0) {
                    //Delete the folder
                    parentFolder.delete();
                }
            }
        }
    }

    private class FileWorkerThread extends HandlerThread {

        private Handler mWorkerHandler;

        public FileWorkerThread() {
            super("FileWorkerThread");
        }

        @Override
        protected void onLooperPrepared() {
            mWorkerHandler = new Handler(getLooper());
        }

        public void postTask(Runnable task) {
            mWorkerHandler.post(task);
        }

    }
}
