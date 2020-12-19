package com.app.restaurantpos.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;

import java.util.HashMap;
import java.util.List;

public class SalesReportAdapter extends RecyclerView.Adapter<SalesReportAdapter.MyViewHolder> {


    Context context;
    private List<HashMap<String, String>> orderData;


    public SalesReportAdapter(Context context, List<HashMap<String, String>> orderData) {
        this.context = context;
        this.orderData = orderData;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_report_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String currency = databaseAccess.getCurrency();


        holder.txtProductName.setText(orderData.get(position).get("product_name"));

        holder.txtProductQty.setText(context.getString(R.string.quantity) + orderData.get(position).get("product_qty"));
        holder.txtProductWeight.setText(context.getString(R.string.weight) + orderData.get(position).get("product_weight"));

        holder.txtDate.setText(context.getString(R.string.date) + orderData.get(position).get("product_order_date"));

        String unitPrice = orderData.get(position).get("product_price");
        String qty = orderData.get(position).get("product_qty");
        double price = Double.parseDouble(unitPrice);
        int quantity = Integer.parseInt(qty);
        double cost = quantity * price;

        holder.txtTotalCost.setText(currency + unitPrice + " x " + qty + " = " + currency + cost);

        String base64Image = orderData.get(position).get("product_image");


        if (base64Image != null) {
            if (base64Image.isEmpty() || base64Image.length() < 6) {
                holder.imgProduct.setImageResource(R.drawable.ic_item_menu3);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.imgProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtDate, txtTotalCost, txtProductPrice, txtProductQty, txtProductWeight;
        ImageView imgProduct;


        public MyViewHolder(View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtProductPrice = itemView.findViewById(R.id.txt_price);
            txtProductQty = itemView.findViewById(R.id.txt_qty);
            txtProductWeight = itemView.findViewById(R.id.txt_weight);
            imgProduct = itemView.findViewById(R.id.img_product);
            txtTotalCost = itemView.findViewById(R.id.txt_total_cost);
            txtDate = itemView.findViewById(R.id.txt_date);


        }

    }


}