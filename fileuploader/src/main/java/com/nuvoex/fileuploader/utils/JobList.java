package com.nuvoex.fileuploader.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

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

public class JobList extends Properties {

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
            Log.e(Consts.TAG, "Error reading job list", e);
        } catch (NullPointerException e) {
            Log.e(Consts.TAG, "Error reading job list", e);
        }
    }

    public void add(UploadInfo uploadInfo) {
        setProperty(uploadInfo.getUploadId(), writeInfoToJson(uploadInfo));
    }

    public UploadInfo get(String uploadId) {
        UploadInfo uploadInfo = readInfoFromJson(getProperty(uploadId));
        return uploadInfo.setUploadId(uploadId);
    }

    public Set<String> getKeys() {
        return stringPropertyNames();
    }

    public void commit() {
        try {
            OutputStream outputStream = new FileOutputStream(getStorageFile());
            storeToXML(outputStream, null);
        } catch (IOException e) {
            Log.e(Consts.TAG, "Error writing job list", e);
        } catch (NullPointerException e) {
            Log.e(Consts.TAG, "Error writing job list", e);
        } catch (ClassCastException e) {
            Log.e(Consts.TAG, "Error writing job list", e);
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
            json.put(Consts.Keys.EXTRA_FILE_PATH, uploadInfo.getFilePath());
            json.put(Consts.Keys.EXTRA_UPLOAD_URL, uploadInfo.getUploadUrl());
            json.put(Consts.Keys.EXTRA_DELETE_ON_UPLOAD, uploadInfo.getDeleteOnUpload());
            JSONObject extras = new JSONObject();
            for (String key : uploadInfo.getExtras().keySet()) {
                extras.put(key, uploadInfo.getExtras().get(key));
            }
            json.put(Consts.Keys.EXTRA_EXTRAS, extras);
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
            uploadInfo.setFilePath(json.getString(Consts.Keys.EXTRA_FILE_PATH))
                    .setUploadUrl(json.getString(Consts.Keys.EXTRA_UPLOAD_URL))
                    .setDeleteOnUpload(json.getBoolean(Consts.Keys.EXTRA_DELETE_ON_UPLOAD));
            Map<String, String> map = new HashMap<>();
            JSONObject extras = json.getJSONObject(Consts.Keys.EXTRA_EXTRAS);
            Iterator<String> keys = extras.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key, extras.getString(key));
            }
            uploadInfo.setExtras(map);
        } catch (JSONException e) {
            Log.e(Consts.TAG, "Upload info parse failed");
        }
        return uploadInfo;
    }
}
