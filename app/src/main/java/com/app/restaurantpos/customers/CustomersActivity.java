package com.app.restaurantpos.customers;

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
import com.app.restaurantpos.adapter.CustomerAdapter;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.database.DatabaseOpenHelper;
import com.app.restaurantpos.utils.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CustomersActivity extends BaseActivity {


    ProgressDialog loading;
    private RecyclerView recyclerView;

    ImageView imgNoProduct;
    EditText etxtSearch;

    FloatingActionButton fabAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_customer);

        recyclerView = findViewById(R.id.recycler_view);
        imgNoProduct = findViewById(R.id.image_no_product);
        etxtSearch = findViewById(R.id.etxt_customer_search);
        fabAdd = findViewById(R.id.fab_add);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CustomersActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> customerData;
        customerData = databaseAccess.getCustomers();

        Log.d("data", "" + customerData.size());

        if (customerData.isEmpty()) {
            Toasty.info(this, R.string.no_customer_found, Toast.LENGTH_SHORT).show();
            imgNoProduct.setImageResource(R.drawable.not_found);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            CustomerAdapter customerAdapter = new CustomerAdapter(CustomersActivity.this, customerData);

            recyclerView.setAdapter(customerAdapter);


        }

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersActivity.this, AddCustomersActivity.class);
                startActivity(intent);
            }
        });


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Log.d("data", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CustomersActivity.this);
                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchCustomerList;

                searchCustomerList = databaseAccess.searchCustomers(s.toString());


                if (searchCustomerList.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.not_found);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    CustomerAdapter customerAdapter = new CustomerAdapter(CustomersActivity.this, searchCustomerList);

                    recyclerView.setAdapter(customerAdapter);


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
        getMenuInflater().inflate(R.menu.add_customer_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_export_customer) {


            folderChooser();

            return true;
        } else if (id == android.R.id.home) {
            this.finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void folderChooser() {
        new ChooserDialog(CustomersActivity.this)

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
        sqliteToExcel.exportSingleTable("customers", "customers.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(CustomersActivity.this);
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(CustomersActivity.this, R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();


                }, 5000);

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(CustomersActivity.this, R.string.data_export_fail, Toast.LENGTH_SHORT).show();

                Log.d("Error", e.toString());
            }
        });
    }


}
