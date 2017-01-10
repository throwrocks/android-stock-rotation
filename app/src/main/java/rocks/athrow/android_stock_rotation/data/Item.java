package rocks.athrow.android_stock_rotation.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Item
 * Created by joselopez on 1/9/17.
 */

public class Item extends RealmObject {
    public static final String FIELD_ID = "id";
    public static final String FIELD_SERIAL_NUMBER = "serialNumber";
    public static final String FIELD_TAG_NUMBER = "tagNumber";
    public static final String FIELD_SKU = "SKU";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_PACK_SIZE = "packSize";
    public static final String FIELD_RECEIVING_ID = "receivingId";
    public static final String FIELD_RECEIVED_DATE = "receivedDate";
    public static final String FIELD_ITEM_TYPE = "itemType";
    @PrimaryKey
    private String id;
    private int serialNumber;
    private int tagNumber;
    private int SKU;
    private String description;
    private String packSize;
    private int receivingId;
    private String receivedDate;
    private String itemType;

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

    public int getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(int tagNumber) {
        this.tagNumber = tagNumber;
    }

    public int getSKU() {
        return SKU;
    }

    public void setSKU(int SKU) {
        this.SKU = SKU;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description;
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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
