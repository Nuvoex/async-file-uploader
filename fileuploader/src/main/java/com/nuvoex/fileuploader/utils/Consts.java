package com.nuvoex.fileuploader.utils;

/**
 * Created by dilip on 04/01/17.
 */

public class Consts {

    public final class Configs {
        public static final String PREF_NAME = "file_uploader_job_list";
        public static final int TIMEOUT = 60;
    }

    public final class Urls {
        public static final String BASE_URL = "https://nuvoex.com";
    }

    public final class Keys {
        public static final String EXTRA_FILE_PATH = "com.nuvoex.fileuploader.FILE_LOCAL_PATH";
        public static final String EXTRA_UPLOAD_URL = "com.nuvoex.fileuploader.FILE_UPLOAD_URL";
        public static final String EXTRA_DELETE_ON_UPLOAD = "com.nuvoex.fileuploader.DELETE_ON_UPLOAD";
        public static final String EXTRA_EXTRAS = "com.nuvoex.fileuploader.EXTRAS";
        public static final String EXTRA_UPLOAD_STATUS = "com.nuvoex.fileuploader.FILE_UPLOAD_STATUS";
    }

    public final class Actions {
        public static final String STATUS_CHANGE = "com.nuvoex.fileuploader.ACTION_STATUS_CHANGE";
    }

    public final class Status {
        public static final int STARTED = 1;
        public static final int FAILED = 2;
        public static final int COMPLETED = 3;
        public static final int CANCELLED = 4;
    }

    public static final String TAG = "FILE_UPLOAD";
}
