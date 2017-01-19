package com.nuvoex.fileuploader;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.nuvoex.fileuploader.network.ApiManager;
import com.nuvoex.fileuploader.network.ApiService;
import com.nuvoex.fileuploader.utils.Consts;
import com.nuvoex.fileuploader.utils.JobList;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

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

        JobList jobList = JobList.getJobList(this);
        Set<String> uploadIds = jobList.getKeys();

        mRemainingFiles = uploadIds.size();
        mPendingUploads = mRemainingFiles;

        if (mRemainingFiles == 0) {
            Log.v(Consts.TAG, "Nothing to upload");
            return false; //nothing to upload, all done
        }

        Log.v(Consts.TAG, mRemainingFiles + " files to upload");

        for (String uploadId : uploadIds) {
            UploadInfo uploadInfo = jobList.get(uploadId);
            uploadFile(jobParameters, uploadInfo);
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

    private void uploadFile(final JobParameters jobParameters, final UploadInfo uploadInfo) {
        final String filePath = uploadInfo.getFilePath();
        final String uploadUrl = uploadInfo.getUploadUrl();

        File file = new File(filePath);
        Log.v(Consts.TAG, "Uploading " + filePath + " (" + file.length() + " bytes)");

        if (!file.exists()) {
            Log.v(Consts.TAG, "Error: File not found");
            mPendingUploads--;
            mRemainingFiles--;
            JobList jobList = JobList.getJobList(this);
            jobList.remove(uploadInfo.getUploadId());
            jobList.commit();
            sendStatusBroadcast(Consts.Status.CANCELLED, uploadInfo);
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
                    JobList jobList = JobList.getJobList(UploadService.this);
                    jobList.remove(uploadInfo.getUploadId());
                    jobList.commit();
                    if (uploadInfo.getDeleteOnUpload()) {
                        mFileWorkerThread.postTask(new DeleteFileTask(filePath));
                    }
                    sendStatusBroadcast(Consts.Status.COMPLETED, uploadInfo);
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
                sendStatusBroadcast(Consts.Status.FAILED, uploadInfo);
                mRemainingFiles--;
                checkCompletion(jobParameters);
            }
        });
        sendStatusBroadcast(Consts.Status.STARTED, uploadInfo);
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

    private void sendStatusBroadcast(int status, UploadInfo uploadInfo) {
        Intent intent = new Intent(Consts.Actions.STATUS_CHANGE);
        intent.putExtra(Intent.EXTRA_UID, uploadInfo.getUploadId());
        intent.putExtra(Consts.Keys.EXTRA_UPLOAD_STATUS, status);
        intent.putExtra(Consts.Keys.EXTRA_EXTRAS, (HashMap<String, String>) uploadInfo.getExtras());
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
