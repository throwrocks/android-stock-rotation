package rocks.athrow.android_stock_rotation.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Transfer
 * Created by joselopez on 1/12/17.
 */

public class Transfer extends RealmObject {
    public final static String FIELD_ID = "id";
    public final static String FIELD_SERIAL_NUMBER = "serialNumber";
    public final static String FIELD_TRANSACTION_ID = "transactionId";
    public final static String FIELD_TRANSACTION_TYPE = "transactionType";
    public final static String FIELD_TYPE = "type";
    public final static String FIELD_DATE = "date";
    public final static String FIELD_ITEM_ID = "itemId";
    public final static String FIELD_SKU = "sku";
    public final static String FIELD_ITEM_DESCRIPTION = "itemDescription";
    public final static String FIELD_RECEIVING_ID = "receivingId";
    public final static String FIELD_RECEIVED_DATE = "receivedDate";
    public final static String FIELD_PACK_SIZE = "packSize";
    public final static String FIELD_LOCATION = "location";
    public final static String FIELD_CASE_QTY = "caseQty";
    public final static String FIELD_LOOSE_QTY = "looseQty";
    @PrimaryKey
    private String id;
    private int serialNumber;
    private String transactionId;
    private String transactionType;
    private Date date;
    private String type; // In or out
    @Index
    private String itemId;
    private int sku;
    private String itemDescription;
    private String packSize;
    @Index
    private int receivingId;
    private String receivedDate;
    @Index
    private String location;
    private int caseQty;
    private int looseQty;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getSku() {
        return sku;
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public int getReceivingId() {
        return receivingId;
    }

    public void setReceivingId(int receivingId) {
        this.receivingId = receivingId;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCaseQty() {
        return caseQty;
    }

    public void setCaseQty(int caseQty) {
        this.caseQty = caseQty;
    }

    public int getLooseQty() {
        return looseQty;
    }

    public void setLooseQty(int looseQty) {
        this.looseQty = looseQty;
    }

}
