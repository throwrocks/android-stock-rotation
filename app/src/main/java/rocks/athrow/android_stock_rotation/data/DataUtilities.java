package rocks.athrow.android_stock_rotation.data;

import android.content.Context;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.api.APIResponse;

/**
 * DataUtilities
 * Created by joselopez on 1/13/17.
 */

public final class DataUtilities {
    private DataUtilities() {
        throw new AssertionError("No Utilities instances for you!");
    }

    /**
     * deleteTransaction
     * A method to delete an individual transaction by id
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
        return apiResponse;
    }

    public static void deleteInvalidTransactions(Context context) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Transaction> results =
                realm.where(Transaction.class).equalTo(Transaction.IS_VALID, false).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    /**
     * deleteRequests
     * Requests are used temporarily for the UpdateDBService to parse new API results and save
     * them to the database
     *
     * @param context a Context object
     */
    public static void deleteRequests(Context context) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Request> results =
                realm.where(Request.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    /**
     * saveTransaction
     * A method to save a Transaction record
     * The only required value is the transaction id
     *
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
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Transaction transaction = new Transaction();
        // transactionId: If not transaction id was provided, we can't save a record
        if (transactionId == null || transactionId.isEmpty()) {
            apiResponse.setResponseCode(0);
            apiResponse.setResponseText("Error: No transaction id was provided.");
            return apiResponse;
        } else {
            transaction.setId(transactionId);
        }
        // date: Set the date if it doesn't exist
        if (transaction.getDate() == null) {
            transaction.setDate(new Date());
        }
        // transactionType: Set the transaction type
        transaction.setType1(transactionType);
        transaction.setType2("");
        // itemId: If an ItemId was provided, set the item and its information
        if (itemId != null && !itemId.isEmpty()) {
            // If an item id is provided, we must have a sku, but we need to validate it
            // because it is passed as string
            if (skuString != null && !skuString.isEmpty() && !skuString.equals("No item selected")) {
                transaction.setItemId(itemId);
                int Sku = Integer.parseInt(skuString);
                transaction.setSku(Sku);
                transaction.setPackSize(packSize);
                transaction.setReceivedDate(receivedDate);
                transaction.setItemDescription(itemDescription);
            }
        }
        // caseQty / looseQty: If quantities were provided, save them
        int caseQty;
        if (caseQtyString != null && !caseQtyString.isEmpty()) {
            caseQty = Integer.parseInt(caseQtyString);
            transaction.setQtyCases(caseQty);
        }
        int looseQty;
        if (looseQtyString != null && !looseQtyString.isEmpty()) {
            looseQty = Integer.parseInt(looseQtyString);
            transaction.setQtyLoose(looseQty);
        }
        // currentLocation / newLocation: Set the locations
        transaction.setLocationStart(currentLocation);
        transaction.setLocationEnd(newLocation);
        // If we are committing the record, set the completed information
        if (transactionAction.equals("commit")) {
            Date completedDate = new Date();
            transaction.setIsCompleted(true);
            transaction.setDateCompleted(completedDate);
        } else {
            transaction.setIsCompleted(false);
        }
        transaction.setIsValidRecord();
        realm.copyToRealmOrUpdate(transaction);
        realm.commitTransaction();
        realm.close();
        apiResponse.setResponseCode(200);
        apiResponse.setResponseText("Record saved!");
        return apiResponse;
    }


    /**
     * saveTransfer
     * A method to save a Trasnfer record
     * The only required value is the transaction id
     *
     * @return an APIResponse object
     */
    public static APIResponse saveTransfer(
            Context context,
            String transactionId,
            String transactionType,
            String type,
            String itemId,
            int sku,
            String itemDescription,
            String packSize,
            String receivedDate,
            String location,
            int caseQty,
            int looseQty
           ) {
        APIResponse apiResponse = new APIResponse();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Transfer transfer = new Transfer();
        transfer.setId(UUID.randomUUID().toString());
        transfer.setTransactionId(transactionId);
        transfer.setTransactionType(transactionType);
        transfer.setDate(new Date());
        transfer.setType(type);
        transfer.setItemId(itemId);
        transfer.setSku(sku);
        transfer.setItemDescription(itemDescription);
        transfer.setPackSize(packSize);
        transfer.setReceivedDate(receivedDate);
        transfer.setLocation(location);
        transfer.setCaseQty(caseQty);
        transfer.setLooseQty(looseQty);
        realm.copyToRealmOrUpdate(transfer);
        realm.commitTransaction();
        realm.close();
        apiResponse.setResponseCode(200);
        apiResponse.setResponseText("Record saved!");
        return apiResponse;
    }

    /**
     * getTransaction
     * A method to get a transaction record by id
     *
     * @param context       a Context object
     * @param transactionId the transaction id to be returned
     * @return a Transaction object
     */
    public static Transaction getTransaction(Context context, String transactionId) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Transaction> realmResults = realm.where(Transaction.class).equalTo(Transaction.ID, transactionId).findAll();
        realm.commitTransaction();
        if (realmResults.size() > 0) {
            return realmResults.get(0);
        } else {
            return null;
        }
    }

    /**
     * getItem
     * A method to get an Item record by id
     *
     * @param context a Context object
     * @param itemId  the item's id
     * @return an Item object
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

    /**
     * getLocation
     * A method to get a Location object. Used with the barcode scanner.
     *
     * @param context a Context object
     * @param barcode the location's barcode
     * @return a Location object
     */
    public static RealmResults<Location> getLocation(Context context, String barcode) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Location> realmResults = realm.where(Location.class).equalTo(Location.FIELD_BARCODE, barcode).findAll();
        realm.beginTransaction();
        realm.commitTransaction();
        return realmResults;
    }

    /**
     * getCountPendingTransactions
     * This is used to populate the counts on the buttons on MainActivity
     *
     * @param context a Context object
     * @param type    the type of Transaction (receiving, moving, picking, salvage)
     * @return the number of pending transactions
     */
    public static int getCountPendingTransactions(Context context, String type) {
        return getPendingTransactions(context, type).size();
    }

    /**
     * getPendingTransactions
     * This is used to populate the RecyclerView on the StockRotation Activity
     *
     * @param context a Context object
     * @param type    the type of Transaction
     * @return a RealResults object with the pending transactions
     */
    public static RealmResults<Transaction> getPendingTransactions(Context context, String type) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Transaction> realmResults =
                realm.where(Transaction.class).
                        equalTo(Transaction.TYPE1, type).
                        equalTo(Transaction.IS_COMPLETE, false).findAll();
        realm.commitTransaction();
        return realmResults;
    }

}
