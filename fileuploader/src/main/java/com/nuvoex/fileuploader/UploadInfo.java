package com.nuvoex.fileuploader;

import java.util.Map;

/**
 * Created by dilip on 12/01/17.
 */

public class UploadInfo {

    private String mUploadId;
    private String mFilePath;
    private String mUploadUrl;
    private boolean mDeleteOnUpload;
    private Map<String, String> mExtras;

    public UploadInfo setUploadId(String uploadId) {
        this.mUploadId = uploadId;
        return this;
    }

    public UploadInfo setFilePath(String filePath) {
        this.mFilePath = filePath;
        return this;
    }

    public UploadInfo setUploadUrl(String uploadUrl) {
        this.mUploadUrl = uploadUrl;
        return this;
    }

    public UploadInfo setDeleteOnUpload(boolean shouldDelete) {
        this.mDeleteOnUpload = shouldDelete;
        return this;
    }

    public UploadInfo setExtras(Map<String, String> extras) {
        this.mExtras = extras;
        return this;
    }

    public String getUploadId() {
        return this.mUploadId;
    }

    public String getFilePath() {
        return this.mFilePath;
    }

    public String getUploadUrl() {
        return this.mUploadUrl;
    }

    public boolean getDeleteOnUpload() {
        return this.mDeleteOnUpload;
    }

    public Map<String, String> getExtras() {
        return this.mExtras;
    }
}
