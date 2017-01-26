package rocks.athrow.android_stock_rotation.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.service.UpdateDBService;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;
import rocks.athrow.android_stock_rotation.util.Utilities;


public class MainActivity extends AppCompatActivity {
    public static final String MODULE_TYPE = "type";
    public static final String MODULE_RECEIVING = "Receive";
    public static final String MODULE_MOVING = "Move";
    public static final String MODULE_PICKING = "Pick";
    public static final String MODULE_SALVAGE = "Salvage";
    private ProgressBar mSyncProgressBar;
    private ImageView mSyncIcon;
    private final BroadcastReceiver mReceiver = new ResponseReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        RealmQueries.deleteInvalidTransactions(context);
        RealmQueries.deleteRequests(context);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
        mSyncProgressBar = (ProgressBar) findViewById(R.id.sync_progress);
        mSyncIcon = (ImageView) findViewById(R.id.sync_icon);
        LinearLayout moduleReceiving = (LinearLayout) findViewById(R.id.module_receiving);
        LinearLayout moduleMoving = (LinearLayout) findViewById(R.id.module_moving);
        LinearLayout modulePicking = (LinearLayout) findViewById(R.id.module_picking);
        LinearLayout moduleSalvage = (LinearLayout) findViewById(R.id.module_salvage);
        LinearLayout moduleTransfers = (LinearLayout) findViewById(R.id.module_transfers);
        LinearLayout moduleLocations = (LinearLayout) findViewById(R.id.module_locations);
        LinearLayout moduleSync = (LinearLayout) findViewById(R.id.module_sync);

        moduleReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MODULE_RECEIVING);
            }
        });
        moduleMoving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MODULE_MOVING);
            }
        });
        modulePicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MODULE_PICKING);
            }
        });
        moduleSalvage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MODULE_SALVAGE);
            }
        });
        moduleLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationsActivity.class);
                startActivity(intent);
            }
        });
        moduleTransfers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TransfersActivity.class);
                startActivity(intent);
            }
        });
        moduleSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sync();
            }
        });
        setUpCounts();
        updateSyncDate();
        updateSyncView(isMyServiceRunning());
    }

    private void setUpCounts() {
        UpdateCounts updateCounts = new UpdateCounts(getApplicationContext());
        updateCounts.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * startActivity
     *
     * @param type the type of RotationActivity
     *             Receiving, Moving, Picking, or Salvage
     */
    private void startActivity(String type) {
        Intent intent = new Intent(this, RotationActivity.class);
        intent.putExtra(MODULE_TYPE, type);
        startActivity(intent);
    }

    /**
     * isMyServiceRunning
     *
     * @return true if UpdateDBService is running, and false if not
     */
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        Log.e("service", " ---------------------------------------------------");
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.e("service ", service.service.getClassName());
            if ("rocks.athrow.android_stock_rotation.service.UpdateDBService".equals(service.service.getClassName())) {
                Log.e("service", " is running");
                Log.e("service", " ---------------------------------------------------");
                return true;

            }
        }
        Log.e("service", " ---------------------------------------------------");
        return false;
    }

    /**
     * sync
     * Runs the UpdateDBService service
     */
    private void sync() {
        if (isMyServiceRunning()) {
            Utilities.showToast(getApplicationContext(), "Sync in progress.", Toast.LENGTH_SHORT);
            updateSyncView(false);
        } else {
            String serviceBroadcast = UpdateDBService.SERVICE_NAME;
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(serviceBroadcast));
            Intent updateDBIntent = new Intent(this, UpdateDBService.class);
            this.startService(updateDBIntent);
            updateSyncView(true);
        }
    }

    /**
     * updateSyncDate
     * Sets the last synced date
     */
    private void updateSyncDate() {
        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
        String date = preferencesHelper.loadString("last_sync", "Never");
        TextView syncDate = (TextView) findViewById(R.id.text_sync);
        syncDate.setText(date);
    }

    /**
     * MupdatedSyncView
     * Sets the sync progress bar animation
     *
     * @param isRunning is the UpdateDBService running
     */
    private void updateSyncView(boolean isRunning) {
        if (isRunning) {
            mSyncProgressBar.setVisibility(View.VISIBLE);
            mSyncIcon.setVisibility(View.GONE);
        } else {
            mSyncProgressBar.setVisibility(View.GONE);
            mSyncIcon.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ResponseReceiver
     * A class to manage handling the UpdateDBService response
     */
    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            updateSyncView(false);
            updateSyncDate();
            setUpCounts();
        }
    }

    /**
     * UpdateCounts
     * AsyncTask to set the total counts, off the UI thread because of multiple database calls
     */
    private class UpdateCounts extends AsyncTask<String, Void, int[]> {
        Context context;

        UpdateCounts(Context context) {
            this.context = context;
        }

        @Override
        protected int[] doInBackground(String... params) {
            int countReceiving = RealmQueries.getCountPendingTransactions(context, MODULE_RECEIVING);
            int countMoving = RealmQueries.getCountPendingTransactions(context, MODULE_MOVING);
            int countPicking = RealmQueries.getCountPendingTransactions(context, MODULE_PICKING);
            int countSalvage = RealmQueries.getCountPendingTransactions(context, MODULE_SALVAGE);
            int countTransfers = RealmQueries.getCountPendingTransfers(context);
            int[] results = new int[5];
            results[0] = countReceiving;
            results[1] = countMoving;
            results[2] = countPicking;
            results[3] = countSalvage;
            results[4] = countTransfers;
            return results;
        }

        @Override
        protected void onPostExecute(int[] counts) {
            super.onPostExecute(counts);
            TextView countReceivingView = (TextView) findViewById(R.id.count_receiving);
            TextView countMovingView = (TextView) findViewById(R.id.count_moving);
            TextView countPickingView = (TextView) findViewById(R.id.count_picking);
            TextView countSalvageView = (TextView) findViewById(R.id.count_salvage);
            TextView countTransfersView = (TextView) findViewById(R.id.count_transfers);
            countReceivingView.setText(String.valueOf(counts[0]));
            countMovingView.setText(String.valueOf(counts[1]));
            countPickingView.setText(String.valueOf(counts[2]));
            countSalvageView.setText(String.valueOf(counts[3]));
            countTransfersView.setText(String.valueOf(counts[4]));
        }
    }
}
