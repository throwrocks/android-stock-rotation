package rocks.athrow.android_stock_rotation.activity;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
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

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.service.SyncDBJobService;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;
import rocks.athrow.android_stock_rotation.util.Utilities;


public class MainActivity extends AppCompatActivity {
    private final static String DATE_TIME_DISPLAY = "MM/dd/yy h:mm:ss a";
    private static final String LOG_TAG = "MainActivity";
    public static final String MODULE_TYPE = "type";
    public static final String MODULE_RECEIVING = "Receive";
    public static final String MODULE_MOVING = "Move";
    public static final String MODULE_PICKING = "Stage";
    public static final String MODULE_SALVAGE = "Salvage";
    private ProgressBar mSyncProgressBar;
    private ImageView mSyncIcon;
    private Runnable mSyncStatusRunnable;
    private Handler mSyncStatusHandler;

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
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm.compactRealm(realmConfig);
        mSyncProgressBar = (ProgressBar) findViewById(R.id.sync_progress);
        mSyncIcon = (ImageView) findViewById(R.id.sync_icon);
        mSyncStatusHandler = new Handler();
        LinearLayout moduleReceiving = (LinearLayout) findViewById(R.id.module_receiving);
        LinearLayout moduleMoving = (LinearLayout) findViewById(R.id.module_moving);
        LinearLayout modulePicking = (LinearLayout) findViewById(R.id.module_picking);
        LinearLayout moduleSalvage = (LinearLayout) findViewById(R.id.module_salvage);
        LinearLayout moduleTransfers = (LinearLayout) findViewById(R.id.module_transfers);
        LinearLayout moduleLocations = (LinearLayout) findViewById(R.id.module_locations);
        LinearLayout moduleValidate = (LinearLayout) findViewById(R.id.module_validate);
        //LinearLayout moduleSync = (LinearLayout) findViewById(R.id.module_sync);

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
        //jobInfo.setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR);
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo.build());
        if (result == 1) {
            Log.e(LOG_TAG, "scheduleSyncDB " + "Success");
        } else {
            Log.e(LOG_TAG, "scheduleSyncDB " + "Failure");
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

    private void updateSyncStatus() {
        mSyncStatusRunnable = new Runnable() {
            @Override
            public void run() {
                if ( isMyServiceRunning()){
                    updateSyncView(true);
                }else{
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
        //Log.d("service", " ------------------------CHEKCING BACKGROUND SERVICES---------------------------");
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //Log.d("service ", service.service.getClassName());
            if ("rocks.athrow.android_stock_rotation.service.SyncDBJobService".equals(service.service.getClassName())) {
                Log.e("service", " SyncDBJobService is running");
                //Log.d("service", " ---------------------------------------------------");
                return true;
            }
        }
        //Log.d("service", " ---------------------------------------------------");
        return false;
    }

    /**
     * updateSyncDate
     * Sets the last synced date
     */
    private void updateSyncDate() {
        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
        String lastSyncString = preferencesHelper.loadString("last_sync", "Never");
        String lastSyncDateDisplay;
        if ( !lastSyncString.equals("Never")){
            Date lastSyncDate = Utilities.getStringAsDate(lastSyncString, DATE_TIME_DISPLAY, null);
            lastSyncDateDisplay = Utilities.getDateAsString(lastSyncDate, DATE_TIME_DISPLAY, null);
        }else{
            lastSyncDateDisplay = lastSyncString;
        }

        TextView syncDate = (TextView) findViewById(R.id.text_sync);
        syncDate.setText(lastSyncDateDisplay);
    }

    /**
     * MupdatedSyncView
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
