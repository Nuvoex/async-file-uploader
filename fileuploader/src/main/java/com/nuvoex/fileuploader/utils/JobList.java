package com.nuvoex.fileuploader.utils;

import android.content.Context;
import android.os.Environment;

import com.nuvoex.fileuploader.UploadInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by dilip on 19/01/17.
 */

/**
 * Helper class that encapsulates storage and retrieval of upload jobs to persistent storage.
 * Data is stored in an external directory outside the application's private space so that it is
 * retained across installs.
 */
public class JobList extends Properties {

    /**
     * Name of directory created in external storage for storing data.
     */
    private static final String STORAGE_DIR = ".fileuploader";

    private static final String DEFAULT_LIST = "default";

    private String mPackageName;

    public static JobList getJobList(Context context) {
        return new JobList(context);
    }

    private JobList(Context context) {
        this.mPackageName = context.getPackageName();
        try {
            File storageFile = getStorageFile();
            if (storageFile.exists()) {
                InputStream inputStream = new FileInputStream(getStorageFile());
                loadFromXML(inputStream);
            }
        } catch (IOException e) {
            Logger.e("Error reading job list", e);
        } catch (NullPointerException e) {
            Logger.e("Error reading job list", e);
        }
    }

    /**
     * Adds a new file upload.
     * @param uploadInfo {@link UploadInfo} for this file upload.
     */
    public void add(UploadInfo uploadInfo) {
        setProperty(uploadInfo.getUploadId(), writeInfoToJson(uploadInfo));
    }

    /**
     * Gets the {@link UploadInfo} with the given ID.
     * @param uploadId ID of the upload.
     * @return A valid {@link UploadInfo} if found, and empty one otherwise.
     */
    public UploadInfo get(String uploadId) {
        UploadInfo uploadInfo = readInfoFromJson(getProperty(uploadId));
        return uploadInfo.setUploadId(uploadId);
    }

    /**
     * Gets list of IDs of all queued file uploads.
     * @return
     */
    public Set<String> getKeys() {
        return stringPropertyNames();
    }

    /**
     * Save all changes to persistent storage. This must be called after any {@link #add(UploadInfo)}
     * or {@link #clear()} call.
     */
    public void commit() {
        try {
            OutputStream outputStream = new FileOutputStream(getStorageFile());
            storeToXML(outputStream, null);
        } catch (IOException e) {
            Logger.e("Error writing job list", e);
        } catch (NullPointerException e) {
            Logger.e("Error writing job list", e);
        } catch (ClassCastException e) {
            Logger.e("Error writing job list", e);
        }
    }

    private File getStorageFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        String root = Environment.getExternalStorageDirectory().toString();
        File storageDir = new File(root + "/" + STORAGE_DIR + "/" + mPackageName);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                return null;
            }
        }
        return new File(storageDir.getPath() + "/" + DEFAULT_LIST);
    }

    private String writeInfoToJson(UploadInfo uploadInfo) {
        try {
            JSONObject json = new JSONObject();
            json.put(Constants.Keys.EXTRA_FILE_PATH, uploadInfo.getFilePath());
            json.put(Constants.Keys.EXTRA_UPLOAD_URL, uploadInfo.getUploadUrl());
            json.put(Constants.Keys.EXTRA_DELETE_ON_UPLOAD, uploadInfo.getDeleteOnUpload());
            JSONObject extras = new JSONObject();
            for (String key : uploadInfo.getExtras().keySet()) {
                extras.put(key, uploadInfo.getExtras().get(key));
            }
            json.put(Constants.Keys.EXTRA_EXTRAS, extras);
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private UploadInfo readInfoFromJson(String uploadJson) {
        UploadInfo uploadInfo = new UploadInfo();
        try {
            JSONObject json = new JSONObject(uploadJson);
            uploadInfo.setFilePath(json.getString(Constants.Keys.EXTRA_FILE_PATH))
                    .setUploadUrl(json.getString(Constants.Keys.EXTRA_UPLOAD_URL))
                    .setDeleteOnUpload(json.getBoolean(Constants.Keys.EXTRA_DELETE_ON_UPLOAD));
            Map<String, String> map = new HashMap<>();
            JSONObject extras = json.getJSONObject(Constants.Keys.EXTRA_EXTRAS);
            Iterator<String> keys = extras.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key, extras.getString(key));
            }
            uploadInfo.setExtras(map);
        } catch (JSONException e) {
            Logger.e("Upload info parse failed");
        }
        return uploadInfo;
    }
}
