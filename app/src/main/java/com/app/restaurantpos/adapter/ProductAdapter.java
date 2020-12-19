package com.app.restaurantpos.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.product.EditProductActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {


    private List<HashMap<String, String>> productData;
    private Context context;


    public ProductAdapter(Context context, List<HashMap<String, String>> productData) {
        this.context = context;
        this.productData = productData;

    }


    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ProductAdapter.MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        final String product_id = productData.get(position).get(Constant.PRODUCT_ID);
        String name = productData.get(position).get(Constant.PRODUCT_NAME);
        String supplierId = productData.get(position).get(Constant.PRODUCT_SUPPLIER);
        String sellPrice = productData.get(position).get(Constant.PRODUCT_SELL_PRICE);
        String base64Image = productData.get(position).get(Constant.PRODUCT_IMAGE);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        String supplierName = databaseAccess.getSupplierName(supplierId);

        holder.txtProductName.setText(name);
        holder.txtSupplierName.setText(context.getString(R.string.supplier) + supplierName);
        holder.txtSellPrice.setText(context.getString(R.string.sell_price) + currency + sellPrice);


        if (base64Image != null) {
            if (base64Image.length() < 6) {

                holder.productImage.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.productImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle(context.getString(R.string.delete))
                        .withMessage(context.getString(R.string.want_to_delete_product))
                        .withEffect(Slidetop)
                        .withDialogColor("#f29161") //use color code for dialog
                        .withButton1Text(context.getString(R.string.yes))
                        .withButton2Text(context.getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                databaseAccess.open();
                                boolean deleteProduct = databaseAccess.deleteProduct(product_id);

                                if (deleteProduct) {
                                    Toasty.error(context, R.string.product_deleted, Toast.LENGTH_SHORT).show();

                                    productData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                } else {
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }


                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogBuilder.dismiss();
                            }
                        })
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtProductName, txtSupplierName, txtSellPrice;
        ImageView imgDelete, productImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtSupplierName = itemView.findViewById(R.id.txt_product_supplier);
            txtSellPrice = itemView.findViewById(R.id.txt_product_sell_price);

            imgDelete = itemView.findViewById(R.id.img_delete);
            productImage = itemView.findViewById(R.id.product_image);

            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, EditProductActivity.class);
            i.putExtra(Constant.PRODUCT_ID, productData.get(getAdapterPosition()).get(Constant.PRODUCT_ID));
            context.startActivity(i);

        }
    }


}
