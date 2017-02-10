package com.nuvoex.fileuploader;

import java.io.Serializable;

/**
 * Created by dilip on 02/02/17.
 */

/**
 * Contains information about a file upload error.
 */
public class UploadError implements Serializable {

    /**
     * Error type for client or server errors.
     */
    public static final int ERROR_RESPONSE = 1;

    /**
     * Error type for network errors.
     */
    public static final int ERROR_NETWORK = 2;

    private int type;
    private int code;
    private String message;

    protected UploadError(int type, int code, String message) {
        this.type = type;
        this.code = code;
        this.message = message;
    }

    /**
     * Gets the error type.
     * @return Integer with value {@link #ERROR_RESPONSE} or {@link #ERROR_NETWORK}.
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the error code.
     * @return HTTP status returned by the server or 0 in case of network error.
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets description of the error.
     * @return Any message returned by the server or exception message in case of network error.
     */
    public String getMessage() {
        return message;
    }
}
