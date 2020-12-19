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
import com.app.restaurantpos.adapter.SalesReportAdapter;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.database.DatabaseOpenHelper;
import com.app.restaurantpos.utils.BaseActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SalesReportActivity extends BaseActivity {


    ProgressDialog loading;
    private RecyclerView recyclerView;
    private SalesReportAdapter orderDetailsAdapter;

    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPrice,txtTotalTax,txtTotalDiscount,txtNetSales;
    List<HashMap<String, String>> orderDetailsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);

        txtNoProducts = findViewById(R.id.txt_no_products);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        txtTotalTax=findViewById(R.id.txt_total_tax);
        txtTotalDiscount=findViewById(R.id.txt_total_discount);
        txtNetSales=findViewById(R.id.txt_net_sales);


        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_sales);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SalesReportActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);


        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(SalesReportActivity.this);
        databaseAccess.open();


        //get data from local database

        orderDetailsList = databaseAccess.getAllSalesItems();

        if (orderDetailsList.isEmpty()) {
            //if no data in local db, then load data from server
            Toasty.info(SalesReportActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();

            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.not_found);
            txtNoProducts.setVisibility(View.VISIBLE);
            txtTotalPrice.setVisibility(View.GONE);

        } else {
            orderDetailsAdapter = new SalesReportAdapter(SalesReportActivity.this, orderDetailsList);

            recyclerView.setAdapter(orderDetailsAdapter);


        }

        databaseAccess.open();
        String currency=databaseAccess.getCurrency();

        databaseAccess.open();
        double subTotal=databaseAccess.getTotalOrderPrice("all");
        txtTotalPrice.setText(getString(R.string.total_sales)+ currency + subTotal);

        databaseAccess.open();
        double getTax=databaseAccess.getTotalTax("all");
        txtTotalTax.setText(getString(R.string.total_tax)+"(+) : "+currency+getTax);


        databaseAccess.open();
        double getDiscount=databaseAccess.getTotalDiscount("all");
        txtTotalDiscount.setText(getString(R.string.total_discount)+"(-) : "+currency+getDiscount);

        double netSales=subTotal+getTax-getDiscount;
        txtNetSales.setText(getString(R.string.net_sales)+": "+currency+netSales);


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
                getSupportActionBar().setTitle(R.string.daily);

                return true;


            case R.id.menu_monthly:
                getReport("monthly");
                getSupportActionBar().setTitle(R.string.monthly);


                return true;

            case R.id.menu_yearly:
                getReport("yearly");
                getSupportActionBar().setTitle(R.string.yearly);


                return true;

            case R.id.menu_export_data:

                folderChooser();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getReport(String type) {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(SalesReportActivity.this);
        databaseAccess.open();

        Log.d("TYPE",type);


        orderDetailsList = databaseAccess.getSalesReport(type);
        if (orderDetailsList.isEmpty()) {
            //if no data in local db, then load data from server
            Toasty.info(SalesReportActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();


            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.not_found);
            txtNoProducts.setVisibility(View.VISIBLE);
            txtTotalPrice.setVisibility(View.GONE);
        } else {
            orderDetailsAdapter = new SalesReportAdapter(SalesReportActivity.this, orderDetailsList);

            recyclerView.setAdapter(orderDetailsAdapter);

            recyclerView.setVisibility(View.VISIBLE);
            imgNoProduct.setVisibility(View.GONE);
            txtNoProducts.setVisibility(View.GONE);
            txtTotalPrice.setVisibility(View.VISIBLE);


        }


        databaseAccess.open();
        String currency=databaseAccess.getCurrency();

        databaseAccess.open();
        double subTotal=databaseAccess.getTotalOrderPrice(type);
        txtTotalPrice.setText(getString(R.string.total_sales)+ currency + subTotal);

        databaseAccess.open();
        double getTax=databaseAccess.getTotalTax(type);
        txtTotalTax.setText(getString(R.string.total_tax)+"(+) : "+currency+getTax);


        databaseAccess.open();
        double getDiscount=databaseAccess.getTotalDiscount(type);
        txtTotalDiscount.setText(getString(R.string.total_discount)+"(-) : "+currency+getDiscount);

        double netSales=subTotal+getTax-getDiscount;
        txtNetSales.setText(getString(R.string.net_sales)+": "+currency+netSales);

    }






    public void folderChooser() {
        new ChooserDialog(SalesReportActivity.this)

                .displayPath(true)
                .withFilter(true, false)

                // to handle the result(s)
                .withChosenListener((path, pathFile) -> {
                    onExport(path);
                    Log.d("path",path);

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
        sqliteToExcel.exportSingleTable("order_details", "order_details.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(SalesReportActivity.this);
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(SalesReportActivity.this, R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();

                }, 5000);

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(SalesReportActivity.this, R.string.data_export_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

