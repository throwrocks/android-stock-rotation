package rocks.athrow.android_stock_rotation.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.internal.Context;

/**
 * Location
 * Created by joselopez on 1/9/17.
 */

public class Location extends RealmObject {
    public final static String FIELD_SERIAL_NUMBER = "serialNumber";
    public final static String FIELD_LOCATION = "location";
    public final static String FIELD_BARCODE = "barcode";
    public final static String FIELD_TYPE = "type";
    public final static String FIELD_EDISON_QTY = "edisonCaseQty";
    @PrimaryKey
    private int serialNumber;
    private String location;
    private String barcode;
    private String type;
    private int fmCaseQty;
    private int edisonCaseQty;

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFmCaseQty() {
        return fmCaseQty;
    }

    public void setFmCaseQty(int casesQty) {
        this.fmCaseQty = casesQty;
    }

    public int getEdisonCaseQty() {
        return edisonCaseQty;
    }

    public void setEdisonCaseQty(int edisonCaseQty) {
        this.edisonCaseQty = edisonCaseQty;
    }
}
