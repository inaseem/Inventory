package ali.naseem.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry._ID;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView name;
    private TextView price;
    private TextView quantity;
    private TextView supplierName;
    private TextView supplierPhone;
    private Button contactSupplier;
    private Button delete;
    private Uri mCurrentInventoryUri;
    private static final int EXISTING_INVENTORY_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        supplierName = findViewById(R.id.supplier_name);
        supplierPhone = findViewById(R.id.supplier_phone);
        contactSupplier = findViewById(R.id.contactSupplier);
        delete = findViewById(R.id.delete);
        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();
        getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {_ID, COLUMN_PRODUCT_NAME, COLUMN_PRODUCT_PRICE, COLUMN_PRODUCT_QUANTITY, COLUMN_PRODUCT_SUPPLIER_NAME, COLUMN_PRODUCT_SUPPLIER_PHONE};
        return new CursorLoader(this, mCurrentInventoryUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_SUPPLIER_PHONE);
            String productName = cursor.getString(nameColumnIndex);
            int productQuantity = cursor.getInt(quantityColumnIndex);
            int productPrice = cursor.getInt(priceColumnIndex);
            String supplier_name = cursor.getString(supplierColumnIndex);
            final String phone = cursor.getString(phoneColumnIndex);
            name.setText(productName);
            price.setText(String.valueOf(productPrice));
            quantity.setText(String.valueOf(productQuantity));
            supplierName.setText(supplier_name);
            supplierPhone.setText(phone);
            contactSupplier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);
                    if (rowsDeleted == 0) {
                        Toast.makeText(DetailActivity.this, "Delete Failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetailActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    getLoaderManager().restartLoader(EXISTING_INVENTORY_LOADER, null, DetailActivity.this);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        name.setText(null);
        price.setText(String.valueOf(0));
        quantity.setText(String.valueOf(0));
        supplierPhone.setText(null);
        supplierName.setText(null);
        contactSupplier.setOnClickListener(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
