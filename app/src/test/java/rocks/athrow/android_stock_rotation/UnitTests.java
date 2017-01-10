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

import rocks.athrow.android_stock_rotation.api.API;
import rocks.athrow.android_stock_rotation.api.APIResponse;

import static junit.framework.Assert.assertTrue;


@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class UnitTests extends Robolectric {
    @Mock
    private Context mContext;
    private APIResponse mItems = null;
    private APIResponse mLocations = null;

    private APIResponse getItems(int lastSerialNumber) {
        return API.getItems(lastSerialNumber);
    }
    private APIResponse getLocations(int lastSerialNumber) {
        return API.getLocations(lastSerialNumber);
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

}