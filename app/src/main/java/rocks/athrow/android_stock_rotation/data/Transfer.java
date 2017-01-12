package rocks.athrow.android_stock_rotation.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by joselopez on 1/12/17.
 */

public class Transfer extends RealmObject {
    @PrimaryKey
    private String id;
    private String itemId;
    int sku;
    private String itemDescription;
    private  String location;
    private int caseQty;
    private  int looseQty;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
