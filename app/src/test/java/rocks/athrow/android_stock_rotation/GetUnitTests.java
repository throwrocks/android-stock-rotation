package rocks.athrow.android_stock_rotation;


import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import rocks.athrow.android_stock_rotation.api.APIRestFM;
import rocks.athrow.android_stock_rotation.api.APIResponse;

import static junit.framework.Assert.assertTrue;


@SuppressWarnings({"ALL", "SameParameterValue"})
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class GetUnitTests extends Robolectric {
    @Mock
    private Context mContext;
    private APIResponse mItems = null;
    private APIResponse mLocations = null;
    private APIResponse mTransfers = null;

    private APIResponse getItems(int lastSerialNumber) {
        return APIRestFM.getItems(lastSerialNumber);
    }
    private APIResponse getLocations(int lastSerialNumber) {
        return APIRestFM.getLocations(lastSerialNumber);
    }
    private APIResponse getTransfers(int lastSerialNumber) {
        return APIRestFM.getTransfers(lastSerialNumber);
    }
    @Before
    public void setUp() throws Exception {
        if (mContext == null) {
            mContext = RuntimeEnvironment.application.getApplicationContext();
        }
        if (mItems == null) {
            mItems = getItems(0);
        }
        if (mLocations == null) {
            mLocations = getLocations(0);
        }
        if (mTransfers == null) {
            mTransfers = getTransfers(0);
        }
    }

    @Test
    public void getItems() throws Exception {
        int responseCode = mItems.getResponseCode();
        assertTrue(responseCode == 200);
    }

    @Test
    public void getLocations() throws Exception {
        int responseCode = mLocations.getResponseCode();
        assertTrue(responseCode == 200);
    }
    @Test
    public void getTransfers() throws Exception {
        int responseCode = mTransfers.getResponseCode();
        assertTrue(responseCode == 200);
    }

}