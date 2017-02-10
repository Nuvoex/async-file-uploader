package com.nuvoex.fileuploader;

import java.util.Map;

/**
 * Created by dilip on 12/01/17.
 */

/**
 * Contains information about a file upload.
 */
public class UploadInfo {

    private String mUploadId;
    private String mFilePath;
    private String mUploadUrl;
    private boolean mDeleteOnUpload;
    private Map<String, String> mExtras;

    /**
     * Sets the application level unique ID for this file upload.
     * @param uploadId String value.
     * @return A reference to the same {@code UploadInfo} object, so calls to set properties can be chained.
     */
    public UploadInfo setUploadId(String uploadId) {
        this.mUploadId = uploadId;
        return this;
    }

    /**
     * Sets the local path of the file to upload.
     * @param filePath Absolute file path.
     * @return A reference to the same {@code UploadInfo} object, so calls to set properties can be chained.
     */
    public UploadInfo setFilePath(String filePath) {
        this.mFilePath = filePath;
        return this;
    }

    /**
     * Sets the url to which the file is to be uploaded. Url must support PUT operation without authentication.
     * @param uploadUrl An HTTP url.
     * @return A reference to the same {@code UploadInfo} object, so calls to set properties can be chained.
     */
    public UploadInfo setUploadUrl(String uploadUrl) {
        this.mUploadUrl = uploadUrl;
        return this;
    }

    /**
     * Sets whether the file should be deleted after it has been uploaded.
     * @param shouldDelete {@code true} to delete, {@code false} otherwise.
     * @return A reference to the same {@code UploadInfo} object, so calls to set properties can be chained.
     */
    public UploadInfo setDeleteOnUpload(boolean shouldDelete) {
        this.mDeleteOnUpload = shouldDelete;
        return this;
    }

    /**
     * Sets any extra information to be associated with this file upload.
     * @param extras A hash map with key value pairs.
     * @return A reference to the same {@code UploadInfo} object, so calls to set properties can be chained.
     */
    public UploadInfo setExtras(Map<String, String> extras) {
        this.mExtras = extras;
        return this;
    }

    /**
     * Gets the unique ID for the file upload.
     * @return String value.
     */
    public String getUploadId() {
        return this.mUploadId;
    }

    /**
     * Gets the absolute path of file to upload.
     * @return File path.
     */
    public String getFilePath() {
        return this.mFilePath;
    }

    /**
     * Gets the url to upload the file to.
     * @return Url.
     */
    public String getUploadUrl() {
        return this.mUploadUrl;
    }

    /**
     * Gets whether the file will be deleted after upload.
     * @return Boolean value.
     */
    public boolean getDeleteOnUpload() {
        return this.mDeleteOnUpload;
    }

    /**
     * Gets any extra information associated with this file upload.
     * @return A hash map of key value pairs.
     */
    public Map<String, String> getExtras() {
        return this.mExtras;
    }
}
