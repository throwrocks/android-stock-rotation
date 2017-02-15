package rocks.athrow.android_stock_rotation.data;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Comparison
 * Created by jose on 2/11/17.
 */

public class Comparison {
    private ArrayList<LocationItem> fmResults;
    private JSONObject edisonResult;

    public Comparison() {
    }

    public ArrayList<LocationItem> getFmResults() {
        return fmResults;
    }

    public void setFmResults(ArrayList<LocationItem> fmResults) {
        this.fmResults = fmResults;
    }

    public JSONObject getEdisonResult() {
        return edisonResult;
    }

    public void setEdisonResult(JSONObject edisonResult) {
        this.edisonResult = edisonResult;
    }
}
