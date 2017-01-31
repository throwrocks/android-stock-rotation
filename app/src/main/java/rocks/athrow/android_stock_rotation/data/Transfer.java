package rocks.athrow.android_stock_rotation.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import rocks.athrow.android_stock_rotation.util.Utilities;

/**
 * Transfer
 * Created by joselopez on 1/12/17.
 */

public class Transfer extends RealmObject {
    private final static String DATE_TIME_DISPLAY = "MM/dd/yy h:mm:ss a";
    public final static String FIELD_ID = "id";
    public final static String FIELD_SERIAL_NUMBER = "serialNumber";
    public final static String FIELD_TRANSACTION_ID = "transactionId";
    public final static String FIELD_TRANSACTION_TYPE = "transactionType";
    public final static String FIELD_TYPE = "type";
    public final static String FIELD_DATE = "date";
    public final static String FIELD_ITEM_ID = "itemId";
    public final static String FIELD_SKU = "sku";
    public final static String FIELD_ITEM_DESCRIPTION = "itemDescription";
    public final static String FIELD_TAG_NUMBER = "tagNumber";
    public final static String FIELD_RECEIVING_ID = "receivingId";
    public final static String FIELD_RECEIVED_DATE = "receivedDate";
    public final static String FIELD_EXPIRATION_DATE = "expirationDate";
    public final static String FIELD_PACK_SIZE = "packSize";
    public final static String FIELD_LOCATION = "location";
    public final static String FIELD_CASE_QTY = "caseQty";
    public final static String FIELD_INIT = "init";
    public final static String FIELD_INIT_DATE = "initDate";
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
    private String tagNumber;
    private String packSize;
    @Index
    private int receivingId;
    private String receivedDate;
    private String expirationDate;
    @Index
    private String location;
    private int caseQty;
    @Index
    private boolean init;
    private Date initDate;

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

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
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


    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
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

    public boolean getInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public String getJSON() {
        return "{\"data\":[{"
                + "\"id\":\"" + id + "\""
                + ", \"transactionId\":\"" + transactionId + "\""
                + ", \"transactionType\":\"" + transactionType + "\""
                + ", \"date\":\"" + Utilities.getDateAsString(date, DATE_TIME_DISPLAY, null) + "\""
                + ", \"type\":\"" + type + "\""
                + ", \"itemId\":\"" + itemId + "\""
                + ", \"sku\":\"" + sku + "\""
                + ", \"itemDescription\":\"" + itemDescription + "\""
                + ", \"tagNumber\":\"" + tagNumber + "\""
                + ", \"packSize\":\"" + packSize + "\""
                + ", \"receivingId\":\"" + receivingId + "\""
                + ", \"receivedDate\":\"" + receivedDate + "\""
                + ", \"expirationDate\":\"" + expirationDate + "\""
                + ", \"location\":\"" + location + "\""
                + ", \"caseQty\":\"" + caseQty + "\""
                + "}]}";
    }
}
