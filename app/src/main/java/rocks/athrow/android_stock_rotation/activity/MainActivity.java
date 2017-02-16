package rocks.athrow.android_stock_rotation.activity;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.service.SyncDBJobService;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;

import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_ADJUST;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_MOVING;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_RECEIVING;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_TYPE;

public class MainActivity extends AppCompatActivity {
    private static final String NEVER = "Never";
    private static final String LAST_SYNC = "last_sync";
    private ProgressBar mSyncProgressBar;
    private ImageView mSyncIcon;
    private Runnable mSyncStatusRunnable;
    private Handler mSyncStatusHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
        RealmQueries.deleteInvalidTransactions(context);
        mSyncProgressBar = (ProgressBar) findViewById(R.id.sync_progress);
        mSyncIcon = (ImageView) findViewById(R.id.sync_icon);
        mSyncStatusHandler = new Handler();
        LinearLayout moduleReceiving = (LinearLayout) findViewById(R.id.module_receiving);
        LinearLayout moduleMoving = (LinearLayout) findViewById(R.id.module_moving);
        LinearLayout moduleAdjust = (LinearLayout) findViewById(R.id.module_adjust);
        LinearLayout moduleTransfers = (LinearLayout) findViewById(R.id.module_transfers);
        LinearLayout moduleLocations = (LinearLayout) findViewById(R.id.module_locations);
        LinearLayout moduleValidate = (LinearLayout) findViewById(R.id.module_validate);

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
        moduleAdjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MODULE_ADJUST);
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
        moduleValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ValidateActivity.class);
                startActivity(intent);
            }
        });
        updateSyncDate();
        scheduleSyncDB();
    }

    private void scheduleSyncDB() {
        ComponentName serviceName = new ComponentName(this, SyncDBJobService.class);
        JobInfo.Builder jobInfo = new JobInfo.Builder(1, serviceName);
        jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        jobInfo.setPeriodic(10000);
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(jobInfo.build());
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
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.main_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSyncStatus() {
        mSyncStatusRunnable = new Runnable() {
            @Override
            public void run() {
                if (isMyServiceRunning()) {
                    updateSyncView(true);
                } else {
                    updateSyncView(false);
                }
                mSyncStatusHandler.postDelayed(this, 500);
            }
        };
        mSyncStatusHandler.post(mSyncStatusRunnable);
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
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("rocks.athrow.android_stock_rotation.service.SyncDBJobService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * updateSyncDate
     * Sets the last synced date
     */
    private void updateSyncDate() {
        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
        String lastSyncString = preferencesHelper.loadString(LAST_SYNC, NEVER);
        TextView syncDate = (TextView) findViewById(R.id.text_sync);
        syncDate.setText(lastSyncString);
    }

    /**
     * updateSyncView
     * Sets the downloadNewRecords progress bar animation
     *
     * @param isRunning is the UpdateDBService running
     */
    private void updateSyncView(boolean isRunning) {
        if (isRunning) {
            mSyncProgressBar.setVisibility(View.VISIBLE);
            mSyncIcon.setVisibility(View.GONE);
            updateSyncDate();
        } else {
            mSyncProgressBar.setVisibility(View.GONE);
            mSyncIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        updateSyncStatus();
        setUpCounts();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSyncStatusHandler.removeCallbacks(mSyncStatusRunnable);
    }

    /**
     * UpdateCounts
     * AsyncTask to set the total counts, off the UI thread because of multiple database calls
     */
    private class UpdateCounts extends AsyncTask<String, Void, int[]> {
        final Context context;

        UpdateCounts(Context context) {
            this.context = context;
        }

        @Override
        protected int[] doInBackground(String... params) {
            int countReceiving = RealmQueries.getCountPendingTransactions(context, MODULE_RECEIVING);
            int countMoving = RealmQueries.getCountPendingTransactions(context, MODULE_MOVING);
            int countAdjust = RealmQueries.getCountPendingTransactions(context, MODULE_ADJUST);
            int countTransfers = RealmQueries.getCountPendingTransfers(context);
            int[] results = new int[4];
            results[0] = countReceiving;
            results[1] = countMoving;
            results[2] = countAdjust;
            results[3] = countTransfers;
            return results;
        }

        @Override
        protected void onPostExecute(int[] counts) {
            super.onPostExecute(counts);
            TextView countReceivingView = (TextView) findViewById(R.id.count_receiving);
            TextView countMovingView = (TextView) findViewById(R.id.count_moving);
            TextView countAdjustView = (TextView) findViewById(R.id.count_adjust);
            TextView countTransfersView = (TextView) findViewById(R.id.count_transfers);
            countReceivingView.setText(String.valueOf(counts[0]));
            countMovingView.setText(String.valueOf(counts[1]));
            countAdjustView.setText(String.valueOf(counts[2]));
            countTransfersView.setText(String.valueOf(counts[3]));
        }
    }
}
