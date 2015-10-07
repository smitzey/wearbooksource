package io.wearbook.blem.shakespeak;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    private final int BOOT_PERMISSION_REQUEST = 99 ;
    private final int CAMERA_PERMISSION_REQUEST = 80 ;
    private int bootPermissionRejectionCount = 0  ;
     // in this run only ? or should this be persisted ?
    // probably better to persist
    //@TODO display friendly explanation to use if user not accepting boot permission





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // do I have the Boot permission already accepted ?

    }

    /**
     * Called after {@link #onCreate} &mdash; or after {@link #onRestart} when
     * the activity had been stopped, but is now again being displayed to the
     * user.  It will be followed by {@link #onResume}.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onCreate
     * @see #onStop
     * @see #onResume
     */
    @Override
    protected void onStart() {
        super.onStart();
        checkBootPermissionAcceptance() ;
    }

    void checkBootPermissionAcceptance() {

        Log.d ( TAG, "checkBootPermissionAcceptance() ...") ;

        if (checkSelfPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

                ) {

            requestPermissions(new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.CAMERA}, BOOT_PERMISSION_REQUEST);
        }

        // once boot permission has been accepted
        // register for boot completion/ probably nothing to do
        // as this is handled in the manifest declaration
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    
        switch ( requestCode) {
            case BOOT_PERMISSION_REQUEST:
                checkBootPermissionAcceptance() ;
                break;

            case CAMERA_PERMISSION_REQUEST:
                checkBootPermissionAcceptance() ;
                break;

            default :
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                
        }
    



        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final String TAG = MainActivity.class.getName() ;
}
