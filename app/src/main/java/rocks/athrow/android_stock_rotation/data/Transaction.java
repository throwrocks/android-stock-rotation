package rocks.athrow.android_stock_rotation.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by joselopez on 1/9/17.
 */

public class Transaction extends RealmObject {
    public static final String ID = "id";
    public static final String TYPE1 = "type1";
    public static final String IS_COMPLETE = "isCompleted";
    public static final String IS_VALID = "isValidRecord";
    @PrimaryKey
    private String id;
    private Date date;
    private String itemId;
    private int sku;
    private String itemDescription;
    private String tagNumber;
    private String packSize;
    private int receivingId;
    private String receivedDate;
    private String expirationDate;
    private String type1; // Receiving, Move, Salvage, Picking
    private String type2; // In or out
    private String locationStart;
    private int qtyCases;
    private int qtyLoose;
    private Date dateCompleted;
    private Boolean isCompleted;
    private String locationEnd;
    private int employeeId;
    private String employeeName;
    private boolean isValidRecord;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getSkuString() {
        if (sku == 0) {
            return "No item selected";
        } else {
            return String.valueOf(sku);
        }
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public String getItemDescription() {
        if (itemDescription == null) {
            return "N/A";
        } else {
            return itemDescription;
        }
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

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getLocationStart() {
        if (locationStart == null) {
            return "";
        } else {
            return locationStart;
        }
    }

    public void setLocationStart(String locationStart) {
        this.locationStart = locationStart;
    }

    public int getQtyCases() {
        return qtyCases;
    }

    public String getQtyCasesString() {
        return String.valueOf(qtyCases);
    }

    public void setQtyCases(int qtyCases) {
        this.qtyCases = qtyCases;
    }

    public int getQtyLoose() {
        return qtyLoose;
    }

    public String getQtyLooseString() {
        return String.valueOf(qtyLoose);
    }

    public void setQtyLoose(int qtyLoose) {
        this.qtyLoose = qtyLoose;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getLocationEnd() {
        if (locationEnd == null) {
            return "N/A";
        } else {
            return locationEnd;
        }
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

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setLocationEnd(String locationEnd) {
        this.locationEnd = locationEnd;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public boolean isValidRecord() {
        if (getId() == null || getId().isEmpty()) {
            return false;
        }
        if (getItemId() == null || getItemId().isEmpty()) {
            return false;
        }
        if (getType1().equals("Moving") && (getLocationStart() == null || getLocationStart().isEmpty()) || (getQtyCases() == 0 )) {
            return false;
        }
        if (getType1().equals("Adjust") && (getLocationStart() == null || getLocationStart().isEmpty())) {
            return false;
        }
        return true;
    }

    public boolean getIsValidRecord(){
        return isValidRecord;
    }

    public void setIsValidRecord() {
        isValidRecord = isValidRecord();
    }
}
