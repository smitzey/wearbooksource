package wearbook.io.toolbar;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

public class ToolbarActivity extends Activity {

    private Toolbar toolbar ;
    private ActionBar actionBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);

        /* The Toolbar -  introduced in API level 21 -  is a generalization of the concept of
        * actiobar, and can be placed at any level of the view heirarchy.
        *
        * The Toolbar is more versatile and powerful.
        *
        * An Activity can designate a Toolbar as its action bar, by invoking  setActionBar with
        * the Toolbar instance is its argument, as the following example illustrates.
        *
        * Sanjay Mishra
        * Author : "Wearable Android ..." 2015/ WILEY
        *
        *
        * */

        toolbar = (Toolbar) findViewById ( R.id.toolbarActionbar) ;
        toolbar.setTitle( R.string.toolbar_title);
        toolbar.setLogo( R.mipmap.ic_launcher);


        setActionBar(toolbar);

        actionBar = getActionBar() ;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
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
}
