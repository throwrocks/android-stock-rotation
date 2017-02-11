package rocks.athrow.android_stock_rotation.data;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Comparison
 * Created by jose on 2/11/17.
 */

public class Comparison {
    ArrayList<LocationItem> fmResults;
    JSONArray edisonResults;

    public Comparison() {
    }

    public ArrayList<LocationItem> getFmResults() {
        return fmResults;
    }

    public void setFmResults(ArrayList<LocationItem> fmResults) {
        this.fmResults = fmResults;
    }

    public JSONArray getEdisonResults() {
        return edisonResults;
    }

    public void setEdisonResults(JSONArray edisonResults) {
        this.edisonResults = edisonResults;
    }
}
