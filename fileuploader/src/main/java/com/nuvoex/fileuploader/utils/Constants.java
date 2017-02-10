package com.nuvoex.fileuploader.utils;

/**
 * Created by dilip on 04/01/17.
 */

/**
 * Utility class for defining all constant values.
 */
public class Constants {

    /**
     * Configuration values.
     */
    public final class Configs {

        /**
         * Timeout for file upload requests in seconds.
         */
        public static final int TIMEOUT = 60;
    }

    public final class Urls {

        // dummy value, not used for uploads
        public static final String BASE_URL = "https://nuvoex.com";
    }

    /**
     * Keys for extra data.
     */
    public final class Keys {

        public static final String EXTRA_FILE_PATH = "com.nuvoex.fileuploader.FILE_LOCAL_PATH";

        public static final String EXTRA_UPLOAD_URL = "com.nuvoex.fileuploader.FILE_UPLOAD_URL";

        public static final String EXTRA_DELETE_ON_UPLOAD = "com.nuvoex.fileuploader.DELETE_ON_UPLOAD";

        public static final String EXTRA_EXTRAS = "com.nuvoex.fileuploader.EXTRAS";

        public static final String EXTRA_UPLOAD_STATUS = "com.nuvoex.fileuploader.FILE_UPLOAD_STATUS";

        public static final String EXTRA_UPLOAD_ERROR = "com.nuvoex.fileuploader.FILE_UPLOAD_ERROR";
    }

    /**
     * Actions broadcast by upload service.
     * Receivers can be declared in the manifest to respond to these broadcasts.
     */
    public final class Actions {

        /**
         * Broadcast action sent whenever a file upload status changes.
         * @see Status List of possible status values
         */
        public static final String STATUS_CHANGE = "com.nuvoex.fileuploader.ACTION_STATUS_CHANGE";
    }

    /**
     * Status associated with each file upload event.
     */
    public final class Status {

        /**
         * File upload has started.
         */
        public static final int STARTED = 1;

        /**
         * File upload failed. It will be automatically retried.
         */
        public static final int FAILED = 2;

        /**
         * File upload completed.
         */
        public static final int COMPLETED = 3;

        /**
         * File upload was cancelled. It will not be retried.
         */
        public static final int CANCELLED = 4;
    }

    public static final String TAG = "FILE_UPLOAD";
}
