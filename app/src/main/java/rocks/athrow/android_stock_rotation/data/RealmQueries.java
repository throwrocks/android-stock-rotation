package rocks.athrow.android_stock_rotation.data;

import android.content.Context;

import com.facebook.stetho.common.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import rocks.athrow.android_stock_rotation.api.APIResponse;

/**
 * DataUtilities
 * Created by joselopez on 1/13/17.
 */

public final class RealmQueries {
    private RealmQueries() {
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

    public static APIResponse commitTransaction(Context context, String transactionId) {
        APIResponse apiResponse = new APIResponse();
        Transaction transaction = getTransaction(context, transactionId);
        if (transaction != null) {
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
            Realm.setDefaultConfiguration(realmConfig);
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            transaction.setIsCompleted(true);
            transaction.setDateCompleted(new Date());
            realm.copyToRealmOrUpdate(transaction);
            realm.commitTransaction();
            realm.close();
            apiResponse.setResponseCode(200);

        }
        return apiResponse;
    }

    /**
     * saveTransaction
     * A method to save a Transaction record
     * The only required value is the transaction id
     *
     * @param transactionType move, in or out
     * @param transactionId   the transaction record id
     * @param skuString       the sku as a string
     * @param caseQtyString   the case qty as string
     *                        // @param looseQtyString  the loose qty as string
     * @param currentLocation the current location
     * @param newLocation     the new location
     * @return an APIResponse object
     */
    public static APIResponse saveTransaction(
            Context context,
            String transactionType,
            String transactionId,
            String itemId,
            String skuString,
            String itemDescription,
            String packSize,
            int receivingId,
            String receivedDate,
            String caseQtyString,
            //String looseQtyString,
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
                transaction.setReceivingId(receivingId);
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
        /*if (looseQtyString != null && !looseQtyString.isEmpty()) {
            looseQty = Integer.parseInt(looseQtyString);
            transaction.setQtyLoose(looseQty);
        }*/
        // currentLocation / newLocation: Set the locations
        transaction.setLocationStart(currentLocation);
        transaction.setLocationEnd(newLocation);
        // If we are committing the record, set the completed information
        if (transaction.getIsCompleted() == null) {
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
            int receivingId,
            String receivedDate,
            String location,
            int caseQty
            //int looseQty
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
        transfer.setReceivingId(receivingId);
        transfer.setReceivedDate(receivedDate);
        transfer.setLocation(location);
        transfer.setCaseQty(caseQty);
        //transfer.setLooseQty(looseQty);
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
     * getTransactions
     * A method to get a transaction record by id
     *
     * @param context a Context object
     * @return a Transaction object
     */
    public static RealmResults<Transfer> getTransfers(Context context) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Transfer> realmResults =
                realm.where(Transfer.class).findAll().sort(Transfer.FIELD_DATE, Sort.DESCENDING);
        realm.commitTransaction();
        return realmResults;
    }


    /**
     * getItem
     * A method to get an Item record by id
     *
     * @param context   a Context object
     * @param tagNumber the item's tag number
     * @return an Item object
     */
    public static RealmResults<Item> getItemByTagNumber(Context context, String tagNumber) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Item> realmResults = realm.where(Item.class).equalTo(Item.FIELD_TAG_NUMBER, tagNumber).findAll();
        realm.beginTransaction();
        realm.commitTransaction();
        return realmResults;
    }

    /**
     * getLocations
     * A method to get all locations by type
     *
     * @param context a Context object
     * @param type    the locations type (freezer, cooler, paper, dry)
     * @return a RealmResults object
     */
    public static RealmResults<Location> getLocations(Context context, String type) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Location> realmResults;
        if (type.equals("All")) {
            realmResults =
                    realm.where(Location.class)
                            .findAll();
        } else {
            realmResults =
                    realm.where(Location.class)
                            .equalTo(Location.FIELD_TYPE, type)
                            .findAll();
        }
        realm.commitTransaction();
        return realmResults;
    }

