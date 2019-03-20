package rocks.athrow.android_stock_rotation.data;

import com.joselopezrosario.androidfm.FmRecord;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Comparison
 * Created by jose on 2/11/17.
 */

public class Comparison {
    private ArrayList<LocationItem> fmResults;
    private FmRecord edisonResult;

    public Comparison() {
    }

    public ArrayList<LocationItem> getFmResults() {
        return fmResults;
    }

    public void setFmResults(ArrayList<LocationItem> fmResults) {
        this.fmResults = fmResults;
    }

    public FmRecord getEdisonResult() {
        return edisonResult;
    }

    public void setEdisonResult(FmRecord edisonResult) {
        this.edisonResult = edisonResult;
    }
}
