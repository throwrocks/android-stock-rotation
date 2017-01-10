package rocks.athrow.android_stock_rotation.interfaces;

import rocks.athrow.android_stock_rotation.api.APIResponse;

/**
 * OnTaskComplete
 * Created by joselopez on 1/10/17.
 */

public interface OnTaskComplete {
    @SuppressWarnings("MethodNameSameAsClassName")
    void OnTaskComplete(APIResponse apiResponses);
}