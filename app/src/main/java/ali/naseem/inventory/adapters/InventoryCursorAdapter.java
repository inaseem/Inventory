package ali.naseem.inventory.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Locale;

import ali.naseem.inventory.EditorActivity;
import ali.naseem.inventory.R;
import ali.naseem.inventory.Utils;

import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.CONTENT_URI;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry._ID;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView name = view.findViewById(R.id.productName);
        TextView price = view.findViewById(R.id.productPrice);
        final TextView quantity = view.findViewById(R.id.quantity);
        Button sale = view.findViewById(R.id.sale);
        Button edit = view.findViewById(R.id.edit);
        final int idIndex = cursor.getColumnIndex(_ID);
        int nameIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
        int priceIndex = cursor.getColumnIndex(COLUMN_PRODUCT_PRICE);
        int quantityIndex = cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY);
        final int currentId = cursor.getInt(idIndex);
        String currentProductName = cursor.getString(nameIndex);
        int currentPrice = cursor.getInt(priceIndex);
        final int currentQuantity = cursor.getInt(quantityIndex);
        name.setText(String.format(Locale.getDefault(), "%s", currentProductName));
        price.setText(String.format(Locale.getDefault(), "%d", currentPrice));
        quantity.setText(String.format(Locale.getDefault(), "%d", currentQuantity));
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri currentUri = ContentUris.withAppendedId(CONTENT_URI, currentId);
                if (Utils.quantityUpdate(currentUri, context, currentQuantity) != 0) {
                    quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString()) - 1));
                }
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(CONTENT_URI, currentId);
                intent.setData(currentProductUri);
                context.startActivity(intent);
            }
        });
    }
}
