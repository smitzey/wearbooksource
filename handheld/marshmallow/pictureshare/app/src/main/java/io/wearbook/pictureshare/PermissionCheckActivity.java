package io.wearbook.pictureshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


/**
 * Activity that ensures that the most essential permissions are available
 *
 */

public class PermissionCheckActivity extends AppCompatActivity {

    private static final  String[] ESSENTIAL_PERMISSIONS =
            {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};

    private boolean permissionsGranted = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_check);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    protected void onStart() {
        super.onStart();

         checkEnsurePermissionsGranted(ESSENTIAL_PERMISSIONS);






    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults == null) {
                    checkEnsurePermissionsGranted(ESSENTIAL_PERMISSIONS);
                }
                for (int aGrantResult : grantResults) {
                    if (aGrantResult != PackageManager.PERMISSION_GRANTED) {
                        // // TODO: use helpful message if this repeats beyond threshold count of 3 ?
                        requestPermissions(ESSENTIAL_PERMISSIONS, PERMISSION_REQUEST);
                    }
                }

                /// permissions good , so head into the main application
                startNextActivity();


                break;
            default:
        }
    }

    private void  checkEnsurePermissionsGranted( final String [] permissionsNeeded) {
        boolean permissionStatus = true ; ;

        for ( String aPermission : permissionsNeeded ) {
            if ( PackageManager.PERMISSION_GRANTED != checkSelfPermission(aPermission) ) {
                permissionStatus = false ;
                break ;
            }
        }


        if (!permissionStatus) {
            // TODO add counter and introduce message indicating relevance of essential  permissions for this app
            requestPermissions(ESSENTIAL_PERMISSIONS, PERMISSION_REQUEST);
        } else {
            startNextActivity();
        }


    }

    /**
     * starts next activity and finishes this/current activity
     */
    private void startNextActivity  () {
        Intent mainApplicationIntent = new Intent (getApplicationContext(), PictureShareMainActivity.class) ;
        startActivity ( mainApplicationIntent) ;
        finish () ; // finish current activity

    }


    private static  final int PERMISSION_REQUEST = 88 ;

}
