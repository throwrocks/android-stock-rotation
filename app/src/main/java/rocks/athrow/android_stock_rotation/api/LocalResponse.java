package rocks.athrow.android_stock_rotation.api;

/**
 * LocalResponse
 * Created by joselopez on 1/9/17.
 */

public final class LocalResponse {
    private String responseText;
    private int responseCode;

    public LocalResponse() {
    }

    /**
     * setResponseCode
     *
     * @param responseCode the API's response code number
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * setResponseText
     *
     * @param responseText the API's response text
     */
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    /**
     * getResponseCode
     *
     * @return the API's response code number
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * getResponseText
     *
     * @return the API's response text
     */
    public String getResponseText() {
        return responseText;
    }

}