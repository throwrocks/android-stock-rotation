package rocks.athrow.android_stock_rotation;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rocks.athrow.android_stock_rotation.api.APIRestFM;
import rocks.athrow.android_stock_rotation.api.APIResponse;

import static junit.framework.Assert.assertTrue;

/**
 * Created by joselopez on 1/31/17.
 */

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class PostUnitTests extends Robolectric {

    @Test
    public void postTransfer() throws Exception {
        String transferJSON =
                "{\"data\":[" +
                        "{\"id\":\"TEST-ID\", " +
                        "\"transactionId\":\"TEST-TRANSACTION-ID\", " +
                        "\"transactionType\":\"Init\", " +
                        "\"date\":\"01/30/17 3:39:51 PM\", " +
                        "\"type\":\"in\", " +
                        "\"itemId\":\"52F796E0-8C6E-2349-A165-8A7232C29073\", " +
                        "\"sku\":\"150624\", " +
                        "\"itemDescription\":\"BURRITO, BEAN & CHEESE INDIVIDUALLY WRAPPED\"," +
                        "\"tagNumber\":\"786144\", " +
                        "\"packSize\":\"96/4.50 oz\", " +
                        "\"receivingId\":\"158\", " +
                        "\"receivedDate\":\"4/19/2016\", " +
                        "\"expirationDate\":\"4/19/2017\"," +
                        "\"location\":\"FU-09\", " +
                        "\"caseQty\":\"114\"}" +
                        "]" +
                        "}";
        APIResponse apiResponse = APIRestFM.postTransfer(transferJSON);
        System.out.println(apiResponse.getResponseText());
        assertTrue(apiResponse.getResponseCode() == 201);

    }

}
