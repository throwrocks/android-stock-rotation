package rocks.athrow.android_stock_rotation.data;

/**
 * LocationItem
 * Created by jose on 1/16/17.
 */

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class LocationItem {

    public LocationItem() {
    }

    private String itemId;
    private String SKU;
    private String description;
    private String packSize;
    private String receivedDate;
    private String expirationDate;
    private String location;
    private String caseQty;
    private String inventoryTag;
    private int receivingId;
    private boolean inPrimary;

    public void setItemId(String itemid) {
        this.itemId = itemid;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCaseQty() {
        return caseQty;
    }

    public void setCaseQty(String caseQty) {
        this.caseQty = caseQty;
    }

    public String getInventoryTag() {
        return inventoryTag;
    }

    public void setInventoryTag(String inventoryTag) {
        this.inventoryTag = inventoryTag;
    }

    public void setReceivingId(int receivingId) {
        this.receivingId = receivingId;
    }

}
