package rocks.athrow.android_stock_rotation.api;

/**
 * APIResponse
 * Created by joselopez on 1/9/17.
 */

public final class APIResponse {

    private String requestURI;
    private String responseText;
    private int responseCode;
    private String meta;

    public APIResponse() {
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
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

    public String getMeta() {
        return meta;
    }

    void setMeta(String meta) {
        this.meta = meta;
    }
}