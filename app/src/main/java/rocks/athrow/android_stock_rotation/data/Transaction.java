package rocks.athrow.android_stock_rotation.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by joselopez on 1/9/17.
 */

public class Transaction extends RealmObject {
    public static final String ID = "id";
    @PrimaryKey
    private String id;
    private Date date;
    private String itemId;
    private String type1; // Receiving, Move, Salvage, Picking
    private String type2; // In or out
    private String locationStart;
    private int qtyCases;
    private int qtyLoose;
    private Date dateCompleted;
    private String locationEnd;
    private int employeeId;
    private String employeeName;

    public String getLocationStart() {
        return locationStart;
    }

    public void setLocationStart(String locationStart) {
        this.locationStart = locationStart;
    }

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

    public int getQtyCases() {
        return qtyCases;
    }

    public void setQtyCases(int qtyCases) {
        this.qtyCases = qtyCases;
    }

    public int getQtyLoose() {
        return qtyLoose;
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

    public String getLocationEnd() {
        return locationEnd;
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
}
