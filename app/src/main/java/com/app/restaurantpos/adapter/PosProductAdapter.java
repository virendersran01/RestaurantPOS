package com.app.restaurantpos.adapter;

import android.content.Context;
import android.content.Intent;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.product.EditProductActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.MyViewHolder> {


    private List<HashMap<String, String>> productData;
    private Context context;
    MediaPlayer player;


    public PosProductAdapter(Context context, List<HashMap<String, String>> productData) {
        this.context = context;
        this.productData = productData;
        player = MediaPlayer.create(context, R.raw.delete_sound);

    }


    @NonNull
    @Override
    public PosProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_product_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PosProductAdapter.MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        final String product_id = productData.get(position).get("product_id");
        String name = productData.get(position).get("product_name");
        final String product_weight = productData.get(position).get("product_weight");
        final String product_price = productData.get(position).get("product_sell_price");
        final String weight_unit_id = productData.get(position).get("product_weight_unit_id");
        String base64Image = productData.get(position).get("product_image");


        databaseAccess.open();
        final String weight_unit_name = databaseAccess.getWeightUnitName(weight_unit_id);

        holder.txtProductName.setText(name);
        holder.txtWeight.setText(product_weight + " " + weight_unit_name);
        holder.txtPrice.setText(currency + product_price);

        holder.cardProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                player.start();
                Intent intent=new Intent(context, EditProductActivity.class);
                intent.putExtra("product_id",product_id);
                context.startActivity(intent);
            }
        });



        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64base", base64Image);
                holder.productImage.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.productImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();

                int check = databaseAccess.addToCart(product_id, product_weight, weight_unit_id, product_price, 1);

                if (check == 1) {
                    Toasty.success(context, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                    player.start();
                } else if (check == 2) {

                    Toasty.info(context, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();

                } else {
                    Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardProduct;
        TextView txtProductName, txtWeight, txtPrice;
        Button btnAddToCart;
        ImageView productImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtWeight = itemView.findViewById(R.id.txt_weight);
            txtPrice = itemView.findViewById(R.id.txt_price);
            productImage = itemView.findViewById(R.id.img_product);
            btnAddToCart = itemView.findViewById(R.id.btn_add_cart);
            cardProduct=itemView.findViewById(R.id.card_product);


        }
    }


}
