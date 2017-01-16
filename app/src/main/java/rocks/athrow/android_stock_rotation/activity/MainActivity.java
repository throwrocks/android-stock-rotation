package rocks.athrow.android_stock_rotation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.api.APIResponse;
import rocks.athrow.android_stock_rotation.api.FetchTask;
import rocks.athrow.android_stock_rotation.data.DataUtilities;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.Request;
import rocks.athrow.android_stock_rotation.interfaces.OnTaskComplete;
import rocks.athrow.android_stock_rotation.service.UpdateDBService;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;
import rocks.athrow.android_stock_rotation.util.Utilities;


public class MainActivity extends AppCompatActivity implements OnTaskComplete {
    public static final String MODULE_TYPE = "type";
    public static final String MODULE_RECEIVING = "Receive";
    public static final String MODULE_MOVING = "Move";
    public static final String MODULE_PICKING = "Pick";
    public static final String MODULE_SALVAGE = "Salvage";
    private final OnTaskComplete onTaskCompleted = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        DataUtilities.deleteInvalidTransactions(context);
        DataUtilities.deleteRequests(context);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
        TextView countReceveingView = (TextView) findViewById(R.id.count_receiving);
        TextView countMovingView = (TextView) findViewById(R.id.count_moving);
        TextView countPickingView = (TextView) findViewById(R.id.count_picking);
        TextView countSalvageView = (TextView) findViewById(R.id.count_salvage);
        TextView countTransfersView = (TextView) findViewById(R.id.count_transfers);
        LinearLayout moduleReceiving = (LinearLayout) findViewById(R.id.module_receiving);
        LinearLayout moduleMoving = (LinearLayout) findViewById(R.id.module_moving);
        LinearLayout modulePicking = (LinearLayout) findViewById(R.id.module_picking);
        LinearLayout moduleSalvage = (LinearLayout) findViewById(R.id.module_salvage);
        LinearLayout moduleTransfers = (LinearLayout) findViewById(R.id.module_transfers);
        LinearLayout moduleLocations = (LinearLayout) findViewById(R.id.module_locations);
        LinearLayout moduleSync = (LinearLayout) findViewById(R.id.module_sync);
        int countReceiving = DataUtilities.getCountPendingTransactions(context, MODULE_RECEIVING);
        int countMoving = DataUtilities.getCountPendingTransactions(context, MODULE_MOVING);
        int countPicking = DataUtilities.getCountPendingTransactions(context, MODULE_PICKING);
        int countSalvage = DataUtilities.getCountPendingTransactions(context, MODULE_SALVAGE);
        int countTransfers = DataUtilities.getCountPendingTransfers(context);
        countReceveingView.setText(String.valueOf(countReceiving));
        countMovingView.setText(String.valueOf(countMoving));
        countPickingView.setText(String.valueOf(countPicking));
        countSalvageView.setText(String.valueOf(countSalvage));
        countTransfersView.setText(String.valueOf(countTransfers));
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

    private void sync(){
        Context context = getApplicationContext();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Number itemLastSerialNumber = realm.where(Item.class).findAll().max(Item.FIELD_SERIAL_NUMBER);
        String itemSerialNumber = null;
        if ( itemLastSerialNumber != null ){
            itemSerialNumber = itemLastSerialNumber.toString();
        }
        Number locationLastSerialNumber = realm.where(Location.class).findAll().max(Location.FIELD_SERIAL_NUMBER);
        String locationSerialNumber = null;
        if ( locationLastSerialNumber != null ){
            locationSerialNumber = locationLastSerialNumber.toString();
        }
        realm.commitTransaction();
        realm.close();
        FetchTask fetchItems = new FetchTask(onTaskCompleted);
        FetchTask fetchLocations = new FetchTask(onTaskCompleted);
        //FetchTask fetchTransactions = new FetchTask(onTaskCompleted);
        fetchItems.execute(FetchTask.ITEMS, itemSerialNumber);
        fetchLocations.execute(FetchTask.LOCATIONS, locationSerialNumber);
        //fetchTransactions.execute(FetchTask.TRANSACTIONS);

    }

    /**
     * onTaskComplete
     *
     * @param apiResponse the API Response
     */
    private void onTaskComplete(APIResponse apiResponse) {
        if (apiResponse != null) {
            int responseCode = apiResponse.getResponseCode();
            Intent updateDBIntent = new Intent(this, UpdateDBService.class);
            switch (responseCode) {
                case 200:
                    String requestId = UUID.randomUUID().toString();
                    String responseMeta = apiResponse.getMeta();
                    String responseText = apiResponse.getResponseText();
                    String requestURI = apiResponse.getRequestURI();
                    updateDBIntent.putExtra(UpdateDBService.TYPE, responseMeta);
                    updateDBIntent.putExtra(UpdateDBService.REQUEST_ID, requestId);
                    RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
                    Realm.setDefaultConfiguration(realmConfig);
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    Request request = realm.createObject(Request.class);
                    request.setId(requestId);
                    request.setRequestURL(requestURI);
                    request.setAPIResponseCode(responseCode);
                    request.setAPIResponseText(responseText);

                    realm.commitTransaction();
                    realm.close();

                    String serviceBroadcast = null;
                    switch (responseMeta) {
                        case FetchTask.ITEMS:
                            serviceBroadcast = UpdateDBService.UPDATE_ITEMS_DB_SERVICE_BROADCAST;
                            break;
                        case FetchTask.LOCATIONS:
                            serviceBroadcast = UpdateDBService.UPDATE_LOCATIONS_DB_SERVICE_BROADCAST;
                            break;
                        case FetchTask.TRANSACTIONS:
                            serviceBroadcast = UpdateDBService.UPDATE_TRANSACTIONS_DB_SERVICE_BROADCAST;
                            break;
                    }
                    if (serviceBroadcast != null) {
                        LocalBroadcastManager.getInstance(this).
                                registerReceiver(new ResponseReceiver(),
                                        new IntentFilter(serviceBroadcast));
                        this.startService(updateDBIntent);
                        break;
                    }
                default:
                    Utilities.showToast(getApplicationContext(), "Nothing found.", Toast.LENGTH_SHORT);
                    break;
            }
        }
    }

    @Override
    public void OnTaskComplete(APIResponse apiResponse) {
        onTaskComplete(apiResponse);
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
            PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
            preferencesHelper.save("last_sync", new Date().toString() );
        }
    }
}
