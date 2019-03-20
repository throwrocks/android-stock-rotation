package rocks.athrow.android_stock_rotation.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import rocks.athrow.android_stock_rotation.util.Utilities;

import static android.R.attr.y;
import static rocks.athrow.android_stock_rotation.data.Constants.IN;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_ADJUST;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_MOVING;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_RECEIVING;
import static rocks.athrow.android_stock_rotation.data.Constants.OUT;

/**
 * Transfer
 * Created by joselopez on 1/12/17.
 */

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Transfer extends RealmObject {
    private final static String DATE_TIME_DISPLAY = "MM/dd/yy h:mm:ss a";
    final static String RECORD_ID = "recordId";
    final static String MOD_ID = "modId";
    final static String FIELD_ID = "id";
    public final static String FIELD_SERIAL_NUMBER = "serialNumber";
    final static String FIELD_TRANSACTION_ID = "transactionId";
    final static String FIELD_TRANSACTION_TYPE = "transactionType";
    final static String FIELD_TYPE = "type";
    final static String FIELD_TYPE_KEY = "typeKey";
    final static String FIELD_DATE = "date";
    final static String FIELD_ITEM_ID = "itemId";
    final static String FIELD_SKU = "sku";
    final static String FIELD_ITEM_DESCRIPTION = "itemDescription";
    final static String FIELD_TAG_NUMBER = "tagNumber";
    final static String FIELD_RECEIVING_ID = "receivingId";
    final static String FIELD_RECEIVED_DATE = "receivedDate";
    final static String FIELD_EXPIRATION_DATE = "expirationDate";
    final static String FIELD_PACK_SIZE = "packSize";
    final static String FIELD_LOCATION = "location";
    final static String FIELD_CASE_QTY = "caseQty";
    final static String FIELD_INIT = "init";
    final static String FIELD_ITEM_LOCATION_KEY = "itemLocationKey";
    final static String FIELD_EMPLOYEE_NUMBER = "employeeNumber";
    final static String FIELD_EMPLOYEE_NAME = "employeeName";
    private int recordId;
    private int modId;
    @PrimaryKey
    private String id;
    private int serialNumber;
    private String transactionId;
    private String transactionType;
    private Date date;
    private String type; // In or out
    private int typeKey;
    @Index
    private String itemId;
    private int sku;
    private String itemDescription;
    @Index
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
    @Index
    private String itemLocationKey;
    private int employeeNumber;
    private String employeeName;

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getModId() {
        return modId;
    }

    public void setModId(int modId) {
        this.modId = modId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    void setTransactionType(String transactionType) {
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

    void setItemId(String itemId) {
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

    int getReceivingId() {
        return receivingId;
    }

    void setReceivingId(int receivingId) {
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

    void setCaseQty(int caseQty) {
        this.caseQty = caseQty;
    }

    public boolean getInit() {
        return init;
    }

    public void setInit(@SuppressWarnings("SameParameterValue") boolean init) {
        this.init = init;
    }

    void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    String getJSON() {
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
                + ", \"employeeNumber\":\"" + employeeNumber + "\""
                + "}]}";
    }

    public String getItemLocationKey() {
        return itemLocationKey;
    }

    public void setItemLocationKey() {
        this.itemLocationKey = this.tagNumber + "-" + this.location;
    }

    public int getTypeKey() {
        return typeKey;
    }

    public void setTypeKey() {
        int typeKey = 0;
        if (this.getTransactionType().equals(MODULE_RECEIVING)) {
            typeKey = 1;
        } else if (this.getTransactionType().equals(MODULE_MOVING) && this.getType().equals(IN)) {
            typeKey = 2;
        } else if (this.getTransactionType().equals(MODULE_MOVING) && this.getType().equals(OUT)) {
            typeKey = 3;
        } else if (this.getTransactionType().equals(MODULE_ADJUST) && this.getType().equals(IN)) {
            typeKey = 5;
        } else if (this.getTransactionType().equals(MODULE_ADJUST) && this.getType().equals(OUT)) {
            typeKey = 6;
        }
        this.typeKey = typeKey;
    }
}
