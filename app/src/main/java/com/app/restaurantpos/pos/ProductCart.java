package com.app.restaurantpos.pos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.R;
import com.app.restaurantpos.adapter.CartAdapter;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.orders.OrdersActivity;
import com.app.restaurantpos.utils.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class ProductCart extends BaseActivity {


    CartAdapter productCartAdapter;
    ImageView imgNoProduct;
    Button btnSubmitOrder;
    TextView txtNoProduct, txtTotalPrice;
    LinearLayout linearLayout;
    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ProductCart.this);


    List<String> customerNames, orderTypeNames, paymentMethodNames;
    ArrayAdapter<String> customerAdapter, orderTypeAdapter, paymentMethodAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.product_cart);

        RecyclerView recyclerView = findViewById(R.id.cart_recyclerview);
        imgNoProduct = findViewById(R.id.image_no_product);
        btnSubmitOrder = findViewById(R.id.btn_submit_order);
        txtNoProduct = findViewById(R.id.txt_no_product);
        linearLayout = findViewById(R.id.linear_layout);
        txtTotalPrice = findViewById(R.id.txt_total_price);

        txtNoProduct.setVisibility(View.GONE);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);


        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> cartProductList;
        cartProductList = databaseAccess.getCartProduct();

        Log.d("CartSize", "" + cartProductList.size());

        if (cartProductList.isEmpty()) {

            imgNoProduct.setImageResource(R.drawable.empty_cart);
            imgNoProduct.setVisibility(View.VISIBLE);
            txtNoProduct.setVisibility(View.VISIBLE);
            btnSubmitOrder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            txtTotalPrice.setVisibility(View.GONE);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            productCartAdapter = new CartAdapter(ProductCart.this, cartProductList, txtTotalPrice, btnSubmitOrder, imgNoProduct, txtNoProduct);

            recyclerView.setAdapter(productCartAdapter);


        }


        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog();


            }
        });

    }


    public void proceedOrder(String type, String paymentMethod, String customerName, double tax, String discount, double price, String tableNo) {

        databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
        databaseAccess.open();

        int itemCount = databaseAccess.getCartItemCount();

        if (itemCount > 0) {

            databaseAccess.open();
            //get data from local database
            final List<HashMap<String, String>> lines;
            lines = databaseAccess.getCartProduct();

            if (lines.isEmpty()) {
                Toasty.error(ProductCart.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();
            } else {

                //get current timestamp
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                //H denote 24 hours and h denote 12 hour hour format
                String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()); //HH:mm:ss a

                //timestamp use for invoice id for unique
                Long tsLong = System.currentTimeMillis() / 1000;
                String timeStamp = tsLong.toString();
                Log.d("Time", timeStamp);

                final JSONObject obj = new JSONObject();
                try {


                    obj.put("order_date", currentDate);
                    obj.put("order_time", currentTime);
                    obj.put("order_type", type);
                    obj.put("order_payment_method", paymentMethod);
                    obj.put("customer_name", customerName);

                    obj.put("tax", tax);
                    obj.put("discount", discount);

                    obj.put("price", price);

                    obj.put("table_no", tableNo);


                    JSONArray array = new JSONArray();


                    for (int i = 0; i < lines.size(); i++) {

                        databaseAccess.open();
                        String productId = lines.get(i).get("product_id");
                        String productName = databaseAccess.getProductName(productId);

                        databaseAccess.open();
                        String productWeightUnit = lines.get(i).get("product_weight_unit");
                        String weightUnit = databaseAccess.getWeightUnitName(productWeightUnit);


                        databaseAccess.open();
                        String productImage = databaseAccess.getProductImage(productId);

                        JSONObject objp = new JSONObject();
                        objp.put("product_name", productName);
                        objp.put("product_weight", lines.get(i).get("product_weight") + " " + weightUnit);
                        objp.put("product_qty", lines.get(i).get("product_qty"));
                        objp.put("product_price", lines.get(i).get("product_price"));
                        objp.put("product_image", productImage);
                        objp.put("product_order_date", currentDate);

                        array.put(objp);

                    }
                    obj.put("lines", array);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                saveOrderInOfflineDb(obj);


            }

        } else {
            Toasty.error(ProductCart.this, R.string.no_product_in_cart, Toast.LENGTH_SHORT).show();
        }
    }


    //for save data in offline
    private void saveOrderInOfflineDb(final JSONObject obj) {

        //get current timestamp
        Long tsLong = System.currentTimeMillis() / 1000;
        String timeStamp = tsLong.toString();

        databaseAccess = DatabaseAccess.getInstance(ProductCart.this);

        databaseAccess.open();
        /*
        timestamp used for un sync order and make it unique id
         */
        databaseAccess.insertOrder(timeStamp, obj);


        dialogSuccess();


    }


    public void dialogSuccess() {


        AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        ImageButton dialogBtnCloseDialog = dialogView.findViewById(R.id.btn_close_dialog);

        AlertDialog alertDialogSuccess = dialog.create();

        dialogBtnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogSuccess.dismiss();

                Intent intent = new Intent(ProductCart.this, OrdersActivity.class);
                startActivity(intent);
                finish();

            }
        });

        alertDialogSuccess.show();


    }


    //dialog for taking otp code
    public void dialog() {


        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();
        String shopCurrency = shopData.get(0).get("shop_currency");
        String tax = shopData.get(0).get("tax");

        double getTax = Double.parseDouble(tax);

        AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        final Button dialogBtnSubmit = dialogView.findViewById(R.id.btn_submit);
        final ImageButton dialogBtnClose = dialogView.findViewById(R.id.btn_close);
        final TextView dialogOrderPaymentMethod = dialogView.findViewById(R.id.dialog_order_status);
        final TextView dialogOrderType = dialogView.findViewById(R.id.dialog_order_type);
        final TextView dialogCustomer = dialogView.findViewById(R.id.dialog_customer);
        final TextView dialogTxtTotal = dialogView.findViewById(R.id.dialog_txt_total);
        final TextView dialogTxtTotalTax = dialogView.findViewById(R.id.dialog_txt_total_tax);
        final TextView dialogTxtLevelTax = dialogView.findViewById(R.id.dialog_level_tax);
        final TextView dialogTxtTotalCost = dialogView.findViewById(R.id.dialog_txt_total_cost);
        final EditText dialogEtxtDiscount = dialogView.findViewById(R.id.etxt_dialog_discount);
        final EditText dialogEtxtDialogTableNo = dialogView.findViewById(R.id.etxt_dialog_table_no);


        final ImageButton dialogImgCustomer = dialogView.findViewById(R.id.img_select_customer);
        final ImageButton dialogImgOrderPaymentMethod = dialogView.findViewById(R.id.img_order_payment_method);
        final ImageButton dialogImgOrderType = dialogView.findViewById(R.id.img_order_type);


        dialogTxtLevelTax.setText(getString(R.string.total_tax) + "( " + tax + "%) : ");
        double totalCost = CartAdapter.totalPrice;
        dialogTxtTotal.setText(shopCurrency + totalCost);

        double calculatedTax = (totalCost * getTax) / 100.0;
        dialogTxtTotalTax.setText(shopCurrency + calculatedTax);


        double discount = 0;
        double calculatedTotalCost = totalCost + calculatedTax - discount;
        dialogTxtTotalCost.setText(shopCurrency + calculatedTotalCost);


        dialogEtxtDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("data", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                double discount = 0;
                String getDiscount = s.toString();
                if (!getDiscount.isEmpty()) {
                    double calculatedTotalCost = totalCost + calculatedTax;
                    discount = Double.parseDouble(getDiscount);
                    if (discount > calculatedTotalCost) {
                        dialogEtxtDiscount.setError(getString(R.string.discount_cant_be_greater_than_total_price));
                        dialogEtxtDiscount.requestFocus();

                        dialogBtnSubmit.setVisibility(View.INVISIBLE);

                    } else {

                        dialogBtnSubmit.setVisibility(View.VISIBLE);
                        calculatedTotalCost = totalCost + calculatedTax - discount;
                        dialogTxtTotalCost.setText(shopCurrency + calculatedTotalCost);
                    }
                } else {

                    double calculatedTotalCost = totalCost + calculatedTax - discount;
                    dialogTxtTotalCost.setText(shopCurrency + calculatedTotalCost);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("data", s.toString());
            }
        });


        customerNames = new ArrayList<>();


        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> customer;
        customer = databaseAccess.getCustomers();

        for (int i = 0; i < customer.size(); i++) {

            // Get the ID of selected Country
            customerNames.add(customer.get(i).get("customer_name"));

        }


        orderTypeNames = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> orderType;
        orderType = databaseAccess.getOrderType();

        for (int i = 0; i < orderType.size(); i++) {

            // Get the ID of selected Country
            orderTypeNames.add(orderType.get(i).get("order_type_name"));

        }


        //payment methods
        paymentMethodNames = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> paymentMethod;
        paymentMethod = databaseAccess.getPaymentMethod();

        for (int i = 0; i < paymentMethod.size(); i++) {

            // Get the ID of selected Country
            paymentMethodNames.add(paymentMethod.get(i).get("payment_method_name"));

        }


        dialogImgOrderPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paymentMethodAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
                paymentMethodAdapter.addAll(paymentMethodNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);


                dialogTitle.setText(R.string.select_payment_method);
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(paymentMethodAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        paymentMethodAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        String selectedItem = paymentMethodAdapter.getItem(position);
                        dialogOrderPaymentMethod.setText(selectedItem);


                    }
                });
            }


        });


        dialogImgOrderType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                orderTypeAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
                orderTypeAdapter.addAll(orderTypeNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);


                dialogTitle.setText(R.string.select_order_type);
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(orderTypeAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        orderTypeAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        String selectedItem = orderTypeAdapter.getItem(position);


                        dialogOrderType.setText(selectedItem);


                    }
                });
            }


        });


        dialogImgCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
                customerAdapter.addAll(customerNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);

                dialogTitle.setText(R.string.select_customer);
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(customerAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        customerAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        String selectedItem = customerAdapter.getItem(position);


                        dialogCustomer.setText(selectedItem);


                    }
                });
            }
        });


        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();


        dialogBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String orderType = dialogOrderType.getText().toString().trim();
                String orderPaymentMethod = dialogOrderPaymentMethod.getText().toString().trim();
                String customerName = dialogCustomer.getText().toString().trim();
                String discount = dialogEtxtDiscount.getText().toString().trim();
                if (discount.isEmpty()) {
                    discount = "0.00";
                }
                String tableNo = dialogEtxtDialogTableNo.getText().toString().trim();

                if (tableNo.isEmpty()) {
                    tableNo = "N/A";
                }

                proceedOrder(orderType, orderPaymentMethod, customerName, calculatedTax, discount, calculatedTotalCost, tableNo);


                alertDialog.dismiss();
            }


        });


        dialogBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });


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

