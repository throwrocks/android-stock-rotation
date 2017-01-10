package rocks.athrow.android_stock_rotation.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import rocks.athrow.android_stock_rotation.R;

public class MainActivity extends AppCompatActivity {
    public static final String MODULE_TYPE = "type";
    private static final String MODULE_RECEIVING = "Receiving";
    private static final String MODULE_MOVING = "Moving";
    private static final String MODULE_PICKING = "Picking";
    private static final String MODULE_SALVAGE = "Salvage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout moduleReceiving = (LinearLayout) findViewById(R.id.moduleReceiving);
        LinearLayout moduleMoving = (LinearLayout) findViewById(R.id.moduleMoving);
        LinearLayout modulePicking = (LinearLayout) findViewById(R.id.modulePicking);
        LinearLayout moduleSalvage = (LinearLayout) findViewById(R.id.moduleSalvage);
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
}
