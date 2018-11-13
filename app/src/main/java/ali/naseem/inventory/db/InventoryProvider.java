package ali.naseem.inventory.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.TABLE_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry._ID;

public class InventoryProvider extends ContentProvider {

    private InventoryHelper mDbHelper;
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    public static final int INVENTORY = 100;
    public static final int PATH_INVENTORY_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", PATH_INVENTORY_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY: {
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            }
            break;
            case PATH_INVENTORY_ID: {
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            }
            break;
            default:
                throw new IllegalArgumentException("Unknown URI Exception");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return CONTENT_LIST_TYPE;
            case PATH_INVENTORY_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }


    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertInventory(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case PATH_INVENTORY_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowUpdated;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowUpdated = updateInventory(uri, contentValues, selection, selectionArgs);
                break;
            case PATH_INVENTORY_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = updateInventory(uri, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product name is required");
            }
        }
        if (values.containsKey(COLUMN_PRODUCT_PRICE)) {
            String price = values.getAsString(COLUMN_PRODUCT_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Price is required");
            }
        }
        if (values.containsKey(COLUMN_PRODUCT_QUANTITY)) {
            String quantity = values.getAsString(COLUMN_PRODUCT_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Quantity is required");
            }
        }
        if (values.containsKey(COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplier = values.getAsString(COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Supplier name is required");
            }
        }
        if (values.containsKey(COLUMN_PRODUCT_SUPPLIER_PHONE)) {
            String number = values.getAsString(COLUMN_PRODUCT_SUPPLIER_PHONE);
            if (number == null) {
                throw new IllegalArgumentException("Phone number is required");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        return database.update(TABLE_NAME, values, selection, selectionArgs);
    }
}