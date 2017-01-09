package rocks.athrow.android_stock_rotation.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;
import rocks.athrow.android_stock_rotation.zxing.IntentResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void scan(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String contents = scanResult.getContents();
        }

    }
}
