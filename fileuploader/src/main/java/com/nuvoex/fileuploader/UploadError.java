package com.nuvoex.fileuploader;

import java.io.Serializable;

/**
 * Created by dilip on 02/02/17.
 */

public class UploadError implements Serializable {

    public static final int ERROR_RESPONSE = 1;
    public static final int ERROR_NETWORK = 2;

    private int type;
    private int code;
    private String message;

    public UploadError(int type, int code, String message) {
        this.type = type;
        this.code = code;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
