# Async File Uploader

[![Download](https://api.bintray.com/packages/dilipvinu/maven/async-file-uploader/images/download.svg) ](https://bintray.com/dilipvinu/maven/async-file-uploader/_latestVersion)

Async File Uploader is an Android library for uploading files in the background.

Features
--------
 - Fire and forget file uploads, just queue an upload and you're done
 - Get notified in case you want to respond to upload events
 - Automatically handles network conditions and reboots

Usage
-----
 1. Create an `UploadInfo` entity which contains information about the upload
        
        UploadInfo info = new UploadInfo()
                .setUploadId(uniqueId)
                .setFilePath(localFilePath)
                .setUploadUrl(remoteUploadUrl);
        
 2. Add it to the `UploadQueue`
 
        UploadQueue.schedule(context, info);

Getting notified
----------------
You can listen to file upload events by registering a `BroadcastReceiver`.

Create a new class which extends `UploadBroadcastReceiver` and register it in the manifest.
Replace `com.foo.example` in the intent filter category with your application package name.

    <receiver android:name="MyUploadReceiver">
        <intent-filter>
            <action android:name="com.nuvoex.fileuploader.ACTION_STATUS_CHANGE" />
            <category android:name="com.foo.example.CATEGORY_UPLOAD" />
        </intent-filter>
    </receiver>
    
Implement the methods in the `UploadBroadcastReceiver` for the events you wish to respond to.

    public class MyUploadReceiver extends UploadBroadcastReceiver {
        @Override
        public void onStart(Context context, String uploadId, Map<String, String> extras) {
            // Upload started
        }
        
        @Override
        public void onFail(Context context, String uploadId, Map<String, String> extras, UploadError error) {
            // Upload failed, reason in UploadError
        }
        
        @Override
        public void onComplete(Context context, String uploadId, Map<String, String> extras) {
            // Upload completed successfully
        }
        
        @Override
        public void onCancel(Context context, String uploadId, Map<String, String> extras) {
            // Upload has been cancelled
        }

Check out the sample application in `sample/` to see it in action.

Documentaion
------------
Visit http://nuvoex.com/async-file-uploader for complete API documentation.

Download
--------
```
compile 'com.nuvoex:fileuploader:1.0.3'
```
License
-------

	Copyright 2017 NuvoEx

	Permission is hereby granted, free of charge, to any person obtaining a copy of 
	this software and associated documentation files (the "Software"), to deal in 
	the Software without restriction, including without limitation the rights to 
	use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
	of the Software, and to permit persons to whom the Software is furnished to do 
	so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all 
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
	SOFTWARE.
