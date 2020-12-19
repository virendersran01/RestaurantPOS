package com.app.restaurantpos.orders;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.R;
import com.app.restaurantpos.adapter.OrderDetailsAdapter;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.pdf_report.BarCodeEncoder;
import com.app.restaurantpos.pdf_report.PDFTemplate;
import com.app.restaurantpos.utils.BaseActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OrderDetailsActivity extends BaseActivity {


    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPrice, txtTax, txtDiscount, txtTotalCost;
    String orderId, orderDate, orderTime, customerName, tax, discount, tableNo;
    double totalPrice, calculatedTotalPrice;
    SharedPreferences sp;
    Button btnPdfReceipt, btnKitchenReceipt;

    //how many headers or column you need, add here by using ,
    //headers and get clients para meter must be equal
    private String[] header = {"Description", "Price"};

    private String[] headerKitchen = {"Item", "Qty"};

    String longText, shortText, userName;

    private PDFTemplate templatePDF;
    String currency;
    Bitmap bm = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        txtTax = findViewById(R.id.txt_tax);
        txtDiscount = findViewById(R.id.txt_discount);
        txtTotalCost = findViewById(R.id.txt_total_cost);
        btnPdfReceipt = findViewById(R.id.btn_pdf_receipt);
        btnKitchenReceipt = findViewById(R.id.btn_kitchen_receipt);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        userName = sp.getString(Constant.SP_USER_NAME, "N/A");


        txtNoProducts = findViewById(R.id.txt_no_products);
        orderId = getIntent().getExtras().getString("order_id");
        orderDate = getIntent().getExtras().getString("order_date");
        orderTime = getIntent().getExtras().getString("order_time");
        customerName = getIntent().getExtras().getString("customer_name");
        tax = getIntent().getExtras().getString("tax");
        discount = getIntent().getExtras().getString("discount");
        tableNo = getIntent().getExtras().getString("table_no");

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.order_details);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);


        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(orderId);

        if (orderDetailsList.isEmpty()) {
            //if no data in local db, then load data from server
            Toasty.info(OrderDetailsActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
        } else {
            OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(OrderDetailsActivity.this, orderDetailsList);

            recyclerView.setAdapter(orderDetailsAdapter);


        }


        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();

        String shopName = shopData.get(0).get("shop_name");
        String shopContact = shopData.get(0).get("shop_contact");
        String shopEmail = shopData.get(0).get("shop_email");
        String shopAddress = shopData.get(0).get("shop_address");
        currency = shopData.get(0).get("shop_currency");


        txtTax.setText(getString(R.string.total_tax) + " : " + currency + tax);
        txtDiscount.setText(getString(R.string.discount) + " : " + currency + discount);


        databaseAccess.open();
        totalPrice = databaseAccess.totalOrderPrice(orderId);
        double getTax = Double.parseDouble(tax);
        double getDiscount = Double.parseDouble(discount);

        calculatedTotalPrice = totalPrice + getTax - getDiscount;
        txtTotalPrice.setText(getString(R.string.sub_total) + currency + totalPrice);
        txtTotalCost.setText(getString(R.string.total_price) + currency + calculatedTotalPrice);


        //for pdf report
        shortText = "Customer Name: Mr/Mrs. " + customerName;
        longText = "<<<<< Have a nice day :)  Visit again >>>>>";
        templatePDF = new PDFTemplate(getApplicationContext());


        BarCodeEncoder qrCodeEncoder = new BarCodeEncoder();
        try {
            bm = qrCodeEncoder.encodeAsBitmap(orderId, BarcodeFormat.CODE_128, 600, 300);
        } catch (WriterException e) {
            Log.d("Data", e.toString());
        }


        btnPdfReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                templatePDF.openDocument();
                templatePDF.addMetaData(Constant.ORDER_RECEIPT, Constant.ORDER_RECEIPT, "Restaurant POS");
                templatePDF.addTitle(shopName + "\nCustomer Receipt", shopAddress + "\n Email: " + shopEmail + "\nContact: " + shopContact + "\nInvoice ID:" + orderId, orderTime + " " + orderDate + "\nServed By: " + userName + "\nTable Number:" + tableNo);
                templatePDF.addParagraph(shortText);

                templatePDF.createTable(header, getPDFReceipt());
                templatePDF.addImage(bm);

                templatePDF.addRightParagraph(longText);

                templatePDF.closeDocument();
                templatePDF.viewPDF();


            }
        });


        btnKitchenReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                templatePDF.openDocument();
                templatePDF.addMetaData("Order Receipt", "Order Receipt", "Smart POS");

                templatePDF.addTitle(shopName + "\nKitchen Receipt", shopAddress + "\n Email: " + shopEmail + "\nContact: " + shopContact + "\nInvoice ID:" + orderId, orderTime + " " + orderDate + "\nServed By: " + userName + "\nTable Number:" + tableNo);

                templatePDF.addParagraph(shortText);

                templatePDF.createTable(headerKitchen, getKitchenReceipt());
                templatePDF.addImage(bm);

                templatePDF.closeDocument();
                templatePDF.viewPDF();


            }
        });
    }


    //for pdf
    private ArrayList<String[]> getPDFReceipt() {
        ArrayList<String[]> rows = new ArrayList<>();

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(orderId);
        String name, price, qty, weight;
        double costTotal;

        for (int i = 0; i < orderDetailsList.size(); i++) {
            name = orderDetailsList.get(i).get("product_name");
            price = orderDetailsList.get(i).get("product_price");
            qty = orderDetailsList.get(i).get("product_qty");
            weight = orderDetailsList.get(i).get("product_weight");

            costTotal = Integer.parseInt(qty) * Double.parseDouble(price);

            rows.add(new String[]{name + "\n" + weight + "\n" + "(" + qty + "x" + currency + price + ")", currency + costTotal});


        }
        rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{"Sub Total: ", currency + totalPrice});
        rows.add(new String[]{"Total Tax: ", currency + tax});
        rows.add(new String[]{"Discount: ", currency + discount});
        rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{"Total Price: ", currency + calculatedTotalPrice});

//        you can add more row above format
        return rows;
    }


    //for pdf
    private ArrayList<String[]> getKitchenReceipt() {
        ArrayList<String[]> rows = new ArrayList<>();

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(orderId);
        String name, qty, weight;


        for (int i = 0; i < orderDetailsList.size(); i++) {
            name = orderDetailsList.get(i).get("product_name");
            qty = orderDetailsList.get(i).get("product_qty");
            weight = orderDetailsList.get(i).get("product_weight");
            rows.add(new String[]{name + "\n" + weight, qty});

        }

//        you can add more row above format
        return rows;
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

