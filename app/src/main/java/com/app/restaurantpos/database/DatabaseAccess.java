package com.app.restaurantpos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.restaurantpos.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;


    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {


        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }


    //get shop information
    public ArrayList<HashMap<String, String>> checkUser(String phone, String password) {
        ArrayList<HashMap<String, String>> userInfo = new ArrayList<>();


        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE user_phone='" + phone + "' AND user_password='" + password + "'", null);


        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.USER_NAME, cursor.getString(cursor.getColumnIndex(Constant.USER_NAME)));
                map.put(Constant.USER_TYPE, cursor.getString(cursor.getColumnIndex(Constant.USER_TYPE)));


                userInfo.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return userInfo;
    }

    //insert customer
    public boolean addCustomer(String customerName, String customerCell, String customerEmail, String customerAddress) {

        ContentValues values = new ContentValues();


        values.put(Constant.CUSTOMER_NAME, customerName);
        values.put(Constant.CUSTOMER_CELL, customerCell);
        values.put(Constant.CUSTOMER_EMAIL, customerEmail);
        values.put(Constant.CUSTOMER_ADDRESS, customerAddress);

        long check = database.insert(Constant.customers, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert category
    public boolean addCategory(String categoryName) {

        ContentValues values = new ContentValues();


        values.put(Constant.CATEGORY_NAME, categoryName);


        long check = database.insert(Constant.productCategory, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert user
    public boolean addUser(String name, String phone, String password, String userType) {


        Cursor result = database.rawQuery("SELECT * FROM users WHERE user_phone='" + phone + "'", null);
        if (result.getCount() >= 1) {

            Log.d("data", "Already added");
            return false;

        } else {
            ContentValues values = new ContentValues();
            values.put(Constant.USER_NAME, name);
            values.put(Constant.USER_PHONE, phone);
            values.put(Constant.USER_PASSWORD, password);
            values.put(Constant.USER_TYPE, userType);


            long check = database.insert(Constant.users, null, values);

            result.close();
            database.close();

            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return false;
            } else {
                return true;
            }
        }
    }


    //update user
    public boolean updateUser(String id, String name, String phone, String password, String userType) {

        ContentValues values = new ContentValues();
        values.put(Constant.USER_NAME, name);
        values.put(Constant.USER_PHONE, phone);
        values.put(Constant.USER_PASSWORD, password);
        values.put(Constant.USER_TYPE, userType);


        long check = database.update(Constant.users, values, "id=?", new String[]{id});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }

    }


    //insert payment method
    public boolean addPaymentMethod(String paymentMethodName) {

        ContentValues values = new ContentValues();


        values.put(Constant.PAYMENT_METHOD_NAME, paymentMethodName);


        long check = database.insert(Constant.paymentMethod, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update category
    public boolean updateCategory(String categoryId, String categoryName) {

        ContentValues values = new ContentValues();


        values.put(Constant.CATEGORY_NAME, categoryName);


        long check = database.update(Constant.productCategory, values, "category_id=? ", new String[]{categoryId});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update payment method
    public boolean updatePaymentMethod(String paymentMethodId, String paymentMethodName) {

        ContentValues values = new ContentValues();


        values.put(Constant.PAYMENT_METHOD_NAME, paymentMethodName);


        long check = database.update(Constant.paymentMethod, values, "payment_method_id=? ", new String[]{paymentMethodId});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update customer
    public boolean updateCustomer(String customerId, String customerName, String customerCell, String customerEmail, String customerAddress) {

        ContentValues values = new ContentValues();


        values.put(Constant.CUSTOMER_NAME, customerName);
        values.put(Constant.CUSTOMER_CELL, customerCell);
        values.put(Constant.CUSTOMER_EMAIL, customerEmail);
        values.put(Constant.CUSTOMER_ADDRESS, customerAddress);

        long check = database.update(Constant.customers, values, " customer_id=? ", new String[]{customerId});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update shop information
    public boolean updateShopInformation(String shopName, String shopContact, String shopEmail, String shopAddress, String shopCurrency, String tax) {


        String shopId = "1";
        ContentValues values = new ContentValues();


        values.put("shop_name", shopName);
        values.put("shop_contact", shopContact);
        values.put("shop_email", shopEmail);
        values.put("shop_address", shopAddress);
        values.put("shop_currency", shopCurrency);
        values.put("tax", tax);

        long check = database.update("shop", values, "shop_id=? ", new String[]{shopId});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert products
    public boolean addProduct(String productName, String productCode, String productCategory, String productDescription, String productSellPrice, String productSupplier, String productImage, String weightUnitId, String productWeight) {

        ContentValues values = new ContentValues();


        values.put(Constant.PRODUCT_NAME, productName);
        values.put(Constant.PRODUCT_CODE, productCode);
        values.put(Constant.PRODUCT_CATEGORY, productCategory);
        values.put(Constant.PRODUCT_DESCRIPTION, productDescription);
        values.put(Constant.PRODUCT_SELL_PRICE, productSellPrice);

        values.put(Constant.PRODUCT_SUPPLIER, productSupplier);
        values.put(Constant.PRODUCT_IMAGE, productImage);
        values.put(Constant.PRODUCT_WEIGHT_UNIT_ID, weightUnitId);

        values.put(Constant.PRODUCT_WEIGHT, productWeight);


        long check = database.insert(Constant.products, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert products
    public boolean updateProduct(String productName, String productCode, String productCategory, String productDescription, String productSellPrice, String productSupplier, String productImage, String weightUnitId, String productWeight, String productId) {

        ContentValues values = new ContentValues();



        values.put(Constant.PRODUCT_NAME, productName);
        values.put(Constant.PRODUCT_CODE, productCode);
        values.put(Constant.PRODUCT_CATEGORY, productCategory);
        values.put(Constant.PRODUCT_DESCRIPTION, productDescription);
        values.put(Constant.PRODUCT_SELL_PRICE, productSellPrice);

        values.put(Constant.PRODUCT_SUPPLIER, productSupplier);
        values.put(Constant.PRODUCT_IMAGE, productImage);
        values.put(Constant.PRODUCT_WEIGHT_UNIT_ID, weightUnitId);

        values.put(Constant.PRODUCT_WEIGHT, productWeight);

        long check = database.update(Constant.products, values, "product_id=?", new String[]{productId});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert expense
    public boolean addExpense(String expenseName, String expenseAmount, String expenseNote, String date, String time) {

        ContentValues values = new ContentValues();


        values.put(Constant.EXPENSE_NAME, expenseName);
        values.put(Constant.EXPENSE_AMOUNT, expenseAmount);
        values.put(Constant.EXPENSE_NOTE, expenseNote);
        values.put(Constant.EXPENSE_DATE, date);
        values.put(Constant.EXPENSE_TIME, time);


        long check = database.insert(Constant.expense, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update expense
    public boolean updateExpense(String expenseId, String expenseName, String expenseAmount, String expenseNote, String date, String time) {

        ContentValues values = new ContentValues();

        values.put(Constant.EXPENSE_NAME, expenseName);
        values.put(Constant.EXPENSE_AMOUNT, expenseAmount);
        values.put(Constant.EXPENSE_NOTE, expenseNote);
        values.put(Constant.EXPENSE_DATE, date);
        values.put(Constant.EXPENSE_TIME, time);


        long check = database.update(Constant.expense, values, "expense_id=?", new String[]{expenseId});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert Suppliers
    public boolean addSuppliers(String suppliersName, String suppliersContactPerson, String suppliersCell, String suppliersEmail, String suppliersAddress) {

        ContentValues values = new ContentValues();


        values.put(Constant.SUPPLIERS_NAME, suppliersName);
        values.put(Constant.SUPPLIERS_CONTACT_PERSON, suppliersContactPerson);
        values.put(Constant.SUPPLIERS_CELL, suppliersCell);
        values.put(Constant.SUPPLIERS_EMAIL, suppliersEmail);
        values.put(Constant.SUPPLIERS_ADDRESS, suppliersAddress);

        long check = database.insert(Constant.suppliers, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update Suppliers
    public boolean updateSuppliers(String suppliersId, String suppliersName, String suppliersContactPerson, String suppliersCell, String suppliersEmail, String suppliersAddress) {

        ContentValues values = new ContentValues();


        values.put(Constant.SUPPLIERS_NAME, suppliersName);
        values.put(Constant.SUPPLIERS_CONTACT_PERSON, suppliersContactPerson);
        values.put(Constant.SUPPLIERS_CELL, suppliersCell);
        values.put(Constant.SUPPLIERS_EMAIL, suppliersEmail);
        values.put(Constant.SUPPLIERS_ADDRESS, suppliersAddress);

        long check = database.update(Constant.suppliers, values, "suppliers_id=?", new String[]{suppliersId});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //get product image base 64
    public String getProductImage(String productId) {

        String image = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_id='" + productId + "'", null);


        if (cursor.moveToFirst()) {
            do {


                image = cursor.getString(cursor.getColumnIndex(Constant.PRODUCT_IMAGE));


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return image;
    }


    //get product weight unit name
    public String getWeightUnitName(String weightUnitId) {

        String weightUnitName = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM product_weight WHERE weight_id=" + weightUnitId + "", null);


        if (cursor.moveToFirst()) {
            do {


                weightUnitName = cursor.getString(1);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return weightUnitName;
    }


    //get product weight unit name
    public String getSupplierName(String supplierId) {

        String supplierName = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers WHERE suppliers_id=" + supplierId + "", null);


        if (cursor.moveToFirst()) {
            do {


                supplierName = cursor.getString(1);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return supplierName;
    }


    //get product weight unit name
    public String getCategoryName(String categoryId) {

        String productCategory = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM product_category WHERE category_id=" + categoryId + "", null);


        if (cursor.moveToFirst()) {
            do {


                productCategory = cursor.getString(1);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return productCategory;
    }


    //Add product into cart
    public int addToCart(String productId, String weight, String weightUnit, String price, int qty) {


        Cursor result = database.rawQuery("SELECT * FROM product_cart WHERE product_id='" + productId + "'", null);
        if (result.getCount() >= 1) {

            return 2;

        } else {
            ContentValues values = new ContentValues();
            values.put(Constant.PRODUCT_ID, productId);
            values.put(Constant.PRODUCT_WEIGHT, weight);
            values.put(Constant.PRODUCT_WEIGHT_UNIT, weightUnit);
            values.put(Constant.PRODUCT_PRICE, price);
            values.put(Constant.PRODUCT_QTY, qty);

            long check = database.insert(Constant.productCart, null, values);


            result.close();
            database.close();


            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return -1;
            } else {
                return 1;
            }
        }

    }


    //get cart product
    public ArrayList<HashMap<String, String>> getCartProduct() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.CART_ID, cursor.getString(0));
                map.put(Constant.PRODUCT_ID, cursor.getString(1));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(2));
                map.put(Constant.PRODUCT_WEIGHT_UNIT, cursor.getString(3));
                map.put(Constant.PRODUCT_PRICE, cursor.getString(4));
                map.put(Constant.PRODUCT_QTY, cursor.getString(5));


                product.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return product;
    }


    //insert order in order list
    public void insertOrder(String orderId, JSONObject obj) {

        ContentValues values = new ContentValues();
        ContentValues values2 = new ContentValues();

        try {
            String orderDate = obj.getString(Constant.ORDER_DATE);
            String orderTime = obj.getString(Constant.ORDER_TIME);
            String orderType = obj.getString(Constant.ORDER_TYPE);
            String orderPaymentMethod = obj.getString(Constant.ORDER_PAYMENT_METHOD);
            String customerName = obj.getString(Constant.CUSTOMER_NAME);

            String tax = obj.getString(Constant.TAX);
            String discount = obj.getString(Constant.DISCOUNT);
            String price = obj.getString(Constant.PRICE);
            String tableNo = obj.getString(Constant.TABLE_NO);


            values.put(Constant.INVOICE_ID, orderId);
            values.put(Constant.ORDER_DATE, orderDate);
            values.put(Constant.ORDER_TIME, orderTime);
            values.put(Constant.ORDER_TYPE, orderType);
            values.put(Constant.ORDER_PAYMENT_METHOD, orderPaymentMethod);
            values.put(Constant.CUSTOMER_NAME, customerName);
            values.put(Constant.TAX, tax);
            values.put(Constant.DISCOUNT, discount);
            values.put(Constant.PRICE, price);
            values.put(Constant.TABLE_NO, tableNo);


            database.insert(Constant.orderList, null, values);

            database.delete(Constant.productCart, null, null);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {

            JSONArray result = obj.getJSONArray("lines");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String productName = jo.getString(Constant.PRODUCT_NAME);
                String productWeight = jo.getString(Constant.PRODUCT_WEIGHT);
                String productQty = jo.getString(Constant.PRODUCT_QTY);
                String productPrice = jo.getString(Constant.PRODUCT_PRICE);
                String productImage = jo.getString(Constant.PRODUCT_IMAGE);
                String productOrderDate = jo.getString(Constant.PRODUCT_ORDER_DATE);


                values2.put(Constant.INVOICE_ID, orderId);
                values2.put(Constant.PRODUCT_NAME, productName);
                values2.put(Constant.PRODUCT_WEIGHT, productWeight);
                values2.put(Constant.PRODUCT_QTY, productQty);
                values2.put(Constant.PRODUCT_PRICE, productPrice);
                values2.put(Constant.PRODUCT_IMAGE, productImage);
                values2.put(Constant.PRODUCT_ORDER_DATE, productOrderDate);


                database.insert(Constant.orderDetails, null, values2);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        database.close();
    }


    public ArrayList<HashMap<String, String>> getOrderList() {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_list ORDER BY order_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.INVOICE_ID, cursor.getString(1));
                map.put(Constant.ORDER_DATE, cursor.getString(2));
                map.put(Constant.ORDER_TIME, cursor.getString(3));
                map.put(Constant.ORDER_TYPE, cursor.getString(4));
                map.put(Constant.ORDER_PAYMENT_METHOD, cursor.getString(5));
                map.put(Constant.CUSTOMER_NAME, cursor.getString(6));
                map.put(Constant.TAX, cursor.getString(7));
                map.put(Constant.DISCOUNT, cursor.getString(8));

                map.put(Constant.TABLE_NO, cursor.getString(cursor.getColumnIndex(Constant.TABLE_NO)));


                orderList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return orderList;
    }


    public ArrayList<HashMap<String, String>> searchOrderList(String s) {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_list WHERE customer_name LIKE '%" + s + "%' OR invoice_id LIKE '%" + s + "%' ORDER BY order_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.INVOICE_ID, cursor.getString(1));
                map.put(Constant.ORDER_DATE, cursor.getString(2));
                map.put(Constant.ORDER_TIME, cursor.getString(3));
                map.put(Constant.ORDER_TYPE, cursor.getString(4));
                map.put(Constant.ORDER_PAYMENT_METHOD, cursor.getString(5));
                map.put(Constant.CUSTOMER_NAME, cursor.getString(6));
                map.put(Constant.TAX, cursor.getString(7));
                map.put(Constant.DISCOUNT, cursor.getString(8));

                map.put(Constant.TABLE_NO, cursor.getString(cursor.getColumnIndex(Constant.TABLE_NO)));


                orderList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return orderList;
    }


    //get order history data
    public ArrayList<HashMap<String, String>> getOrderDetailsList(String orderId) {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_details WHERE invoice_id='" + orderId + "' ORDER BY order_details_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.PRODUCT_NAME, cursor.getString(2));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(3));
                map.put(Constant.PRODUCT_QTY, cursor.getString(4));
                map.put(Constant.PRODUCT_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(6));
                map.put(Constant.PRODUCT_ORDER_DATE, cursor.getString(7));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return orderDetailsList;
    }


    //get order history data
    public ArrayList<HashMap<String, String>> getAllSalesItems() {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_details  ORDER BY order_details_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.PRODUCT_NAME, cursor.getString(2));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(3));
                map.put(Constant.PRODUCT_QTY, cursor.getString(4));
                map.put(Constant.PRODUCT_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(6));
                map.put(Constant.PRODUCT_ORDER_DATE, cursor.getString(7));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return orderDetailsList;
    }


    //get order history data
    public ArrayList<HashMap<String, String>> getSalesReport(String type) {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = null;
        if (type.equals("all")) {
            cursor = database.rawQuery("SELECT * FROM order_details  ORDER BY order_details_id DESC", null);
        } else if (type.equals(Constant.DAILY)) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM order_details WHERE   product_order_date='" + currentDate + "' ORDER BY order_Details_id DESC", null);

        } else if (type.equals(Constant.MONTHLY)) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM order_details WHERE strftime('%m', product_order_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals(Constant.YEARLY)) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            Log.d("YEAR", currentYear);
            String sql = "SELECT * FROM order_details WHERE strftime('%Y', product_order_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        }


        if (cursor!=null && cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put(Constant.PRODUCT_NAME, cursor.getString(2));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(3));
                map.put(Constant.PRODUCT_QTY, cursor.getString(4));
                map.put(Constant.PRODUCT_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(6));
                map.put(Constant.PRODUCT_ORDER_DATE, cursor.getString(7));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return orderDetailsList;
    }


    //get order history data
    public ArrayList<HashMap<String, String>> getExpenseReport(String type) {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = null;
        if (type.equals("all")) {
            cursor = database.rawQuery("SELECT * FROM expense  ORDER BY expense_id DESC", null);
        } else if (type.equals(Constant.DAILY)) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM expense WHERE   expense_date='" + currentDate + "' ORDER BY expense_id DESC", null);

        } else if (type.equals(Constant.MONTHLY)) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals(Constant.YEARLY)) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM expense WHERE strftime('%Y', expense_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        }


        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put(Constant.EXPENSE_ID, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_ID)));
                map.put(Constant.EXPENSE_NAME, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_NAME)));
                map.put(Constant.EXPENSE_NOTE, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_NOTE)));
                map.put(Constant.EXPENSE_AMOUNT, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_AMOUNT)));
                map.put(Constant.EXPENSE_DATE, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_DATE)));
                map.put(Constant.EXPENSE_TIME, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_TIME)));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return orderDetailsList;
    }


    //calculate total price in month
    public float getMonthlySalesAmount(String month, String getYear) {


        float totalPrice = 0;
        Cursor cursor = null;


        String year = getYear;


        String sql = "SELECT * FROM order_details WHERE strftime('%m', product_order_date) = '" + month + "' AND strftime('%Y', product_order_date) = '" + year + "'  ";

        cursor = database.rawQuery(sql, null);


        if (cursor.moveToFirst()) {
            do {

                float price = Float.parseFloat(cursor.getString(4));
                int qty = Integer.parseInt(cursor.getString(5));
                float subTotal = price * qty;
                totalPrice = totalPrice + subTotal;


            } while (cursor.moveToNext());
        } else {
            totalPrice = 0;
        }
        cursor.close();
        database.close();

        Log.d("total_price", "" + totalPrice);
        return totalPrice;
    }


    //calculate total price in month
    public float getMonthlyExpenseAmount(String month, String getYear) {


        float totalCost = 0;
        Cursor cursor = null;


        String year = getYear;


        String sql = "SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + month + "' AND strftime('%Y', expense_date) = '" + year + "'  ";

        cursor = database.rawQuery(sql, null);


        if (cursor.moveToFirst()) {
            do {

                float cost = Float.parseFloat(cursor.getString(3));

                totalCost = totalCost + cost;


            } while (cursor.moveToNext());
        } else {
            totalCost = 0;
        }
        cursor.close();
        database.close();

        Log.d("total_price", "" + totalCost);
        return totalCost;
    }


    //delete product from cart
    public boolean deleteProductFromCart(String id) {


        long check = database.delete(Constant.productCart, "cart_id=?", new String[]{id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //get cart item count
    public int getCartItemCount() {

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        int itemCount = cursor.getCount();


        cursor.close();
        database.close();
        return itemCount;
    }


    //delete product from cart
    public void updateProductQty(String id, String qty) {

        ContentValues values = new ContentValues();

        values.put("product_qty", qty);

        database.update("product_cart", values, "cart_id=?", new String[]{id});


    }


    //get product name
    public String getProductName(String productId) {

        String productName = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_id='" + productId + "'", null);


        if (cursor.moveToFirst()) {
            do {


                productName = cursor.getString(1);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return productName;
    }


    //get product name
    public String getCurrency() {

        String currency = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM shop", null);


        if (cursor.moveToFirst()) {
            do {


                currency = cursor.getString(5);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return currency;
    }


    //calculate total price of product
    public double getTotalPrice() {


        double totalPrice = 0;

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {

                double price = Double.parseDouble(cursor.getString(4));
                int qty = Integer.parseInt(cursor.getString(5));
                double subTotal = price * qty;
                totalPrice = totalPrice + subTotal;


            } while (cursor.moveToNext());
        } else {
            totalPrice = 0;
        }
        cursor.close();
        database.close();
        return totalPrice;
    }


    //calculate total discount of product
    public double getTotalDiscount(String type) {


        double totalDiscount = 0;
        Cursor cursor = null;


        if (type.equals(Constant.MONTHLY)) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            String sql = "SELECT * FROM order_list WHERE strftime('%m', order_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals(Constant.YEARLY)) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM order_list WHERE strftime('%Y', order_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals(Constant.DAILY)) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM order_list WHERE   order_date='" + currentDate + "' ORDER BY order_id DESC", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM order_list", null);

        }

        if (cursor.moveToFirst()) {
            do {

                double discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("discount")));
                totalDiscount = totalDiscount + discount;


            } while (cursor.moveToNext());
        } else {
            totalDiscount = 0;
        }
        cursor.close();
        database.close();
        return totalDiscount;
    }


    //calculate total tax of product
    public double getTotalTax(String type) {


        double totalTax = 0;
        Cursor cursor = null;


        if (type.equals(Constant.MONTHLY)) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            String sql = "SELECT * FROM order_list WHERE strftime('%m', order_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals(Constant.YEARLY)) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM order_list WHERE strftime('%Y', order_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals(Constant.DAILY)) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM order_list WHERE   order_date='" + currentDate + "' ORDER BY order_id DESC", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM order_list", null);

        }

        if (cursor.moveToFirst()) {
            do {

                double tax = Double.parseDouble(cursor.getString(cursor.getColumnIndex("tax")));
                totalTax = totalTax + tax;


            } while (cursor.moveToNext());
        } else {
            totalTax = 0;
        }
        cursor.close();
        database.close();
        return totalTax;
    }


    //calculate total price of product
    public double getTotalOrderPrice(String type) {


        double totalPrice = 0;
        Cursor cursor = null;


        if (type.equals("monthly")) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            String sql = "SELECT * FROM order_details WHERE strftime('%m', product_order_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("yearly")) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM order_details WHERE strftime('%Y', product_order_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("daily")) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM order_details WHERE   product_order_date='" + currentDate + "' ORDER BY order_Details_id DESC", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM order_details", null);

        }

        if (cursor.moveToFirst()) {
            do {

                double price = Double.parseDouble(cursor.getString(4));
                int qty = Integer.parseInt(cursor.getString(5));
                double subTotal = price * qty;
                totalPrice = totalPrice + subTotal;


            } while (cursor.moveToNext());
        } else {
            totalPrice = 0;
        }
        cursor.close();
        database.close();
        return totalPrice;
    }


    //calculate total price of product
    public double totalOrderPrice(String invoiceId) {


        double totalPrice = 0;


        Cursor cursor = database.rawQuery("SELECT * FROM order_details WHERE invoice_id='" + invoiceId + "'", null);


        if (cursor.moveToFirst()) {
            do {

                double price = Double.parseDouble(cursor.getString(4));
                int qty = Integer.parseInt(cursor.getString(5));
                double subTotal = price * qty;
                totalPrice = totalPrice + subTotal;


            } while (cursor.moveToNext());
        } else {
            totalPrice = 0;
        }
        cursor.close();
        database.close();
        return totalPrice;
    }


    //calculate total price of expense
    public double getTotalExpense(String type) {


        double totalCost = 0;
        Cursor cursor = null;


        if (type.equals("monthly")) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            String sql = "SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("yearly")) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM expense WHERE strftime('%Y', expense_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("daily")) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM expense WHERE   expense_date='" + currentDate + "' ORDER BY expense_id DESC", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM expense", null);

        }

        if (cursor.moveToFirst()) {
            do {

                double expense = Double.parseDouble(cursor.getString(3));

                totalCost = totalCost + expense;


            } while (cursor.moveToNext());
        } else {
            totalCost = 0;
        }
        cursor.close();
        database.close();
        return totalCost;
    }


    //get customer data
    public ArrayList<HashMap<String, String>> getCustomers() {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM customers ORDER BY customer_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("customer_id", cursor.getString(0));
                map.put("customer_name", cursor.getString(1));
                map.put("customer_cell", cursor.getString(2));
                map.put("customer_email", cursor.getString(3));
                map.put("customer_address", cursor.getString(4));


                customer.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return customer;
    }


    //get order type data
    public ArrayList<HashMap<String, String>> getOrderType() {
        ArrayList<HashMap<String, String>> orderType = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_type ORDER BY order_type_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("order_type_id", cursor.getString(0));
                map.put("order_type_name", cursor.getString(1));


                orderType.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return orderType;
    }


    //get order type data
    public ArrayList<HashMap<String, String>> getPaymentMethod() {
        ArrayList<HashMap<String, String>> paymentMethod = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM payment_method ORDER BY payment_method_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("payment_method_id", cursor.getString(0));
                map.put("payment_method_name", cursor.getString(1));


                paymentMethod.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return paymentMethod;
    }


    //get customer data
    public ArrayList<HashMap<String, String>> searchCustomers(String s) {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM customers WHERE customer_name LIKE '%" + s + "%' ORDER BY customer_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("customer_id", cursor.getString(0));
                map.put("customer_name", cursor.getString(1));
                map.put("customer_cell", cursor.getString(2));
                map.put("customer_email", cursor.getString(3));
                map.put("customer_address", cursor.getString(4));


                customer.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return customer;
    }


    //get customer data
    public ArrayList<HashMap<String, String>> searchSuppliers(String s) {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers WHERE suppliers_name LIKE '%" + s + "%' ORDER BY suppliers_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.SUPPLIERS_ID, cursor.getString(0));
                map.put(Constant.SUPPLIERS_NAME, cursor.getString(1));
                map.put(Constant.SUPPLIERS_CONTACT_PERSON, cursor.getString(2));
                map.put(Constant.SUPPLIERS_CELL, cursor.getString(3));
                map.put(Constant.SUPPLIERS_EMAIL, cursor.getString(4));
                map.put(Constant.SUPPLIERS_ADDRESS, cursor.getString(5));
                customer.add(map);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return customer;
    }


    //get shop information
    public ArrayList<HashMap<String, String>> getShopInformation() {
        ArrayList<HashMap<String, String>> shopInfo = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM shop", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("shop_name", cursor.getString(1));
                map.put("shop_contact", cursor.getString(2));
                map.put("shop_email", cursor.getString(3));
                map.put("shop_address", cursor.getString(4));
                map.put("shop_currency", cursor.getString(5));
                map.put("tax", cursor.getString(6));


                shopInfo.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return shopInfo;
    }


    //get product data
    public ArrayList<HashMap<String, String>> getProducts() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM products ORDER BY product_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.PRODUCT_ID, cursor.getString(0));
                map.put(Constant.PRODUCT_NAME, cursor.getString(1));
                map.put(Constant.PRODUCT_CODE, cursor.getString(2));
                map.put(Constant.PRODUCT_CATEGORY, cursor.getString(3));
                map.put(Constant.PRODUCT_DESCRIPTION, cursor.getString(4));
                map.put(Constant.PRODUCT_SELL_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_SUPPLIER, cursor.getString(6));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(7));
                map.put(Constant.PRODUCT_WEIGHT_UNIT_ID, cursor.getString(8));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(9));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }


    //get product data
    public ArrayList<HashMap<String, String>> getProductsInfo(String productId) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_id='" + productId + "'", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put(Constant.PRODUCT_ID, cursor.getString(0));
                map.put(Constant.PRODUCT_NAME, cursor.getString(1));
                map.put(Constant.PRODUCT_CODE, cursor.getString(2));
                map.put(Constant.PRODUCT_CATEGORY, cursor.getString(3));
                map.put(Constant.PRODUCT_DESCRIPTION, cursor.getString(4));
                map.put(Constant.PRODUCT_SELL_PRICE, cursor.getString(5));
                map.put(Constant.PRODUCT_SUPPLIER, cursor.getString(6));
                map.put(Constant.PRODUCT_IMAGE, cursor.getString(7));
                map.put(Constant.PRODUCT_WEIGHT_UNIT_ID, cursor.getString(8));
                map.put(Constant.PRODUCT_WEIGHT, cursor.getString(9));



                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }


    //get product data
    public ArrayList<HashMap<String, String>> getAllExpense() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM expense ORDER BY expense_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.EXPENSE_ID, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_ID)));
                map.put(Constant.EXPENSE_NAME, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_NAME)));
                map.put(Constant.EXPENSE_NOTE, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_NOTE)));
                map.put(Constant.EXPENSE_AMOUNT, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_AMOUNT)));
                map.put(Constant.EXPENSE_DATE, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_DATE)));
                map.put(Constant.EXPENSE_TIME, cursor.getString(cursor.getColumnIndex(Constant.EXPENSE_TIME)));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }


    //get product category data
    public ArrayList<HashMap<String, String>> getProductCategory() {
        ArrayList<HashMap<String, String>> productCategory = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM product_category ORDER BY category_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("category_id", cursor.getString(0));
                map.put("category_name", cursor.getString(1));

                productCategory.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return productCategory;
    }


    //get user data
    public ArrayList<HashMap<String, String>> getUsers() {
        ArrayList<HashMap<String, String>> users = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM users ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.ID, cursor.getString(cursor.getColumnIndex(Constant.ID)));
                map.put(Constant.USER_NAME, cursor.getString(cursor.getColumnIndex(Constant.USER_NAME)));
                map.put(Constant.USER_TYPE, cursor.getString(cursor.getColumnIndex(Constant.USER_TYPE)));
                map.put(Constant.USER_PHONE, cursor.getString(cursor.getColumnIndex(Constant.USER_PHONE)));
                map.put(Constant.USER_PASSWORD, cursor.getString(cursor.getColumnIndex(Constant.USER_PASSWORD)));

                users.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return users;
    }


    //get product category data
    public ArrayList<HashMap<String, String>> searchProductCategory(String s) {
        ArrayList<HashMap<String, String>> productCategory = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM product_category WHERE category_name LIKE '%" + s + "%' ORDER BY category_id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.CATEGORY_ID, cursor.getString(0));
                map.put(Constant.CATEGORY_NAME, cursor.getString(1));

                productCategory.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return productCategory;
    }


    //search user data
    public ArrayList<HashMap<String, String>> searchUser(String s) {
        ArrayList<HashMap<String, String>> userData = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM users WHERE user_name LIKE '%" + s + "%' ORDER BY id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put(Constant.ID, cursor.getString(cursor.getColumnIndex(Constant.ID)));
                map.put(Constant.USER_NAME, cursor.getString(cursor.getColumnIndex(Constant.USER_NAME)));
                map.put(Constant.USER_TYPE, cursor.getString(cursor.getColumnIndex(Constant.USER_TYPE)));
                map.put(Constant.USER_PHONE, cursor.getString(cursor.getColumnIndex(Constant.USER_PHONE)));
                map.put(Constant.USER_PASSWORD, cursor.getString(cursor.getColumnIndex(Constant.USER_PASSWORD)));
                userData.add(map);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return userData;
    }


    //get product payment method
    public ArrayList<HashMap<String, String>> searchPaymentMethod(String s) {
        ArrayList<HashMap<String, String>> paymentMethod = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM payment_method WHERE payment_method_name LIKE '%" + s + "%' ORDER BY payment_method_id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.PAYMENT_METHOD_ID, cursor.getString(0));
                map.put(Constant.PAYMENT_METHOD_NAME, cursor.getString(1));

                paymentMethod.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return paymentMethod;
    }


    //get product supplier data
    public ArrayList<HashMap<String, String>> getProductSupplier() {
        ArrayList<HashMap<String, String>> productSuppliers = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("suppliers_id", cursor.getString(0));
                map.put("suppliers_name", cursor.getString(1));

                productSuppliers.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return productSuppliers;
    }


    //get product supplier data
    public ArrayList<HashMap<String, String>> getWeightUnit() {
        ArrayList<HashMap<String, String>> productWeightUnit = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM product_weight", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put("weight_id", cursor.getString(0));
                map.put("weight_unit", cursor.getString(1));

                productWeightUnit.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return productWeightUnit;
    }

    //get product data
    public ArrayList<HashMap<String, String>> searchExpense(String s) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM expense WHERE expense_name LIKE '%" + s + "%' ORDER BY expense_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put("expense_id", cursor.getString(cursor.getColumnIndex("expense_id")));
                map.put("expense_name", cursor.getString(cursor.getColumnIndex("expense_name")));
                map.put("expense_note", cursor.getString(cursor.getColumnIndex("expense_note")));
                map.put("expense_amount", cursor.getString(cursor.getColumnIndex("expense_amount")));
                map.put("expense_date", cursor.getString(cursor.getColumnIndex("expense_date")));
                map.put("expense_time", cursor.getString(cursor.getColumnIndex("expense_time")));


                product.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return product;
    }


    //get product data
    public ArrayList<HashMap<String, String>> getSearchProducts(String s) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_name LIKE '%" + s + "%' OR product_code LIKE '%" + s + "%' ORDER BY product_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                map.put("product_id", cursor.getString(0));
                map.put("product_name", cursor.getString(1));
                map.put("product_code", cursor.getString(2));
                map.put("product_category", cursor.getString(3));
                map.put("product_description", cursor.getString(4));

                map.put("product_sell_price", cursor.getString(5));
                map.put("product_supplier", cursor.getString(6));
                map.put("product_image", cursor.getString(7));

                map.put("product_weight_unit_id", cursor.getString(8));
                map.put("product_weight", cursor.getString(9));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }


    //get suppliers data
    public ArrayList<HashMap<String, String>> getSuppliers() {
        ArrayList<HashMap<String, String>> supplier = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers ORDER BY suppliers_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();


                map.put(Constant.SUPPLIERS_ID, cursor.getString(0));
                map.put(Constant.SUPPLIERS_NAME, cursor.getString(1));
                map.put(Constant.SUPPLIERS_CONTACT_PERSON, cursor.getString(2));
                map.put(Constant.SUPPLIERS_CELL, cursor.getString(3));
                map.put(Constant.SUPPLIERS_EMAIL, cursor.getString(4));
                map.put(Constant.SUPPLIERS_ADDRESS, cursor.getString(5));


                supplier.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return supplier;
    }


    //delete customer
    public boolean deleteCustomer(String customerId) {


        long check = database.delete("customers", "customer_id=?", new String[]{customerId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete category
    public boolean deleteUser(String id) {


        long check = database.delete("users", "id=?", new String[]{id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete category
    public boolean deleteCategory(String categoryId) {


        long check = database.delete("product_category", "category_id=?", new String[]{categoryId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }


    }


    //delete payment method
    public boolean deletePaymentMethod(String paymentMethodId) {


        long check = database.delete(Constant.paymentMethod, "payment_method_id=?", new String[]{paymentMethodId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete order
    public boolean deleteOrder(String invoiceId) {


        long check = database.delete(Constant.orderList, "invoice_id=?", new String[]{invoiceId});
        database.delete(Constant.orderDetails, "invoice_id=?", new String[]{invoiceId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete product
    public boolean deleteProduct(String productId) {


        long check = database.delete(Constant.products, "product_id=?", new String[]{productId});
        database.delete(Constant.productCart, "product_id=?", new String[]{productId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete product
    public boolean deleteExpense(String expenseId) {


        long check = database.delete(Constant.expense, "expense_id=?", new String[]{expenseId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete supplier
    public boolean deleteSupplier(String customerId) {


        long check = database.delete(Constant.suppliers, "suppliers_id=?", new String[]{customerId});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }
}