package com.app.restaurantpos.report;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.app.restaurantpos.R;
import com.app.restaurantpos.adapter.ExpenseAdapter;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.database.DatabaseOpenHelper;
import com.app.restaurantpos.utils.BaseActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ExpenseReportActivity extends BaseActivity {


    ProgressDialog loading;
    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;

    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPrice;
    List<HashMap<String, String>> expenseList;
    double totalPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);

        txtNoProducts = findViewById(R.id.txt_no_products);
        txtTotalPrice = findViewById(R.id.txt_total_price);


        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_expense);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ExpenseReportActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);


        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ExpenseReportActivity.this);
        databaseAccess.open();


        //get data from local database

        expenseList = databaseAccess.getAllExpense();

        if (expenseList.isEmpty()) {

            Toasty.info(ExpenseReportActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();

            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.not_found);
            txtNoProducts.setVisibility(View.VISIBLE);
            txtTotalPrice.setVisibility(View.GONE);

        } else {
            expenseAdapter = new ExpenseAdapter(ExpenseReportActivity.this, expenseList);

            recyclerView.setAdapter(expenseAdapter);


        }

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        totalPrice = databaseAccess.getTotalExpense("all");
        txtTotalPrice.setText(getString(R.string.total_expense) + currency + totalPrice);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_sales_menu, menu);
        return true;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_all_sales:
                getReport("all");

                return true;

            case R.id.menu_daily:
                getReport("daily");

                return true;


            case R.id.menu_monthly:
                getReport("monthly");


                return true;

            case R.id.menu_yearly:
                getReport("yearly");


                return true;

            case R.id.menu_export_data:

                folderChooser();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getReport(String type) {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ExpenseReportActivity.this);
        databaseAccess.open();


        //get data from local database

        expenseList = databaseAccess.getExpenseReport(type);
        if (expenseList.isEmpty()) {


            Toasty.info(ExpenseReportActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();


            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.not_found);
            txtNoProducts.setVisibility(View.VISIBLE);
            txtTotalPrice.setVisibility(View.GONE);
        } else {
            expenseAdapter = new ExpenseAdapter(ExpenseReportActivity.this, expenseList);

            recyclerView.setAdapter(expenseAdapter);

            recyclerView.setVisibility(View.VISIBLE);
            imgNoProduct.setVisibility(View.GONE);
            txtNoProducts.setVisibility(View.GONE);
            txtTotalPrice.setVisibility(View.VISIBLE);


        }

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        totalPrice = databaseAccess.getTotalExpense(type);
        txtTotalPrice.setText(getString(R.string.total_expense) + currency + totalPrice);
    }


    public void folderChooser() {
        new ChooserDialog(ExpenseReportActivity.this)

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
        sqliteToExcel.exportSingleTable("expense", "expense.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(ExpenseReportActivity.this);
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(ExpenseReportActivity.this, R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();


                }, 5000);

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(ExpenseReportActivity.this, R.string.data_export_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }


}