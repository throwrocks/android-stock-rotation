package rocks.athrow.android_stock_rotation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import rocks.athrow.android_stock_rotation.R;

/**
 * RotationActivity
 * Created by joselopez on 1/9/17.
 */

public class RotationActivity extends AppCompatActivity {
    public static final String ADD_ITEM_ACTION = "action";
    public static final String ACTION_SCAN = "scan";
    private String mRotationType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);
        Intent intent = getIntent();
        mRotationType = intent.getStringExtra(MainActivity.MODULE_TYPE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rotation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.rotation_add_item:
                Intent intent = new Intent(this, ScanActivity.class);
                intent.putExtra(MainActivity.MODULE_TYPE, mRotationType);
                intent.putExtra(ADD_ITEM_ACTION, ACTION_SCAN);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
