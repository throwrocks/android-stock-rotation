package rocks.athrow.android_stock_rotation.data;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
/**
 * Item
 * Created by joselopez on 1/9/17.
 */

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class Item extends RealmObject {
    public static final String FIELD_ID = "id";
    public static final String FIELD_SERIAL_NUMBER = "serialNumber";
    public static final String FIELD_TAG_NUMBER = "tagNumber";
    public static final String FIELD_SKU = "SKU";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_PACK_SIZE = "packSize";
    public static final String FIELD_RECEIVING_ID = "receivingId";
    public static final String FIELD_RECEIVED_DATE = "receivedDate";
    public static final String FIELD_EXPIRATION_DATE ="expirationDate";
    public static final String FIELD_ITEM_TYPE = "itemType";
    public static final String FIELD_EDISON_QTY = "edisonCaseQty";
    public static final String FIELD_PRIMARY_LOCATION = "primaryLocation";
    @PrimaryKey
    private String id;
    private int serialNumber;
    private String tagNumber;
    @Index
    private int SKU;
    private String description;
    private String packSize;
    private int receivingId;
    private String receivedDate;
    private String expirationDate;
    private String itemType;
    private int fmCaseQty;
    private int edisonCaseQty;
    private String primaryLocation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }

    public int getSKU() {
        return SKU;
    }

    void setSKU(int SKU) {
        this.SKU = SKU;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
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

    void setReceivingId(int receivingId) {
        this.receivingId = receivingId;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    void setItemType(String itemType) {
        this.itemType = itemType;
    }

    void setEdisonCaseQty(int edisonCaseQty) {
        this.edisonCaseQty = edisonCaseQty;
    }

    void setPrimaryLocation(String primaryLocation) {
        this.primaryLocation = primaryLocation;
    }
}
