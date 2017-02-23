package rocks.athrow.android_stock_rotation.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Location
 * Created by joselopez on 1/9/17.
 */

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Location extends RealmObject {
    public final static String FIELD_SERIAL_NUMBER = "serialNumber";
    public final static String FIELD_LOCATION = "location";
    public final static String FIELD_BARCODE = "barcode";
    public final static String FIELD_TYPE = "type";
    public final static String FIELD_EDISON_QTY = "edisonCaseQty";
    public final static String FIELD_IS_PRIMARY = "isPrimary";
    public final static String FIELD_ROW = "row";
    private int serialNumber;
    private String location;
    @PrimaryKey
    private String barcode;
    private String type;
    private int fmCaseQty;
    private boolean init;
    private Date initDate;
    @Index
    private boolean isPrimary;
    @Index
    private String row;

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public void setFmCaseQty(int fmCaseQty) {
        this.fmCaseQty = fmCaseQty;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }
}
