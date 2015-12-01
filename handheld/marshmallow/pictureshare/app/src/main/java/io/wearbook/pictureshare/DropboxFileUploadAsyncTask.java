package io.wearbook.pictureshare;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import android.app.ProgressDialog;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by sanjay on 11/30/15.
 */

//TODO implement progress status callback

public class DropboxFileUploadAsyncTask extends AsyncTask<Void, Long, Boolean> {
    private Context context ;
    private DropboxAPI dropboxAPI ;
    private File file ;
    private final String DROPBOX_PHOTOS_PATH = "/photos/" ;
    private boolean allGoodToGoFlag = true ;

    private DropboxAPI.UploadRequest uploadRequest ;


    public DropboxFileUploadAsyncTask(Context theContext, DropboxAPI theDropboxAPI, File theFile) {
        this.context = theContext ;
        this.dropboxAPI = theDropboxAPI ;
        this.file = theFile ;
        Log.d ( TAG, "DropboxFileUploadAsyncTask() toString()" + this.toString()) ;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        Log.d ( TAG, "doInBackground() ..."  ) ;

        FileInputStream fileInputStream = null  ;
        try {
            fileInputStream = new FileInputStream( file ) ;
        } catch ( Exception e ) {
            Log.e ( TAG, "doInBackground() file exception=" + e ) ;
            allGoodToGoFlag = false ;
        }

        if ( allGoodToGoFlag) {
            try {
                uploadRequest = dropboxAPI.putFileOverwriteRequest( DROPBOX_PHOTOS_PATH + file.getName(), fileInputStream, file.length(),new ProgressListener() {
                    @Override
                    public long progressInterval() {
                        return 2000;
                    }

                    @Override
                    public void onProgress(long b, long total) {
                        publishProgress(b);
                    }
                });

                Log.d ( TAG, "doInBackground() about to call upload()..."  ) ;
                uploadRequest.upload() ;
                Log.d ( TAG, "doInBackground() (hopefully) uploaded"  ) ;


            } catch ( Exception e) {
                Log.e ( TAG, "doInBackground() upload exception=" + e ) ;
            }


        }

        return true;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DropboxFileUploadAsyncTask{");
        sb.append("context=").append(context);
        sb.append(", dropboxAPI=").append(dropboxAPI);
        sb.append(", file=").append(file);
        sb.append(", DROPBOX_PHOTOS_PATH='").append(DROPBOX_PHOTOS_PATH).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private static final String TAG = DropboxFileUploadAsyncTask.class.getName() ;
}
