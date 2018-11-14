package ali.naseem.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import ali.naseem.inventory.db.InventoryContract;
import ali.naseem.inventory.db.InventoryHelper;

import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.CONTENT_URI;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.TABLE_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry._ID;

public class Utils {
    private static final String LOG_TAG = Utils.class.getSimpleName();

    public static Uri insertProducts(Context mContext, String product, String price, String quantity, String supplier, String phone) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, Integer.parseInt(price));
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(quantity));
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplier);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, phone);
        Uri newRowID = mContext.getContentResolver().insert(CONTENT_URI, values);
        if (newRowID == null) {
            Toast.makeText(mContext, R.string.error_in_data_insertion_str, Toast.LENGTH_SHORT).show();
            return null;
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.data_insertion_sucessful_str), Toast.LENGTH_SHORT).show();
            return newRowID;
        }
    }

    public static int updateProducts(Uri currentUri, Context mContext, String product, String price, String quantity, String supplier, String phone) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, Integer.parseInt(price));
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(quantity));
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplier);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, phone);
        int rowsAffected = mContext.getContentResolver().update(currentUri, values, null, null);
        if (rowsAffected == 0) {
            Toast.makeText(mContext, mContext.getString(R.string.editor_update_item_failed), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(mContext, R.string.editor_update_item_successful, Toast.LENGTH_SHORT).show();
        return rowsAffected;

    }

    public static int quantityUpdate(Uri currentUri, Context context, int currentQuantity) {
        int rowsAffected = 0;
        if (currentQuantity > 0) {
            currentQuantity -= 1;
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCT_QUANTITY, currentQuantity);
            rowsAffected = context.getContentResolver().update(currentUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(context, context.getString(R.string.sale_update_item_failed), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, R.string.sale_update_item_successful, Toast.LENGTH_SHORT).show();
        } else {
            rowsAffected = 0;
            Toast.makeText(context, context.getString(R.string.editor_update_zero_item_failed), Toast.LENGTH_SHORT).show();
        }
        return rowsAffected;
    }

    public static Cursor readItem(Context context) {
        InventoryHelper dbHelper = new InventoryHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String projection[] = {_ID, COLUMN_PRODUCT_NAME, COLUMN_PRODUCT_PRICE, COLUMN_PRODUCT_QUANTITY, COLUMN_PRODUCT_SUPPLIER_NAME, COLUMN_PRODUCT_SUPPLIER_PHONE
        };
        return db.query(TABLE_NAME, projection, null, null, null, null, null);
    }

//    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
//        Cursor cursor = null;
//        final String column = "_data";
//        final String[] projection = {column};
//        try {
//            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                final int column_index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(column_index);
//            }
//        } finally {
//            if (cursor != null) cursor.close();
//        }
//        return null;
//    }

}
