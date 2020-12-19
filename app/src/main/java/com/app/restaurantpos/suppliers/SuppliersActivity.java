package com.app.restaurantpos.suppliers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.app.restaurantpos.R;
import com.app.restaurantpos.adapter.SupplierAdapter;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.database.DatabaseOpenHelper;
import com.app.restaurantpos.utils.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SuppliersActivity extends BaseActivity {


    ProgressDialog loading;

    private RecyclerView recyclerView;

    ImageView imgNoProduct;
    EditText etxtSearch;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_suppliers);

        recyclerView = findViewById(R.id.cart_recyclerview);
        imgNoProduct = findViewById(R.id.image_no_product);
        etxtSearch = findViewById(R.id.etxt_supplier_search);
        fabAdd = findViewById(R.id.fab_add);


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuppliersActivity.this, AddSuppliersActivity.class);
                startActivity(intent);
            }
        });

        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(SuppliersActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> suppliersData;
        suppliersData = databaseAccess.getSuppliers();

        Log.d("data", "" + suppliersData.size());

        if (suppliersData.isEmpty()) {
            Toasty.info(this, R.string.no_suppliers_found, Toast.LENGTH_SHORT).show();
            imgNoProduct.setImageResource(R.drawable.not_found);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            SupplierAdapter supplierAdapter = new SupplierAdapter(SuppliersActivity.this, suppliersData);

            recyclerView.setAdapter(supplierAdapter);


        }


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("data", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(SuppliersActivity.this);
                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchSupplier;

                searchSupplier = databaseAccess.searchSuppliers(s.toString());


                if (searchSupplier.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.not_found);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    SupplierAdapter supplierAdapter = new SupplierAdapter(SuppliersActivity.this, searchSupplier);

                    recyclerView.setAdapter(supplierAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("data", s.toString());
            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.export_suppliers_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_export_supplier) {

            folderChooser();
        } else if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void folderChooser() {
        new ChooserDialog(SuppliersActivity.this)

                .displayPath(true)
                .withFilter(true, false)

                // to handle the result(s)
                .withChosenListener((path, pathFile) -> {
                    onExport(path);
                    Log.d("path", path);

                })
                .build()
                .show();
    }


    public void onExport(String path) {

        String directoryPath = path;
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Export SQLite DB as EXCEL FILE
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, directoryPath);
        sqliteToExcel.exportSingleTable("suppliers", "suppliers.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(SuppliersActivity.this);
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(SuppliersActivity.this, R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();


                }, 5000);  //time in mile seconds

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(SuppliersActivity.this, R.string.data_export_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
