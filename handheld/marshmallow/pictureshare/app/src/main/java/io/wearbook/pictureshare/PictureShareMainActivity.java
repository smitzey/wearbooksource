package io.wearbook.pictureshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import com.dropbox.client2.android.AndroidAuthSession;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is the core class for this PictureShare App.
 * It facilitates
 * Dropbox authentication
 * access to the camera
 * and also fires the background task to upload the image
 */

public class PictureShareMainActivity extends AppCompatActivity {

    /* please input your API KEY and SECRET below:
    https://www.dropbox.com/developers/apps/create */
    private static final String DROPBOX_APPKEY = "yourApiKey";
    private static final String DROPBOX_APPSECRET = "yourAppSecret";

    private AndroidAuthSession androidAuthSession;
    private DropboxAPI<AndroidAuthSession> dropboxAPI;
    private boolean dropboxSessionSuccessFlag = false;
    private AtomicBoolean authInProgress = new AtomicBoolean(false);

    //private String currentPhotoPath ;
    private File currentFile;

    //private DropboxSessionInitAsyncTask dropboxSessionInitAsyncTask ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_share_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( dropboxAPI != null && dropboxAPI.getSession().authenticationSuccessful()) {

                    fireCameraActionImageCaptureIntent();
                } else {
                    initDropBoxSession() ;
                }

                //Snackbar.make(view, "Take Picture", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

            }
        });

        //(new DropboxSessionInitAsyncTask ()).execute();

        /*FloatingActionButton fabMap = (FloatingActionButton) findViewById(R.id.fabMap);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Show Map", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        //initDropBoxSession() ;

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dropboxAPI != null) {

            if (authInProgress.get() == true) {

                Log.d(TAG, "onResume() dropboxAPI auth successful=" + dropboxAPI.getSession().authenticationSuccessful());

                if (dropboxAPI.getSession().authenticationSuccessful()) {
                    try {
                        // Required to complete auth, sets the access token on the session
                        dropboxAPI.getSession().finishAuthentication();
                        authInProgress.set(false);

                        //String accessToken = dropboxAPI.getSession().getAccessTokenPair().

                        // now enabled the Camera button


                    } catch (IllegalStateException e) {
                        Log.d(TAG, "onResume() exception=" + e);
                    }
                }
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture_share_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_dropbox_link) {

            // dropbox authenticate here:
            initDropBoxSession();

            return true;
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_IMAGE_CAPTURE_REQUEST && resultCode == RESULT_OK) {

            // the intent saved the image to the file.
            Log.d(TAG, "onActivityResult currentPhotoPath =" + currentFile.getAbsolutePath());

            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            // save bitmap to local photos
            // and then upload it to dropdox

            new DropboxFileUploadAsyncTask(this, dropboxAPI, currentFile).execute();

        }
    }


    // this method entails network access, therefore it should run the background -- or so I originally though
    private boolean initDropBoxSession() {
        boolean retVal = true; // add api key validation TODO

        AppKeyPair appKeyPair = new AppKeyPair(DROPBOX_APPKEY, DROPBOX_APPSECRET);
        Log.d(TAG, "initDropBoxSession dropbox appKeyPair=" + appKeyPair);
        AccessTokenPair accessTokenPair = new AccessTokenPair(DROPBOX_APPKEY, DROPBOX_APPSECRET);
        Log.d(TAG, "initDropBoxSession dropbox accessTokenPair=" + accessTokenPair);
        this.androidAuthSession = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessTokenPair);
        Log.d(TAG, "initDropBoxSession dropbox androidAuthSession =" + androidAuthSession);

        dropboxAPI = new DropboxAPI<AndroidAuthSession>(androidAuthSession);

        authInProgress.set(true);
        dropboxAPI.getSession().startAuthentication(this);


        return retVal;

    }


    private void fireCameraActionImageCaptureIntent() {
        Log.d(TAG, "fireCameraActionImageCaptureIntent()...");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ensure intent is resolvable on this device
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // ensure we can create file
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException exception) {
                Log.d(TAG, "fireCameraActionImageCaptureIntent exception=" + exception);

            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile)); // or current

                startActivityForResult(takePictureIntent, CAMERA_IMAGE_CAPTURE_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs(); // create it

        File file = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        Log.d(TAG, "createImageFile() file exists=" + file.exists());

        currentFile = file;
        Log.d(TAG, "createImageFile() file exists=" + file.getAbsolutePath());

        return file;
    }

    private void showDropboxProblemDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Dropbox API keys ")
                .setMessage("You have an issue with the Dropbox API keys, please contact App developer")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    /**
     * This class has been deprecated. Originally I was inclined to perform the dropbox operations
     * in the backbround, subsequently it appeared that the drobpox api addresses the backgrounding
     * to a large extent.
     */

    @Deprecated
    private class DropboxSessionInitAsyncTask extends AsyncTask<Void, Void, String> {

        //validate you have dropbox authenticated session
        private static final String SUCCESS = "SUCCESS";

        @Override
        protected String doInBackground(Void... params) {
            boolean result = initDropBoxSession();

            if (result) {
                dropboxSessionSuccessFlag = true;
                return SUCCESS;
            } else
                return "failure";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!SUCCESS.equals(s)) {
                Log.d(TAG, "dropbox auth session issues, show dialog");
                showDropboxProblemDialog();
            } else {

                dropboxAPI.getSession().startAuthentication(getApplicationContext());

            }


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    /* trivial constants at the end of file*/

    private static final int CAMERA_IMAGE_CAPTURE_REQUEST = 99;
    private static final  AccessType ACCESS_TYPE = AccessType.DROPBOX; // more than we need for testing, folder is enough
    private static final String TAG = PictureShareMainActivity.class.getName();
}
