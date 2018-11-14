package ali.naseem.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry._ID;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText name;
    private EditText price;
    private EditText quantity;
    private EditText supplierName;
    private EditText supplierPhone;
    private Button save;
    private Uri mCurrentInventoryUri;
    private static final int EXISTING_INVENTORY_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        supplierName = findViewById(R.id.supplier_name);
        supplierPhone = findViewById(R.id.supplier_phone);
        save = findViewById(R.id.save);
        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();
        if (mCurrentInventoryUri == null) {
            setTitle("New Item");
            invalidateOptionsMenu();
            quantity.setText("0");
            price.setText("0");
        } else {
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
            setTitle("Edit Item");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(name.getText()) & !TextUtils.isEmpty(price.getText()) & !TextUtils.isEmpty(quantity.getText()) & !TextUtils.isEmpty(supplierName.getText()) & !TextUtils.isEmpty(supplierPhone.getText())) {
                    if (mCurrentInventoryUri == null) {
                        Uri ID = Utils.insertProducts(getApplicationContext(), name.getText().toString().trim(), price.getText().toString().trim(), quantity.getText().toString().trim(), supplierName.getText().toString().trim(), supplierPhone.getText().toString().trim());
                        if (ID != null) {
                            finish();
                        }
                    } else {
                        int rowsAffected = Utils.updateProducts(mCurrentInventoryUri, getApplicationContext(), name.getText().toString().trim(), price.getText().toString().trim(), quantity.getText().toString().trim(), supplierName.getText().toString().trim(), supplierPhone.getText().toString().trim());
                        if (rowsAffected != 0) {
                            finish();
                        }

                    }
                } else {
                    Toast.makeText(EditorActivity.this, "Please Enter Correct Data Values", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            String phone = cursor.getString(phoneColumnIndex);
            name.setText(productName);
            price.setText(String.valueOf(productPrice));
            quantity.setText(String.valueOf(productQuantity));
            supplierName.setText(supplier_name);
            supplierPhone.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        price.setText(String.valueOf(0));
        quantity.setText(String.valueOf(0));
        name.setText(null);
        supplierPhone.setText(null);
        supplierName.setText(null);
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
