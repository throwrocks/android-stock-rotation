package rocks.athrow.android_stock_rotation.api;

/**
 * APIResponse
 * Created by joselopez on 1/9/17.
 */

public final class APIResponse {
    private String responseText;
    private int responseCode;

    public APIResponse() {
    }

    /**
     * setResponseCode
     *
     * @param responseCode the APIRestFM's response code number
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * setResponseText
     *
     * @param responseText the APIRestFM's response text
     */
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    /**
     * getResponseCode
     *
     * @return the APIRestFM's response code number
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * getResponseText
     *
     * @return the APIRestFM's response text
     */
    public String getResponseText() {
        return responseText;
    }

}