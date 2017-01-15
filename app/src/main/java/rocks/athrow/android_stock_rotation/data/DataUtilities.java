package rocks.athrow.android_stock_rotation.data;

import android.content.Context;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.api.APIResponse;

/**
 * Created by joselopez on 1/13/17.
 */

public final class DataUtilities {
    private DataUtilities() {
        throw new AssertionError("No Utilities instances for you!");
    }

    /**
     * deleteTransaction
     *
     * @param context       a context object
     * @param transactionId the transaction id to delete
     * @return an APIResponse object
     */
    public static APIResponse deleteTransaction(Context context, String transactionId) {
        final APIResponse apiResponse = new APIResponse();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Transaction> results =
                realm.where(Transaction.class).equalTo(Transaction.ID, transactionId).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
                if (results.size() == 0) {
                    apiResponse.setResponseCode(200);
                    apiResponse.setResponseText("Record deleted");
                } else {
                    apiResponse.setResponseCode(0);
                    apiResponse.setResponseText("Could not delete record");
                }
            }
        });
        return new APIResponse();
    }

    /**
     * @param transactionType   move, in or out
     * @param transactionAction save or commit
     * @param transactionId     the transaction record id
     * @param skuString         the sku as a string
     * @param caseQtyString     the case qty as string
     * @param looseQtyString    the loose qty as string
     * @param currentLocation   the current location
     * @param newLocation       the new location
     * @return an APIResponse object
     */
    public static APIResponse saveTransaction(
            Context context,
            String transactionType,
            String transactionAction,
            String transactionId,
            String itemId,
            String skuString,
            String itemDescription,
            String packSize,
            String receivedDate,
            String caseQtyString,
            String looseQtyString,
            String currentLocation,
            String newLocation) {
        APIResponse apiResponse = new APIResponse();
        if (skuString.isEmpty() || skuString.equals("No item selected")) {
            apiResponse.setResponseCode(0);
            apiResponse.setResponseText("No item is selected.");
            return apiResponse;
        }
        if (caseQtyString.isEmpty() && looseQtyString.isEmpty()) {
            apiResponse.setResponseCode(0);
            apiResponse.setResponseText("Quantities are empty.");
            return apiResponse;
        }
        if (transactionType == "move" && transactionAction == "commit") {
            if (currentLocation.isEmpty() || currentLocation.equals("N/A")) {
                apiResponse.setResponseCode(0);
                apiResponse.setResponseText("The current location is empty");
                return apiResponse;
            }
        }
        if (transactionType == "move" && transactionAction == "commit") {
            if (newLocation.isEmpty() || currentLocation.equals("N/A")) {
                newLocation = "";
            }
        }

        int Sku = Integer.parseInt(skuString);
        int caseQty = 0;
        if (!caseQtyString.isEmpty()) {
            caseQty = Integer.parseInt(caseQtyString);
        }
        int looseQty = 0;
        if (!looseQtyString.isEmpty()) {
            looseQty = Integer.parseInt(looseQtyString);
        }
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setItemId(itemId);
        transaction.setSku(Sku);
        transaction.setPackSize(packSize);
        transaction.setReceivedDate(receivedDate);
        transaction.setItemDescription(itemDescription);
        transaction.setType1(transactionType);
        transaction.setType2("");
        transaction.setLocationStart(currentLocation);
        transaction.setLocationEnd(newLocation);
        transaction.setQtyCases(caseQty);
        transaction.setQtyLoose(looseQty);
        transaction.setIsCompleted(false);
        if ( transaction.getDate() == null){
            transaction.setDate(new Date());
        }
        realm.copyToRealmOrUpdate(transaction);
        realm.commitTransaction();
        realm.close();
        apiResponse.setResponseCode(200);
        apiResponse.setResponseText("Record saved!");
        return apiResponse;
    }

    public static RealmResults<Transaction> getTransaction(Context context, String transactionId) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Transaction> realmResults = realm.where(Transaction.class).equalTo(Transaction.ID, transactionId).findAll();
        realm.beginTransaction();
        realm.commitTransaction();
        return realmResults;
    }

    /**
     * getItem
     * @param itemId itemId
     * @return
     */
    public static RealmResults<Item> getItem(Context context, String itemId) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Item> realmResults = realm.where(Item.class).equalTo(Item.FIELD_TAG_NUMBER, itemId).findAll();
        realm.beginTransaction();
        realm.commitTransaction();
        return realmResults;
    }

    public static RealmResults<Location> getLocation(Context context, String barcode) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Location> realmResults = realm.where(Location.class).equalTo(Location.FIELD_BARCODE, barcode).findAll();
        realm.beginTransaction();
        realm.commitTransaction();
        return realmResults;
    }


}