    /**
     * getLocations
     * A method to get locations by type and containing a location name
     * This is used for the find method in the LocationsListActivity
     *
     * @param context      a Context object
     * @param type         the locations type (freezer, cooler, paper, dry)
     * @param locationName the location's name
     * @return a RealmResults object
     */
    public static RealmResults<Location> getLocations(Context context, String type, String locationName) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Location> realmResults;
        if (type.equals("All")) {
            realmResults = realm.where(Location.class)
                    .contains(Location.FIELD_LOCATION, locationName.toUpperCase())
                    .findAll();
        } else {
            realmResults = realm.where(Location.class)
                    .equalTo(Location.FIELD_TYPE, type)
                    .contains(Location.FIELD_LOCATION, locationName.toUpperCase())
                    .findAll();
        }
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
        RealmResults<Location> realmResults =
                realm.where(Location.class).equalTo(Location.FIELD_BARCODE, barcode).findAll();
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
     * getCountTransfers
     * This is used to populate the transfer count on the button on MainActivity
     *
     * @param context a Context object
     * @return the number of pending transfer
     */
    public static int getCountPendingTransfers(Context context) {
        return getTransfers(context).size();
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

    /**
     * getCountCasesByLocation
     *
     * @param context  a Context object
     * @param location the location
     * @param itemId   an optional itemId
     * @return the total number of cases
     */
    public static Number getCountCasesByLocation(Context context, String location, String itemId) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Transfer> inResults;
        RealmResults<Transfer> outResults;
        if (itemId != null) {
            inResults = realm.where(Transfer.class).
                    equalTo(Transfer.FIELD_LOCATION, location).
                    equalTo(Transfer.FIELD_TYPE, "in").
                    equalTo(Transfer.FIELD_ITEM_ID, itemId).findAll();

            outResults = realm.where(Transfer.class).
                    equalTo(Transfer.FIELD_LOCATION, location).
                    equalTo(Transfer.FIELD_TYPE, "out").
                    equalTo(Transfer.FIELD_ITEM_ID, itemId).findAll();
        } else {
            inResults =
                    realm.where(Transfer.class).
                            equalTo(Transfer.FIELD_LOCATION, location).
                            equalTo(Transfer.FIELD_TYPE, "in").findAll();
            outResults =
                    realm.where(Transfer.class).
                            equalTo(Transfer.FIELD_LOCATION, location).
                            equalTo(Transfer.FIELD_TYPE, "out").findAll();
        }
        Number inTransfers = inResults.sum(Transfer.FIELD_CASE_QTY);
        Number outTransfers = outResults.sum(Transfer.FIELD_CASE_QTY);
        realm.commitTransaction();
        return inTransfers.longValue() - outTransfers.longValue();
    }

    /**
     * findItemsWithLocations
     *
     * @param context
     * @param searchCriteria
     * @return
     */
    public static ArrayList<LocationItem> findItemsWithLocations(Context context, String location, String searchCriteria) {
        if ((location == null || location.isEmpty()) && (searchCriteria == null || searchCriteria.isEmpty())) {
            return null;
        }
        if (location != null && !location.isEmpty() && searchCriteria != null && !searchCriteria.isEmpty()) {
            return null;
        }
        String searchType;
        if (location != null) {
            searchType = "location";
        } else if (searchCriteria.matches("[0-9]+")) {
            searchType = "sku";
        } else {
            searchCriteria = searchCriteria.toUpperCase();
            searchType = "desc";
        }
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Transfer> transfers = null;
        switch (searchType) {
            case "location":
                transfers = realm.where(Transfer.class).
                        equalTo(Transfer.FIELD_LOCATION, location).
                        equalTo(Transfer.FIELD_TYPE, "in").findAll();
                transfers.distinct(Transfer.FIELD_ITEM_ID);
                transfers = transfers.sort(Transfer.FIELD_RECEIVING_ID);
                break;
            case "sku":
                int skuNumber = Integer.parseInt(searchCriteria);
                transfers = realm.where(Transfer.class).
                        equalTo(Transfer.FIELD_SKU, skuNumber).
                        equalTo(Transfer.FIELD_TYPE, "in").findAll();
                transfers = transfers.sort(Transfer.FIELD_RECEIVING_ID);
                break;
            case "desc":
                transfers = realm.where(Transfer.class).
                        contains(Transfer.FIELD_ITEM_DESCRIPTION, searchCriteria).
                        equalTo(Transfer.FIELD_TYPE, "in").findAll();
                transfers = transfers.sort(Transfer.FIELD_RECEIVING_ID);
                break;
            default:
        }
        realm.commitTransaction();
        realm.close();
        return getLocationItemsFromTransfers(context, transfers);
    }

    private static ArrayList<LocationItem> getLocationItemsFromTransfers(Context context, RealmResults<Transfer> transfers) {
        if ( transfers == null ){
            return  null;
        }
        int size = transfers.size();
        if (size == 0) {
            return null;
        }
        ArrayList<LocationItem> results = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Transfer transfer = transfers.get(i);
            String itemId = transfer.getItemId();
            String itemLocation = transfer.getLocation();
            String tagNumber = transfer.getTagNumber();
            String casesQty = String.valueOf(getCountCasesByLocation(context, itemLocation, itemId));
            if (Integer.parseInt(casesQty) > 0) {
                String itemSku = String.valueOf(transfer.getSku());
                String description = transfer.getItemDescription();
                String packSize = transfer.getPackSize();
                int receivingId = transfer.getReceivingId();
                String receivedDate = transfer.getReceivedDate();
                LocationItem locationItem = new LocationItem();
                locationItem.setItemId(itemId);
                locationItem.setSKU(itemSku);
                locationItem.setDescription(description);
                locationItem.setInventoryTag(tagNumber);
                locationItem.setPackSize(packSize);
                locationItem.setReceivingId(receivingId);
                locationItem.setReceivedDate(receivedDate);
                locationItem.setLocation(itemLocation);
                locationItem.setCaseQty(casesQty);
                results.add(i, locationItem);
            }
        }
        return results;
    }

}
