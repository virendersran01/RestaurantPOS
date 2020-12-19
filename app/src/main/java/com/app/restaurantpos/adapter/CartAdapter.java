package com.app.restaurantpos.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {


    private List<HashMap<String, String>> cartProduct;
    private Context context;
    MediaPlayer player;
    TextView txtNoProduct;

    TextView txtTotalPrice;

    public static Double totalPrice;
    Button btnSubmitOrder;
    ImageView imgNoProduct;


    public CartAdapter(Context context, List<HashMap<String, String>> cartProduct, TextView txtTotalPrice, Button btnSubmitOrder, ImageView imgNoProduct, TextView txtNoProduct) {
        this.context = context;
        this.cartProduct = cartProduct;
        player = MediaPlayer.create(context, R.raw.delete_sound);
        this.txtTotalPrice = txtTotalPrice;
        this.btnSubmitOrder = btnSubmitOrder;
        this.imgNoProduct = imgNoProduct;
        this.txtNoProduct = txtNoProduct;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_product_items, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();

        final String cart_id = cartProduct.get(position).get("cart_id");
        String productId = cartProduct.get(position).get("product_id");
        String productName = databaseAccess.getProductName(productId);


        final String price = cartProduct.get(position).get("product_price");
        final String productWeightUnit = cartProduct.get(position).get("product_weight_unit");
        final String weight = cartProduct.get(position).get("product_weight");
        final String qty = cartProduct.get(position).get("product_qty");


        databaseAccess.open();
        String base64Image = databaseAccess.getProductImage(productId);

        databaseAccess.open();
        String weightUnitName = databaseAccess.getWeightUnitName(productWeightUnit);


        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        totalPrice = databaseAccess.getTotalPrice();
        txtTotalPrice.setText(context.getString(R.string.total_price) + currency + totalPrice);


        if (base64Image != null) {
            if (base64Image.isEmpty() || base64Image.length() < 6) {
                holder.imgProduct.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.imgProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


        final DecimalFormat f = new DecimalFormat("#0.00");
        final double getPrice = Double.parseDouble(price) * Integer.parseInt(qty);


        holder.txtItemName.setText(productName);
        holder.txtPrice.setText(currency + f.format(getPrice));
        holder.txtWeight.setText(weight + " " + weightUnitName);
        holder.txtQtyNumber.setText(qty);

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                boolean deleteProduct = databaseAccess.deleteProductFromCart(cart_id);

                if (deleteProduct) {
                    Toasty.success(context, context.getString(R.string.product_removed_from_cart), Toast.LENGTH_SHORT).show();

                    // Calculate Cart's Total Price Again
                    player.start();

                    //for delete cart item dynamically
                    cartProduct.remove(holder.getAdapterPosition());

                    // Notify that item at position has been removed
                    notifyItemRemoved(holder.getAdapterPosition());


                    databaseAccess.open();
                    totalPrice = databaseAccess.getTotalPrice();
                    txtTotalPrice.setText(context.getString(R.string.total_price) + currency + totalPrice);


                } else {
                    Toasty.error(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }


                databaseAccess.open();
                int itemCount = databaseAccess.getCartItemCount();
                Log.d("itemCount", "" + itemCount);
                if (itemCount <= 0) {
                    txtTotalPrice.setVisibility(View.GONE);
                    btnSubmitOrder.setVisibility(View.GONE);

                    imgNoProduct.setVisibility(View.VISIBLE);
                    txtNoProduct.setVisibility(View.VISIBLE);
                }

            }
        });


        holder.txtPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String qty1 = holder.txtQtyNumber.getText().toString();
                int getQty = Integer.parseInt(qty1);

                getQty++;


                double cost = Double.parseDouble(price) * getQty;


                holder.txtPrice.setText(currency + f.format(cost));
                holder.txtQtyNumber.setText(String.valueOf(getQty));


                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                databaseAccess.updateProductQty(cart_id, "" + getQty);

                totalPrice = totalPrice + Double.valueOf(price);
                txtTotalPrice.setText(context.getString(R.string.total_price) + currency + f.format(totalPrice));

            }
        });


        holder.txtMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String qty = holder.txtQtyNumber.getText().toString();
                int getQty = Integer.parseInt(qty);


                if (getQty >= 2) {
                    getQty--;

                    double cost = Double.parseDouble(price) * getQty;

                    holder.txtPrice.setText(currency + f.format(cost));
                    holder.txtQtyNumber.setText(String.valueOf(getQty));


                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    databaseAccess.updateProductQty(cart_id, "" + getQty);

                    totalPrice = totalPrice - Double.valueOf(price);
                    txtTotalPrice.setText(context.getString(R.string.total_price) + currency + f.format(totalPrice));


                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return cartProduct.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemName;
        TextView txtPrice;
        TextView txtWeight;
        TextView txtQtyNumber;
        TextView txtPlus;
        TextView txtMinus;
        ImageView imgProduct;
        ImageView imgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtItemName = itemView.findViewById(R.id.txt_item_name);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtWeight = itemView.findViewById(R.id.txt_weight);
            txtQtyNumber = itemView.findViewById(R.id.txt_number);
            imgProduct = itemView.findViewById(R.id.cart_product_image);
            imgDelete = itemView.findViewById(R.id.img_delete);
            txtMinus = itemView.findViewById(R.id.txt_minus);
            txtPlus = itemView.findViewById(R.id.txt_plus);

        }


    }


}
