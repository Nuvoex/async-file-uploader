package com.nuvoex.fileuploadersample;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nuvoex.fileuploader.UploadInfo;
import com.nuvoex.fileuploader.UploadQueue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String UPLOAD_URL = "http://media-nuvo.s3.amazonaws.com/test/AWB/images/QWERTY_TEST_png_pid_1234567_3728123.png?Signature=cDI4FJGcuzT7mH2%2BI2XF0Meah%2BU%3D&Expires=1515073865&AWSAccessKeyId=AKIAJKYQRPL5EOZJMFFQ";

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelUpload();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            scheduleUpload();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.nuvoex.fileuploadersample.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void scheduleUpload() {
        Map<String, String> extras = new HashMap<>();
        extras.put("awb", "10000");
        UploadInfo info = new UploadInfo()
                .setUploadId("10000_SBI_3443_pid_344349.png")
                .setFilePath(mCurrentPhotoPath)
                .setUploadUrl(UPLOAD_URL)
                .setDeleteOnUpload(false)
                .setExtras(extras);
        UploadQueue.schedule(this, info);
    }

    private void cancelUpload() {
        UploadQueue.clear(this);
    }
}
