package ali.naseem.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ali.naseem.inventory.adapters.InventoryCursorAdapter;
import ali.naseem.inventory.db.InventoryHelper;

import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry.CONTENT_URI;
import static ali.naseem.inventory.db.InventoryContract.InventoryEntry._ID;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private InventoryHelper helper;
    private ListView listView;
    private InventoryCursorAdapter adapter;
    private TextView emptyView;
    public static final int INVENTORY_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new InventoryHelper(this);
        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);
        adapter = new InventoryCursorAdapter(this, null);
        listView.setEmptyView(emptyView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                Cursor cursor = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cursor = getContentResolver().query(CONTENT_URI, null, null, null);
                }
                if (cursor.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "List Is Already Empty", Toast.LENGTH_SHORT).show();
                } else {
                    int del = getContentResolver().delete(CONTENT_URI, null, null);
                    getLoaderManager().restartLoader(INVENTORY_LOADER, null, this);
                    Toast.makeText(getApplicationContext(), "DELETED :" + del, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_add:
                Intent intent = new Intent(this, EditorActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {_ID, COLUMN_PRODUCT_NAME, COLUMN_PRODUCT_PRICE, COLUMN_PRODUCT_QUANTITY, COLUMN_PRODUCT_SUPPLIER_NAME, COLUMN_PRODUCT_SUPPLIER_PHONE};
        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.swapCursor(Utils.readItem(this));
    }
}
