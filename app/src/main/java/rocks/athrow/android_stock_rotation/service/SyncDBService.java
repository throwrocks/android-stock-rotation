package rocks.athrow.android_stock_rotation.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import rocks.athrow.android_stock_rotation.data.SyncDB;

/**
 * UpdateDBService
 * Created by joselopez on 1/10/17.
 */

public class SyncDBService extends IntentService {
    public static final String SERVICE_NAME = "SyncDBService";

    public SyncDBService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        SyncDB.downloadNewRecords(getApplicationContext());
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SERVICE_NAME));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
