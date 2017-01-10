package rocks.athrow.android_stock_rotation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.api.APIResponse;
import rocks.athrow.android_stock_rotation.api.FetchTask;
import rocks.athrow.android_stock_rotation.data.Request;
import rocks.athrow.android_stock_rotation.interfaces.OnTaskComplete;
import rocks.athrow.android_stock_rotation.service.UpdateDBService;
import rocks.athrow.android_stock_rotation.util.Utilities;

public class MainActivity extends AppCompatActivity implements OnTaskComplete {
    public static final String MODULE_TYPE = "type";
    private static final String MODULE_RECEIVING = "Receiving";
    private static final String MODULE_MOVING = "Moving";
    private static final String MODULE_PICKING = "Picking";
    private static final String MODULE_SALVAGE = "Salvage";
    final OnTaskComplete onTaskCompleted = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
        //LinearLayout moduleReceiving = (LinearLayout) findViewById(R.id.module_receiving);
        LinearLayout moduleMoving = (LinearLayout) findViewById(R.id.module_moving);
        LinearLayout modulePicking = (LinearLayout) findViewById(R.id.module_picking);
        LinearLayout moduleSalvage = (LinearLayout) findViewById(R.id.module_salvage);
        LinearLayout moduleSync = (LinearLayout) findViewById(R.id.module_sync);
        /*moduleReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MODULE_RECEIVING);
            }
        });*/
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
        moduleSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sync();
            }
        });
    }


    /**
     * startActivity
     *
     * @param type the type of RotationActivity
     *             Receiving, Moving, Picking, or Salvage
     */
    public void startActivity(String type) {
        Intent intent = new Intent(this, RotationActivity.class);
        intent.putExtra(MODULE_TYPE, type);
        startActivity(intent);
    }

    private void sync(){
        FetchTask fetchItems = new FetchTask(onTaskCompleted);
        //FetchTask fetchLocations = new FetchTask(onTaskCompleted);
        //FetchTask fetchTransactions = new FetchTask(onTaskCompleted);
        fetchItems.execute(FetchTask.ITEMS, null);
        //fetchLocations.execute(FetchTask.LOCATIONS, null);
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
                    Utilities.showToast(getApplicationContext(), apiResponse.getResponseText(), Toast.LENGTH_SHORT);
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
            Utilities.showToast(context, "show", 3);
        }
    }
}
