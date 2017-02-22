package rocks.athrow.android_stock_rotation.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import rocks.athrow.android_stock_rotation.api.APIResponse;

import static rocks.athrow.android_stock_rotation.data.Location.FIELD_ROW;

/**
 * DataUtilities
 * Created by joselopez on 1/13/17.
 */

public final class RealmQueries {
    private RealmQueries() {
        throw new AssertionError("No Utilities instances for you!");
    }

    /**--------------------------------------------------------------------------------------------
     GENERAL
     ---------------------------------------------------------------------------------------------**/
    /**
     * deleteDatabase
     *
     * @param context required context object
     */
    public static void deleteDatabase(Context context) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Transfer> transfers = realm.where(Transfer.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                transfers.deleteAllFromRealm();
            }
        });
        final RealmResults<Item> items = realm.where(Item.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                items.deleteAllFromRealm();
            }
        });
        final RealmResults<Location> locations = realm.where(Location.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                locations.deleteAllFromRealm();
            }
        });
        final RealmResults<Transaction> transactions = realm.where(Transaction.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                transactions.deleteAllFromRealm();
            }
        });
        realm.close();
        Realm.compactRealm(realmConfig);
    }
    /**--------------------------------------------------------------------------------------------
     TRANSACTIONS
     ---------------------------------------------------------------------------------------------**/

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
     * deleteInvalidTransactions
     *
     * @param context required context object
     */
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
        realm.close();
        Realm.compactRealm(realmConfig);
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

    /**
     * commitTransaction
     *
     * @param context       required context object
     * @param transactionId the transaction's id
     * @return an APIResponse object
     */
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
            String currentLocation = transaction.getLocationStart();
            String newLocation = transaction.getLocationEnd();
            realm.copyToRealmOrUpdate(transaction);
            realm.commitTransaction();
            if (currentLocation != null) {
                updateLocationQty(context, currentLocation);
            }
            if (newLocation != null) {
                updateLocationQty(context, newLocation);
            }
            apiResponse.setResponseCode(200);
            realm.close();
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
            String tagNumber,
            String packSize,
            int receivingId,
            String receivedDate,
            String expirationDate,
            String caseQtyString,
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
        // itemId: If an ItemId was provided, set the item and its information
        if (itemId != null && !itemId.isEmpty()) {
            // If an item id is provided, we must have a sku, but we need to validate it
            // because it is passed as string
            if (skuString != null && !skuString.isEmpty() && !skuString.equals("No item selected")) {
                transaction.setItemId(itemId);
                int Sku = Integer.parseInt(skuString);
                transaction.setSku(Sku);
                transaction.setItemDescription(itemDescription);
                transaction.setTagNumber(tagNumber);
                transaction.setPackSize(packSize);
                transaction.setReceivingId(receivingId);
                transaction.setReceivedDate(receivedDate);
                transaction.setExpirationDate(expirationDate);
            }
        }
        int caseQty;
        if (caseQtyString != null && !caseQtyString.isEmpty()) {
            caseQty = Integer.parseInt(caseQtyString);
            transaction.setQtyCases(caseQty);
        }
        transaction.setLocationStart(currentLocation);
        transaction.setLocationEnd(newLocation);

        if (transaction.getIsCompleted() == null) {
            transaction.setIsCompleted(false);
        }
        boolean valid = true;
        if (transaction.getId() == null || transaction.getId().isEmpty()) {
            valid = false;
        } else if (transaction.getItemId() == null || transaction.getItemId().isEmpty()) {
            valid = false;
        } else if (transaction.getType1().equals("Moving") && ( transaction.getLocationStart() == null || transaction.getLocationStart().isEmpty() ) || (transaction.getQtyCases() == 0)) {
            valid = false;
        } else if (transaction.getType1().equals("Adjust") && (transaction.getLocationStart() == null || transaction.getLocationStart().isEmpty())) {
            valid = false;
        }
        transaction.setIsValid(valid);
        realm.copyToRealmOrUpdate(transaction);
        realm.commitTransaction();
        realm.close();
        apiResponse.setResponseCode(200);
        apiResponse.setResponseText("Record saved!");
        return apiResponse;
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
    /**--------------------------------------------------------------------------------------------
     TRANSFERS
     ---------------------------------------------------------------------------------------------**/
    /**
     * getTransactions
     * A method to get all transfer records
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
     * saveTransfer
     * A method to save a Transfer record
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
            String tagNumber,
            String packSize,
            int receivingId,
            String receivedDate,
            String expirationDate,
            String location,
            int caseQty
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
        transfer.setTagNumber(tagNumber);
        transfer.setPackSize(packSize);
        transfer.setReceivingId(receivingId);
        transfer.setReceivedDate(receivedDate);
        transfer.setExpirationDate(expirationDate);
        transfer.setLocation(location);
        transfer.setCaseQty(caseQty);
        transfer.setItemLocationKey();
        transfer.setTypeKey();
        realm.copyToRealmOrUpdate(transfer);
        realm.commitTransaction();
        realm.close();
        apiResponse.setResponseCode(200);
        apiResponse.setResponseText("Record saved!");
        return apiResponse;
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
        RealmResults<Transfer> inResults;
        RealmResults<Transfer> outResults;
        if (itemId != null) {
            inResults = realm.where(Transfer.class).
                    equalTo(Transfer.FIELD_LOCATION, location).
                    equalTo(Transfer.FIELD_TYPE, Constants.IN).
                    equalTo(Transfer.FIELD_ITEM_ID, itemId).findAll();
            outResults = realm.where(Transfer.class).
                    equalTo(Transfer.FIELD_LOCATION, location).
                    equalTo(Transfer.FIELD_TYPE, Constants.OUT).
                    equalTo(Transfer.FIELD_ITEM_ID, itemId).findAll();
        } else {
            inResults =
                    realm.where(Transfer.class).
                            equalTo(Transfer.FIELD_LOCATION, location).
                            equalTo(Transfer.FIELD_TYPE, Constants.IN).findAll();
            outResults =
                    realm.where(Transfer.class).
                            equalTo(Transfer.FIELD_LOCATION, location).
                            equalTo(Transfer.FIELD_TYPE, Constants.OUT).findAll();
        }
        Number inTransfers = inResults.sum(Transfer.FIELD_CASE_QTY);
        Number outTransfers = outResults.sum(Transfer.FIELD_CASE_QTY);
        return inTransfers.longValue() - outTransfers.longValue();
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

    /**--------------------------------------------------------------------------------------------
     ITEMS
     ---------------------------------------------------------------------------------------------**/

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
     * getSKUFromTag
     *
     * @param context   required context object
     * @param tagNumber the item's tag number
     * @return the item's sku number
     */
    public static int getSKUFromTag(Context context, String tagNumber) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Item> results = realm.where(Item.class).
                equalTo(Item.FIELD_TAG_NUMBER, tagNumber).findAll().
                distinct(Item.FIELD_SKU);
        realm.commitTransaction();
        if (results.size() > 0) {
            return results.get(0).getSKU();
        } else {
            return 0;
        }
    }
    /**--------------------------------------------------------------------------------------------
     LOCATIONS
     ---------------------------------------------------------------------------------------------**/

    /**
     * getLocations
     * A method to get all locations by type
     *
     * @param context a Context object
     * @param type    the locations type (freezer, cooler, paper, dry)
     * @return a RealmResults object
     */
    public static RealmResults<Location> getLocations(Context context, String type, String row, boolean isPrimary) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Location> realmResults;
        if (row != null && !isPrimary) {
            realmResults =
                    realm.where(Location.class)
                            .equalTo(Location.FIELD_TYPE, type)
                            .equalTo(FIELD_ROW, row)
                            .findAll();
        } else if (row != null ) {
            realmResults =
                    realm.where(Location.class)
                            .equalTo(Location.FIELD_TYPE, type)
                            .equalTo(FIELD_ROW, row)
                            .equalTo(Location.FIELD_IS_PRIMARY, true)
                            .findAll();
        } else if (isPrimary) {
            realmResults =
                    realm.where(Location.class)
                            .equalTo(Location.FIELD_TYPE, type)
                            .equalTo(Location.FIELD_IS_PRIMARY, true)
                            .findAll();
        } else{
            realmResults =
                    realm.where(Location.class)
                            .equalTo(Location.FIELD_TYPE, type)
                            .findAll();
        }

        realm.commitTransaction();
        return realmResults.sort(Location.FIELD_SERIAL_NUMBER);
    }

    /**
     * getLocationByBarcode
     * A method to get a Location object. Used with the barcode scanner.
     *
     * @param context a Context object
     * @param barcode the location's barcode
     * @return a Location object
     */
    public static RealmResults<Location> getLocationByBarcode(Context context, String barcode) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Location> realmResults =
                realm.where(Location.class).equalTo(Location.FIELD_BARCODE, barcode).findAll();
        realm.beginTransaction();
        realm.commitTransaction();
        return realmResults;
    }

    public static RealmResults<Location> getLocationByName(Context context, String locationName) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Location> realmResults =
                realm.where(Location.class).equalTo(Location.FIELD_LOCATION, locationName).findAll();
        realm.beginTransaction();
        realm.commitTransaction();
        return realmResults;
    }

    private static int updateLocationQty(Context context, String locationName) {
        int count = getCountCasesByLocation(context, locationName, null).intValue();
        RealmResults<Location> locations = getLocationByName(context, locationName);
        if (locations.size() > 0) {
            Location location = locations.get(0);
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
            Realm.setDefaultConfiguration(realmConfig);
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            location.setFmCaseQty(count);
            realm.copyToRealmOrUpdate(location);
            realm.commitTransaction();
        }
        return count;
    }

    public static ArrayList<LocationRows> getRows(Context context, String locationType) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Location> realmResults;
        if (locationType.equals("All")) {
            realmResults = realm.where(Location.class).findAll().distinct(FIELD_ROW);
        } else {
            realmResults = realm.where(Location.class).equalTo(Location.FIELD_TYPE, locationType).findAll().distinct(FIELD_ROW).sort(FIELD_ROW);
        }
        int countResults = realmResults.size();
        if (countResults > 0) {
            ArrayList<LocationRows> rows = new ArrayList<>();
            for (int i = 0; i < countResults; i++) {
                LocationRows locationRows = new LocationRows();
                locationRows.setRow(realmResults.get(i).getRow());
                rows.add(locationRows);
            }
            return rows;
        } else {
            return null;
        }
    }

    /**--------------------------------------------------------------------------------------------
     LOCATION ITEMS
     ---------------------------------------------------------------------------------------------**/

    /**
     * searchActivityQuery
     *
     * @param context        required context object
     * @param searchCriteria the search criteria, can be a sku number or a description
     * @return an ArrayList of LocationItem objects
     */
    public static ArrayList<LocationItem> searchActivityQuery(Context context, String searchCriteria) {
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return null;
        }
        String searchType;
        if (searchCriteria.matches("[0-9]+")) {
            searchType = "sku";
        } else {
            searchType = "desc";
            searchCriteria = searchCriteria.toUpperCase();
        }
        return getLocationItems(context, searchType, searchCriteria);
    }

    /**
     * getLocationItems
     *
     * @param context        required context object
     * @param searchType     the type of search (location, tagNumber , sku, desc (description))
     * @param searchCriteria the search criteria entered in the edit text view
     * @return an ArrayList of LocationItem objects
     */
    public static ArrayList<LocationItem> getLocationItems(Context context, String searchType, String searchCriteria) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Transfer> transfers = null;
        switch (searchType) {
            case "location":
                transfers = realm.where(Transfer.class).
                        equalTo(Transfer.FIELD_LOCATION, searchCriteria).
                        equalTo(Transfer.FIELD_TYPE, "in").findAll();
                transfers.distinct(Transfer.FIELD_ITEM_ID);
                transfers = transfers.sort(Transfer.FIELD_RECEIVING_ID);
                break;
            case "sku":
                int skuNumber = Integer.parseInt(searchCriteria);
                transfers = realm.where(Transfer.class).
                        equalTo(Transfer.FIELD_SKU, skuNumber).
                        equalTo(Transfer.FIELD_TYPE, "in").findAll();
                transfers.distinct(Transfer.FIELD_ITEM_LOCATION_KEY);
                transfers = transfers.sort(Transfer.FIELD_RECEIVING_ID);
                break;
            case "tagNumber":
                transfers = realm.where(Transfer.class).
                        equalTo(Transfer.FIELD_TAG_NUMBER, searchCriteria).
                        equalTo(Transfer.FIELD_TYPE, "in").findAll();
                transfers = transfers.sort(Transfer.FIELD_RECEIVING_ID);
                break;
            case "desc":
                transfers = realm.where(Transfer.class).
                        contains(Transfer.FIELD_ITEM_DESCRIPTION, searchCriteria).
                        equalTo(Transfer.FIELD_TYPE, "in").findAll();
                transfers.distinct(Transfer.FIELD_ITEM_LOCATION_KEY);
                transfers = transfers.sort(Transfer.FIELD_RECEIVING_ID);
                break;
            default:
        }
        realm.commitTransaction();
        return getLocationItemsFromTransfers(context, transfers);
    }

    /**
     * getLocationItemsFromTransfers
     *
     * @param context   required context object
     * @param transfers a transfer object
     * @return an ArrayList of LocationItem objects
     */
    private static ArrayList<LocationItem> getLocationItemsFromTransfers(Context context, RealmResults<Transfer> transfers) {
        if (transfers == null) {
            return null;
        }
        int size = transfers.size();
        if (size == 0) {
            return null;
        }
        int index = 0;
        ArrayList<LocationItem> results = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Transfer transfer = transfers.get(i);
            String itemId = transfer.getItemId();
            String itemLocation = transfer.getLocation();
            String casesQty = String.valueOf(getCountCasesByLocation(context, itemLocation, itemId));
            if (Integer.parseInt(casesQty) > 0) {
                String tagNumber = transfer.getTagNumber();
                String itemSku = String.valueOf(transfer.getSku());
                String description = transfer.getItemDescription();
                String packSize = transfer.getPackSize();
                int receivingId = transfer.getReceivingId();
                String receivedDate = transfer.getReceivedDate();
                String expirationDate = transfer.getExpirationDate();
                LocationItem locationItem = new LocationItem();
                locationItem.setItemId(itemId);
                locationItem.setSKU(itemSku);
                locationItem.setDescription(description);
                locationItem.setInventoryTag(tagNumber);
                locationItem.setPackSize(packSize);
                locationItem.setReceivingId(receivingId);
                locationItem.setReceivedDate(receivedDate);
                locationItem.setExpirationDate(expirationDate);
                locationItem.setLocation(itemLocation);
                locationItem.setCaseQty(casesQty);
                results.add(index, locationItem);
                index++;
            }
        }
        return results;
    }
}
